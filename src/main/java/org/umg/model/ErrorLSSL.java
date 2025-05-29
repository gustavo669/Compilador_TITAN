package org.umg.model;

/**
 * Clase que representa un error en el análisis del compilador.
 * Puede ser léxico, sintáctico o semántico.
 */
public class ErrorLSSL {

    private final int type; // 1: léxico, 2: sintáctico, 3: semántico
    private final String message;
    private final int line;
    private final int column;

    public ErrorLSSL(int type, String message, int line, int column) {
        this.type = type;
        this.message = message;
        this.line = line;
        this.column = column;
    }

    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    /**
     * Devuelve una representación en texto del error,
     * incluyendo el tipo, mensaje y su ubicación.
     */
    @Override
    public String toString() {
        String tipo = switch (type) {
            case 1 -> "LÉXICO";
            case 2 -> "SINTÁCTICO";
            case 3 -> "SEMÁNTICO";
            default -> "DESCONOCIDO";
        };
        return "Error " + tipo + ": " + message + " (línea: " + line + ", columna: " + column + ")";
    }
}
