package org.umg.parser;

import org.umg.model.Token;
import org.umg.model.ErrorLSSL;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Parser {

    private static final Set<String> TIPOS_DATOS = Set.of(
            "INT", "FLOAT", "DOUBLE", "CHAR", "BOOLEANO", "STRING"
    );

    private static final String ID = "ID";
    private static final String PUNTOCOMA = "PUNTOCOMA";
    private static final String ASIGNACION = "ASIGNACION";
    private static final String NUMERO = "NUMERO";
    private static final String REAL = "REAL";

    private final List<Token> tokens;
    private final List<ErrorLSSL> errores = new ArrayList<>();
    private int index = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens != null ? tokens : new ArrayList<>();
    }

    public void analizar() {
        while (!isAtEnd()) {
            if (!declaracion()) {
                recuperarError();
            }
        }
    }

    private boolean declaracion() {
        int inicioDeclaracion = index;

        if (esTipoValido()) {
            String tipo = currentToken().getType();
            advance();

            if (match(ID)) {
                String nombreVariable = previousToken().getLexeme();

                if (match(ASIGNACION)) {
                    if (!valor()) {
                        addError("Se esperaba un valor después de '='");
                        return false;
                    }
                }

                if (match(PUNTOCOMA)) {
                    return true;
                } else {
                    addError("Se esperaba ';' al final de la declaración de variable '" + nombreVariable + "'");
                    return false;
                }
            } else {
                addError("Se esperaba un identificador después del tipo '" + tipo + "'");
                return false;
            }
        }

        index = inicioDeclaracion;
        return false;
    }

    private boolean esTipoValido() {
        return !isAtEnd() && TIPOS_DATOS.contains(currentToken().getType());
    }

    private boolean valor() {
        return match(NUMERO) || match(REAL) || match(ID);
    }

    private boolean match(String tipoEsperado) {
        if (check(tipoEsperado)) {
            advance();
            return true;
        }
        return false;
    }

    private boolean check(String tipo) {
        if (isAtEnd()) return false;
        return currentToken().getType().equals(tipo);
    }

    private Token advance() {
        if (!isAtEnd()) index++;
        return previousToken();
    }

    private boolean isAtEnd() {
        return index >= tokens.size();
    }

    private Token currentToken() {
        return isAtEnd() ? null : tokens.get(index);
    }

    private Token previousToken() {
        return tokens.get(index - 1);
    }

    private void addError(String mensaje) {
        Token token = isAtEnd()
                ? (tokens.isEmpty() ? null : tokens.get(tokens.size() - 1))
                : currentToken();

        if (token != null) {
            errores.add(new ErrorLSSL(2, mensaje, token.getLine(), token.getColumn()));
        } else {
            errores.add(new ErrorLSSL(2, mensaje + " (fin de archivo)", -1, -1));
        }
    }

    private void recuperarError() {
        Token token = currentToken();
        if (token != null) {
            errores.add(new ErrorLSSL(2,
                    "Error de sintaxis cerca de '" + token.getLexeme() + "'",
                    token.getLine(), token.getColumn()));
        }

        while (!isAtEnd() && !check(PUNTOCOMA)) {
            advance();
        }

        if (check(PUNTOCOMA)) {
            advance();
        }
    }

    public List<ErrorLSSL> getErrores() {
        return new ArrayList<>(errores);
    }
}
