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

public class RendererAND implements CompuertaRenderer {
    @Override
    public void dibujar(Graphics g, Compuerta compuerta) {
        Graphics2D g2d = (Graphics2D)g.create();
        g2d.setStroke(new BasicStroke(3));
        
        g2d.setColor(Color.RED);
        g2d.fillArc(compuerta.getX(), compuerta.getY(), 40, 40, 90, -180);
        g2d.fillRect(compuerta.getX(), compuerta.getY(), 20, 40);
         
        g2d.setColor(Color.BLACK);
        g2d.drawLine(compuerta.getX(), compuerta.getY(), compuerta.getX() + 20, compuerta.getY());
        g2d.drawLine(compuerta.getX(), compuerta.getY() + 40, compuerta.getX() + 20, compuerta.getY() + 40);
        g2d.drawArc(compuerta.getX(), compuerta.getY(), 40, 40, 90, -180);
        
        g2d.drawLine(compuerta.getX(), compuerta.getY() + 12, compuerta.getX() - 20, compuerta.getY() + 12);
        g2d.drawLine(compuerta.getX(), compuerta.getY() + 28, compuerta.getX() - 20, compuerta.getY() + 28);
        g2d.drawLine(compuerta.getX() + 40, compuerta.getY() + 20, compuerta.getX() + 60, compuerta.getY() + 20);
        g2d.drawLine(compuerta.getX(), compuerta.getY(), compuerta.getX(), compuerta.getY()+ compuerta.getAlto());

        // Dibujar estados
        g2d.setColor(Color.BLACK);
        if (!compuerta.getEntradas().isEmpty()) {
            g2d.drawString(compuerta.getEntradas().get(0).obtenerEstado() ? "1" : "0", 
                          compuerta.getX() - 25, compuerta.getY() + 15);
            if (compuerta.getEntradas().size() > 1) {
                g2d.drawString(compuerta.getEntradas().get(1).obtenerEstado() ? "1" : "0", 
                              compuerta.getX() - 25, compuerta.getY() + 35);
            }
        }
        if (!compuerta.getSalidas().isEmpty()) {
            g2d.drawString(compuerta.getSalidas().get(0).obtenerEstado() ? "1" : "0", 
                          compuerta.getX() + 65, compuerta.getY() + 25);
        }
        
        g2d.dispose();
    }
}