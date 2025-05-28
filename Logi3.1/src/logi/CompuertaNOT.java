/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logi;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class CompuertaNOT extends Compuerta {
    public CompuertaNOT(int x, int y) {
        super(x, y, 1);
    }
    
   @Override
public void evaluar() {
    if (!entradas.isEmpty()) {
        boolean resultado = !entradas.get(0).obtenerEstado();
        salidas.get(0).cambiarEstado(resultado);
    } else {
        salidas.get(0).cambiarEstado(true); 
    }
}
    
    @Override
    public void dibujarCompuerta(Graphics g) {
        Graphics2D g2d = (Graphics2D)g.create();
        g2d.setStroke(new BasicStroke(2));
        int[] xPoints = {x, x, x + 40};
        int[] yPoints = {y, y + 40, y + 20};
        g2d.drawPolygon(xPoints, yPoints, 3);
        g2d.drawOval(x + 40, y + 15, 10, 10);
        g2d.drawLine(x - 20, y + 20, x, y + 20);
        g2d.drawLine(x + 50, y + 20, x + 70, y + 20);
        g2d.setColor(Color.BLACK);
        if (!entradas.isEmpty()) {
            g2d.drawString(entradas.get(0).obtenerEstado() ? "1" : "0", x - 25, y + 25);
        }
        if (!salidas.isEmpty()) {
            g2d.drawString(salidas.get(0).obtenerEstado() ? "1" : "0", x + 75, y + 25);
        }
        g2d.dispose();
    }
}