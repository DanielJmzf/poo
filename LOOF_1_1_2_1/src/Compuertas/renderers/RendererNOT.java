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

public class RendererNOT implements CompuertaRenderer {
    @Override
    public void dibujar(Graphics g, Compuerta compuerta) {
        Graphics2D g2d = (Graphics2D)g.create();
        g2d.setStroke(new BasicStroke(3));
        
        int[] xPoints = {compuerta.getX(), compuerta.getX(), compuerta.getX() + 40};
        int[] yPoints = {compuerta.getY(), compuerta.getY() + 40, compuerta.getY() + 20};

        g2d.setColor(Color.RED);
        g2d.fillPolygon(xPoints, yPoints, 3);
        g2d.setColor(Color.BLACK);
        g2d.drawPolygon(xPoints, yPoints, 3);

        g2d.setColor(Color.RED);
        g2d.fillOval(compuerta.getX() + 40, compuerta.getY() + 15, 10, 10);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(compuerta.getX() + 40, compuerta.getY() + 15, 10, 10);
        
        g2d.drawLine(compuerta.getX() - 20, compuerta.getY() + 20, compuerta.getX(), compuerta.getY() + 20);
        g2d.drawLine(compuerta.getX() + 50, compuerta.getY() + 20, compuerta.getX() + 70, compuerta.getY() + 20);

        // Dibujar estados
        g2d.setColor(Color.BLACK);
        if (!compuerta.getEntradas().isEmpty()) {
            g2d.drawString(compuerta.getEntradas().get(0).obtenerEstado() ? "1" : "0", 
                          compuerta.getX() - 25, compuerta.getY() + 25);
        }
        if (!compuerta.getSalidas().isEmpty()) {
            g2d.drawString(compuerta.getSalidas().get(0).obtenerEstado() ? "1" : "0", 
                          compuerta.getX() + 75, compuerta.getY() + 25);
        }

        g2d.dispose();
    }
}