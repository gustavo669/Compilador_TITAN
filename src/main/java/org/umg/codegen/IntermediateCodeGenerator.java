package org.umg.codegen;

import org.umg.model.Token;

import java.util.ArrayList;
import java.util.List;

public class IntermediateCodeGenerator {

    private final List<Token> tokens;
    private final StringBuilder intermedio = new StringBuilder();
    private int index = 0;
    private int tempNum = 1;

    public IntermediateCodeGenerator(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void generar() {
        while (index < tokens.size()) {
            if (match("ID") && match("ASIGNACION")) {
                String destino = tokens.get(index - 2).getLexeme();
                generarExpresion(destino);
            } else {
                index++;
            }
        }
    }

    private void generarExpresion(String destino) {
        List<String> expr = new ArrayList<>();

        while (index < tokens.size() && !tokens.get(index).getType().equals("PUNTOCOMA")) {
            expr.add(tokens.get(index).getLexeme());
            index++;
        }

        if (match("PUNTOCOMA")) {
            if (expr.size() == 1) {
                intermedio.append(destino).append(" = ").append(expr.get(0)).append("\n");
            } else {
                // Generar tripletas T1, T2...
                while (expr.size() >= 3) {
                    String op1 = expr.remove(0);
                    String operador = expr.remove(0);
                    String op2 = expr.remove(0);
                    String temp = "T" + tempNum++;
                    intermedio.append(temp).append(" = ").append(op1).append(" ").append(operador).append(" ").append(op2).append("\n");
                    expr.add(0, temp);
                }
                intermedio.append(destino).append(" = ").append(expr.get(0)).append("\n");
            }
        }
    }

    private boolean match(String tipo) {
        if (index < tokens.size() && tokens.get(index).getType().equals(tipo)) {
            index++;
            return true;
        }
        return false;
    }

    public String getCodigoIntermedio() {
        return intermedio.toString();
    }
}
