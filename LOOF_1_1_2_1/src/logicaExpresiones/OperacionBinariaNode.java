/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logicaExpresiones;

public class OperacionBinariaNode extends ASTNode {
    private TipoOperador operador;
    private ASTNode izquierdo;
    private ASTNode derecho;

    public OperacionBinariaNode(TipoOperador operador, ASTNode izquierdo, ASTNode derecho) {
        if (operador != TipoOperador.AND && operador != TipoOperador.OR) {
            throw new IllegalArgumentException("Operador binario inválido: " + operador);
        }
        this.operador = operador;
        this.izquierdo = izquierdo;
        this.derecho = derecho;
    }

    public TipoOperador getOperador() {
        return operador;
    }

    public ASTNode getIzquierdo() {
        return izquierdo;
    }

    public ASTNode getDerecho() {
        return derecho;
    }

    @Override
    public String toString() {
        return "(" + izquierdo.toString() + " " + operador + " " + derecho.toString() + ")";
    }
}