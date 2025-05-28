/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Render;

import Gates.Compuerta;
import java.awt.Graphics;
import java.io.Serializable;


public interface GatesRenderer extends Serializable { 
    void dibujar(Graphics g, Compuerta cmp);
}
