package org.umg.lexer;

import org.umg.model.Token;
import org.umg.model.ErrorLSSL;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Analizador léxico del compilador TITAN.
 * Detecta tokens como identificadores, operadores, números, delimitadores y errores.
 */
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

    /**
     * Ejecuta el análisis léxico sobre el código fuente.
     * @param codigoFuente Código ingresado por el usuario
     */
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

    /**
     * Determina el tipo de token basado en los grupos nombrados del patrón.
     */
    private String getTipoToken(Matcher matcher) {
        for (String name : new String[]{
                "NUMERO", "REAL", "ID", "SUMA", "RESTA", "MULTIPLICACION", "DIVISION", "ASIGNACION", "PUNTOCOMA"}) {
            if (matcher.group(name) != null) return name;
        }
        return null;
    }

    /**
     * Clasifica el token según su tipo y valor.
     * @param lexema Valor del token
     * @param tipo Tipo detectado por regex
     */
    private String clasificar(String lexema, String tipo) {
        return switch (tipo) {
            case "ID" -> esPalabraReservada(lexema) ? "Palabra Reservada" : "Identificador";
            case "NUMERO", "REAL" -> "Constante Numérica";
            case "SUMA", "RESTA", "MULTIPLICACION", "DIVISION", "ASIGNACION" -> "Operador";
            case "PUNTOCOMA" -> "Delimitador";
            default -> "Desconocido";
        };
    }

    /**
     * Determina si un lexema es una palabra reservada del lenguaje.
     */
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
