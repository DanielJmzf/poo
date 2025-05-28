/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Circuito;

import Componentes.Componente;
import Componentes.Led;
import Componentes.Switch;
import Compuertas.CompuertaAND;
import Compuertas.CompuertaNOT;
import Compuertas.CompuertaOR;
import java.io.Serializable;

public class CircuitoComponenteFactory implements ComponenteFactory, Serializable {
    private static final long serialVersionUID = 1L;  
    @Override
    public Componente crearComponente(String tipo, int x, int y) {
        tipo = tipo.toLowerCase();  
        
        switch (tipo) {
            case "and":
                return new CompuertaAND(x, y);
            case "or":
                return new CompuertaOR(x, y);
            case "not":
                return new CompuertaNOT(x, y);
            case "switch":
                return new Switch(x, y);
            case "led":
                return new Led(x, y);
            default:
                throw new IllegalArgumentException("Tipo de componente no soportado: " + tipo);
        }
    }
}