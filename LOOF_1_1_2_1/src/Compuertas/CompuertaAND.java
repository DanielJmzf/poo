/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Compuertas;

import compuertas.renderers.RendererAND; // Asegúrate que la importación sea correcta

public class CompuertaAND extends Compuerta {
    public CompuertaAND(int x, int y) {
        super(x, y, 2, new RendererAND()); // Se pasan 2 entradas al constructor de Compuerta
    }
    
    @Override
    public void reinitializeRenderer() {
        // Re-inicializa el renderer después de la deserialización
        this.renderer = new RendererAND();
        // O usar el setter si se prefiere: this.setRenderer(new RendererAND());
    }
    
    @Override
    public void evaluar() {
        if (getEntradas().size() >= 2) { // Verificar que hay suficientes pines de entrada
            boolean valEntrada1 = getEntradas().get(0) != null ? getEntradas().get(0).obtenerEstado() : false;
            boolean valEntrada2 = getEntradas().get(1) != null ? getEntradas().get(1).obtenerEstado() : false;
            boolean resultado = valEntrada1 && valEntrada2;
            
            if (!getSalidas().isEmpty() && getSalidas().get(0) != null) {
                getSalidas().get(0).cambiarEstado(resultado);
            }
        } else {
            // Manejar caso de entradas insuficientes, por ejemplo, salida a false
            if (!getSalidas().isEmpty() && getSalidas().get(0) != null) {
                getSalidas().get(0).cambiarEstado(false);
            }
        }
    }
}
