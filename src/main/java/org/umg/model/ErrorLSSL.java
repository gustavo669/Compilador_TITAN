package org.umg.model;

public class ErrorLSSL {

    private final int type;
    private final String message;
    private final int line;
    private final int column;

    public ErrorLSSL(int type, String message, int line, int column) {
        this.type = type;
        this.message = message;
        this.line = line;
        this.column = column;
    }

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
