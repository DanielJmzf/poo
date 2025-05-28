/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Main;

import Circuit.Circuito;
import Circuit.Conector;
import Circuit.Pin;
import Components.Componente;
import Components.Switch;
import Gates.Compuerta;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;


public class MiPanel extends JPanel {
    private JPopupMenu menuContextual;
    private Circuito circuito;
    private Componente componenteSeleccionado;
    private Conector conectorSeleccionado;
    private Pin pinSeleccionado;
    private Modo modoActual = Modo.SELECCION;
    private int offsetX, offsetY;
    private final String mensajeError = null;
    private final long tiempoError = 0;
    private Point puntoInicioSeleccion;
    private Rectangle rectanguloSeleccion;
    private List<Componente> componentesSeleccionados = new ArrayList<>();
    private javax.swing.JToggleButton toggleButtonCopia;
    private Point puntoInicioArrastre;
    private final Map<Componente, Point> offsetsComponents = new HashMap<>();

    // Constructor
    public MiPanel() {
        setBackground(Color.WHITE);
        addMouseListener(new MouseHandler());
        addMouseMotionListener(new MouseHandler());
        menuContextual = new JPopupMenu();
    }

    // Setters y Getters / Métodos Públicos Principales
    public void setCircuit(Circuito circuito) {
        this.circuito = circuito;
        repaint();
    }

    public Circuito getCircuit() {
        return circuito;
    }

    public void setModo(Modo modo) {
        this.modoActual = modo;
        componenteSeleccionado = null;
        conectorSeleccionado = null;
        pinSeleccionado = null;
        repaint();
    }

    public void setComponenteSeleccionado(Componente componente) {
        this.componenteSeleccionado = componente;
        this.conectorSeleccionado = null;
    }

    public Componente getComponenteSeleccionado() {
        return componenteSeleccionado;
    }
    
    public void setConectorSeleccionado(Conector conector) {
        this.conectorSeleccionado = conector;
        this.componenteSeleccionado = null;
    }

    public Conector getConectorSeleccionado() {
        return conectorSeleccionado;
    }

    public void setPinSeleccionado(Pin pin) {
        this.pinSeleccionado = pin;
    }

    public List<Componente> getComponentsSeleccionados() {
        if (!componentesSeleccionados.isEmpty()) {
            return componentesSeleccionados;
        } else if (componenteSeleccionado != null) {
            List<Componente> seleccion = new ArrayList<>();
            seleccion.add(componenteSeleccionado);
            return seleccion;
        }
        return new ArrayList<>();
    }

    public void limpiarSeleccion() {
        componenteSeleccionado = null;
        conectorSeleccionado = null;
        componentesSeleccionados.clear();
        if (toggleButtonCopia != null) {
            toggleButtonCopia.setEnabled(false);
        }
        setComponenteSeleccionado(null);
        setConectorSeleccionado(null);
        repaint();
    }
    
    public void setToggleButtonCopia(javax.swing.JToggleButton button) {
        this.toggleButtonCopia = button;
    }

    public void mostrarMensajeError(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            Object[] options = {"Aceptar"};
            JOptionPane.showOptionDialog(
                this,
                mensaje,
                "Error en el Circuit",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null,
                options,
                options[0]
            );
        });
    }

    // Métodos de Renderizado
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (circuito == null) return;

        Graphics2D g2d = (Graphics2D) g;

        if (rectanguloSeleccion != null) {
            g2d.setColor(new Color(0, 100, 255, 50));
            g2d.fill(rectanguloSeleccion);
            g2d.setColor(Color.BLUE);
            g2d.draw(rectanguloSeleccion);
        }

        if (!componentesSeleccionados.isEmpty()) {
            g2d.setColor(new Color(0, 100, 255, 80));
            for (Componente c : componentesSeleccionados) {
                Rectangle bounds = calcularAreaTotal(c);
                g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
                g2d.setColor(Color.BLUE);
                g2d.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }
        }

        for (Componente c : circuito.getComponents()) {
            if (c != null) {
                c.dibujar(g);

                if (c == componenteSeleccionado) {
                    Rectangle bounds = calcularAreaTotal(c);
                    g2d.setColor(new Color(255, 165, 0, 80));
                    g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
                    g2d.setColor(Color.ORANGE);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
                }
            }
        }

        for (Conector conector : circuito.getConexiones()) {
            if (conector != null) {
                Pin salida = conector.obtenerPinSalida();
                Pin entrada = conector.obtenerPinEntrada();

                if (salida != null && entrada != null) {
                    boolean estado = salida.obtenerEstado();

                    if (conector == conectorSeleccionado) {
                        g2d.setColor(new Color(255, 165, 0));
                        g2d.setStroke(new BasicStroke(3));
                    } else {
                        g2d.setColor(estado ? new Color(100, 255, 100) : new Color(0, 100, 0));
                        g2d.setStroke(new BasicStroke(2));
                    }

                    g2d.drawLine(
                        salida.getComponente().getX() + getPinOffset(salida, true),
                        salida.getComponente().getY() + getPinY(salida),
                        entrada.getComponente().getX() + getPinOffset(entrada, false),
                        entrada.getComponente().getY() + getPinY(entrada)
                    );
                }
            }
        }

        if (conectorSeleccionado != null) {
            dibujarConexion(g2d, conectorSeleccionado, true);
        }

        if (modoActual == Modo.CONEXION && pinSeleccionado != null && getMousePosition() != null) {
            Point mousePos = getMousePosition();
            g2d.setColor(Color.BLUE);
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int startX = pinSeleccionado.getComponente().getX() + getPinOffset(pinSeleccionado, true);
            int startY = pinSeleccionado.getComponente().getY() + getPinY(pinSeleccionado);

            g2d.drawLine(startX, startY, mousePos.x, mousePos.y);
        }

        if (mensajeError != null) { // Aunque la variable mensajeError es final y null, se mantiene la estructura
            g2d.setColor(new Color(255, 50, 50, 220));
            g2d.fillRoundRect(20, 50, getWidth() - 40, 40, 15, 15);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));

            if (mensajeError.length() > 60) {
                String primeraLinea = mensajeError.substring(0, 60);
                String segundaLinea = mensajeError.length() > 60 ? mensajeError.substring(60) : "";

                g2d.drawString(primeraLinea, 30, 70);
                if (!segundaLinea.isEmpty()) {
                    g2d.drawString(segundaLinea, 30, 90);
                }
            } else {
                g2d.drawString(mensajeError, 30, 70);
            }
        }


        if (circuito.getRutaArchivo() != null) {
            g.setFont(new Font("Arial", Font.PLAIN, 12));
            g.drawString("Ubicación: " + circuito.getRutaArchivo(), 10, 40);
        }

        if (componenteSeleccionado == null && conectorSeleccionado == null) {
            g.setColor(new Color(0, 0, 0, 150));
            g.setFont(new Font("Arial", Font.ITALIC, 12));
            g.drawString("Seleccione un componente o conexión para modificarlo", 10, getHeight() - 20);
        }
    }

    private void dibujarConexion(Graphics2D g2d, Conector conector, boolean seleccionada) {
        Pin salida = conector.obtenerPinSalida();
        Pin entrada = conector.obtenerPinEntrada();

        if (salida != null && entrada != null &&
            salida.getComponente() != null && entrada.getComponente() != null) {

            boolean estado = salida.obtenerEstado();

            if (seleccionada) {
                g2d.setColor(new Color(255, 165, 0));
                g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{10, 5}, 0));
            } else {
                g2d.setColor(estado ? new Color(100, 255, 100) : new Color(0, 100, 0));
                g2d.setStroke(new BasicStroke(2));
            }

            g2d.drawLine(
                salida.getComponente().getX() + getPinOffset(salida, true),
                salida.getComponente().getY() + getPinY(salida),
                entrada.getComponente().getX() + getPinOffset(entrada, false),
                entrada.getComponente().getY() + getPinY(entrada)
            );
        }
    }

    private Rectangle calcularAreaTotal(Componente c) {
        if (c == null) return new Rectangle(0, 0, 0, 0);

        int x = c.getX();
        int y = c.getY();
        int width = c instanceof Compuerta ? ((Compuerta)c).ancho * 2 : 40;
        int height = c instanceof Compuerta ? ((Compuerta)c).alto : 40;

        x -= 25;
        width += 50;
        y -= 15;
        height += 30;

        return new Rectangle(x, y, width, height);
    }

    private int getPinOffset(Pin pin, boolean isSource) {
        if (pin == null || pin.getComponente() == null) return 0;
        if (pin.getTipo().equals("salida")) {
            if (pin.getComponente() instanceof Compuerta) {
                return ((Compuerta)pin.getComponente()).ancho * 2 + 20;
            }
            return (pin.getComponente() instanceof Switch) ? 45 : 30;
        } else {
            return -20;
        }
    }

    private int getPinY(Pin pin) {
        if (pin == null || pin.getComponente() == null) return 0;
        if (pin.getComponente() instanceof Gates.Or) {
            Gates.Or or = (Gates.Or)pin.getComponente();
            if (pin.getTipo().equals("entrada")) {
                return pin == or.getEntradas().get(0) ? 10 : 30;
            } else {
                return or.alto / 2;
            }
        }
        else if (pin.getComponente() instanceof Compuerta) {
            Compuerta c = (Compuerta)pin.getComponente();
            int index = pin.getTipo().equals("entrada") ?
                c.getEntradas().indexOf(pin) :
                c.getSalidas().indexOf(pin);
            return c.alto / (pin.getTipo().equals("entrada") ?
                (c.getEntradas().size() + 1) : 2) * (index + 1);
        }
        return 15;
    }
    
    // Métodos Ayudantes para Manejadores de Eventos (Lógica de Búsqueda)
    private Pin buscarPinEnPosicion(int x, int y) {
        if (circuito == null) return null;
        for (Componente c : circuito.getComponents()) {
            if (c != null) {
                for (Pin pin : c.getEntradas()) {
                    if (pin != null) {
                        int pinX = c.getX() + getPinOffset(pin, false);
                        int pinY = c.getY() + getPinY(pin);
                        Rectangle areaPinEntrada = new Rectangle(pinX - 10, pinY - 10, 60, 25);
                        if (areaPinEntrada.contains(x, y)) {
                            return pin;
                        }
                    }
                }
                for (Pin pin : c.getSalidas()) {
                    if (pin != null) {
                        int pinX = c.getX() + getPinOffset(pin, true);
                        int pinY = c.getY() + getPinY(pin);
                        int tamañoArea = 40;
                        if (c instanceof Gates.Or ) {
                            tamañoArea = 48;
                        }
                        else if(c instanceof Switch){
                            tamañoArea= 30;
                        }
                        Rectangle areaPinSalida = new Rectangle(pinX - tamañoArea/2,pinY - tamañoArea/2,tamañoArea,tamañoArea);
                        if (areaPinSalida.contains(x, y)) {
                            return pin;
                        }
                    }
                }
            }
        }
        return null;
    }

    private Conector buscarConectorEnPosicion(int x, int y) {
        if (circuito == null) return null;

        Conector conectorMasCercano = null;
        double distanciaMinima = 12;

        for (Conector conector : circuito.getConexiones()) {
            if (conector != null) {
                Pin salida = conector.obtenerPinSalida();
                Pin entrada = conector.obtenerPinEntrada();

                if (salida != null && entrada != null &&
                    salida.getComponente() != null && entrada.getComponente() != null) {

                    Line2D linea = new Line2D.Double(
                        salida.getComponente().getX() + getPinOffset(salida, true),
                        salida.getComponente().getY() + getPinY(salida),
                        entrada.getComponente().getX() + getPinOffset(entrada, false),
                        entrada.getComponente().getY() + getPinY(entrada)
                    );

                    double distancia = linea.ptSegDist(x, y);
                    if (distancia < distanciaMinima) {
                        distanciaMinima = distancia;
                        conectorMasCercano = conector;
                    }
                }
            }
        }
        return conectorMasCercano;
    }

    private int obtenerIndicePin(Pin pin) {
        Componente comp = pin.getComponente();
        if ("salida".equals(pin.getTipo())) {
            return comp.getSalidas().indexOf(pin);
        } else {
            return comp.getSalidas().size() + comp.getEntradas().indexOf(pin);
        }
    }

    // Clase Interna para Manejo del Mouse
    private class MouseHandler extends MouseAdapter {
        private Point dragStartPoint;

        @Override
        public void mousePressed(MouseEvent e) {
            puntoInicioArrastre = e.getPoint();

            offsetsComponents.clear();

            for (Componente c : componentesSeleccionados) {
                int offsetX = c.getX() - puntoInicioArrastre.x;
                int offsetY = c.getY() - puntoInicioArrastre.y;
                offsetsComponents.put(c, new Point(offsetX, offsetY));
            }

            if (circuito == null) return;

            if (SwingUtilities.isLeftMouseButton(e)) {
                if (rectanguloSeleccion != null && !rectanguloSeleccion.contains(e.getPoint())) {
                    rectanguloSeleccion = null;
                    componentesSeleccionados.clear();
                }

                if (modoActual == Modo.SELECCION) {
                    puntoInicioSeleccion = e.getPoint();
                    dragStartPoint = e.getPoint();

                    boolean clicEnComponenteSeleccionado = !componentesSeleccionados.isEmpty() &&
                        componentesSeleccionados.stream().anyMatch(c ->
                            calcularAreaTotal(c).contains(e.getPoint()));

                    if (!clicEnComponenteSeleccionado) {
                        componenteSeleccionado = null;
                        componentesSeleccionados.clear();
                    }

                    for (Componente c : circuito.getComponents()) {
                        if (c != null) {
                            Rectangle bounds = calcularAreaTotal(c);
                            if (bounds.contains(e.getPoint())) {
                                componenteSeleccionado = c;
                                offsetX = e.getX() - c.getX();
                                offsetY = e.getY() - c.getY();

                                if (clicEnComponenteSeleccionado) {
                                } else if (rectanguloSeleccion == null) {
                                    componentesSeleccionados.add(c);
                                }

                                if (getTopLevelAncestor() instanceof Main) {
                                    ((Main)getTopLevelAncestor()).actualizarControlesEntrada(c);
                                }
                                break;
                            }
                        }
                    }

                    if (componenteSeleccionado == null && componentesSeleccionados.isEmpty()) {
                        conectorSeleccionado = buscarConectorEnPosicion(e.getX(), e.getY());
                        if (conectorSeleccionado != null && getTopLevelAncestor() instanceof Main) {
                            ((Main)getTopLevelAncestor()).actualizarControlesEntrada(null);
                        }

                        if (conectorSeleccionado == null && rectanguloSeleccion == null) {
                            rectanguloSeleccion = new Rectangle(puntoInicioSeleccion);
                        }
                    } else {
                        conectorSeleccionado = null;
                    }
                } else if (modoActual == Modo.CONEXION) {
                    pinSeleccionado = buscarPinEnPosicion(e.getX(), e.getY());
                }
            }
            repaint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {

            if (SwingUtilities.isLeftMouseButton(e)) {
                if (modoActual == Modo.SELECCION) {
                    if (componenteSeleccionado != null && !componentesSeleccionados.isEmpty()) {
                        int newX = e.getX() - offsetX;
                        int newY = e.getY() - offsetY;

                        for (Componente c : componentesSeleccionados) {
                            c.mover(newX, newY);
                        }
                        circuito.setModificado(true);
                    }
                    else if (puntoInicioSeleccion != null) {
                        int x = Math.min(puntoInicioSeleccion.x, e.getX());
                        int y = Math.min(puntoInicioArrastre.y, e.getY());
                        int width = Math.abs(e.getX() - puntoInicioSeleccion.x);
                        int height = Math.abs(e.getY() - puntoInicioArrastre.y);

                        rectanguloSeleccion = new Rectangle(x, y, width, height);
                        componentesSeleccionados = circuito.seleccionarComponentsEnArea(rectanguloSeleccion);
                    }
                }
                repaint();
            }
            if (!componentesSeleccionados.isEmpty() && puntoInicioArrastre != null) {
                Point puntoActual = e.getPoint();

                for (Componente c : componentesSeleccionados) {
                    Point offset = offsetsComponents.get(c);
                    if (offset != null) {
                        c.mover(puntoActual.x + offset.x, puntoActual.y + offset.y);
                    }
                }

                circuito.setModificado(true);
                repaint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            puntoInicioArrastre = null;
            if (SwingUtilities.isLeftMouseButton(e)) {
                if (modoActual == Modo.SELECCION) {
                    if (puntoInicioSeleccion != null) {
                        if (rectanguloSeleccion != null &&
                            (rectanguloSeleccion.width > 5 || rectanguloSeleccion.height > 5)) {

                            if (!componentesSeleccionados.isEmpty()) {
                                componenteSeleccionado = null;
                                conectorSeleccionado = null;
                            } else {
                                componenteSeleccionado = null;
                                conectorSeleccionado = null;
                                rectanguloSeleccion = null;
                            }
                        } else {
                            rectanguloSeleccion = null;

                            boolean clicEnComponente = false;
                            for (Componente c : circuito.getComponents()) {
                                if (c != null && calcularAreaTotal(c).contains(e.getPoint())) {
                                    clicEnComponente = true;
                                    break;
                                }
                            }

                            if (!clicEnComponente) {
                                componentesSeleccionados.clear();
                            }
                        }
                        puntoInicioSeleccion = null;
                    }
                } else if (modoActual == Modo.CONEXION && pinSeleccionado != null) {
                    try {
                        Pin otroPin = buscarPinEnPosicion(e.getX(), e.getY());

                        if (otroPin == null) {
                            throw new Exception("Debe seleccionar un pin válido para conectar");
                        }

                        if (otroPin.equals(pinSeleccionado)) {
                            throw new Exception("No puede conectar un pin consigo mismo");
                        }

                        if (otroPin.getConector() != null && "entrada".equals(otroPin.getTipo())) {
                            throw new Exception("El pin de entrada ya está conectado");
                        }

                        circuito.conectar(
                            pinSeleccionado.getComponente(),
                            obtenerIndicePin(pinSeleccionado),
                            otroPin.getComponente(),
                            obtenerIndicePin(otroPin)
                        );

                    } catch (Exception ex) {
                        mostrarMensajeError(ex.getMessage());
                    } finally {
                        pinSeleccionado = null;
                    }
                }
            }
            repaint();
        }
    }

    // Enum (definición de tipo)
    public enum Modo {
        SELECCION, CONEXION
    }
}