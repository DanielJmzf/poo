/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Compuertas;

import compuertas.renderers.RendererNOT; // Asegúrate que la importación sea correcta

public class CompuertaNOT extends Compuerta {
    public CompuertaNOT(int x, int y) {
        super(x, y, 1, new RendererNOT()); // Se pasa 1 entrada al constructor de Compuerta
    }

    @Override
    public void reinitializeRenderer() {
        // Re-inicializa el renderer después de la deserialización
        this.renderer = new RendererNOT();
    }
    
    @Override
    public void evaluar() {
        if (!getEntradas().isEmpty() && getEntradas().get(0) != null) {
            boolean resultado = !getEntradas().get(0).obtenerEstado();
            if (!getSalidas().isEmpty() && getSalidas().get(0) != null) {
                getSalidas().get(0).cambiarEstado(resultado);
            }
        } else {
            // Si no hay entrada, la salida por defecto podría ser true (o false, según definición)
             if (!getSalidas().isEmpty() && getSalidas().get(0) != null) {
                getSalidas().get(0).cambiarEstado(true); // O false, según la lógica deseada
            }
        }
    }
}
