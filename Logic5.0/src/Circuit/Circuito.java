/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Circuit;

import Components.Componente;
import Components.Led;
import Components.Switch;
import Gates.Compuerta;
import Circuit.CirComponentFactory; // Asumo que esta es la importación correcta
import Gates.And;
import Gates.Not;
import Gates.Or;
import Main.MiPanel;
import logicaExpresion.Ast;
import logicaExpresion.Variable;
import logicaExpresion.Tipo; // Asumo que esta es la importación correcta
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
import logicaExpresion.OperacionB; // Asumo que esta es la importación correcta
import logicaExpresion.OperacionU; // Asumo que esta es la importación correcta

public class Circuito implements Serializable, Cloneable {
    private final ComponentFactory componenteFactory;
    private static final long serialVersionUID = 1L;
    private String nombre;
    private List<Componente> componentes = new ArrayList<>();
    private List<Conector> conexiones = new ArrayList<>();
    private boolean modificado = false;
    private transient String rutaArchivo;
    private transient MiPanel panelReferencia;
    // Se eliminaron: public List<Componente> portapapeles y private Point centroPortapapeles;

    public Stack<CircuitoState> undoStack = new Stack<>();
    public Stack<CircuitoState> redoStack = new Stack<>();

    private transient Map<Integer, Integer> maxYPorColumna;

    private static final int X_INICIAL_LAYOUT_SWITCHES = 50;
    private static final int Y_INICIAL_LAYOUT = 50;
    private static final int ESPACIADO_Y_LAYOUT = 70;
    private static final int ESPACIADO_X_COLUMNA_LAYOUT = 180;

    // Constructores
    public Circuito(String nombre) {
        this(nombre, new CirComponentFactory());
    }

    public Circuito(String nombre, ComponentFactory factory) {
        this.nombre = nombre;
        this.componenteFactory = factory != null ? factory : new CirComponentFactory();
        if (this.componentes == null) this.componentes = new ArrayList<>();
        if (this.conexiones == null) this.conexiones = new ArrayList<>();
        // Se eliminó la inicialización de this.portapapeles
        if (this.undoStack == null) this.undoStack = new Stack<>();
        if (this.redoStack == null) this.redoStack = new Stack<>();
    }

    // Getters y Setters / Métodos Públicos Principales
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; this.modificado = true; }
    public List<Componente> getComponents() { return new ArrayList<>(componentes); } // Devuelve copia
    public List<Conector> getConexiones() { return new ArrayList<>(conexiones); } // Devuelve copia
    public boolean isModificado() { return modificado; }
    public void setModificado(boolean modificado) { this.modificado = modificado; }
    public String getRutaArchivo() { return rutaArchivo; }
    public void setRutaArchivo(String ruta) { this.rutaArchivo = ruta; }
    public void setPanelReferencia(MiPanel panel) {
        this.panelReferencia = panel;
    }

    // Manejo de Componentes y Conexiones
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
            agregarComponente(componente); // Reutiliza el método anterior
        } catch (Exception e) {
            mostrarError(e.getMessage());
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
            eliminarConectorInterno(conector); // Usa el método privado
        }
        componentes.remove(componente);
        modificado = true;
        evaluar();
    }

    public void conectar(Componente origenComp, int pinOrigenIndexAbs, Componente destinoComp, int pinDestinoIndexAbs) {
        try {
            if (origenComp == null || destinoComp == null) throw new Exception("Los componentes para conectar no pueden ser nulos");
            guardarEstado(); // Guardar estado antes de la acción
            Pin pinOrigen = obtenerPin(origenComp, pinOrigenIndexAbs); // Usa el método privado
            Pin pinDestino = obtenerPin(destinoComp, pinDestinoIndexAbs); // Usa el método privado
            if (pinOrigen == null || pinDestino == null) throw new Exception("Uno o ambos pines especificados no existen o son inválidos para la conexión.");
            Conector nuevoConector = new Conector();
            if (!nuevoConector.conectar(pinOrigen, pinDestino)) throw new Exception("No se pudo crear la conexión (verifique tipos de pin y si ya están conectados).");
            conexiones.add(nuevoConector);
            modificado = true;
            if (pinDestino.getComponente() != null) pinDestino.getComponente().evaluar(); // Evaluar componente afectado
        } catch (Exception e) {
            mostrarError("Error de conexión: " + e.getMessage());
        }
    }

    public void eliminarConector(Conector conector) {
        if (conector == null) return;
        guardarEstado(); // Guardar estado antes de la acción
        eliminarConectorInterno(conector); // Usa el método privado
        modificado = true;
        evaluar();
    }

    public void toggleSwitch(Switch sw) {
        try {
            if (sw == null) throw new Exception("El switch no puede ser nulo."); // Mensaje mejorado
            if (!componentes.contains(sw)) throw new Exception("El switch especificado no pertenece a este circuito."); // Mensaje mejorado
            guardarEstado(); // Guardar estado antes de la acción
            sw.toggle();
            this.modificado = true;
            this.evaluar();
        } catch (Exception e) {
            mostrarError(e.getMessage());
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

    public List<Componente> seleccionarComponentsEnArea(Rectangle area) {
        List<Componente> seleccionados = new ArrayList<>();
        for (Componente c : componentes) {
            if (c != null) {
                Rectangle bounds;
                // Esta lógica de tamaño podría estar en Componente.getBounds()
                if (c instanceof Compuerta) {
                    bounds = new Rectangle(c.getX(), c.getY(), ((Compuerta)c).ancho * 2, ((Compuerta)c).alto);
                } else if (c instanceof Switch || c instanceof Led) {
                    bounds = new Rectangle(c.getX(), c.getY(), 30, 30); 
                } else {
                    bounds = new Rectangle(c.getX(), c.getY(), 40, 40); // Default
                }
                if (area.intersects(bounds) || area.contains(bounds)) {
                    seleccionados.add(c);
                }
            }
        }
        return seleccionados;
    }

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

    // Funcionalidad Deshacer/Rehacer
    public void guardarEstado() {
        if (!undoStack.isEmpty()) {
            CircuitoState ultimoEstado = undoStack.peek();
            if (estadoIgualAlUltimo(ultimoEstado)) { // Usa el método privado
                return;
            }
        }
        undoStack.push(new CircuitoState(this.componentes, this.conexiones));
        redoStack.clear();
        if (undoStack.size() > 100) { // Limitar tamaño del stack
            undoStack.remove(0);
        }
    }

    public boolean deshacer() {
        if (undoStack.isEmpty()) return false;
        redoStack.push(new CircuitoState(this.componentes, this.conexiones)); // Guardar estado actual para rehacer
        CircuitoState estadoAnterior = undoStack.pop();
        restaurarEstado(estadoAnterior); // Usa el método privado
        return true;
    }

    public boolean rehacer() {
        if (redoStack.isEmpty()) return false;
        undoStack.push(new CircuitoState(this.componentes, this.conexiones)); // Guardar estado actual para deshacer
        CircuitoState estadoRedo = redoStack.pop();
        restaurarEstado(estadoRedo); // Usa el método privado
        return true;
    }

    // SE ELIMINARON: copiarAlPortapeles, pegarDesdePortapapeles, calcularAreaPortapapeles, encontrarPosicionLibre, esAreaLibre

    public void eliminarSeleccion(List<Componente> componentesAEliminar) {
        if (componentesAEliminar == null || componentesAEliminar.isEmpty()) {
            mostrarError("No hay componentes seleccionados para eliminar."); // Mensaje mejorado
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
        componentes.removeAll(componentesAEliminar); // Eliminar de la lista principal
        modificado = true;
        evaluar();
    }

    // Generación de Expresión Booleana
    public String generarExpresionBooleana() throws IllegalStateException {
        List<Led> leds = new ArrayList<>();
        Map<Componente, String> nombresSwitches = new HashMap<>();
        char proximoNombreVariable = 'A';
        List<Switch> switchesOrdenados = new ArrayList<>();

        for (Componente comp : this.componentes) {
            if (comp instanceof Switch) switchesOrdenados.add((Switch)comp);
            else if (comp instanceof Led) leds.add((Led)comp);
        }
        // Ordenar switches por posición para consistencia en nombres de variables
        Collections.sort(switchesOrdenados, Comparator.comparingInt(Componente::getY).thenComparingInt(Componente::getX));
        for(Switch sw : switchesOrdenados) nombresSwitches.put(sw, String.valueOf(proximoNombreVariable++));

        if (leds.isEmpty()) throw new IllegalStateException("No se encontraron LEDs (salidas) en el circuito para generar la expresión."); // Mensaje mejorado
        // Asumir un solo LED de salida principal o el primero encontrado
        Led ledSalida = leds.get(0); // Podría mejorarse para manejar múltiples salidas
        if (ledSalida.getEntradas() == null || ledSalida.getEntradas().isEmpty()) throw new IllegalStateException("El LED de salida principal no tiene pines de entrada definidos."); // Mensaje mejorado

        Pin pinEntradaLed = ledSalida.getEntradas().get(0);
        if (pinEntradaLed == null || pinEntradaLed.getConector() == null) {
            // Si el LED no está conectado, su expresión es su estado (si tuviera uno) o un literal.
            return pinEntradaLed != null ? (pinEntradaLed.obtenerEstado() ? "1" : "0") : "[LED DE SALIDA NO CONECTADO]"; // Mensaje mejorado
        }
        Pin pinFuenteAlLed = pinEntradaLed.getConector().obtenerPinSalida();
        if (pinFuenteAlLed == null) throw new IllegalStateException("La entrada del LED de salida está conectada a un conector inválido."); // Mensaje mejorado

        return construirExpresionParaPin(pinFuenteAlLed, new HashMap<>(), nombresSwitches, new HashSet<>()); // Usa el método privado
    }

    // Generación de Circuito desde AST
    public void generarCircuitoDesdeAST(Ast raizAST, List<String> nombresVariables, MiPanel panelDibujo) {
        limpiarParaNuevoDiseno();
        this.panelReferencia = panelDibujo;
        this.maxYPorColumna = new HashMap<>(); // Inicializar tracker de Y para layout
        Map<String, Componente> mapaVariablesASwitch = new HashMap<>();
        int currentSwitchY = Y_INICIAL_LAYOUT;

        for (String nombreVar : nombresVariables) {
            if (!mapaVariablesASwitch.containsKey(nombreVar)) { // Evitar duplicados si la lista de nombres tiene repetidos
                Componente nuevoSwitch = this.componenteFactory.crearComponente("switch", X_INICIAL_LAYOUT_SWITCHES, currentSwitchY);
                this.agregarComponenteSinUndo(nuevoSwitch); // Usa el método privado
                mapaVariablesASwitch.put(nombreVar, nuevoSwitch);
                currentSwitchY += ESPACIADO_Y_LAYOUT;
            }
        }
        if (!nombresVariables.isEmpty()){
             maxYPorColumna.put(0, currentSwitchY - ESPACIADO_Y_LAYOUT); // Registrar Y máximo de la columna de switches
        } else {
             maxYPorColumna.put(0, Y_INICIAL_LAYOUT - ESPACIADO_Y_LAYOUT); // Si no hay switches
        }


        PinYPosicion pinSalidaFinalConPos = construirComponenteDesdeNodoRecursivo(raizAST, mapaVariablesASwitch, new HashMap<>(), maxYPorColumna); // Usa el método privado

        if (pinSalidaFinalConPos == null || pinSalidaFinalConPos.pin == null) {
            mostrarError("No se pudo generar la salida final a partir de la expresión proporcionada."); // Mensaje mejorado
            return;
        }

        // Colocar el LED de salida
        int ledX = pinSalidaFinalConPos.xMaxComponente + ESPACIADO_X_COLUMNA_LAYOUT;
        int ledY = pinSalidaFinalConPos.yCentroPinSalida - (30 / 2); // Centrar el LED (asumiendo alto 30)

        Componente ledSalida = this.componenteFactory.crearComponente("led", ledX, ledY);
        this.agregarComponenteSinUndo(ledSalida); // Usa el método privado

        if (ledSalida.getEntradas() != null && !ledSalida.getEntradas().isEmpty()) {
            Pin pinEntradaLed = ledSalida.getEntradas().get(0);
            conectarPinesDirecto(pinSalidaFinalConPos.pin, pinEntradaLed); // Usa el método privado
        } else {
            mostrarError("El LED de salida generado no tiene pines de entrada disponibles para la conexión final."); // Mensaje mejorado
        }
        this.evaluar();
        if (panelDibujo != null) {
            panelDibujo.repaint();
        }
        this.setModificado(true);
        guardarEstado(); // Guardar estado después de la generación completa
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
        if (componentesAEvaluar.isEmpty() && this.componentes.isEmpty()){ // Si ambas listas están vacías
             throw new IllegalStateException("No hay componentes en el circuito o en la selección para analizar y generar la tabla de verdad."); // Mensaje mejorado
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
             throw new IllegalStateException("No se encontraron Switches (entradas) en la selección para generar la tabla de verdad."); // Mensaje mejorado
        }
        if (leds.isEmpty()) {
            throw new IllegalStateException("No se encontraron LEDs (salidas) en la selección para generar la tabla de verdad."); // Mensaje mejorado
        }

        List<String> nombresSwitches = new ArrayList<>();
        char currentName = 'A';
        for(Switch sw : switches) nombresSwitches.add(String.valueOf(currentName++));

        List<String> nombresLeds = new ArrayList<>();
        int ledCount = 1;
        for(Led l : leds) nombresLeds.add("Z" + ledCount++);

        List<Map<String, Boolean>> tablaCompleta = new ArrayList<>();
        int numSwitches = switches.size();
        int numCombinaciones = 1 << numSwitches; // 2^numSwitches

        // Guardar estados originales para restaurarlos después
        boolean[] estadosOriginalesSwitches = new boolean[numSwitches];
        for (int i = 0; i < numSwitches; i++) {
            estadosOriginalesSwitches[i] = switches.get(i).getEstado();
        }

        for (int i = 0; i < numCombinaciones; i++) {
            Map<String, Boolean> fila = new LinkedHashMap<>(); // Para mantener el orden
            // Configurar estados de los switches para esta combinación
            for (int j = 0; j < numSwitches; j++) {
                boolean estadoActualSwitch = ((i >> (numSwitches - 1 - j)) & 1) == 1;
                switches.get(j).cambiarEstadoManual(estadoActualSwitch); // Método para forzar estado sin trigger de evento
                fila.put(nombresSwitches.get(j), estadoActualSwitch);
            }

            this.evaluar(); // Evaluar el circuito con los nuevos estados de los switches

            // Leer estados de los LEDs
            for (int k = 0; k < leds.size(); k++) {
                Led led = leds.get(k);
                boolean estadoLed = false; // Por defecto si no está conectado o no tiene entrada
                if (led.getEntradas() != null && !led.getEntradas().isEmpty() && led.getEntradas().get(0) != null) {
                    estadoLed = led.getEntradas().get(0).obtenerEstado();
                }
                if (k < nombresLeds.size()) { // Asegurar que no se exceda el tamaño de nombresLeds
                    fila.put(nombresLeds.get(k), estadoLed);
                } else {
                    fila.put("LED_EXTRA_" + k, estadoLed); // Nombre genérico si hay más LEDs que nombres generados
                }
            }
            tablaCompleta.add(fila);
        }

        // Restaurar estados originales de los switches
        for (int i = 0; i < numSwitches; i++) {
            switches.get(i).cambiarEstadoManual(estadosOriginalesSwitches[i]);
        }
        this.evaluar(); // Evaluar una última vez para dejar el circuito en su estado original

        return tablaCompleta;
    }

    // Métodos de Serialización (Personalizados)
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject(); // Serializa campos no transient y no static
        // Serialización personalizada de conexiones
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
        in.defaultReadObject(); // Deserializa campos no transient y no static
        // Inicializar listas si son null después de la deserialización (importante si se añadieron después)
        if (componentes == null) componentes = new ArrayList<>();
        conexiones = new ArrayList<>(); // Siempre reinicializar para la deserialización personalizada

        // Re-inicializar pines y asignar circuito a componentes
        for (Componente comp : componentes) {
            if (comp != null) {
                // Determinar número de pines basado en el tipo (esto es un hack, debería estar en Componente.clone o similar)
                int numEntradas = 0; int numSalidas = 0;
                if (comp instanceof Gates.And) { numEntradas = 2; numSalidas = 1; }
                else if (comp instanceof Gates.Or) { numEntradas = 2; numSalidas = 1; }
                else if (comp instanceof Gates.Not) { numEntradas = 1; numSalidas = 1; }
                else if (comp instanceof Components.Switch) { numEntradas = 0; numSalidas = 1; }
                else if (comp instanceof Components.Led) { numEntradas = 1; numSalidas = 0; }
                // Añadir más tipos si es necesario

                comp.inicializarPines(numEntradas, numSalidas);
                comp.setCircuito(this);
                if (comp instanceof Compuerta) { // Re-inicializar renderer si es transient
                    ((Compuerta) comp).reinitializeRenderer();
                }
            }
        }

        // Crear mapa para búsqueda rápida de componentes por ID
        Map<String, Componente> mapaComponents = new HashMap<>();
        for (Componente c : componentes) {
            if (c != null) mapaComponents.put(c.getId(), c);
        }

        // Deserialización personalizada de conexiones
        int numConectores = in.readInt();
        for (int i = 0; i < numConectores; i++) {
            try {
                String idCompSalida = (String) in.readObject();
                int indexPinSalida = in.readInt();
                String idCompEntrada = (String) in.readObject();
                int indexPinEntrada = in.readInt();

                Componente compSalida = mapaComponents.get(idCompSalida);
                Componente compEntrada = mapaComponents.get(idCompEntrada);

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
                                if (panelReferencia != null) System.err.println("Fallo al reconectar pines durante la carga del circuito: " + // Mensaje mejorado
                                    pinSalida.getComponente().getId() + "["+indexPinSalida+"] -> " +
                                    pinEntrada.getComponente().getId() + "["+indexPinEntrada+"]");
                            }
                        } else {
                             if (panelReferencia != null) System.err.println("Error: Pin nulo encontrado al reconstruir la conexión #" + i); // Mensaje mejorado
                        }
                    } else {
                         if (panelReferencia != null) System.err.println("Error: Índice de pin fuera de rango para la conexión #" + i); // Mensaje mejorado
                    }
                } else {
                     if (panelReferencia != null) System.err.println("Error: Componente nulo encontrado al reconstruir la conexión #" + i); // Mensaje mejorado
                }
            } catch (Exception e) {
                if (panelReferencia != null) System.err.println("Excepción durante la reconstrucción de la conexión #" + i + ": " + e.getMessage()); // Mensaje mejorado
                // e.printStackTrace(); // Podría ser útil durante la depuración
            }
        }
        evaluar(); // Reevaluar el circuito después de cargar
    }

    // Métodos Privados Ayudantes
    private Pin obtenerPin(Componente componente, int pinIndex) {
        if (componente == null || componente.getSalidas() == null || componente.getEntradas() == null) {
            return null; // Evitar NullPointerException
        }
        // Asumiendo que pinIndex es 0 para la primera salida, 1 para la segunda, etc.,
        // y luego continúa con las entradas.
        if (pinIndex < componente.getSalidas().size()) {
            return componente.getSalidas().get(pinIndex);
        }
        int entradaIndex = pinIndex - componente.getSalidas().size();
        if (entradaIndex >= 0 && entradaIndex < componente.getEntradas().size()) {
            return componente.getEntradas().get(entradaIndex);
        }
        return null; // Índice fuera de rango
    }

    private boolean estadoIgualAlUltimo(CircuitoState ultimoEstado) {
        // Comprobación básica. Una comparación profunda sería más costosa.
        if (ultimoEstado.componentes.size() != componentes.size() ||
            ultimoEstado.conexiones.size() != conexiones.size()) {
            return false;
        }
        // Podría añadir más lógica aquí si se quiere una detección más fina sin ser demasiado lento
        // Por ejemplo, comparar hashCodes de los componentes si están bien implementados.
        return true; // Asume igual si los tamaños son iguales (simplificación)
    }

    private void restaurarEstado(CircuitoState estado) {
        this.componentes.clear();
        this.conexiones.clear();

        Map<String, Componente> idToRestoredComponentMap = new HashMap<>();

        for (Componente clonComp : estado.componentes) {
            // El clon ya tiene sus pines. Solo necesita la referencia al circuito actual.
            clonComp.setCircuito(this);
            this.componentes.add(clonComp);
            idToRestoredComponentMap.put(clonComp.getId(), clonComp);
        }

        for (Conector clonConector : estado.conexiones) {
            // Los pines del conector clonado ya deberían referenciar a los componentes clonados
            // correctos debido a la lógica de clonación en CircuitoState.
            // Solo necesitamos añadir el conector a la lista del circuito.
            this.conexiones.add(clonConector);
            // La asignación del conector a los pines ya se hizo en CircuitoState.
            // No es necesario llamar a pin.conectarA(conector) aquí.
        }
        this.modificado = true; // El circuito ha sido modificado al restaurar un estado.
        this.evaluar(); // Reevaluar el circuito.
        // Considerar actualizar la UI si hay un panelReferencia.
        if (panelReferencia != null) {
            panelReferencia.limpiarSeleccion();
            panelReferencia.repaint();
        }
    }

    public void mostrarError(String mensaje) {
        if (panelReferencia != null) {
            panelReferencia.mostrarMensajeError(mensaje);
        } else {
            // Fallback si no hay panel de referencia (ej. en pruebas unitarias)
            JOptionPane.showMessageDialog(null, mensaje, "Error en el Circuito", JOptionPane.ERROR_MESSAGE); // Título mejorado
        }
    }

    private void eliminarConectorInterno(Conector conector) {
        if (conector == null) return;
        conector.desconectar(); // Desvincular el conector de sus pines
        conexiones.remove(conector);
    }

    // SE ELIMINARON MÉTODOS PRIVADOS RELACIONADOS CON PORTAPAPELES: calcularAreaPortapapeles, encontrarPosicionLibre, esAreaLibre

    private String construirExpresionParaPin(Pin pinSalidaActual, Map<Componente, String> expresionesCalculadas, Map<Componente, String> nombresSwitches, Set<Componente> visitadosEnRutaActual) throws IllegalStateException {
        if (pinSalidaActual == null) return "?"; // Pin nulo
        Componente origen = pinSalidaActual.getComponente();
        if (origen == null) return "[PIN_SIN_COMPONENTE]"; // Mensaje mejorado

        if (expresionesCalculadas.containsKey(origen)) return expresionesCalculadas.get(origen); // Ya calculado
        if (visitadosEnRutaActual.contains(origen)) return "[CICLO_DETECTADO]"; // Mensaje mejorado
        visitadosEnRutaActual.add(origen);

        String expresionGenerada;
        if (origen instanceof Switch) {
            expresionGenerada = nombresSwitches.getOrDefault(origen, origen.getId()); // Usar nombre de variable A, B, C...
        } else if (origen instanceof Not) {
            Not compuertaNot = (Not) origen;
            if (compuertaNot.getEntradas().isEmpty() || compuertaNot.getEntradas().get(0).getConector() == null) expresionGenerada = "!(ENTRADA_NOT_DESCONECTADA)"; // Mensaje mejorado
            else {
                Pin pinEntradaNot = compuertaNot.getEntradas().get(0).getConector().obtenerPinSalida();
                String exprEntrada = construirExpresionParaPin(pinEntradaNot, expresionesCalculadas, nombresSwitches, visitadosEnRutaActual);
                expresionGenerada = "!(" + exprEntrada + ")";
            }
        } else if (origen instanceof And) {
            And compuertaAnd = (And) origen;
            if (compuertaAnd.getEntradas().size() < 2 || compuertaAnd.getEntradas().get(0).getConector() == null || compuertaAnd.getEntradas().get(1).getConector() == null) expresionGenerada = "[AND_CON_ENTRADAS_DESCONECTADAS]"; // Mensaje mejorado
            else {
                Pin pinEntrada1 = compuertaAnd.getEntradas().get(0).getConector().obtenerPinSalida();
                Pin pinEntrada2 = compuertaAnd.getEntradas().get(1).getConector().obtenerPinSalida();
                String expr1 = construirExpresionParaPin(pinEntrada1, expresionesCalculadas, nombresSwitches, visitadosEnRutaActual);
                String expr2 = construirExpresionParaPin(pinEntrada2, expresionesCalculadas, nombresSwitches, visitadosEnRutaActual);
                expresionGenerada = "(" + expr1 + " & " + expr2 + ")";
            }
        } else if (origen instanceof Or) {
            Or compuertaOr = (Or) origen;
             if (compuertaOr.getEntradas().size() < 2 || compuertaOr.getEntradas().get(0).getConector() == null || compuertaOr.getEntradas().get(1).getConector() == null) expresionGenerada = "[OR_CON_ENTRADAS_DESCONECTADAS]"; // Mensaje mejorado
            else {
                Pin pinEntrada1 = compuertaOr.getEntradas().get(0).getConector().obtenerPinSalida();
                Pin pinEntrada2 = compuertaOr.getEntradas().get(1).getConector().obtenerPinSalida();
                String expr1 = construirExpresionParaPin(pinEntrada1, expresionesCalculadas, nombresSwitches, visitadosEnRutaActual);
                String expr2 = construirExpresionParaPin(pinEntrada2, expresionesCalculadas, nombresSwitches, visitadosEnRutaActual);
                expresionGenerada = "(" + expr1 + " | " + expr2 + ")";
            }
        } else {
            // Caso para otros componentes no lógicos (o no manejados explícitamente)
            expresionGenerada = "[" + origen.getClass().getSimpleName() + "_ID:" + origen.getId() + "]";
        }
        visitadosEnRutaActual.remove(origen); // Liberar para otras rutas
        expresionesCalculadas.put(origen, expresionGenerada);
        return expresionGenerada;
    }

    private PinYPosicion construirComponenteDesdeNodoRecursivo(Ast nodo,
                                                               Map<String, Componente> mapaVariablesASwitch,
                                                               Map<Ast, PinYPosicion> nodosProcesados,
                                                               Map<Integer, Integer> yTrackerPorColumna) {
        if (nodo == null) return null;
        if (nodosProcesados.containsKey(nodo)) return nodosProcesados.get(nodo); // Ya procesado

        PinYPosicion resultado;
        Componente componenteActual;
        int compAncho = 40; // Default
        int compAlto = 40;  // Default

        if (nodo instanceof Variable) {
            Variable varNode = (Variable) nodo;
            componenteActual = mapaVariablesASwitch.get(varNode.getNombre());
            if (componenteActual == null || componenteActual.getSalidas().isEmpty()) {
                 mostrarError("Switch no encontrado o sin pines de salida para la variable: " + varNode.getNombre()); // Mensaje mejorado
                 return new PinYPosicion(null, 0, 0, 0); // Posición y columna por defecto
            }
            if(componenteActual instanceof Switch){ // Tamaño específico para Switch
                compAncho = 30; compAlto = 30;
            }
            resultado = new PinYPosicion(componenteActual.getSalidas().get(0), // Pin de salida del Switch
                                         componenteActual.getY() + compAlto / 2, // Centro Y del pin
                                         componenteActual.getX() + compAncho,    // Borde derecho del Switch
                                         0); // Columna 0 para Switches
        } else if (nodo instanceof OperacionU) { // NOT
            OperacionU unariaNode = (OperacionU) nodo;
            PinYPosicion operandoInfo = construirComponenteDesdeNodoRecursivo(unariaNode.getOperando(), mapaVariablesASwitch, nodosProcesados, yTrackerPorColumna);
            if (operandoInfo == null || operandoInfo.pin == null) {
                mostrarError("Error al procesar el operando para una operación NOT."); // Mensaje mejorado
                return new PinYPosicion(null,0,0,0);
            }

            int miColumna = operandoInfo.columnaComponente + 1;
            int compuertaX = X_INICIAL_LAYOUT_SWITCHES + miColumna * ESPACIADO_X_COLUMNA_LAYOUT;
            // Usar yTrackerPorColumna para colocar debajo del último componente en esa columna
            int compuertaY = yTrackerPorColumna.getOrDefault(miColumna, Y_INICIAL_LAYOUT - ESPACIADO_Y_LAYOUT) + ESPACIADO_Y_LAYOUT;

            componenteActual = this.componenteFactory.crearComponente("not", compuertaX, compuertaY);
            this.agregarComponenteSinUndo(componenteActual); // Usa el método privado
            compAncho = ((Compuerta)componenteActual).ancho * 2; // Ancho visual de la compuerta
            compAlto = ((Compuerta)componenteActual).alto;
            yTrackerPorColumna.put(miColumna, compuertaY ); // Actualizar Y máximo para esta columna


            if (componenteActual.getEntradas() != null && !componenteActual.getEntradas().isEmpty()) {
                conectarPinesDirecto(operandoInfo.pin, componenteActual.getEntradas().get(0)); // Usa el método privado
            } else {
                 mostrarError("Compuerta NOT generada no tiene pines de entrada."); // Mensaje mejorado
            }
            resultado = new PinYPosicion(componenteActual.getSalidas().get(0),
                                         compuertaY + compAlto / 2,
                                         compuertaX + compAncho,
                                         miColumna);
        } else if (nodo instanceof OperacionB) { // AND, OR
            OperacionB binariaNode = (OperacionB) nodo;
            PinYPosicion izquierdoInfo = construirComponenteDesdeNodoRecursivo(binariaNode.getIzquierdo(), mapaVariablesASwitch, nodosProcesados, yTrackerPorColumna);
            PinYPosicion derechoInfo = construirComponenteDesdeNodoRecursivo(binariaNode.getDerecho(), mapaVariablesASwitch, nodosProcesados, yTrackerPorColumna);

            if (izquierdoInfo == null || izquierdoInfo.pin == null || derechoInfo == null || derechoInfo.pin == null) {
                 mostrarError("Error al procesar operandos para una operación binaria."); // Mensaje mejorado
                return new PinYPosicion(null,0,0,0);
            }

            int miColumna = Math.max(izquierdoInfo.columnaComponente, derechoInfo.columnaComponente) + 1;
            int compuertaX = X_INICIAL_LAYOUT_SWITCHES + miColumna * ESPACIADO_X_COLUMNA_LAYOUT;

            // Intentar centrar la compuerta verticalmente entre sus entradas, o colocarla debajo
            int yTentativa = (izquierdoInfo.yCentroPinSalida + derechoInfo.yCentroPinSalida) / 2;
            int compuertaY = yTrackerPorColumna.getOrDefault(miColumna, Y_INICIAL_LAYOUT - ESPACIADO_Y_LAYOUT); // Última Y en esta columna

            if (compuertaY < yTentativa - ESPACIADO_Y_LAYOUT/2) { // Si hay espacio suficiente debajo de la media de los hijos
                compuertaY = yTentativa - 20; // -20 para centrar la compuerta de alto 40
            } else { // Sino, colocarlo después de lo último en esa columna
                compuertaY = compuertaY + ESPACIADO_Y_LAYOUT;
            }
            compuertaY = Math.max(compuertaY, Y_INICIAL_LAYOUT); // No subir por encima del Y inicial

            String tipoCompuerta = (binariaNode.getOperador() == Tipo.AND) ? "and" : "or"; // Asumiendo Tipo.AND y Tipo.OR
            componenteActual = this.componenteFactory.crearComponente(tipoCompuerta, compuertaX, compuertaY);
            this.agregarComponenteSinUndo(componenteActual); // Usa el método privado
            compAncho = ((Compuerta)componenteActual).ancho * 2;
            compAlto = ((Compuerta)componenteActual).alto;
            yTrackerPorColumna.put(miColumna, compuertaY); // Actualizar Y máximo

            if (componenteActual.getEntradas() != null && componenteActual.getEntradas().size() >= 2) {
                conectarPinesDirecto(izquierdoInfo.pin, componenteActual.getEntradas().get(0)); // Usa el método privado
                conectarPinesDirecto(derechoInfo.pin, componenteActual.getEntradas().get(1)); // Usa el método privado
            } else {
                 mostrarError("Compuerta binaria generada no tiene suficientes pines de entrada."); // Mensaje mejorado
            }
            resultado = new PinYPosicion(componenteActual.getSalidas().get(0),
                                         compuertaY + compAlto / 2,
                                         compuertaX + compAncho,
                                         miColumna);
        } else {
            throw new IllegalArgumentException("Tipo de nodo AST desconocido durante la generación del circuito: " + nodo.getClass().getName()); // Mensaje mejorado
        }

        if (!(nodo instanceof Variable)) { // No cachear variables, ya que pueden ser usadas por múltiples nodos
             nodosProcesados.put(nodo, resultado);
        }
        return resultado;
    }

    private void agregarComponenteSinUndo(Componente componente) {
        // Método para añadir componentes durante la generación desde AST sin afectar el historial de deshacer.
        if (componente == null) return;
        componentes.add(componente);
        componente.setCircuito(this);
    }

    private void conectarPinesDirecto(Pin pinOrigen, Pin pinDestino) {
        // Método para conectar pines durante la generación desde AST sin afectar el historial y sin errores al usuario.
        if (pinOrigen == null || pinDestino == null) {
            // Loggear error internamente si es necesario, pero no mostrar popup al usuario.
            System.err.println("Error Interno: Intento de conectar un pin nulo durante la generación del circuito desde AST."); // Mensaje mejorado
            return;
        }
        Conector nuevoConector = new Conector();
        if (nuevoConector.conectar(pinOrigen, pinDestino)) {
            conexiones.add(nuevoConector);
        } else {
            // Falla de conexión interna, también loggear.
            System.err.println("Error Interno: Fallo al conectar pines durante la generación del circuito desde AST: " + // Mensaje mejorado
                               (pinOrigen.getComponente()!=null ? pinOrigen.getComponente().getId() : "N/A") + " -> " +
                               (pinDestino.getComponente()!=null ? pinDestino.getComponente().getId() : "N/A"));
        }
    }

    // Clase Interna para Estado (Deshacer/Rehacer)
    private static class CircuitoState implements Serializable {
        private static final long serialVersionUID = 2L; // Mantener si la estructura cambia
        List<Componente> componentes;
        List<Conector> conexiones;

        public CircuitoState(List<Componente> componentesOriginales, List<Conector> conexionesOriginales) {
            this.componentes = new ArrayList<>();
            Map<String, Componente> originalToCloneMap = new HashMap<>(); // Usar ID del componente original

            // Clonar componentes
            for (Componente original : componentesOriginales) {
                try {
                    Componente clon = original.clone();
                    clon.setCircuito(null); // El circuito se reasignará al restaurar
                    this.componentes.add(clon);
                    originalToCloneMap.put(original.getId(), clon); // Mapear por ID
                } catch (Exception e) {
                    // Manejar o loggear error de clonación de componente
                    System.err.println("Error al clonar componente para el estado: " + (original != null ? original.getId() : "null") + " - " + e.getMessage());
                    // e.printStackTrace();
                }
            }

            // Clonar conexiones y reasignar pines a los componentes clonados
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
                                Pin pinSalidaClon = encontrarPinCorrespondiente( // Usa el método privado de la clase externa
                                    originalConector.obtenerPinSalida(), origenClon);
                                Pin pinEntradaClon = encontrarPinCorrespondiente( // Usa el método privado de la clase externa
                                    originalConector.obtenerPinEntrada(), destinoClon);

                                if (pinSalidaClon != null && pinEntradaClon != null) {
                                    Conector conectorClon = new Conector();
                                    // Usar conectarForzado para establecer la conexión en los clones
                                    // sin las validaciones de si ya pertenecen a un circuito.
                                    if (conectorClon.conectarForzado(pinSalidaClon, pinEntradaClon)) {
                                        this.conexiones.add(conectorClon);
                                    } else {
                                         System.err.println("Error al forzar conexión para el estado entre " + origenClon.getId() + " y " + destinoClon.getId());
                                    }
                                } else {
                                     System.err.println("No se encontraron pines correspondientes para clonar conexión en estado: " + origenClon.getId() + " -> " + destinoClon.getId());
                                }
                            } else {
                                 System.err.println("No se encontraron componentes clonados para rehacer conexión en estado.");
                            }
                        }
                    } catch (Exception e) {
                        // Manejar o loggear error de clonación de conector
                         System.err.println("Excepción al clonar conector para el estado: " + e.getMessage());
                        // e.printStackTrace();
                    }
                }
            }
             // Asegurarse de que todos los componentes clonados en el estado no tengan referencia al circuito principal.
            for (Componente clon : this.componentes) {
                 clon.setCircuito(null);
            }
        }

        // Método estático para encontrar el pin correspondiente en un componente clonado
        private static Pin encontrarPinCorrespondiente(Pin pinOriginal, Componente componenteClon) {
            if (pinOriginal == null || componenteClon == null) return null;

            Componente componenteOriginal = pinOriginal.getComponente();
            if (componenteOriginal == null) return null; // El pin original debe tener un componente

            if ("entrada".equals(pinOriginal.getTipo())) {
                int index = componenteOriginal.getEntradas().indexOf(pinOriginal);
                if (index >= 0 && index < componenteClon.getEntradas().size()) {
                    return componenteClon.getEntradas().get(index);
                }
            } else { // "salida"
                int index = componenteOriginal.getSalidas().indexOf(pinOriginal);
                if (index >= 0 && index < componenteClon.getSalidas().size()) {
                    return componenteClon.getSalidas().get(index);
                }
            }
            System.err.println("No se encontró pin correspondiente para " + pinOriginal + " en " + componenteClon.getId());
            return null; // No se encontró el pin correspondiente
        }
    }

    // Clase Interna para Layout (Generación desde AST)
    private static class PinYPosicion { // Helper class para el layout desde AST
        Pin pin; // El pin de salida del componente generado
        int yCentroPinSalida; // Posición Y del centro del pin de salida (para alinear el siguiente componente)
        int xMaxComponente;   // Coordenada X más a la derecha del componente (para espaciado)
        int columnaComponente;// Columna lógica en el layout

        PinYPosicion(Pin pin, int yPin, int xMax, int col) {
            this.pin = pin;
            this.yCentroPinSalida = yPin;
            this.xMaxComponente = xMax;
            this.columnaComponente = col;
        }
    }
}