/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Circuit;

import Components.Componente;
import Components.Led;
import Components.Switch;
import Gates.And;
import Gates.Not;
import Gates.Or;
import java.io.Serializable;

public class CirComponentFactory implements ComponentFactory, Serializable {
    private static final long serialVersionUID = 1L;  
    @Override
    public Componente crearComponente(String tipo, int x, int y) {
        tipo = tipo.toLowerCase();  
        switch (tipo) {
            case "and":
                return new And(x, y);
            case "or":
                return new Or(x, y);
            case "not":
                return new Not(x, y);
            case "switch":
                return new Switch(x, y);
            case "led":
                return new Led(x, y);
            default:
                throw new IllegalArgumentException("Tipo de componente no soportado: " + tipo);
        }
    }
}