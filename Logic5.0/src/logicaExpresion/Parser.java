/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logicaExpresion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Set; 

public class Parser {

    private static final Map<Character, Integer> ORDEN = new HashMap<>();
    static {
        ORDEN.put('!', 3);
        ORDEN.put('&', 2); 
        ORDEN.put('|', 1);
        ORDEN.put('(', 0); 
    }

    
    private List<String> tokenizar(String expresion, Set<String> nombresVariablesValidas) throws IllegalArgumentException {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < expresion.length(); i++) {
            char ch = expresion.charAt(i);
            if (Character.isWhitespace(ch)) {
                continue; 
            }
            if (Character.isLetter(ch)) { 
                String var = String.valueOf(ch).toUpperCase();
                if (!nombresVariablesValidas.contains(var)) {
                    throw new IllegalArgumentException("Variable no definida '" + var + "' en la expresión. Variables válidas: " + nombresVariablesValidas);
                }
                tokens.add(var);
            } else if (ch == '&' || ch == '|' || ch == '!' || ch == '(' || ch == ')') {
                tokens.add(String.valueOf(ch));
            } else {
                throw new IllegalArgumentException("Carácter inválido en la expresión: " + ch);
            }
        }
        return tokens;
    }

    
    private List<String> infijoAPostfijo(List<String> tokens) throws IllegalArgumentException {
        List<String> salidaRPN = new ArrayList<>();
        Stack<String> pilaOperadores = new Stack<>();
        for (String token : tokens) {
            if (Character.isLetter(token.charAt(0))) { 
                salidaRPN.add(token);
            } else if (token.equals("(")) {
                pilaOperadores.push(token);
            } else if (token.equals(")")) {
                while (!pilaOperadores.isEmpty() && !pilaOperadores.peek().equals("(")) {
                    salidaRPN.add(pilaOperadores.pop());
                }
                if (pilaOperadores.isEmpty() || !pilaOperadores.peek().equals("(")) {
                    throw new IllegalArgumentException("Paréntesis desbalanceados o mal colocados.");
                }
                pilaOperadores.pop(); 
            } else { 
                while (!pilaOperadores.isEmpty() &&
                       ORDEN.getOrDefault(token.charAt(0), 0) <= ORDEN.getOrDefault(pilaOperadores.peek().charAt(0), 0)) {
                    salidaRPN.add(pilaOperadores.pop());
                }
                pilaOperadores.push(token);
            }
        }
        while (!pilaOperadores.isEmpty()) {
            if (pilaOperadores.peek().equals("(") || pilaOperadores.peek().equals(")")) {
                 throw new IllegalArgumentException("Paréntesis desbalanceados al final.");
            }
            salidaRPN.add(pilaOperadores.pop());
        }
        return salidaRPN;
    }

    
    private Ast construirASTDesdeRPN(List<String> rpnTokens) throws IllegalArgumentException {
        Stack<Ast> pilaAST = new Stack<>();

        for (String token : rpnTokens) {
            if (Character.isLetter(token.charAt(0))) { 
                pilaAST.push(new Variable(token));
            } else if (token.equals("!")) { 
                if (pilaAST.isEmpty()) throw new IllegalArgumentException("Expresión inválida: Falta operando para NOT.");
                Ast operando = pilaAST.pop();
                pilaAST.push(new OperacionU(Tipo.NOT, operando));
            } else if (token.equals("&") || token.equals("|")) { 
                if (pilaAST.size() < 2) throw new IllegalArgumentException("Expresión inválida: Faltan operandos para " + token);
                Ast derecho = pilaAST.pop();
                Ast izquierdo = pilaAST.pop();
                Tipo op = token.equals("&") ? Tipo.AND : Tipo.OR;
                pilaAST.push(new OperacionB(op, izquierdo, derecho));
            } else {
                 throw new IllegalArgumentException("Token desconocido en RPN: " + token);
            }
        }

        if (pilaAST.size() != 1) {
            throw new IllegalArgumentException("Expresión RPN inválida, resultado final no es un único árbol.");
        }
        return pilaAST.pop();
    }

    public Ast parse(String expresion, Set<String> nombresVariablesDefinidas) throws IllegalArgumentException {
        if (expresion == null || expresion.trim().isEmpty()) {
            throw new IllegalArgumentException("La expresión no puede estar vacía.");
        }
        if (nombresVariablesDefinidas == null || nombresVariablesDefinidas.isEmpty()) {
             throw new IllegalArgumentException("Se deben definir las variables de entrada.");
        }
        List<String> tokens = tokenizar(expresion.replaceAll("\\s+", ""), nombresVariablesDefinidas);
        if (tokens.isEmpty() && !expresion.trim().isEmpty()) { 
             throw new IllegalArgumentException("La expresión contiene solo caracteres no válidos o está mal formada.");
        }
        if (tokens.isEmpty() && expresion.trim().isEmpty()) {
             throw new IllegalArgumentException("La expresión no puede estar vacía después de procesar espacios.");
        }
        List<String> rpn = infijoAPostfijo(tokens);
        return construirASTDesdeRPN(rpn);
    }
}
