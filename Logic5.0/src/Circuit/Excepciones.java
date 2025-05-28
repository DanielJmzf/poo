
package Circuit;

import Components.Componente;
import Gates.Compuerta;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


public class Excepciones extends Exception {
    public Excepciones(String message) {
        super(message);
    }

    public void validarCircuito(Iterable<Componente> componentes) throws Excepciones {
        for (Componente c : componentes) {
            if (c instanceof Compuerta) {
                for (Pin entrada : c.getEntradas()) {
                    if (entrada.getConector() == null) {
                        throw new Excepciones("Error de validación: La compuerta con ID '" + c.getId() +"' tiene una o más entradas sin conexión.");                    
                    }
                }
            }
        }
    }
}