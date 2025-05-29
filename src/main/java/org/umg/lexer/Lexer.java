package org.umg.lexer;

import org.umg.model.Token;
import org.umg.model.ErrorLSSL;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

    private final ArrayList<Token> tokens = new ArrayList<>();
    private final ArrayList<ErrorLSSL> errors = new ArrayList<>();

    // Expresiones regulares por tipo de token
    private final String[] patterns = {
            "(?<NUMERO>\\b\\d+\\b)",
            "(?<REAL>\\b\\d+\\.\\d+\\b)",
            "(?<ID>\\b[a-zA-Z_][a-zA-Z0-9_]*\\b)",
            "(?<SUMA>\\+)",
            "(?<RESTA>-)",
            "(?<MULTIPLICACION>\\*)",
            "(?<DIVISION>/)",
            "(?<ASIGNACION>=)",
            "(?<PUNTOCOMA>;)"
    };

    private final Pattern pattern;

    public Lexer() {
        String fullPattern = String.join("|", patterns);
        this.pattern = Pattern.compile(fullPattern);
    }

    public void analizar(String codigoFuente) {
        tokens.clear();
        errors.clear();

        String[] lineas = codigoFuente.split("\n");
        for (int i = 0; i < lineas.length; i++) {
            Matcher matcher = pattern.matcher(lineas[i]);

            while (matcher.find()) {
                String lexema = matcher.group();
                String tipo = getTipoToken(matcher);

                if (tipo != null) {
                    String clasificacion = clasificar(lexema, tipo);
                    tokens.add(new Token(lexema, tipo, i + 1, matcher.start() + 1, clasificacion));
                } else {
                    errors.add(new ErrorLSSL(1, "Token no reconocido: " + lexema, i + 1, matcher.start() + 1));
                }
            }
        }
    }

    private String getTipoToken(Matcher matcher) {
        for (String name : new String[]{
                "NUMERO", "REAL", "ID", "SUMA", "RESTA", "MULTIPLICACION", "DIVISION", "ASIGNACION", "PUNTOCOMA"}) {
            if (matcher.group(name) != null) return name;
        }
        return null;
    }


    private String clasificar(String lexema, String tipo) {
        return switch (tipo) {
            case "ID" -> esPalabraReservada(lexema) ? "Palabra Reservada" : "Identificador";
            case "NUMERO", "REAL" -> "Constante Numérica";
            case "SUMA", "RESTA", "MULTIPLICACION", "DIVISION", "ASIGNACION" -> "Operador";
            case "PUNTOCOMA" -> "Delimitador";
            default -> "Desconocido";
        };
    }

    private boolean esPalabraReservada(String lexema) {
        return switch (lexema.toLowerCase()) {
            case "int", "float", "while", "if", "else", "for", "return", "void", "main" -> true;
            default -> false;
        };
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public ArrayList<ErrorLSSL> getErrors() {
        return errors;
    }
}
