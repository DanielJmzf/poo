/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Compuertas;

import compuertas.renderers.RendererOR; // Asegúrate que la importación sea correcta

public class CompuertaOR extends Compuerta {
    public CompuertaOR(int x, int y) {
        super(x, y, 2, new RendererOR()); // Se pasan 2 entradas al constructor de Compuerta
    }

    @Override
    public void reinitializeRenderer() {
        // Re-inicializa el renderer después de la deserialización
        this.renderer = new RendererOR();
    }
    
    @Override
    public void evaluar() {
        if (getEntradas().size() >= 2) {
            boolean valEntrada1 = getEntradas().get(0) != null ? getEntradas().get(0).obtenerEstado() : false;
            boolean valEntrada2 = getEntradas().get(1) != null ? getEntradas().get(1).obtenerEstado() : false;
            boolean resultado = valEntrada1 || valEntrada2;

            if (!getSalidas().isEmpty() && getSalidas().get(0) != null) {
                getSalidas().get(0).cambiarEstado(resultado);
            }
        } else if (getEntradas().size() == 1) { // Comportamiento si solo hay una entrada conectada
             boolean valEntrada1 = getEntradas().get(0) != null ? getEntradas().get(0).obtenerEstado() : false;
             if (!getSalidas().isEmpty() && getSalidas().get(0) != null) {
                getSalidas().get(0).cambiarEstado(valEntrada1);
            }
        }
        else {
            // Caso de ninguna entrada conectada o menos de las esperadas
            if (!getSalidas().isEmpty() && getSalidas().get(0) != null) {
                getSalidas().get(0).cambiarEstado(false);
            }
        }
    }
}
