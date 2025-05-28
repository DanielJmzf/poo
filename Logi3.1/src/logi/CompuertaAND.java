/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logi;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class CompuertaAND extends Compuerta {
    public CompuertaAND(int x, int y) {
        super(x, y, 2);
    }
    
    @Override
public void evaluar() {
    if (entradas.size() >= 2) {
        boolean resultado = entradas.get(0).obtenerEstado() && entradas.get(1).obtenerEstado();
        salidas.get(0).cambiarEstado(resultado);
    } else {
        salidas.get(0).cambiarEstado(false);
    }
}
    
    @Override
    public void dibujarCompuerta(Graphics g) {
        Graphics2D g2d = (Graphics2D)g.create();
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(Color.BLACK);
        g2d.drawLine(x, y, x, y + alto);
        g2d.drawLine(x, y, x + ancho, y);
        g2d.drawLine(x, y + alto, x + ancho, y + alto);
        g2d.drawArc(x, y, 2 * ancho, alto, 90, -180);
        g2d.drawLine(x, y + 12, x - 20, y + 12);
        g2d.drawLine(x, y + 28, x - 20, y + 28);
        g2d.drawLine(x + 2 * ancho, y + alto / 2, x + 2 * ancho + 20, y + alto / 2);
        g2d.setColor(Color.BLACK);
        if (!entradas.isEmpty()) {
            g2d.drawString(entradas.get(0).obtenerEstado() ? "1" : "0", x - 25, y + 15);
            if (entradas.size() > 1) {
                g2d.drawString(entradas.get(1).obtenerEstado() ? "1" : "0", x - 25, y + 35);
            }
        }
        if (!salidas.isEmpty()) {
            g2d.drawString(salidas.get(0).obtenerEstado() ? "1" : "0", x + 2 * ancho + 25, y + 25);
        }
        
        g2d.dispose();
    }
}