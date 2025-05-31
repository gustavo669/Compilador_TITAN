package org.umg.semantic;

import org.umg.model.Token;
import org.umg.model.ErrorLSSL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class SemanticAnalyzer {

    private final List<Token> tokens;
    private final List<ErrorLSSL> errores = new ArrayList<>();
    private final HashMap<String, String> simbolos = new HashMap<>();

    private int index = 0;

    private static final Set<String> TIPOS_VALIDOS = Set.of(
            "INT", "FLOAT", "DOUBLE", "CHAR", "BOOLEANO", "STRING"
    );

    public SemanticAnalyzer(List<Token> tokens) {
        this.tokens = tokens != null ? tokens : new ArrayList<>();
    }

    public void analizar() {
        while (index < tokens.size()) {
            Token actual = tokens.get(index);

            if (TIPOS_VALIDOS.contains(actual.getType())) {
                String tipo = actual.getType();
                index++;

                if (match("ID")) {
                    Token id = tokens.get(index - 1);

                    if (simbolos.containsKey(id.getLexeme())) {
                        errores.add(new ErrorLSSL(3, "Identificador duplicado: '" + id.getLexeme() + "'", id.getLine(), id.getColumn()));
                    } else {
                        simbolos.put(id.getLexeme(), tipo);
                    }

                    if (match("ASIGNACION")) {
                        if (!match("NUMERO") && !match("REAL") && !match("BOOLEAN_LITERAL") && !match("CHAR") && !match("STRING") && !match("ID")) {
                            errores.add(new ErrorLSSL(3, "Asignación inválida para variable '" + id.getLexeme() + "'", id.getLine(), id.getColumn()));
                        }
                    }

                    if (!match("PUNTOCOMA")) {
                        errores.add(new ErrorLSSL(3, "Falta ';' después de la declaración de '" + id.getLexeme() + "'", id.getLine(), id.getColumn()));
                    }
                } else {
                    errores.add(new ErrorLSSL(3, "Se esperaba un identificador después del tipo", actual.getLine(), actual.getColumn()));
                }
            }

            else if (actual.getType().equals("ID")) {
                if (!simbolos.containsKey(actual.getLexeme())) {
                    errores.add(new ErrorLSSL(3, "Uso de variable no declarada: '" + actual.getLexeme() + "'", actual.getLine(), actual.getColumn()));
                }
                index++;
            }

            else {
                index++;
            }
        }
    }

    private boolean match(String tipoEsperado) {
        if (index < tokens.size() && tokens.get(index).getType().equals(tipoEsperado)) {
            index++;
            return true;
        }
        return false;
    }

    public List<ErrorLSSL> getErrores() {
        return errores;
    }

    public HashMap<String, String> getTablaSimbolos() {
        return simbolos;
    }
}
