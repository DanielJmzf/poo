/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Circuito;

import Componentes.Componente;
import Componentes.Led;
import Componentes.Switch;
import Compuertas.Compuerta;
import Compuertas.CompuertaAND;
import Compuertas.CompuertaNOT;
import Compuertas.CompuertaOR;
import Circuito.CircuitoComponenteFactory;
import principal.MiPanel;

import logicaExpresiones.ASTNode;
import logicaExpresiones.VariableNode;
import logicaExpresiones.OperacionUnariaNode;
import logicaExpresiones.OperacionBinariaNode;
import logicaExpresiones.TipoOperador;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;
import javax.swing.JOptionPane;

public class Circuito implements Serializable, Cloneable {
    private final ComponenteFactory componenteFactory;
    private static final long serialVersionUID = 1L;
    private String nombre;
    private List<Componente> componentes = new ArrayList<>();
    private List<Conector> conexiones = new ArrayList<>();
    private boolean modificado = false;
    private transient String rutaArchivo;
    private transient MiPanel panelReferencia;
    public List<Componente> portapapeles = new ArrayList<>();
    private Point centroPortapapeles;

    public Stack<CircuitoState> undoStack = new Stack<>();
    public Stack<CircuitoState> redoStack = new Stack<>();

    private transient Map<Integer, Integer> maxYPorColumna;

    private static final int X_INICIAL_LAYOUT_SWITCHES = 50;
    private static final int Y_INICIAL_LAYOUT = 50;
    private static final int ESPACIADO_Y_LAYOUT = 70;
    private static final int ESPACIADO_X_COLUMNA_LAYOUT = 180;

    public Circuito(String nombre) {
        this(nombre, new CircuitoComponenteFactory());
    }

    public Circuito(String nombre, ComponenteFactory factory) {
        this.nombre = nombre;
        this.componenteFactory = factory != null ? factory : new CircuitoComponenteFactory();
        if (this.componentes == null) this.componentes = new ArrayList<>();
        if (this.conexiones == null) this.conexiones = new ArrayList<>();
        if (this.portapapeles == null) this.portapapeles = new ArrayList<>();
        if (this.undoStack == null) this.undoStack = new Stack<>();
        if (this.redoStack == null) this.redoStack = new Stack<>();
    }

    private Pin obtenerPin(Componente componente, int pinIndex) {
        if (componente == null || componente.getSalidas() == null || componente.getEntradas() == null) {
            return null;
        }
        if (pinIndex < componente.getSalidas().size()) {
            return componente.getSalidas().get(pinIndex);
        }
        int entradaIndex = pinIndex - componente.getSalidas().size();
        if (entradaIndex >= 0 && entradaIndex < componente.getEntradas().size()) {
            return componente.getEntradas().get(entradaIndex);
        }
        return null;
    }

    private static class CircuitoState implements Serializable {
        private static final long serialVersionUID = 2L;
        List<Componente> componentes;
        List<Conector> conexiones;

        public CircuitoState(List<Componente> componentesOriginales, List<Conector> conexionesOriginales) {
            this.componentes = new ArrayList<>();
            Map<String, Componente> originalToCloneMap = new HashMap<>();

            for (Componente original : componentesOriginales) {
                try {
                    Componente clon = original.clone();
                    clon.setCircuito(null); // Se establece temporalmente a null para el estado.
                    this.componentes.add(clon);
                    originalToCloneMap.put(original.getId(), clon);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            this.conexiones = new ArrayList<>();
            if (conexionesOriginales != null) {
                for (Conector originalConector : conexionesOriginales) {
                    try {
                        if (originalConector != null &&
                            originalConector.obtenerPinSalida() != null &&
                            originalConector.obtenerPinEntrada() != null &&
                            originalConector.obtenerPinSalida().getComponente() != null &&
                            originalConector.obtenerPinEntrada().getComponente() != null) {

                            Componente origenClon = originalToCloneMap.get(
                                originalConector.obtenerPinSalida().getComponente().getId());
                            Componente destinoClon = originalToCloneMap.get(
                                originalConector.obtenerPinEntrada().getComponente().getId());

                            if (origenClon != null && destinoClon != null) {
                                Pin pinSalidaClon = encontrarPinCorrespondiente(
                                    originalConector.obtenerPinSalida(), origenClon);
                                Pin pinEntradaClon = encontrarPinCorrespondiente(
                                    originalConector.obtenerPinEntrada(), destinoClon);

                                if (pinSalidaClon != null && pinEntradaClon != null) {
                                    Conector conectorClon = new Conector();
                                    // Usamos conectarForzado para establecer la conexión en los objetos clonados.
                                    // Esto ya asigna el conector a los pines clonados.
                                    if (conectorClon.conectarForzado(pinSalidaClon, pinEntradaClon)) {
                                        this.conexiones.add(conectorClon);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            // Asegurarse de que todos los componentes clonados en el estado no tengan referencia al circuito principal.
            for (Componente clon : this.componentes) {
                clon.setCircuito(null);
            }
        }

        private Pin encontrarPinCorrespondiente(Pin pinOriginal, Componente componenteClon) {
            if (pinOriginal == null || componenteClon == null) return null;

            Componente componenteOriginal = pinOriginal.getComponente();
            if (componenteOriginal == null) return null;

            if ("entrada".equals(pinOriginal.getTipo())) {
                int index = componenteOriginal.getEntradas().indexOf(pinOriginal);
                if (index >= 0 && index < componenteClon.getEntradas().size()) {
                    return componenteClon.getEntradas().get(index);
                }
            } else {
                int index = componenteOriginal.getSalidas().indexOf(pinOriginal);
                if (index >= 0 && index < componenteClon.getSalidas().size()) {
                    return componenteClon.getSalidas().get(index);
                }
            }
            return null;
        }
    }

    public void guardarEstado() {
        if (!undoStack.isEmpty()) {
            CircuitoState ultimoEstado = undoStack.peek();
            // Esta comparación solo verifica el tamaño, no un cambio profundo.
            // Para una detección más robusta de cambios, se necesitaría una comparación de contenido.
            if (estadoIgualAlUltimo(ultimoEstado)) {
                return;
            }
        }
        undoStack.push(new CircuitoState(this.componentes, this.conexiones));
        redoStack.clear();
        if (undoStack.size() > 100) {
            undoStack.remove(0);
        }
    }

    private boolean estadoIgualAlUltimo(CircuitoState ultimoEstado) {
        // Esta función solo compara el tamaño de las listas.
        // Para una detección de cambios más precisa (movimientos, cambios de estado),
        // se necesitaría una comparación profunda de los componentes y conexiones.
        if (ultimoEstado.componentes.size() != componentes.size() ||
            ultimoEstado.conexiones.size() != conexiones.size()) {
            return false;
        }
        // Podrías añadir lógica aquí para comparar posiciones, estados de switches, etc.
        // Por ejemplo:
        /*
        for (int i = 0; i < componentes.size(); i++) {
            Componente c1 = componentes.get(i);
            Componente c2 = ultimoEstado.componentes.get(i);
            if (c1.getX() != c2.getX() || c1.getY() != c2.getY()) {
                return false;
            }
            // Añadir más comparaciones si es necesario (ej. estado de switches, tipo de compuerta)
        }
        // Similar para conexiones
        */
        return true;
    }

    public boolean deshacer() {
        if (undoStack.isEmpty()) return false;
        // Guardar el estado actual en el redoStack antes de deshacer
        redoStack.push(new CircuitoState(this.componentes, this.conexiones));
        CircuitoState estadoAnterior = undoStack.pop();
        restaurarEstado(estadoAnterior);
        return true;
    }

    public boolean rehacer() {
        if (redoStack.isEmpty()) return false;
        // Guardar el estado actual en el undoStack antes de rehacer
        undoStack.push(new CircuitoState(this.componentes, this.conexiones));
        CircuitoState estadoRedo = redoStack.pop();
        restaurarEstado(estadoRedo);
        return true;
    }

    private void restaurarEstado(CircuitoState estado) {
        this.componentes.clear();
        this.conexiones.clear();

        // Restaurar componentes y establecer su referencia al circuito
        for (Componente clonComp : estado.componentes) {
            clonComp.setCircuito(this);
            this.componentes.add(clonComp);
        }

        // Restaurar conexiones. Los pines de estos conectores ya están correctamente
        // enlazados a los componentes clonados por el constructor de CircuitoState.
        // NO es necesario llamar a conectarA() en los pines aquí, ya que eso causaría
        // la excepción de "pin ya conectado" y los mensajes de error.
        for (Conector clonConector : estado.conexiones) {
            this.conexiones.add(clonConector);
            // Las siguientes líneas fueron la causa de los mensajes de error repetidos.
            // Los pines ya están conectados a este conector clonado por la lógica de clonación.
            // Eliminadas para evitar la reconexión redundante y las excepciones.
            /*
            if (clonConector.obtenerPinSalida() != null && clonConector.obtenerPinSalida().getComponente() != null) {
                clonConector.obtenerPinSalida().getComponente().setCircuito(this);
                clonConector.obtenerPinSalida().conectarA(clonConector);
            }
            if (clonConector.obtenerPinEntrada() != null && clonConector.obtenerPinEntrada().getComponente() != null) {
                clonConector.obtenerPinEntrada().getComponente().setCircuito(this);
                clonConector.obtenerPinEntrada().conectarA(clonConector);
            }
            */
        }
        this.modificado = true;
        this.evaluar();
    }

    public void setPanelReferencia(MiPanel panel) {
        this.panelReferencia = panel;
    }

    public void mostrarError(String mensaje) {
        if (panelReferencia != null) {
            panelReferencia.mostrarMensajeError(mensaje);
        } else {
            JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        List<Conector> conexionesValidas = new ArrayList<>();
        for (Conector conector : conexiones) {
            if (conector != null &&
                conector.obtenerPinSalida() != null &&
                conector.obtenerPinEntrada() != null &&
                conector.obtenerPinSalida().getComponente() != null &&
                conector.obtenerPinEntrada().getComponente() != null) {
                conexionesValidas.add(conector);
            }
        }
        out.writeInt(conexionesValidas.size());
        for (Conector conector : conexionesValidas) {
            Pin pinSalida = conector.obtenerPinSalida();
            Pin pinEntrada = conector.obtenerPinEntrada();
            out.writeObject(pinSalida.getComponente().getId());
            out.writeInt(pinSalida.getComponente().getSalidas().indexOf(pinSalida));
            out.writeObject(pinEntrada.getComponente().getId());
            out.writeInt(pinEntrada.getComponente().getEntradas().indexOf(pinEntrada));
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (componentes == null) componentes = new ArrayList<>();
        conexiones = new ArrayList<>();
        for (Componente comp : componentes) {
            if (comp != null) {
                int numEntradas = 0;
                int numSalidas = 0;
                if (comp instanceof Compuertas.CompuertaAND) { numEntradas = 2; numSalidas = 1; }
                else if (comp instanceof Compuertas.CompuertaOR) { numEntradas = 2; numSalidas = 1; }
                else if (comp instanceof Compuertas.CompuertaNOT) { numEntradas = 1; numSalidas = 1; }
                else if (comp instanceof Componentes.Switch) { numEntradas = 0; numSalidas = 1; }
                else if (comp instanceof Componentes.Led) { numEntradas = 1; numSalidas = 0; }
                comp.inicializarPines(numEntradas, numSalidas);
                comp.setCircuito(this);
                if (comp instanceof Compuerta) { // Re-inicializar renderer si es transient
                    ((Compuerta) comp).reinitializeRenderer();
                }
            }
        }
        Map<String, Componente> mapaComponentes = new HashMap<>();
        for (Componente c : componentes) {
            if (c != null) mapaComponentes.put(c.getId(), c);
        }
        int numConectores = in.readInt();
        for (int i = 0; i < numConectores; i++) {
            try {
                String idCompSalida = (String) in.readObject();
                int indexPinSalida = in.readInt();
                String idCompEntrada = (String) in.readObject();
                int indexPinEntrada = in.readInt();
                Componente compSalida = mapaComponentes.get(idCompSalida);
                Componente compEntrada = mapaComponentes.get(idCompEntrada);
                if (compSalida != null && compEntrada != null) {
                    if (indexPinSalida >= 0 && indexPinSalida < compSalida.getSalidas().size() &&
                        indexPinEntrada >= 0 && indexPinEntrada < compEntrada.getEntradas().size()) {
                        Pin pinSalida = compSalida.getSalidas().get(indexPinSalida);
                        Pin pinEntrada = compEntrada.getEntradas().get(indexPinEntrada);
                        if (pinSalida != null && pinEntrada != null) {
                            Conector nuevoConector = new Conector();
                            if (nuevoConector.conectar(pinSalida, pinEntrada)) {
                                conexiones.add(nuevoConector);
                            } else {
                                // Se eliminó el JOptionPane.showMessageDialog aquí para evitar pop-ups al cargar.
                                // Si hay un problema de conexión durante la carga, se puede registrar en consola.
                                if (panelReferencia != null) System.err.println("Fallo al conectar pines durante carga: " + pinSalida.getComponente().getId() + "[" + indexPinSalida + "] -> " + pinEntrada.getComponente().getId() + "[" + indexPinEntrada + "]");
                            }
                        } else {
                            if (panelReferencia != null) System.err.println("Pin nulo encontrado al reconstruir conector #" + i);
                        }
                    } else {
                        if (panelReferencia != null) System.err.println("Índice de pin fuera de rango para conector #" + i);
                    }
                } else {
                    if (panelReferencia != null) System.err.println("Componente nulo encontrado al reconstruir conector #" + i);
                }
            } catch (Exception e) {
                if (panelReferencia != null) System.err.println("Excepción reconstruyendo conector #" + i + ": " + e.getMessage());
            }
        }
        evaluar();
    }

    public void toggleSwitch(Switch sw) {
        try {
            if (sw == null) throw new Exception("El switch no puede ser nulo");
            if (!componentes.contains(sw)) throw new Exception("El switch no existe en este circuito");
            guardarEstado(); // Guardar estado antes de la acción
            sw.toggle();
            this.modificado = true;
            this.evaluar();
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    public void agregarComponente(Componente componente) {
        try {
            if (componente == null) throw new Exception("No se puede agregar un componente nulo");
            guardarEstado(); // Guardar estado antes de la acción
            componentes.add(componente);
            componente.setCircuito(this);
            modificado = true;
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    public void agregarComponente(String tipo, int x, int y) {
        try {
            Componente componente = componenteFactory.crearComponente(tipo, x, y);
            agregarComponente(componente);
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    public void conectar(Componente origenComp, int pinOrigenIndexAbs, Componente destinoComp, int pinDestinoIndexAbs) {
        try {
            if (origenComp == null || destinoComp == null) throw new Exception("Los componentes para conectar no pueden ser nulos");
            guardarEstado(); // Guardar estado antes de la acción
            Pin pinOrigen = obtenerPin(origenComp, pinOrigenIndexAbs);
            Pin pinDestino = obtenerPin(destinoComp, pinDestinoIndexAbs);
            if (pinOrigen == null || pinDestino == null) throw new Exception("Uno o ambos pines especificados no existen o son inválidos para la conexión.");
            Conector nuevoConector = new Conector();
            if (!nuevoConector.conectar(pinOrigen, pinDestino)) throw new Exception("No se pudo crear la conexión (verifique tipos de pin y si ya están conectados).");
            conexiones.add(nuevoConector);
            modificado = true;
            if (pinDestino.getComponente() != null) pinDestino.getComponente().evaluar();
        } catch (Exception e) {
            mostrarError("Error de conexión: " + e.getMessage());
        }
    }

    public void evaluar() {
        // Primero evaluar switches para asegurar que sus estados se propaguen
        for (Componente componente : componentes) {
            if (componente instanceof Switch) {
                componente.evaluar();
            }
        }
        // Luego propagar estados a través de los conectores
        for (Conector conector : conexiones) {
            if (conector != null) conector.propagarEstado();
        }
        // Finalmente, evaluar el resto de los componentes (compuertas, leds)
        for (Componente componente : componentes) {
            if (componente != null && !(componente instanceof Switch)) componente.evaluar();
        }
    }

    public void eliminarComponente(Componente componente) {
        if (componente == null) return;
        guardarEstado(); // Guardar estado antes de la acción
        List<Conector> conexionesAEliminar = new ArrayList<>();
        for (Conector conector : conexiones) {
            if (conector != null) {
                Pin pinSalida = conector.obtenerPinSalida();
                Pin pinEntrada = conector.obtenerPinEntrada();
                if ((pinSalida != null && pinSalida.getComponente() == componente) ||
                    (pinEntrada != null && pinEntrada.getComponente() == componente)) {
                    conexionesAEliminar.add(conector);
                }
            }
        }
        for (Conector conector : conexionesAEliminar) {
            eliminarConectorInterno(conector);
        }
        componentes.remove(componente);
        modificado = true;
        evaluar();
    }

    private void eliminarConectorInterno(Conector conector) {
        if (conector == null) return;
        conector.desconectar();
        conexiones.remove(conector);
    }

    public List<Componente> seleccionarComponentesEnArea(Rectangle area) {
        List<Componente> seleccionados = new ArrayList<>();
        for (Componente c : componentes) {
            if (c != null) {
                Rectangle bounds;
                if (c instanceof Compuerta) {
                    bounds = new Rectangle(c.getX(), c.getY(), ((Compuerta)c).ancho * 2, ((Compuerta)c).alto);
                } else if (c instanceof Switch || c instanceof Led) {
                    bounds = new Rectangle(c.getX(), c.getY(), 30, 30);
                } else {
                    bounds = new Rectangle(c.getX(), c.getY(), 40, 40);
                }
                if (area.intersects(bounds) || area.contains(bounds)) {
                    seleccionados.add(c);
                }
            }
        }
        return seleccionados;
    }

    public void eliminarConector(Conector conector) {
        if (conector == null) return;
        guardarEstado(); // Guardar estado antes de la acción
        eliminarConectorInterno(conector);
        modificado = true;
        evaluar();
    }

    public void copiarAlPortapeles(List<Componente> componentesACopiar) {
        if (componentesACopiar == null || componentesACopiar.isEmpty()) {
            mostrarError("No hay componentes seleccionados para copiar");
            return;
        }
        portapapeles.clear();
        int sumX = 0, sumY = 0;
        for (Componente original : componentesACopiar) {
            int compAncho = 40;
            int compAlto = 40;
            if (original instanceof Compuerta) {
                compAncho = ((Compuerta)original).ancho * 2;
                compAlto = ((Compuerta)original).alto;
            } else if (original instanceof Switch || original instanceof Led){
                compAncho = 30;
                compAlto = 30;
            }
            sumX += original.getX() + compAncho / 2;
            sumY += original.getY() + compAlto / 2;
        }
        if (!componentesACopiar.isEmpty()) {
            this.centroPortapapeles = new Point(sumX / componentesACopiar.size(), sumY / componentesACopiar.size());
        } else {
            this.centroPortapapeles = new Point(0,0);
        }
        Map<Componente, Componente> originalToCopiaMap = new HashMap<>();
        Map<Pin, Pin> pinOriginalToCopiaMap = new HashMap<>();
        for (Componente original : componentesACopiar) {
            try {
                Componente copia = original.clone();
                copia.setCircuito(null);
                copia.id = UUID.randomUUID().toString();
                portapapeles.add(copia);
                originalToCopiaMap.put(original, copia);
                for (int i = 0; i < original.getEntradas().size(); i++) {
                    pinOriginalToCopiaMap.put(original.getEntradas().get(i), copia.getEntradas().get(i));
                }
                for (int i = 0; i < original.getSalidas().size(); i++) {
                    pinOriginalToCopiaMap.put(original.getSalidas().get(i), copia.getSalidas().get(i));
                }
            } catch (Exception e) {
                mostrarError("Error al copiar componente: " + e.getMessage());
            }
        }
        for (Conector originalConector : this.conexiones) {
            try {
                Pin pinSalidaOriginal = originalConector.obtenerPinSalida();
                Pin pinEntradaOriginal = originalConector.obtenerPinEntrada();
                if (pinSalidaOriginal != null && pinEntradaOriginal != null &&
                    originalToCopiaMap.containsKey(pinSalidaOriginal.getComponente()) &&
                    originalToCopiaMap.containsKey(pinEntradaOriginal.getComponente())) {
                    Pin pinSalidaCopia = pinOriginalToCopiaMap.get(pinSalidaOriginal);
                    Pin pinEntradaCopia = pinOriginalToCopiaMap.get(pinEntradaOriginal);
                    if (pinSalidaCopia != null && pinEntradaCopia != null) {
                        Conector conectorCopia = new Conector();
                        conectorCopia.conectarForzado(pinSalidaCopia, pinEntradaCopia);
                    }
                }
            } catch (Exception e) { /* Ignorar */ }
        }
    }

    public void eliminarSeleccion(List<Componente> componentesAEliminar) {
        if (componentesAEliminar == null || componentesAEliminar.isEmpty()) {
            mostrarError("No hay componentes seleccionados para eliminar");
            return;
        }
        guardarEstado(); // Guardar estado antes de la acción
        List<Conector> conexionesParaRemover = new ArrayList<>();
        for (Conector conector : this.conexiones) {
            if (conector != null) {
                Pin pinSalida = conector.obtenerPinSalida();
                Pin pinEntrada = conector.obtenerPinEntrada();
                if ((pinSalida != null && componentesAEliminar.contains(pinSalida.getComponente())) ||
                    (pinEntrada != null && componentesAEliminar.contains(pinEntrada.getComponente()))) {
                    conexionesParaRemover.add(conector);
                }
            }
        }
        for (Conector conector : conexionesParaRemover) {
            eliminarConectorInterno(conector);
        }
        componentes.removeAll(componentesAEliminar);
        modificado = true;
        evaluar();
    }

    private Rectangle calcularAreaPortapapeles() {
        if (portapapeles.isEmpty()) return new Rectangle();
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
        for (Componente c : portapapeles) {
            minX = Math.min(minX, c.getX());
            minY = Math.min(minY, c.getY());
            int compAncho = 40, compAlto = 40;
            if (c instanceof Compuerta) {
                compAncho = ((Compuerta)c).ancho*2;
                compAlto = ((Compuerta)c).alto;
            } else if (c instanceof Switch || c instanceof Led) {
                compAncho = 30; compAlto = 30;
            }
            maxX = Math.max(maxX, c.getX() + compAncho);
            maxY = Math.max(maxY, c.getY() + compAlto);
        }
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    private Point encontrarPosicionLibre(int anchoAreaPegado, int altoAreaPegado) {
        int panelWidth = (panelReferencia != null) ? panelReferencia.getWidth() : 800;
        int panelHeight = (panelReferencia != null) ? panelReferencia.getHeight() : 600;
        int startX = Math.max(20, (panelWidth - anchoAreaPegado) / 2);
        int startY = Math.max(20, (panelHeight - altoAreaPegado) / 2);
        for (int r = 0; r < 300; r += 50) {
            for (int angleDeg = 0; angleDeg < 360; angleDeg += 45) {
                int testX = startX + (int)(r * Math.cos(Math.toRadians(angleDeg)));
                int testY = startY + (int)(r * Math.sin(Math.toRadians(angleDeg)));
                if (testX < 20) testX = 20;
                if (testY < 20) testY = 20;
                if (panelWidth > 0 && testX + anchoAreaPegado > panelWidth - 20) testX = panelWidth - 20 - anchoAreaPegado;
                if (panelHeight > 0 && testY + altoAreaPegado > panelHeight - 20) testY = panelHeight - 20 - altoAreaPegado;
                if (esAreaLibre(testX, testY, anchoAreaPegado, altoAreaPegado)) {
                    return new Point(testX, testY);
                }
            }
        }
        return new Point(50, 50);
    }

    private boolean esAreaLibre(int x, int y, int ancho, int alto) {
        Rectangle areaNueva = new Rectangle(x, y, ancho, alto);
        for (Componente cExistente : componentes) {
            if (cExistente != null) {
                int compAncho = 40, compAlto = 40;
                if (cExistente instanceof Compuerta) {
                    compAncho = ((Compuerta)cExistente).ancho*2;
                    compAlto = ((Compuerta)cExistente).alto;
                } else if (cExistente instanceof Switch || cExistente instanceof Led) {
                    compAncho = 30; compAlto = 30;
                }
                Rectangle areaComponenteExistente = new Rectangle(cExistente.getX(), cExistente.getY(), compAncho, compAlto);
                if (areaNueva.intersects(areaComponenteExistente)) {
                    return false;
                }
            }
        }
        if (panelReferencia != null) {
            return panelReferencia.getBounds().contains(areaNueva);
        }
        return true;
    }

    public void pegarDesdePortapapeles(Point puntoPegadoMouse) {
        if (portapapeles.isEmpty() || this.centroPortapapeles == null) {
            mostrarError("No hay componentes en el portapapeles para pegar");
            return;
        }
        guardarEstado(); // Guardar estado antes de la acción
        Rectangle areaOriginalPortapapeles = calcularAreaPortapapeles();
        Point puntoAnclajeSuperiorIzquierdo;
        if (puntoPegadoMouse != null) {
            int offsetX = puntoPegadoMouse.x - this.centroPortapapeles.x;
            int offsetY = puntoPegadoMouse.y - this.centroPortapapeles.y;
            puntoAnclajeSuperiorIzquierdo = new Point(areaOriginalPortapapeles.x + offsetX, areaOriginalPortapapeles.y + offsetY);
        } else {
            puntoAnclajeSuperiorIzquierdo = encontrarPosicionLibre(areaOriginalPortapapeles.width, areaOriginalPortapapeles.height);
            if (puntoAnclajeSuperiorIzquierdo == null) puntoAnclajeSuperiorIzquierdo = new Point(50,50);
        }
        int deltaX = puntoAnclajeSuperiorIzquierdo.x - areaOriginalPortapapeles.x;
        int deltaY = puntoAnclajeSuperiorIzquierdo.y - areaOriginalPortapapeles.y;
        List<Componente> componentesPegados = new ArrayList<>();
        Map<Componente, Componente> portapapelesToPegadoMap = new HashMap<>();
        Map<Pin, Pin> pinPortapapelesToPegadoMap = new HashMap<>();
        for (Componente originalEnPortapapeles : portapapeles) {
            try {
                Componente copiaPegada = originalEnPortapapeles.clone();
                copiaPegada.setCircuito(this);
                copiaPegada.id = UUID.randomUUID().toString();
                copiaPegada.mover(originalEnPortapapeles.getX() + deltaX, originalEnPortapapeles.getY() + deltaY);
                componentes.add(copiaPegada);
                componentesPegados.add(copiaPegada);
                portapapelesToPegadoMap.put(originalEnPortapapeles, copiaPegada);
                for (int i = 0; i < originalEnPortapapeles.getEntradas().size(); i++) {
                    pinPortapapelesToPegadoMap.put(originalEnPortapapeles.getEntradas().get(i), copiaPegada.getEntradas().get(i));
                }
                for (int i = 0; i < originalEnPortapapeles.getSalidas().size(); i++) {
                    pinPortapapelesToPegadoMap.put(originalEnPortapapeles.getSalidas().get(i), copiaPegada.getSalidas().get(i));
                }
            } catch (Exception e) {
                mostrarError("Error al clonar componente para pegar: " + e.getMessage());
            }
        }
        for (Componente originalEnPortapapeles : portapapeles) {
            for (Pin pinOriginalSalida : originalEnPortapapeles.getSalidas()) {
                if (pinOriginalSalida.getConector() != null) {
                    Pin pinOriginalEntrada = pinOriginalSalida.getConector().obtenerPinEntrada();
                    if (pinOriginalEntrada != null && portapapelesToPegadoMap.containsKey(pinOriginalEntrada.getComponente())) {
                        Pin pinPegadoSalida = pinPortapapelesToPegadoMap.get(pinOriginalSalida);
                        Pin pinPegadoEntrada = pinPortapapelesToPegadoMap.get(pinOriginalEntrada);
                        if (pinPegadoSalida != null && pinPegadoEntrada != null) {
                            Conector nuevoConector = new Conector();
                            if (nuevoConector.conectar(pinPegadoSalida, pinPegadoEntrada)) {
                                conexiones.add(nuevoConector);
                            }
                        }
                    }
                }
            }
        }
        evaluar();
        modificado = true;
        if (panelReferencia != null) panelReferencia.limpiarSeleccion();
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; this.modificado = true; }
    public List<Componente> getComponentes() { return new ArrayList<>(componentes); }
    public List<Conector> getConexiones() { return new ArrayList<>(conexiones); }
    public boolean isModificado() { return modificado; }
    public void setModificado(boolean modificado) { this.modificado = modificado; }
    public String getRutaArchivo() { return rutaArchivo; }
    public void setRutaArchivo(String ruta) { this.rutaArchivo = ruta; }

    public Componente buscarComponentePorId(String id) {
        for (Componente c : componentes) {
            if (c != null && c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    public List<Conector> buscarConectoresDeComponente(Componente componente) {
        List<Conector> resultado = new ArrayList<>();
        if (componente == null) return resultado;
        for (Conector c : conexiones) {
            if (c != null &&
                ((c.obtenerPinSalida() != null && c.obtenerPinSalida().getComponente() == componente) ||
                 (c.obtenerPinEntrada() != null && c.obtenerPinEntrada().getComponente() == componente))) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    public String generarExpresionBooleana() throws IllegalStateException {
        List<Led> leds = new ArrayList<>();
        Map<Componente, String> nombresSwitches = new HashMap<>();
        char proximoNombreVariable = 'A';
        List<Switch> switchesOrdenados = new ArrayList<>();
        for (Componente comp : this.componentes) {
            if (comp instanceof Switch) switchesOrdenados.add((Switch)comp);
            else if (comp instanceof Led) leds.add((Led)comp);
        }
        Collections.sort(switchesOrdenados, Comparator.comparingInt(Componente::getY).thenComparingInt(Componente::getX));
        for(Switch sw : switchesOrdenados) nombresSwitches.put(sw, String.valueOf(proximoNombreVariable++));

        if (leds.isEmpty()) throw new IllegalStateException("No se encontraron LEDs (salidas) en el circuito.");
        Led ledSalida = leds.get(0);
        if (ledSalida.getEntradas() == null || ledSalida.getEntradas().isEmpty()) throw new IllegalStateException("El LED de salida no tiene pines de entrada definidos.");
        Pin pinEntradaLed = ledSalida.getEntradas().get(0);
        if (pinEntradaLed == null || pinEntradaLed.getConector() == null) return pinEntradaLed != null ? (pinEntradaLed.obtenerEstado() ? "1" : "0") : "[LED no conectado]";
        Pin pinFuenteAlLed = pinEntradaLed.getConector().obtenerPinSalida();
        if (pinFuenteAlLed == null) throw new IllegalStateException("La entrada del LED está conectada a un conector inválido.");
        return construirExpresionParaPin(pinFuenteAlLed, new HashMap<>(), nombresSwitches, new HashSet<>());
    }

    private String construirExpresionParaPin(Pin pinSalidaActual, Map<Componente, String> expresionesCalculadas, Map<Componente, String> nombresSwitches, Set<Componente> visitadosEnRutaActual) throws IllegalStateException {
        if (pinSalidaActual == null) return "?";
        Componente origen = pinSalidaActual.getComponente();
        if (origen == null) return "[PinSinComponente]";
        if (expresionesCalculadas.containsKey(origen)) return expresionesCalculadas.get(origen);
        if (visitadosEnRutaActual.contains(origen)) return "[CICLO]";
        visitadosEnRutaActual.add(origen);
        String expresionGenerada;
        if (origen instanceof Switch) {
            expresionGenerada = nombresSwitches.getOrDefault(origen, origen.getId());
        } else if (origen instanceof CompuertaNOT) {
            CompuertaNOT compuertaNot = (CompuertaNOT) origen;
            if (compuertaNot.getEntradas().isEmpty() || compuertaNot.getEntradas().get(0).getConector() == null) expresionGenerada = "![NO_CONECTADO]";
            else {
                Pin pinEntradaNot = compuertaNot.getEntradas().get(0).getConector().obtenerPinSalida();
                String exprEntrada = construirExpresionParaPin(pinEntradaNot, expresionesCalculadas, nombresSwitches, visitadosEnRutaActual);
                expresionGenerada = "!(" + exprEntrada + ")";
            }
        } else if (origen instanceof CompuertaAND) {
            CompuertaAND compuertaAnd = (CompuertaAND) origen;
            if (compuertaAnd.getEntradas().size() < 2 || compuertaAnd.getEntradas().get(0).getConector() == null || compuertaAnd.getEntradas().get(1).getConector() == null) expresionGenerada = "[AND_INCOMPLETA]";
            else {
                Pin pinEntrada1 = compuertaAnd.getEntradas().get(0).getConector().obtenerPinSalida();
                Pin pinEntrada2 = compuertaAnd.getEntradas().get(1).getConector().obtenerPinSalida();
                String expr1 = construirExpresionParaPin(pinEntrada1, expresionesCalculadas, nombresSwitches, visitadosEnRutaActual);
                String expr2 = construirExpresionParaPin(pinEntrada2, expresionesCalculadas, nombresSwitches, visitadosEnRutaActual);
                expresionGenerada = "(" + expr1 + " & " + expr2 + ")";
            }
        } else if (origen instanceof CompuertaOR) {
            CompuertaOR compuertaOr = (CompuertaOR) origen;
             if (compuertaOr.getEntradas().size() < 2 || compuertaOr.getEntradas().get(0).getConector() == null || compuertaOr.getEntradas().get(1).getConector() == null) expresionGenerada = "[OR_INCOMPLETA]";
            else {
                Pin pinEntrada1 = compuertaOr.getEntradas().get(0).getConector().obtenerPinSalida();
                Pin pinEntrada2 = compuertaOr.getEntradas().get(1).getConector().obtenerPinSalida();
                String expr1 = construirExpresionParaPin(pinEntrada1, expresionesCalculadas, nombresSwitches, visitadosEnRutaActual);
                String expr2 = construirExpresionParaPin(pinEntrada2, expresionesCalculadas, nombresSwitches, visitadosEnRutaActual);
                expresionGenerada = "(" + expr1 + " | " + expr2 + ")";
            }
        } else {
            expresionGenerada = "[" + origen.getClass().getSimpleName() + "_ID:" + origen.getId() + "]";
        }
        visitadosEnRutaActual.remove(origen);
        expresionesCalculadas.put(origen, expresionGenerada);
        return expresionGenerada;
    }

    public void limpiarParaNuevoDiseno() {
        if (this.componentes != null) this.componentes.clear();
        if (this.conexiones != null) this.conexiones.clear();
        if (this.undoStack != null) this.undoStack.clear();
        if (this.redoStack != null) this.redoStack.clear();
        this.setModificado(false);
        if (this.panelReferencia != null) {
            this.panelReferencia.limpiarSeleccion();
            this.panelReferencia.repaint();
        }
    }

    private static class PinYPosicion {
        Pin pin;
        int yCentroPinSalida;
        int xMaxComponente;
        int columnaComponente;

        PinYPosicion(Pin pin, int yPin, int xMax, int col) {
            this.pin = pin;
            this.yCentroPinSalida = yPin;
            this.xMaxComponente = xMax;
            this.columnaComponente = col;
        }
    }

    public void generarCircuitoDesdeAST(ASTNode raizAST, List<String> nombresVariables, MiPanel panelDibujo) {
        limpiarParaNuevoDiseno();
        this.panelReferencia = panelDibujo;
        this.maxYPorColumna = new HashMap<>();
        Map<String, Componente> mapaVariablesASwitch = new HashMap<>();
        int currentSwitchY = Y_INICIAL_LAYOUT;
        for (String nombreVar : nombresVariables) {
            if (!mapaVariablesASwitch.containsKey(nombreVar)) {
                Componente nuevoSwitch = this.componenteFactory.crearComponente("switch", X_INICIAL_LAYOUT_SWITCHES, currentSwitchY);
                this.agregarComponenteSinUndo(nuevoSwitch);
                mapaVariablesASwitch.put(nombreVar, nuevoSwitch);
                currentSwitchY += ESPACIADO_Y_LAYOUT;
            }
        }
        if (!nombresVariables.isEmpty()){
             maxYPorColumna.put(0, currentSwitchY - ESPACIADO_Y_LAYOUT);
        } else {
             maxYPorColumna.put(0, Y_INICIAL_LAYOUT - ESPACIADO_Y_LAYOUT);
        }


        PinYPosicion pinSalidaFinalConPos = construirComponenteDesdeNodoRecursivo(raizAST, mapaVariablesASwitch, new HashMap<>(), maxYPorColumna);

        if (pinSalidaFinalConPos == null || pinSalidaFinalConPos.pin == null) {
            mostrarError("No se pudo generar la salida final de la expresión.");
            return;
        }

        int ledX = pinSalidaFinalConPos.xMaxComponente + ESPACIADO_X_COLUMNA_LAYOUT;
        int ledY = pinSalidaFinalConPos.yCentroPinSalida - (30 / 2);

        Componente ledSalida = this.componenteFactory.crearComponente("led", ledX, ledY);
        this.agregarComponenteSinUndo(ledSalida);

        if (ledSalida.getEntradas() != null && !ledSalida.getEntradas().isEmpty()) {
            Pin pinEntradaLed = ledSalida.getEntradas().get(0);
            conectarPinesDirecto(pinSalidaFinalConPos.pin, pinEntradaLed);
        } else {
            mostrarError("El LED de salida no tiene pines de entrada para conectar.");
        }
        this.evaluar();
        if (panelDibujo != null) {
            panelDibujo.repaint();
        }
        this.setModificado(true);
        guardarEstado();
    }

    private PinYPosicion construirComponenteDesdeNodoRecursivo(ASTNode nodo,
                                                               Map<String, Componente> mapaVariablesASwitch,
                                                               Map<ASTNode, PinYPosicion> nodosProcesados,
                                                               Map<Integer, Integer> yTrackerPorColumna) {
        if (nodo == null) return null;
        if (nodosProcesados.containsKey(nodo)) return nodosProcesados.get(nodo);

        PinYPosicion resultado;
        Componente componenteActual;
        int compAncho = 40;
        int compAlto = 40;

        if (nodo instanceof VariableNode) {
            VariableNode varNode = (VariableNode) nodo;
            componenteActual = mapaVariablesASwitch.get(varNode.getNombre());
            if (componenteActual == null || componenteActual.getSalidas().isEmpty()) {
                 mostrarError("Switch no encontrado o sin salida para variable: " + varNode.getNombre());
                 return new PinYPosicion(null, 0, 0, 0);
            }
            if(componenteActual instanceof Switch){
                compAncho = 30; compAlto = 30;
            }
            resultado = new PinYPosicion(componenteActual.getSalidas().get(0),
                                         componenteActual.getY() + compAlto / 2,
                                         componenteActual.getX() + compAncho,
                                         0);
        } else if (nodo instanceof OperacionUnariaNode) {
            OperacionUnariaNode unariaNode = (OperacionUnariaNode) nodo;
            PinYPosicion operandoInfo = construirComponenteDesdeNodoRecursivo(unariaNode.getOperando(), mapaVariablesASwitch, nodosProcesados, yTrackerPorColumna);
            if (operandoInfo == null || operandoInfo.pin == null) return new PinYPosicion(null,0,0,0);

            int miColumna = operandoInfo.columnaComponente + 1;
            int compuertaX = X_INICIAL_LAYOUT_SWITCHES + miColumna * ESPACIADO_X_COLUMNA_LAYOUT;
            int compuertaY = yTrackerPorColumna.getOrDefault(miColumna, Y_INICIAL_LAYOUT - ESPACIADO_Y_LAYOUT) + ESPACIADO_Y_LAYOUT;

            componenteActual = this.componenteFactory.crearComponente("not", compuertaX, compuertaY);
            this.agregarComponenteSinUndo(componenteActual);
            compAncho = ((Compuerta)componenteActual).ancho * 2;
            compAlto = ((Compuerta)componenteActual).alto;
            yTrackerPorColumna.put(miColumna, compuertaY );


            if (componenteActual.getEntradas() != null && !componenteActual.getEntradas().isEmpty()) {
                conectarPinesDirecto(operandoInfo.pin, componenteActual.getEntradas().get(0));
            }
            resultado = new PinYPosicion(componenteActual.getSalidas().get(0),
                                         compuertaY + compAlto / 2,
                                         compuertaX + compAncho,
                                         miColumna);
        } else if (nodo instanceof OperacionBinariaNode) {
            OperacionBinariaNode binariaNode = (OperacionBinariaNode) nodo;
            PinYPosicion izquierdoInfo = construirComponenteDesdeNodoRecursivo(binariaNode.getIzquierdo(), mapaVariablesASwitch, nodosProcesados, yTrackerPorColumna);
            PinYPosicion derechoInfo = construirComponenteDesdeNodoRecursivo(binariaNode.getDerecho(), mapaVariablesASwitch, nodosProcesados, yTrackerPorColumna);

            if (izquierdoInfo == null || izquierdoInfo.pin == null || derechoInfo == null || derechoInfo.pin == null) return new PinYPosicion(null,0,0,0);

            int miColumna = Math.max(izquierdoInfo.columnaComponente, derechoInfo.columnaComponente) + 1;
            int compuertaX = X_INICIAL_LAYOUT_SWITCHES + miColumna * ESPACIADO_X_COLUMNA_LAYOUT;

            int yTentativa = (izquierdoInfo.yCentroPinSalida + derechoInfo.yCentroPinSalida) / 2;
            int compuertaY = yTrackerPorColumna.getOrDefault(miColumna, Y_INICIAL_LAYOUT - ESPACIADO_Y_LAYOUT);

            if (compuertaY < yTentativa - ESPACIADO_Y_LAYOUT/2) { // Si hay espacio suficiente debajo de la media de los hijos
                compuertaY = yTentativa - 20; // -20 para centrar la compuerta de alto 40
            } else { // Sino, colocarlo después de lo último en esa columna
                compuertaY = compuertaY + ESPACIADO_Y_LAYOUT;
            }
            compuertaY = Math.max(compuertaY, Y_INICIAL_LAYOUT); // No subir por encima del Y inicial

            String tipoCompuerta = (binariaNode.getOperador() == TipoOperador.AND) ? "and" : "or";
            componenteActual = this.componenteFactory.crearComponente(tipoCompuerta, compuertaX, compuertaY);
            this.agregarComponenteSinUndo(componenteActual);
            compAncho = ((Compuerta)componenteActual).ancho * 2;
            compAlto = ((Compuerta)componenteActual).alto;
            yTrackerPorColumna.put(miColumna, compuertaY);

            if (componenteActual.getEntradas() != null && componenteActual.getEntradas().size() >= 2) {
                conectarPinesDirecto(izquierdoInfo.pin, componenteActual.getEntradas().get(0));
                conectarPinesDirecto(derechoInfo.pin, componenteActual.getEntradas().get(1));
            }
            resultado = new PinYPosicion(componenteActual.getSalidas().get(0),
                                         compuertaY + compAlto / 2,
                                         compuertaX + compAncho,
                                         miColumna);
        } else {
            throw new IllegalArgumentException("Tipo de nodo AST desconocido: " + nodo.getClass().getName());
        }

        if (!(nodo instanceof VariableNode)) {
             nodosProcesados.put(nodo, resultado);
        }
        return resultado;
    }

    private void agregarComponenteSinUndo(Componente componente) {
        if (componente == null) return;
        componentes.add(componente);
        componente.setCircuito(this);
    }

    private void conectarPinesDirecto(Pin pinOrigen, Pin pinDestino) {
        if (pinOrigen == null || pinDestino == null) {
            mostrarError("Intento de conectar un pin nulo.");
            return;
        }
        Conector nuevoConector = new Conector();
        if (nuevoConector.conectar(pinOrigen, pinDestino)) {
            conexiones.add(nuevoConector);
        } else {
            // No mostrar error aquí, ya que es una conexión interna del generador de circuito.
            // Si falla, es un problema de lógica del generador, no de interacción del usuario.
        }
    }

    public List<String> getNombresDeSwitchesOrdenados(List<Componente> componentesDelCircuitoAAnalizar) {
        List<String> nombres = new ArrayList<>();
        char currentName = 'A';
        List<Switch> switches = new ArrayList<>();
        for (Componente comp : componentesDelCircuitoAAnalizar) {
            if (comp instanceof Switch) {
                switches.add((Switch) comp);
            }
        }
        Collections.sort(switches, Comparator.comparingInt(Componente::getY).thenComparingInt(Componente::getX));
        for (Switch sw : switches) {
            nombres.add(String.valueOf(currentName++));
        }
        return nombres;
    }

    public List<String> getNombresDeLedsOrdenados(List<Componente> componentesDelCircuitoAAnalizar) {
        List<String> nombres = new ArrayList<>();
        int ledCount = 1;
        List<Led> leds = new ArrayList<>();
        for (Componente comp : componentesDelCircuitoAAnalizar) {
            if (comp instanceof Led) {
                leds.add((Led) comp);
            }
        }
        Collections.sort(leds, Comparator.comparingInt(Componente::getY).thenComparingInt(Componente::getX));
        for (Led led : leds) {
            nombres.add("Z" + ledCount++);
        }
        return nombres;
    }

    public List<Map<String, Boolean>> generarDatosTablaDeVerdad(List<Componente> componentesDelCircuitoAAnalizar) throws IllegalStateException {
        List<Componente> componentesAEvaluar = (componentesDelCircuitoAAnalizar == null || componentesDelCircuitoAAnalizar.isEmpty())
                                               ? this.componentes
                                               : componentesDelCircuitoAAnalizar;
        if (componentesAEvaluar.isEmpty() && this.componentes.isEmpty()){
             throw new IllegalStateException("No hay componentes en el circuito o en la selección para analizar.");
        }

        List<Switch> switches = new ArrayList<>();
        for (Componente comp : componentesAEvaluar) {
            if (comp instanceof Switch) {
                switches.add((Switch) comp);
            }
        }
        Collections.sort(switches, Comparator.comparingInt(Componente::getY).thenComparingInt(Componente::getX));

        List<Led> leds = new ArrayList<>();
        for (Componente comp : componentesAEvaluar) {
            if (comp instanceof Led) {
                leds.add((Led) comp);
            }
        }
        Collections.sort(leds, Comparator.comparingInt(Componente::getY).thenComparingInt(Componente::getX));

        if (switches.isEmpty()) {
             throw new IllegalStateException("No se encontraron Switches (entradas) en la selección para generar la tabla.");
        }
        if (leds.isEmpty()) {
            throw new IllegalStateException("No se encontraron LEDs (salidas) en la selección para generar la tabla.");
        }

        List<String> nombresSwitches = new ArrayList<>();
        char currentName = 'A';
        for(Switch sw : switches) nombresSwitches.add(String.valueOf(currentName++));

        List<String> nombresLeds = new ArrayList<>();
        int ledCount = 1;
        for(Led l : leds) nombresLeds.add("Z" + ledCount++);

        List<Map<String, Boolean>> tablaCompleta = new ArrayList<>();
        int numSwitches = switches.size();
        int numCombinaciones = 1 << numSwitches;

        boolean[] estadosOriginalesSwitches = new boolean[numSwitches];
        for (int i = 0; i < numSwitches; i++) {
            estadosOriginalesSwitches[i] = switches.get(i).getEstado();
        }

        for (int i = 0; i < numCombinaciones; i++) {
            Map<String, Boolean> fila = new LinkedHashMap<>();
            for (int j = 0; j < numSwitches; j++) {
                boolean estadoActualSwitch = ((i >> (numSwitches - 1 - j)) & 1) == 1;
                switches.get(j).cambiarEstadoManual(estadoActualSwitch);
                fila.put(nombresSwitches.get(j), estadoActualSwitch);
            }

            this.evaluar();

            for (int k = 0; k < leds.size(); k++) {
                Led led = leds.get(k);
                boolean estadoLed = false;
                if (led.getEntradas() != null && !led.getEntradas().isEmpty() && led.getEntradas().get(0) != null) {
                    estadoLed = led.getEntradas().get(0).obtenerEstado();
                }
                if (k < nombresLeds.size()) {
                    fila.put(nombresLeds.get(k), estadoLed);
                } else {
                    fila.put("LED_EXTRA_" + k, estadoLed);
                }
            }
            tablaCompleta.add(fila);
        }

        for (int i = 0; i < numSwitches; i++) {
            switches.get(i).cambiarEstadoManual(estadosOriginalesSwitches[i]);
        }
        this.evaluar();

        return tablaCompleta;
    }

    public void limpiarConexionesInvalidas() {
        conexiones.removeIf(conector ->
            conector == null ||
            conector.obtenerPinSalida() == null ||
            conector.obtenerPinEntrada() == null ||
            conector.obtenerPinSalida().getComponente() == null ||
            conector.obtenerPinEntrada().getComponente() == null ||
            !componentes.contains(conector.obtenerPinSalida().getComponente()) ||
            !componentes.contains(conector.obtenerPinEntrada().getComponente())
        );
    }
}