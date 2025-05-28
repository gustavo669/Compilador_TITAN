package org.umg.parser;

import org.umg.model.Token;
import org.umg.model.ErrorLSSL;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final List<Token> tokens;
    private final List<ErrorLSSL> errores = new ArrayList<>();
    private int index = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void analizar() {
        while (index < tokens.size()) {
            if (!declaracion()) {
                Token t = tokens.get(index);
                errores.add(new ErrorLSSL(2, "Error de sintaxis cerca de '" + t.getLexeme() + "'", t.getLine(), t.getColumn()));
                index++; // avanzar para evitar bucle infinito
            }
        }
    }

    private boolean declaracion() {
        if (match("INT") || match("FLOAT")) {
            if (match("ID")) {
                if (match("PUNTOCOMA")) {
                    return true;
                } else {
                    addError("Falta ';' al final de la declaración");
                    return false;
                }
            } else {
                addError("Se esperaba un identificador después del tipo de dato");
                return false;
            }
        }
        return false;
    }

    private boolean match(String tipoEsperado) {
        if (index < tokens.size() && tokens.get(index).getType().equals(tipoEsperado)) {
            index++;
            return true;
        }
        return false;
    }

    private void addError(String msg) {
        if (index < tokens.size()) {
            Token t = tokens.get(index);
            errores.add(new ErrorLSSL(2, msg, t.getLine(), t.getColumn()));
        }
    }

    public List<ErrorLSSL> getErrores() {
        return errores;
    }
}
