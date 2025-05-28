
package Circuito;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import Compuertas.Compuerta;
import Componentes.Componente;
import Circuito.Pin;

/**
 * @author Jesus
 */
public class Excepciones extends Exception {
    public Excepciones(String message) {
        super(message);
    }

    public void validarCircuito(Iterable<Componente> componentes) throws Excepciones {
        // Verifica que no haya pines de entrada desconectados en compuertas
        for (Componente c : componentes) {
            if (c instanceof Compuerta) {
                for (Pin entrada : c.getEntradas()) {
                    if (entrada.getConector() == null) {
                        throw new Excepciones("La compuerta " + c.getId() + 
                                            " tiene entradas desconectadas");
                    }
                }
            }
        }
    }
}