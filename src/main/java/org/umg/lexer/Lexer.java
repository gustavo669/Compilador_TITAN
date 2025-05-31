package org.umg.lexer;

import org.umg.model.Token;
import org.umg.model.ErrorLSSL;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

    private final ArrayList<Token> tokens = new ArrayList<>();
    private final ArrayList<ErrorLSSL> errors = new ArrayList<>();

    private final String[] patterns = {
            "(?<IGUALDAD>==)",
            "(?<DIFERENTE>!=)",
            "(?<MENORIGUAL><=)",
            "(?<MAYORIGUAL>>=)",
            "(?<AND>&&)",
            "(?<OR>\\|\\|)",
            "(?<NOT>!)",
            "(?<MENOR><)",
            "(?<MAYOR>>)",
            "(?<LLAVEIZQ>\\{)",
            "(?<LLAVEDER>\\})",
            "(?<PARENIZQ>\\()",
            "(?<PARENDER>\\))",
            "(?<CORCHETEIZQ>\\[)",
            "(?<CORCHETEDER>\\])",
            "(?<COMA>,)",
            "(?<COMILLASDOBLES>\")",
            "(?<COMILLASSIMPLES>')",
            "(?<PUNTO>\\.)",
            "(?<PUNTOCOMA>;)",
            "(?<ASIGNACION>=)",
            "(?<SUMA>\\+)",
            "(?<RESTA>-)",
            "(?<MULTIPLICACION>\\*)",
            "(?<DIVISION>/)",
            "(?<REAL>\\b\\d+\\.\\d+\\b|\\b\\d+\\.(?!\\d))",
            "(?<NUMERO>\\b\\d+\\b)",
            "(?<NUMERAL>#)",
            "(?<ID>\\b[a-zA-Z_][a-zA-Z0-9_]*\\b)"
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
            String linea = lineas[i];
            Matcher matcher = pattern.matcher(linea);
            int posicion = 0;

            while (posicion < linea.length()) {
                if (Character.isWhitespace(linea.charAt(posicion))) {
                    posicion++;
                    continue;
                }

                matcher.region(posicion, linea.length());
                if (matcher.find() && matcher.start() == posicion) {
                    String lexema = matcher.group();
                    String tipo = getTipoToken(matcher, lexema);

                    if (tipo != null) {
                        String clasificacion = clasificar(lexema, tipo);
                        tokens.add(new Token(lexema, tipo, i + 1, posicion + 1, clasificacion));
                    } else {
                        errors.add(new ErrorLSSL(1, "Token no reconocido: " + lexema, i + 1, posicion + 1));
                    }

                    posicion = matcher.end();
                } else {
                    errors.add(new ErrorLSSL(1, "Carácter inválido: '" + linea.charAt(posicion) + "'", i + 1, posicion + 1));
                    posicion++;
                }
            }
        }
    }

    private String getTipoToken(Matcher matcher, String lexema) {
        if (matcher.group("ID") != null) {
            return switch (lexema.toLowerCase()) {
                // Tipos de datos
                case "int"    -> "INT";
                case "float"  -> "FLOAT";
                case "char"   -> "CHAR";
                case "double" -> "DOUBLE";
                case "bool", "boolean" -> "BOOLEANO";
                case "string" -> "STRING";

                // Literales
                case "true", "false" -> "BOOLEAN_LITERAL";
                case "null" -> "NULL_LITERAL";

                // Palabras clave de control
                case "if" -> "IF";
                case "else" -> "ELSE";
                case "while" -> "WHILE";
                case "for" -> "FOR";
                case "do" -> "DO";
                case "switch" -> "SWITCH";
                case "case" -> "CASE";
                case "break" -> "BREAK";
                case "continue" -> "CONTINUE";
                case "return" -> "RETURN";

                // Declaraciones
                case "void" -> "VOID";
                case "main" -> "MAIN";
                case "class" -> "CLASS";
                case "static" -> "STATIC";
                case "public" -> "PUBLIC";
                case "private" -> "PRIVATE";
                case "protected" -> "PROTECTED";
                case "new" -> "NEW";
                case "this" -> "THIS";

                // Manejo de excepciones
                case "try" -> "TRY";
                case "catch" -> "CATCH";
                case "finally" -> "FINALLY";

                default -> "ID";
            };
        }

        // Otros patrones
        for (String name : new String[]{
                "IGUALDAD", "DIFERENTE", "MENORIGUAL", "MAYORIGUAL", "AND", "OR", "NOT",
                "MENOR", "MAYOR", "LLAVEIZQ", "LLAVEDER", "PARENIZQ", "PARENDER",
                "CORCHETEIZQ", "CORCHETEDER", "COMA", "COMILLASDOBLES", "COMILLASSIMPLES", "PUNTO",
                "NUMERO", "NUMERAL", "REAL", "SUMA", "RESTA", "MULTIPLICACION",
                "DIVISION", "ASIGNACION", "PUNTOCOMA"
        }) {
            if (matcher.group(name) != null) return name;
        }

        return null;
    }

    private String clasificar(String lexema, String tipo) {
        return switch (tipo) {
            case "ID" -> esPalabraReservada(lexema) ? "Palabra Reservada" : "Identificador";

            // Tipos de datos
            case "INT", "FLOAT", "DOUBLE", "CHAR", "BOOLEANO", "VOID", "STRING" -> "Tipo de Dato";

            // Palabras clave de control
            case "IF", "ELSE", "WHILE", "FOR", "DO", "SWITCH", "CASE",
                 "BREAK", "CONTINUE", "RETURN" -> "Palabra Clave de Control";

            // Palabras clave relacionadas con clases y estructuras
            case "CLASS", "STATIC", "PUBLIC", "PRIVATE", "PROTECTED", "NEW", "THIS",
                 "TRY", "CATCH", "FINALLY", "MAIN" -> "Palabra Clave de Clase";

            // Literales
            case "BOOLEAN_LITERAL" -> "Literal Booleana";
            case "NULL_LITERAL" -> "Literal Nula";

            // Constantes numéricas
            case "NUMERO", "REAL" -> "Constante Numérica";

            // Operadores aritméticos
            case "SUMA", "RESTA", "MULTIPLICACION", "DIVISION", "ASIGNACION" -> "Operador";

            // Delimitadores y puntuación
            case "PUNTOCOMA" -> "Delimitador";

            // Operadores relacionales y lógicos
            case "IGUALDAD", "DIFERENTE", "MENORIGUAL", "MAYORIGUAL", "MENOR", "MAYOR",
                 "AND", "OR", "NOT" -> "Operador Relacional o Lógico";

            // Símbolos especiales
            case "LLAVEIZQ", "LLAVEDER", "PARENIZQ", "PARENDER",
                 "CORCHETEIZQ", "CORCHETEDER", "COMA", "NUMERAL", "PUNTO" -> "Símbolo Especial";
            case "COMILLASDOBLES", "COMILLASSIMPLES" -> "Comilla";

            default -> "Desconocido";
        };
    }


    private boolean esPalabraReservada(String lexema) {
        String[] reservadas = {
                "int", "float", "char", "double", "bool", "boolean", "void",
                "if", "else", "while", "do", "for", "switch", "case", "break", "continue",
                "return", "main", "const", "static", "public", "private", "protected",
                "true", "false", "null", "this", "new", "class", "struct",
                "try", "catch", "finally"
        };
        for (String res : reservadas) {
            if (res.equalsIgnoreCase(lexema)) return true;
        }
        return false;
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public ArrayList<ErrorLSSL> getErrors() {
        return errors;
    }
}

