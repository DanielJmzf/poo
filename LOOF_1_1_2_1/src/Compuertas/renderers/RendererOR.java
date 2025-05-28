/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compuertas.renderers;

import Compuertas.Compuerta;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class RendererOR implements CompuertaRenderer {
    @Override
    public void dibujar(Graphics g, Compuerta compuerta) {
        Graphics2D g2d = (Graphics2D)g.create();
        g2d.setStroke(new BasicStroke(3));
        
        g2d.setColor(Color.RED);
        g2d.fillArc(compuerta.getX() - 30, compuerta.getY(), 83, 40, 270, 180);
        g2d.fillArc(compuerta.getX() - 15, compuerta.getY(), 70, 40, 270, 180);
        g2d.fillArc(compuerta.getX(), compuerta.getY(), 55, 40, 270, 90);
        g2d.fillArc(compuerta.getX(), compuerta.getY(), 55, 50, 0, 90);

        g2d.setColor(Color.BLACK);
        g2d.drawArc(compuerta.getX() - 11, compuerta.getY(), 20, 40, 270, 180);
        g2d.drawArc(compuerta.getX(), compuerta.getY(), 55, 40, 270, 90);
        g2d.drawArc(compuerta.getX(), compuerta.getY(), 55, 50, 0, 90);
        g2d.drawLine(compuerta.getX(), compuerta.getY(), compuerta.getX() + 30, compuerta.getY());
        g2d.drawLine(compuerta.getX(), compuerta.getY() + 40, compuerta.getX() + 30, compuerta.getY() + 40);
        
        g2d.drawLine(compuerta.getX() - 20, compuerta.getY() + 10, compuerta.getX() + 6, compuerta.getY() + 10);
        g2d.drawLine(compuerta.getX() - 20, compuerta.getY() + 30, compuerta.getX() + 6, compuerta.getY() + 30);
        g2d.drawLine(compuerta.getX() + 55, compuerta.getY() + 22, compuerta.getX() + 75, compuerta.getY() + 22);

        // Dibujar estados
        g2d.setColor(Color.BLACK);
        if (!compuerta.getEntradas().isEmpty()) {
            g2d.drawString(compuerta.getEntradas().get(0).obtenerEstado() ? "1" : "0", 
                          compuerta.getX() - 25, compuerta.getY() + 13);
            if (compuerta.getEntradas().size() > 1) {
                g2d.drawString(compuerta.getEntradas().get(1).obtenerEstado() ? "1" : "0", 
                              compuerta.getX() - 25, compuerta.getY() + 33);
            }
        }
        if (!compuerta.getSalidas().isEmpty()) {
            g2d.drawString(compuerta.getSalidas().get(0).obtenerEstado() ? "1" : "0", 
                          compuerta.getX() + 80, compuerta.getY() + 25);
        }

        g2d.dispose();
    }
}