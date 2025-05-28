/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logicaExpresiones;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Set; // Para validar variables

public class BooleanExpressionParser {

    private static final Map<Character, Integer> PRECEDENCIA = new HashMap<>();
    static {
        PRECEDENCIA.put('!', 3); // NOT
        PRECEDENCIA.put('&', 2); // AND
        PRECEDENCIA.put('|', 1); // OR
        PRECEDENCIA.put('(', 0); // Paréntesis (menor precedencia en el stack)
    }

    // Valida y tokeniza la expresión
    private List<String> tokenizar(String expresion, Set<String> nombresVariablesValidas) throws IllegalArgumentException {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < expresion.length(); i++) {
            char ch = expresion.charAt(i);

            if (Character.isWhitespace(ch)) {
                continue; // Ignorar espacios
            }

            if (Character.isLetter(ch)) { // Asumimos variables de una letra
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

    // Algoritmo Shunting-yard para convertir de infijo a postfijo (RPN)
    private List<String> infijoAPostfijo(List<String> tokens) throws IllegalArgumentException {
        List<String> salidaRPN = new ArrayList<>();
        Stack<String> pilaOperadores = new Stack<>();

        for (String token : tokens) {
            if (Character.isLetter(token.charAt(0))) { // Es una variable
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
                pilaOperadores.pop(); // Sacar el '('
            } else { // Es un operador
                while (!pilaOperadores.isEmpty() &&
                       PRECEDENCIA.getOrDefault(token.charAt(0), 0) <= PRECEDENCIA.getOrDefault(pilaOperadores.peek().charAt(0), 0)) {
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

    // Construir AST desde la expresión RPN
    private ASTNode construirASTDesdeRPN(List<String> rpnTokens) throws IllegalArgumentException {
        Stack<ASTNode> pilaAST = new Stack<>();

        for (String token : rpnTokens) {
            if (Character.isLetter(token.charAt(0))) { // Variable
                pilaAST.push(new VariableNode(token));
            } else if (token.equals("!")) { // Operador NOT
                if (pilaAST.isEmpty()) throw new IllegalArgumentException("Expresión inválida: Falta operando para NOT.");
                ASTNode operando = pilaAST.pop();
                pilaAST.push(new OperacionUnariaNode(TipoOperador.NOT, operando));
            } else if (token.equals("&") || token.equals("|")) { // Operadores AND, OR
                if (pilaAST.size() < 2) throw new IllegalArgumentException("Expresión inválida: Faltan operandos para " + token);
                ASTNode derecho = pilaAST.pop();
                ASTNode izquierdo = pilaAST.pop();
                TipoOperador op = token.equals("&") ? TipoOperador.AND : TipoOperador.OR;
                pilaAST.push(new OperacionBinariaNode(op, izquierdo, derecho));
            } else {
                 throw new IllegalArgumentException("Token desconocido en RPN: " + token);
            }
        }

        if (pilaAST.size() != 1) {
            throw new IllegalArgumentException("Expresión RPN inválida, resultado final no es un único árbol.");
        }
        return pilaAST.pop();
    }

    /**
     * Parsea una expresión booleana infija y la convierte a un Árbol de Sintaxis Abstracta (AST).
     * @param expresion La cadena de la expresión booleana (ej. "A & (B | !C)").
     * @param nombresVariablesDefinidas Un Set con los nombres de las variables válidas (ej. {"A", "B", "C"}).
     * @return El nodo raíz del AST.
     * @throws IllegalArgumentException Si la expresión es inválida o contiene variables no definidas.
     */
    public ASTNode parse(String expresion, Set<String> nombresVariablesDefinidas) throws IllegalArgumentException {
        if (expresion == null || expresion.trim().isEmpty()) {
            throw new IllegalArgumentException("La expresión no puede estar vacía.");
        }
        if (nombresVariablesDefinidas == null || nombresVariablesDefinidas.isEmpty()) {
             throw new IllegalArgumentException("Se deben definir las variables de entrada.");
        }

        List<String> tokens = tokenizar(expresion.replaceAll("\\s+", ""), nombresVariablesDefinidas);
        if (tokens.isEmpty() && !expresion.trim().isEmpty()) { // Si solo eran espacios y no vacío
             throw new IllegalArgumentException("La expresión contiene solo caracteres no válidos o está mal formada.");
        }
        if (tokens.isEmpty() && expresion.trim().isEmpty()) {
             throw new IllegalArgumentException("La expresión no puede estar vacía después de procesar espacios.");
        }

        List<String> rpn = infijoAPostfijo(tokens);
        return construirASTDesdeRPN(rpn);
    }
}
