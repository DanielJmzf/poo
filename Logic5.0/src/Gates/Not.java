/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Gates;

import Render.CompuertaNOT;

public class Not extends Compuerta {
    public Not(int x, int y) {
        super(x, y, 1, new CompuertaNOT()); 
    }

    @Override
    public void reinitializeRenderer() {
        this.renderer = new CompuertaNOT();
    }
    
    @Override
    public void evaluar() {
        if (!getEntradas().isEmpty() && getEntradas().get(0) != null) {
            boolean resultado = !getEntradas().get(0).obtenerEstado();
            if (!getSalidas().isEmpty() && getSalidas().get(0) != null) {
                getSalidas().get(0).cambiarEstado(resultado);
            }
        } else {
             if (!getSalidas().isEmpty() && getSalidas().get(0) != null) {
                getSalidas().get(0).cambiarEstado(true);
            }
        }
    }
}
