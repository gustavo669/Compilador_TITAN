package org.umg.codegen;

import java.util.ArrayList;
import java.util.List;

/**
 * Generador de código objeto (ensamblador simulado) desde código intermedio.
 * Traduce líneas tipo: T1 = A + B  →  LOAD A, ADD B, STORE T1
 */
public class ObjectCodeGenerator {

    private final List<String> objeto = new ArrayList<>();

    /**
     * Recibe el código intermedio como string y lo convierte en instrucciones tipo ensamblador.
     * @param codigoIntermedio Código generado por IntermediateCodeGenerator
     */
    public void generarDesdeIntermedio(String codigoIntermedio) {
        objeto.clear();

        String[] lineas = codigoIntermedio.split("\\n");

        for (String linea : lineas) {
            if (!linea.contains("=")) continue;

            String[] partes = linea.split("=");
            if (partes.length != 2) continue; // Validación básica

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
                // Asignación directa: x = 5
                objeto.add("LOAD " + expr);
                objeto.add("STORE " + destino);
            }
        }
    }

    /**
     * Devuelve el código objeto generado como un solo string.
     */
    public String getCodigoObjeto() {
        return String.join("\n", objeto);
    }
}
