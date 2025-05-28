/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logi;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MiPanel extends JPanel {
    private Circuito circuito;
    private Componente componenteSeleccionado;
    private Conector conectorSeleccionado;
    private Pin pinSeleccionado;
    private Modo modoActual = Modo.SELECCION;
    private int offsetX, offsetY;
    private String mensajeError = null;
    private long tiempoError = 0;
    
    public MiPanel() {
        setBackground(Color.WHITE);
        addMouseListener(new MouseHandler());
        addMouseMotionListener(new MouseHandler());
    }
    
    public void setCircuito(Circuito circuito) {
        this.circuito = circuito;
        repaint();
    }
    
    public void setModo(Modo modo) {
        this.modoActual = modo;
        componenteSeleccionado = null;
        conectorSeleccionado = null;
        pinSeleccionado = null;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (circuito == null) return;
        Graphics2D g2d = (Graphics2D) g;
        for (Componente c : circuito.getComponentes()) {
            if (c != null) {
                c.dibujar(g);
                if (c == componenteSeleccionado) {
                    Rectangle bounds = calcularAreaTotal(c);
                    g2d.setColor(new Color(10,10,10,10));
                    g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
                    g2d.setStroke(new BasicStroke(1));
                }
            }
        }
        
        for (Conector conector : circuito.getConexiones()) {
            if (conector != null) {
                Pin salida = conector.obtenerPinSalida();
                boolean estado = salida != null ? salida.obtenerEstado() : false;
                if (conector == conectorSeleccionado) {
                    g2d.setColor(new Color(255, 165, 0));
                    g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{10, 5}, 0));
                } else {
                    g2d.setColor(estado ? new Color(100, 255, 100) : new Color(0, 100, 0));
                    g2d.setStroke(new BasicStroke(2));
                }
                if (salida != null) {
                    for (Pin entrada : conector.obtenerPinesEntrada()) {
                        if (entrada != null && entrada.getComponente() != null && salida.getComponente() != null) {
                            g2d.drawLine(salida.getComponente().getX() + getPinOffset(salida, true), salida.getComponente().getY() + getPinY(salida), entrada.getComponente().getX() + getPinOffset(entrada, false), entrada.getComponente().getY() + getPinY(entrada));
                        }
                    }
                }
            }
            
            if (mensajeError != null) {
        g2d.setColor(new Color(190, 70, 50, 220)); 
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
        }
        if (modoActual == Modo.CONEXION && pinSeleccionado != null && getMousePosition() != null) {
            g2d.setColor(Color.BLUE);
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawLine(
                pinSeleccionado.getComponente().getX() + getPinOffset(pinSeleccionado, true),
                pinSeleccionado.getComponente().getY() + getPinY(pinSeleccionado),
                getMousePosition().x,
                getMousePosition().y
            );
        }
        if (circuito.getRutaArchivo() != null) {
            g.setFont(new Font("Arial", Font.PLAIN, 12));
            g.drawString("Ubicación: " + circuito.getRutaArchivo(), 10, 40); 
        }
        if (componenteSeleccionado == null && conectorSeleccionado == null) {
            g.setColor(new Color(0, 0, 0, 150));
            g.setFont(new Font("Arial", Font.ITALIC, 12));
            g.drawString("Seleccione un componente o conexion", 10, getHeight() - 20);
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
        if (pin.getComponente() instanceof logi.CompuertaOR) {
            logi.CompuertaOR or = (logi.CompuertaOR)pin.getComponente();
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
    
   private Pin buscarPinEnPosicion(int x, int y) {
        if (circuito == null) return null;
        for (Componente c : circuito.getComponentes()) {
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
                        if (c instanceof logi.CompuertaOR ) {
                            tamañoArea = 48;    
                        }
                        else if(c instanceof Switch){
                            tamañoArea= 30;
                        }
                        Rectangle areaPinSalida = new Rectangle(pinX - tamañoArea/2, pinY - tamañoArea/2,tamañoArea,tamañoArea);
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
                    if (linea.ptSegDist(x, y) < 12) {
                        return conector;
                    }
                }
            }
        }
        return null;
    }

    public void mostrarMensajeError(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            Object[] options = {"Aceptar"};
            JOptionPane.showOptionDialog(this, mensaje, "Error en el Circuito", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
        }
        );
    }

    
    
    private class MouseHandler extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            if (circuito == null) return;
            if (modoActual == Modo.SELECCION) {
                componenteSeleccionado = null;
                for (Componente c : circuito.getComponentes()) {
                    if (c != null) {
                        Rectangle bounds = calcularAreaTotal(c);
                        if (bounds.contains(e.getPoint())) {
                            componenteSeleccionado = c;
                            offsetX = e.getX() - c.getX();
                            offsetY = e.getY() - c.getY();
                            if (getTopLevelAncestor() instanceof Main) {
                                ((Main)getTopLevelAncestor()).actualizarControlesEntrada(c);
                            }
                            break;
                        }
                    }
                }
                if (componenteSeleccionado == null) {
                    conectorSeleccionado = buscarConectorEnPosicion(e.getX(), e.getY());
                    if (conectorSeleccionado != null && getTopLevelAncestor() instanceof Main) {
                        ((Main)getTopLevelAncestor()).actualizarControlesEntrada(null);
                    }
                } else {
                    conectorSeleccionado = null;
                }
                repaint();
            } else if (modoActual == Modo.CONEXION) {
                pinSeleccionado = buscarPinEnPosicion(e.getX(), e.getY());
            }
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
        if (modoActual == Modo.CONEXION && pinSeleccionado != null) {
            try {
                Pin otroPin = buscarPinEnPosicion(e.getX(), e.getY());
                if (otroPin == null) {
                    throw new Exception("Seleccionar un pin válido para conectar");
                }
                if (otroPin.equals(pinSeleccionado)) {
                    throw new Exception("No puede conectar un pin al mismo pin");
                }
                if (otroPin.getConector() != null && "entrada".equals(otroPin.getTipo())) {
                    throw new Exception("El pin de entrada ya está conectado");
                }
                circuito.conectar(pinSeleccionado.getComponente(), obtenerIndicePin(pinSeleccionado), otroPin.getComponente(), obtenerIndicePin(otroPin));
            } catch (Exception ex) {
                mostrarMensajeError(ex.getMessage());
            } finally {
                pinSeleccionado = null;
                repaint();
            }
        }
    }
    
    private int obtenerIndicePin(Pin pin) {
        Componente comp = pin.getComponente();
        if ("salida".equals(pin.getTipo())) {
            return comp.getSalidas().indexOf(pin);
        } else {
            return comp.getSalidas().size() + comp.getEntradas().indexOf(pin);
        }
    }
    
        @Override
        public void mouseDragged(MouseEvent e) {
            if (componenteSeleccionado != null && modoActual == Modo.SELECCION) {
                componenteSeleccionado.mover(e.getX() - offsetX, e.getY() - offsetY);
                repaint();
            } else if (pinSeleccionado != null && modoActual == Modo.CONEXION) {
                repaint();
            }
        }
    }
    

    public enum Modo {
        SELECCION, CONEXION
    }
    
    public Circuito getCircuito() {
        return circuito;
    }

    public void setComponenteSeleccionado(Componente componente) {
        this.componenteSeleccionado = componente;
        this.conectorSeleccionado = null;
    }

    public void setPinSeleccionado(Pin pin) {
        this.pinSeleccionado = pin;
    }
    
    public Componente getComponenteSeleccionado() {
        return componenteSeleccionado;
    }
    
    public Conector getConectorSeleccionado() {
        return conectorSeleccionado;
    }
    
    public void setConectorSeleccionado(Conector conector) {
        this.conectorSeleccionado = conector;
        this.componenteSeleccionado = null;
    }
}