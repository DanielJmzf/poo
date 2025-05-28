/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package compuertas.renderers;


import Compuertas.Compuerta;
import java.awt.Graphics;
import java.io.Serializable;

/**
 *
 * @author jesus
 */
public interface CompuertaRenderer extends Serializable { // Debe ser Serializable
    void dibujar(Graphics g, Compuerta compuerta);
}
