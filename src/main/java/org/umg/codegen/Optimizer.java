package org.umg.codegen;

import java.util.ArrayList;
import java.util.List;

/**
 * Optimiza el código intermedio aplicando reglas algebraicas simples.
 * Ej: x = x + 0 → x = x
 */
public class Optimizer {

    private final List<String> optimizado = new ArrayList<>();

    /**
     * Recibe el código intermedio y aplica reglas de optimización.
     * @param codigoIntermedio Código intermedio en texto plano
     */
    public void optimizar(String codigoIntermedio) {
        optimizado.clear();
        String[] lineas = codigoIntermedio.split("\\n");

        for (String linea : lineas) {
            linea = linea.trim();
            if (linea.isEmpty()) continue;

            if (!linea.contains("=")) {
                optimizado.add(linea);
                continue;
            }

            String[] partes = linea.split("=");
            if (partes.length != 2) {
                optimizado.add(linea);
                continue;
            }

            String destino = partes[0].trim();
            String expr = partes[1].trim();

            // Reglas específicas de optimización
            if (expr.equals(destino + " + 0") || expr.equals("0 + " + destino)) {
                optimizado.add(destino + " = " + destino + "    ; eliminado +0");
            } else if (expr.equals(destino + " * 1") || expr.equals("1 * " + destino)) {
                optimizado.add(destino + " = " + destino + "    ; eliminado *1");
            } else if (expr.equals(destino + " * 2") || expr.equals("2 * " + destino)) {
                optimizado.add(destino + " = " + destino + " + " + destino + "    ; reemplazado *2 por suma");
            } else if (expr.matches("0 \\+ \\w+")) {
                String[] ops = expr.split("\\+");
                optimizado.add(destino + " = " + ops[1].trim() + "    ; eliminado 0 + y");
            } else if (expr.matches("\\w+ \\+ 0")) {
                String[] ops = expr.split("\\+");
                optimizado.add(destino + " = " + ops[0].trim() + "    ; eliminado x + 0");
            } else if (expr.equals(destino + " * 0") || expr.equals("0 * " + destino)) {
                optimizado.add(destino + " = 0    ; multiplicación por 0");
            } else if (expr.equals(destino + " + " + destino)) {
                optimizado.add(destino + " = " + destino + " * 2    ; x + x → x * 2");
            } else {
                // Sin optimización aplicable
                optimizado.add(destino + " = " + expr);
            }
        }
    }

    /**
     * Devuelve el código optimizado generado.
     * @return Código como texto
     */
    public String getCodigoOptimizado() {
        return String.join("\n", optimizado);
    }
}
