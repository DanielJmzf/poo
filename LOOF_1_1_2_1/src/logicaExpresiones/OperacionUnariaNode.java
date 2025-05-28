/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logicaExpresiones;

public class OperacionUnariaNode extends ASTNode {
    private TipoOperador operador;
    private ASTNode operando;

    public OperacionUnariaNode(TipoOperador operador, ASTNode operando) {
        if (operador != TipoOperador.NOT) {
            throw new IllegalArgumentException("Operador unario inválido: " + operador);
        }
        this.operador = operador;
        this.operando = operando;
    }

    public TipoOperador getOperador() {
        return operador;
    }

    public ASTNode getOperando() {
        return operando;
    }

    @Override
    public String toString() {
        return operador + "(" + operando.toString() + ")";
    }
}

