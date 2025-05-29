package org.umg.codegen;

import org.umg.model.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * Generador de código intermedio tipo tripletas (T1 = a + b).
 * Interpreta instrucciones del tipo: a = b + c * d;
 */
public class IntermediateCodeGenerator {

    private final List<Token> tokens;
    private final StringBuilder intermedio = new StringBuilder();
    private int index = 0;
    private int tempNum = 1;

    public IntermediateCodeGenerator(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Método principal para iniciar la generación del código intermedio.
     */
    public void generar() {
        while (index < tokens.size()) {
            // Detectar patrón: ID = expresión ;
            if (match("ID") && match("ASIGNACION")) {
                Token destinoToken = getToken(index - 2); // ID está dos pasos atrás
                if (destinoToken != null) {
                    generarExpresion(destinoToken.getLexeme());
                }
            } else {
                index++;
            }
        }
    }

    /**
     * Genera código intermedio para una expresión aritmética simple o compuesta.
     * @param destino Variable que recibirá el resultado (ej. "x" en x = a + b)
     */
    private void generarExpresion(String destino) {
        List<String> expr = new ArrayList<>();

        // Recolectar todos los lexemas hasta el ;
        while (index < tokens.size() && !tokens.get(index).getType().equals("PUNTOCOMA")) {
            expr.add(tokens.get(index).getLexeme());
            index++;
        }

        match("PUNTOCOMA"); // Consumir el ;

        if (expr.isEmpty()) return;

        if (expr.size() == 1) {
            intermedio.append(destino).append(" = ").append(expr.get(0)).append("\n");
        } else {
            while (expr.size() >= 3) {
                String op1 = expr.remove(0);
                String operador = expr.remove(0);
                String op2 = expr.remove(0);
                String temp = "T" + tempNum++;
                intermedio.append(temp).append(" = ").append(op1).append(" ")
                        .append(operador).append(" ").append(op2).append("\n");
                expr.add(0, temp);
            }
            intermedio.append(destino).append(" = ").append(expr.get(0)).append("\n");
        }
    }

    /**
     * Verifica si el token actual coincide con el tipo esperado y avanza el índice.
     */
    private boolean match(String tipo) {
        if (index < tokens.size() && tokens.get(index).getType().equals(tipo)) {
            index++;
            return true;
        }
        return false;
    }

    /**
     * Devuelve el token en la posición dada o null si está fuera de rango.
     */
    private Token getToken(int i) {
        return (i >= 0 && i < tokens.size()) ? tokens.get(i) : null;
    }

    /**
     * Devuelve el código intermedio generado.
     */
    public String getCodigoIntermedio() {
        return intermedio.toString();
    }
}
