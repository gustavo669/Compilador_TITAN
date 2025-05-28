package org.umg.utils;

public class LenguajeDetector {

    public static String detectarLenguaje(String codigo) {
        String lower = codigo.toLowerCase();

        if (lower.contains("<html") || lower.contains("<body")) {
            return "HTML";
        } else if (lower.contains("#include") || lower.contains("cout <<") || lower.contains("cin >>")) {
            return "C++";
        } else if (lower.contains("def ") || lower.contains("print(")) {
            return "Python";
        } else if (lower.contains("begin") && lower.contains("end.")) {
            return "Pascal";
        } else if (lower.contains("function") || lower.contains("let ") || lower.contains("console.log")) {
            return "JavaScript";
        } else if (lower.contains("begin") && lower.contains("end;") && lower.contains("declare")) {
            return "PL/SQL";
        } else if (lower.contains("create table") || lower.contains("select *") || lower.contains("insert into")) {
            return "T-SQL";
        } else {
            return "Desconocido";
        }
    }
}
