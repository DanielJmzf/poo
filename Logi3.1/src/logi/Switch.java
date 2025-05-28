/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logi;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;

public class Switch extends Componente {
    private boolean estado;
    
    public Switch(int x, int y) {
        super(x, y, 0, 1);
        this.estado = false;
    }
    
    public void toggle() {
        estado = !estado;
        
        salidas.get(0).cambiarEstado(estado);
    }
    
    @Override
    public void evaluar() {
        
        salidas.get(0).cambiarEstado(estado);
    }
    
    @Override
    public void dibujar(Graphics g) {
        Graphics2D g2d = (Graphics2D)g.create();
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x, y, 30, 30);     
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString(estado ? "1" : "0", x + 12, y + 20);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(x + 30, y + 15, x + 45, y + 15);
        g2d.setColor(Color.BLUE);
        g2d.dispose();
    }
    
    public boolean getEstado() {
        return estado;
    }
}