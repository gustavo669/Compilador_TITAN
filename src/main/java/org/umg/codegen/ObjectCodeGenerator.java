package org.umg.codegen;

import java.util.ArrayList;
import java.util.List;

public class ObjectCodeGenerator {

    private final List<String> objeto = new ArrayList<>();

    public void generarDesdeIntermedio(String codigoIntermedio) {
        objeto.clear();

        String[] lineas = codigoIntermedio.split("\\n");

        for (String linea : lineas) {
            if (!linea.contains("=")) continue;

            String[] partes = linea.split("=");
            if (partes.length != 2) continue;

            String destino = partes[0].trim();
            String expr = partes[1].trim();

            // Expresiones con operadores
            if (expr.contains("+")) {
                String[] ops = expr.split("\\+");
                if (ops.length == 2) {
                    objeto.add("LOAD " + ops[0].trim());
                    objeto.add("ADD " + ops[1].trim());
                    objeto.add("STORE " + destino);
                }
            } else if (expr.contains("-")) {
                String[] ops = expr.split("-");
                if (ops.length == 2) {
                    objeto.add("LOAD " + ops[0].trim());
                    objeto.add("SUB " + ops[1].trim());
                    objeto.add("STORE " + destino);
                }
            } else if (expr.contains("*")) {
                String[] ops = expr.split("\\*");
                if (ops.length == 2) {
                    objeto.add("LOAD " + ops[0].trim());
                    objeto.add("MUL " + ops[1].trim());
                    objeto.add("STORE " + destino);
                }
            } else if (expr.contains("/")) {
                String[] ops = expr.split("/");
                if (ops.length == 2) {
                    objeto.add("LOAD " + ops[0].trim());
                    objeto.add("DIV " + ops[1].trim());
                    objeto.add("STORE " + destino);
                }
            } else {
                objeto.add("LOAD " + expr);
                objeto.add("STORE " + destino);
            }
        }
    }

    public String getCodigoObjeto() {
        return String.join("\n", objeto);
    }
}
