package org.umg.parser;

import org.umg.model.Token;
import org.umg.model.ErrorLSSL;

import java.util.ArrayList;
import java.util.List;

/**
 * Analizador sintáctico que verifica declaraciones válidas del tipo:
 * int x;
 * float y;
 */
public class Parser {

    private final List<Token> tokens;
    private final List<ErrorLSSL> errores = new ArrayList<>();
    private int index = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Método principal que recorre la lista de tokens y aplica análisis sintáctico.
     */
    public void analizar() {
        while (index < tokens.size()) {
            if (!declaracion()) {
                Token t = tokens.get(index);
                errores.add(new ErrorLSSL(2,
                        "Error de sintaxis cerca de '" + t.getLexeme() + "'",
                        t.getLine(), t.getColumn()));
                index++; // evitar bucle infinito
            }
        }
    }

    /**
     * Reconoce una declaración válida del tipo: int x;
     * @return true si la declaración es válida
     */
    private boolean declaracion() {
        if (match("ID")) {
            Token tipoToken = tokens.get(index - 1);
            if (tipoToken.getLexeme().equalsIgnoreCase("int") ||
                    tipoToken.getLexeme().equalsIgnoreCase("float")) {

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
            } else {
                index--; // no era un tipo válido, revertir avance
                return false;
            }
        }
        return false;
    }

    /**
     * Intenta hacer match con el tipo de token actual.
     * @param tipoEsperado Tipo de token esperado
     * @return true si hay coincidencia
     */
    private boolean match(String tipoEsperado) {
        if (index < tokens.size() && tokens.get(index).getType().equals(tipoEsperado)) {
            index++;
            return true;
        }
        return false;
    }

    /**
     * Agrega un error con la ubicación del token actual.
     */
    private void addError(String mensaje) {
        if (index < tokens.size()) {
            Token t = tokens.get(index);
            errores.add(new ErrorLSSL(2, mensaje, t.getLine(), t.getColumn()));
        }
    }

    /**
     * Devuelve la lista de errores encontrados.
     */
    public List<ErrorLSSL> getErrores() {
        return errores;
    }
}
