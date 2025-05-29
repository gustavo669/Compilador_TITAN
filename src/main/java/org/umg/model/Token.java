package org.umg.model;

public class Token {

    private final String lexeme;         // Valor textual del token (ej: "x", "5", "+")
    private final String type;           // Tipo léxico (ej: ID, NUMERO, SUMA)
    private final int line;              // Línea del código fuente
    private final int column;            // Columna en la línea
    private final String clasificacion;  // Clasificación semántica (ej: Identificador, Operador)

    public Token(String lexeme, String type, int line, int column, String clasificacion) {
        this.lexeme = lexeme;
        this.type = type;
        this.line = line;
        this.column = column;
        this.clasificacion = clasificacion;
    }

    public String getLexeme() {
        return lexeme;
    }

    public String getType() {
        return type;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getClasificacion() {
        return clasificacion;
    }

    @Override
    public String toString() {
        return "Lexema: " + lexeme +
                ", Tipo: " + type +
                ", Clase: " + clasificacion +
                ", Línea: " + line +
                ", Columna: " + column;
    }
}
