/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logicaExpresion;

public class OperacionB extends Ast {
    private Tipo operador;
    private Ast izquierdo;
    private Ast derecho;

    public OperacionB(Tipo operador, Ast izquierdo, Ast derecho) {
        if (operador != Tipo.AND && operador != Tipo.OR) {
            throw new IllegalArgumentException("Operador binario inválido: " + operador);
        }
        this.operador = operador;
        this.izquierdo = izquierdo;
        this.derecho = derecho;
    }

    public Tipo getOperador() {
        return operador;
    }

    public Ast getIzquierdo() {
        return izquierdo;
    }

    public Ast getDerecho() {
        return derecho;
    }

    @Override
    public String toString() {
        return "(" + izquierdo.toString() + " " + operador + " " + derecho.toString() + ")";
    }
}