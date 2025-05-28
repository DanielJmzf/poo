/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Gates;

import Render.CompuertaOR; // Asegúrate que la importación sea correcta

public class Or extends Compuerta {
    public Or(int x, int y) {
        super(x, y, 2, new CompuertaOR()); 
    }

    @Override
    public void reinitializeRenderer() {
        this.renderer = new CompuertaOR();
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
        } else if (getEntradas().size() == 1) {
             boolean valEntrada1 = getEntradas().get(0) != null ? getEntradas().get(0).obtenerEstado() : false;
             if (!getSalidas().isEmpty() && getSalidas().get(0) != null) {
                getSalidas().get(0).cambiarEstado(valEntrada1);
            }
        }
        else {
            if (!getSalidas().isEmpty() && getSalidas().get(0) != null) {
                getSalidas().get(0).cambiarEstado(false);
            }
        }
    }
}
