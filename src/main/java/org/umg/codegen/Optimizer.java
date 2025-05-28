package org.umg.codegen;

import java.util.ArrayList;
import java.util.List;

public class Optimizer {

    private final List<String> optimizado = new ArrayList<>();

    public void optimizar(String codigoIntermedio) {
        String[] lineas = codigoIntermedio.split("\n");

        for (String linea : lineas) {
            linea = linea.trim();
            if (linea.isEmpty()) continue;

            if (linea.contains("=")) {
                String[] partes = linea.split("=");
                String destino = partes[0].trim();
                String expresion = partes[1].trim();

                // Regla: x = x + 0 / x = 0 + x
                if (expresion.matches(destino + " \\+ 0") || expresion.matches("0 \\+ " + destino)) {
                    optimizado.add(destino + " = " + destino + "    ; eliminado +0");
                }

                // Regla: x = x * 1 / x = 1 * x
                else if (expresion.matches(destino + " \\* 1") || expresion.matches("1 \\* " + destino)) {
                    optimizado.add(destino + " = " + destino + "    ; eliminado *1");
                }

                // Regla: x = x * 2 → x + x
                else if (expresion.matches(destino + " \\* 2") || expresion.matches("2 \\* " + destino)) {
                    optimizado.add(destino + " = " + destino + " + " + destino + "    ; reemplazado *2 por suma");
                }

                // Regla: x = 0 + y → x = y
                else if (expresion.matches("0 \\+ \\w+")) {
                    String y = expresion.split("\\+")[1].trim();
                    optimizado.add(destino + " = " + y + "    ; eliminado 0 + y");
                }

                else {
                    optimizado.add(linea);
                }
            } else {
                optimizado.add(linea);
            }
        }
    }

    public String getCodigoOptimizado() {
        return String.join("\n", optimizado);
    }
}
