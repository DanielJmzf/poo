/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Circuito;  

import Componentes.Componente;

public interface ComponenteFactory {
    Componente crearComponente(String tipo, int x, int y);
}