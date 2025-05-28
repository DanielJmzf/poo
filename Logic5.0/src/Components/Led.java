/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Components;

import Components.Componente;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;

public class Led extends Componente {
    public Led(int x, int y) {
        super(x, y, 1, 0);
    }
    
    @Override
    public void evaluar() {
    }
    
    @Override
    public void dibujar(Graphics g) {
        Graphics2D g2d = (Graphics2D)g.create();
        g2d.setStroke(new BasicStroke(2));
        boolean encendido = !entradas.isEmpty() && entradas.get(0).obtenerEstado();
        g2d.setColor(encendido ? Color.GREEN : Color.BLACK);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(x, y, 30, 30);
        g2d.drawLine(x - 15, y + 15, x, y + 15);
        if (encendido) {
            g2d.setColor(new Color(255, 255, 0, 100));
            g2d.fillOval(x - 5, y - 5, 40, 40);
        }
        g2d.setColor(Color.BLACK);
        g2d.drawString(encendido ? "1" : "0", x + 35, y + 20);
        g2d.dispose();
    }
}