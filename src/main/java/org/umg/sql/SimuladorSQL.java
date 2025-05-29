package org.umg.sql;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class SimuladorSQL {

    private final Map<String, String> tablas = new HashMap<>();
    private final JTextArea areaSalida;

    public SimuladorSQL(JTextArea areaSalida) {
        this.areaSalida = areaSalida;
    }

    public void procesar(String codigo) {
        String[] lineas = codigo.split(";");
        for (String linea : lineas) {
            String l = linea.trim().toLowerCase();
            if (l.startsWith("create table")) {
                String nombre = extraerNombreTabla(linea);
                tablas.put(nombre, "estructura_simulada");
                areaSalida.append("Tabla '" + nombre + "' creada correctamente.\n");
            } else if (l.startsWith("insert into")) {
                String nombre = extraerNombreTabla(linea);
                areaSalida.append("Insert realizado en tabla '" + nombre + "'.\n");
            } else if (l.startsWith("select")) {
                String nombre = extraerNombreTabla(linea);
                areaSalida.append("Simulación de SELECT sobre '" + nombre + "'.\n");
            } else if (l.startsWith("update")) {
                String nombre = extraerNombreTabla(linea);
                areaSalida.append("UPDATE simulado en tabla '" + nombre + "'.\n");
            } else if (l.startsWith("delete from")) {
                String nombre = extraerNombreTabla(linea);
                areaSalida.append("DELETE simulado en tabla '" + nombre + "'.\n");
            }
        }
    }

    private String extraerNombreTabla(String linea) {
        String[] palabras = linea.trim().split("\\s+");
        for (int i = 0; i < palabras.length; i++) {
            if (palabras[i].equalsIgnoreCase("table") || palabras[i].equalsIgnoreCase("into") || palabras[i].equalsIgnoreCase("from")) {
                if (i + 1 < palabras.length) return palabras[i + 1].replaceAll("[^a-zA-Z0-9_]", "");
            }
        }
        return "desconocida";
    }
}
