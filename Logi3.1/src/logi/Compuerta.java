/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logi;

import java.awt.Graphics;

public abstract class Compuerta extends Componente {
    public final int ancho = 20;
    public final int alto = 40;
    
    public Compuerta(int x, int y, int numEntradas) {
        super(x, y, numEntradas, 1);
    }
    
    public abstract void dibujarCompuerta(Graphics g);
    
    @Override
    public void dibujar(Graphics g) {
        dibujarCompuerta(g);
    }
    
    public void setPosicion(int x, int y) {
        this.x = x;
        this.y = y;
    }
}