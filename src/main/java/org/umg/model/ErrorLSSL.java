package org.umg.model;

public class ErrorLSSL {
    private int type; // 1: léxico, 2: sintáctico, 3: semántico
    private String message;
    private int line;
    private int column;

    public ErrorLSSL(int type, String message, int line, int column) {
        this.type = type;
        this.message = message;
        this.line = line;
        this.column = column;
    }

    public int getType() { return type; }
    public String getMessage() { return message; }
    public int getLine() { return line; }
    public int getColumn() { return column; }

    @Override
    public String toString() {
        String tipo = switch (type) {
            case 1 -> "LÉXICO";
            case 2 -> "SINTÁCTICO";
            case 3 -> "SEMÁNTICO";
            default -> "DESCONOCIDO";
        };
        return "Error " + tipo + ": " + message + " (línea: " + line + ", col: " + column + ")";
    }
}
