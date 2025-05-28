/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logicaExpresion;

public class OperacionU extends Ast {
    private Tipo operador;
    private Ast operando;

    public OperacionU(Tipo operador, Ast operando) {
        if (operador != Tipo.NOT) {
            throw new IllegalArgumentException("Operador unario inválido: " + operador);
        }
        this.operador = operador;
        this.operando = operando;
    }

    public Tipo getOperador() {
        return operador;
    }

    public Ast getOperando() {
        return operando;
    }

    @Override
    public String toString() {
        return operador + "(" + operando.toString() + ")";
    }
}

