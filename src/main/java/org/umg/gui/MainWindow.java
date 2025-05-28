package org.umg.gui;

import org.umg.codegen.IntermediateCodeGenerator;
import org.umg.codegen.ObjectCodeGenerator;
import org.umg.codegen.Optimizer;
import org.umg.lexer.Lexer;
import org.umg.model.ErrorLSSL;
import org.umg.model.Token;
import org.umg.parser.Parser;
import org.umg.semantic.SemanticAnalyzer;
import org.umg.sql.SimuladorSQL;
import org.umg.utils.LenguajeDetector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class MainWindow extends javax.swing.JFrame {

    private final JTextArea inputArea;
    private final JTextArea outputTokens;
    private final JTextArea outputErrorsLex;
    private final JTextArea outputErrorsSint;
    private final JTextArea outputErrorsSem;
    private final JTextArea outputIntermedio;
    private final JTextArea outputOptimizado;
    private final JTextArea outputObjeto;
    private final JTextArea outputLenguaje;
    private final JButton btnAnalizar;
    private final JButton btnCargarArchivo;
    private final JTextArea outputSQL;
    private final JButton btnExportar;


    public MainWindow() {
        setTitle("TITAN - Compilador");
        setSize(1200, 1000);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        inputArea = new JTextArea(10, 50);
        outputTokens = new JTextArea(10, 30);
        outputErrorsLex = new JTextArea(10, 30);
        outputErrorsSint = new JTextArea(10, 30);
        outputErrorsSem = new JTextArea(10, 30);
        outputIntermedio = new JTextArea(10, 30);
        outputOptimizado = new JTextArea(10, 30);
        outputObjeto = new JTextArea(10, 30);
        outputLenguaje = new JTextArea(10, 30);
        btnAnalizar = new JButton("Analizar Código");
        btnCargarArchivo = new JButton("Cargar Archivo .txt");


        // Deshabilitar edición
        outputTokens.setEditable(false);
        outputErrorsLex.setEditable(false);
        outputErrorsSint.setEditable(false);
        outputErrorsSem.setEditable(false);
        outputIntermedio.setEditable(false);
        outputOptimizado.setEditable(false);
        outputObjeto.setEditable(false);
        outputLenguaje.setEditable(false);

        // Panel superior
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Código Fuente"));
        topPanel.add(new JScrollPane(inputArea), BorderLayout.CENTER);

        // Panel central
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(6, 2));
        centerPanel.add(createPanel("Tokens Reconocidos", outputTokens));
        centerPanel.add(createPanel("Errores Léxicos", outputErrorsLex));
        centerPanel.add(createPanel("Errores Sintácticos", outputErrorsSint));
        centerPanel.add(createPanel("Errores Semánticos", outputErrorsSem));
        centerPanel.add(createPanel("Código Intermedio", outputIntermedio));
        centerPanel.add(createPanel("Código Optimizado", outputOptimizado));
        centerPanel.add(createPanel("Código Objeto", outputObjeto));
        centerPanel.add(createPanel("Lenguaje Detectado", outputLenguaje));


        outputSQL = new JTextArea(10, 30);
        outputSQL.setEditable(false);
        centerPanel.setLayout(new GridLayout(7, 2));
        centerPanel.add(createPanel("Simulación SQL", outputSQL));

        btnExportar = new JButton("Exportar Análisis");

        // Panel inferior
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(btnAnalizar);
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        btnAnalizar.addActionListener(this::analizarCodigo);
        bottomPanel.add(btnCargarArchivo);

        btnCargarArchivo.addActionListener(this::cargarArchivo);

        bottomPanel.add(btnExportar);
        btnExportar.addActionListener(this::exportarAnalisis);

    }

    private void cargarArchivo(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivo de código fuente (.txt)");

        int resultado = fileChooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            try (java.util.Scanner scanner = new java.util.Scanner(archivo)) {
                StringBuilder contenido = new StringBuilder();
                while (scanner.hasNextLine()) {
                    contenido.append(scanner.nextLine()).append("\n");
                }
                inputArea.setText(contenido.toString());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al leer el archivo:\n" + ex.getMessage());
            }
        }
    }

    private JPanel createPanel(String title, JTextArea area) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }

    private void ejecutarHTML(String codigoHTML) {
        try {
            File archivo = File.createTempFile("preview_", ".html");
            archivo.deleteOnExit();
            try (FileWriter writer = new FileWriter(archivo)) {
                writer.write(codigoHTML);
            }
            Desktop.getDesktop().browse(archivo.toURI());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al abrir el navegador:\n" + ex.getMessage());
        }
    }

    private void analizarCodigo(ActionEvent e) {
        // Limpiar áreas
        outputTokens.setText("");
        outputErrorsLex.setText("");
        outputErrorsSint.setText("");
        outputErrorsSem.setText("");
        outputIntermedio.setText("");
        outputOptimizado.setText("");
        outputObjeto.setText("");
        outputLenguaje.setText("");

        // Código fuente actual
        String codigoFuente = inputArea.getText();

        // Detección de lenguaje
        String lenguaje = LenguajeDetector.detectarLenguaje(codigoFuente);
        outputLenguaje.setText("Lenguaje detectado: " + lenguaje);

        // Sql Simulador
        outputSQL.setText("");
        if (lenguaje.equals("T-SQL") || lenguaje.equals("PL/SQL")) {
            SimuladorSQL simulador = new SimuladorSQL(outputSQL);
            simulador.procesar(codigoFuente);
        }

        // Análisis léxico
        Lexer lexer = new Lexer();
        lexer.analizar(codigoFuente);

        for (Token t : lexer.getTokens()) {
            outputTokens.append(t.toString() + "\n");
        }

        java.util.List<ErrorLSSL> erroresLex = lexer.getErrors();
        if (erroresLex.isEmpty()) {
            outputErrorsLex.setText("Sin errores léxicos.");
        } else {
            for (ErrorLSSL error : erroresLex) {
                outputErrorsLex.append(error.toString() + "\n");            }
        }

        // Análisis sintáctico
        Parser parser = new Parser(lexer.getTokens());
        if (erroresLex.isEmpty()) {
            parser.analizar();
            java.util.List<ErrorLSSL> erroresSint = parser.getErrores();
            if (erroresSint.isEmpty()) {
                outputErrorsSint.setText("Sin errores sintácticos.");
            } else {
                for (ErrorLSSL error : erroresSint) {
                    outputErrorsSint.append(error.toString() + "\n");                }
            }
        } else {
            outputErrorsSint.setText("Análisis sintáctico omitido por errores léxicos.");
        }

        // Análisis semántico y demás
        if (erroresLex.isEmpty() && parser.getErrores().isEmpty()) {
            SemanticAnalyzer semantic = new SemanticAnalyzer(lexer.getTokens());
            semantic.analizar();
            List<ErrorLSSL> erroresSem = semantic.getErrores();

            if (erroresSem.isEmpty()) {
                outputErrorsSem.setText("Sin errores semánticos.");

                IntermediateCodeGenerator generator = new IntermediateCodeGenerator(lexer.getTokens());
                generator.generar();
                String intermedio = generator.getCodigoIntermedio();
                outputIntermedio.setText(intermedio);

                Optimizer optimizer = new Optimizer();
                optimizer.optimizar(intermedio);
                String optimizado = optimizer.getCodigoOptimizado();
                outputOptimizado.setText(optimizado);

                ObjectCodeGenerator objGen = new ObjectCodeGenerator();
                objGen.generarDesdeIntermedio(optimizado);
                outputObjeto.setText(objGen.getCodigoObjeto());

                // Detección HTML y ejecución
                String codigoMin = codigoFuente.toLowerCase();
                if (codigoMin.contains("<html") && codigoMin.contains("<body") && codigoMin.contains("</html>")) {
                    int opcion = JOptionPane.showConfirmDialog(this, "Se detectó código HTML.\n¿Deseas ejecutarlo en el navegador?", "Ejecutar HTML", JOptionPane.YES_NO_OPTION);
                    if (opcion == JOptionPane.YES_OPTION) {
                        ejecutarHTML(codigoFuente);
                    }
                }


            } else {
                for (ErrorLSSL error : erroresSem) {
                    outputErrorsSem.append(error.toString() + "\n");}
                outputIntermedio.setText("Código intermedio omitido por errores semánticos.");
                outputOptimizado.setText("Optimización omitida por errores semánticos.");
                outputObjeto.setText("Código objeto omitido por errores semánticos.");
            }
        } else {
            outputErrorsSem.setText("Análisis semántico omitido por errores previos.");
            outputIntermedio.setText("Código intermedio omitido por errores previos.");
            outputOptimizado.setText("Optimización omitida por errores previos.");
            outputObjeto.setText("Código objeto omitido por errores previos.");
        }
    }

    private void exportarAnalisis(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar análisis como HTML");
        chooser.setSelectedFile(new File("analisis.html"));

        int resultado = chooser.showSaveDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = chooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(archivo)) {
                writer.write("<html><head><title>Reporte de Análisis</title></head><body>");
                writer.write("<h1>Reporte del Compilador TITAN</h1>");
                writer.write(bloqueHTML("Código Fuente", inputArea.getText()));
                writer.write(bloqueHTML("Lenguaje Detectado", outputLenguaje.getText()));
                writer.write(bloqueHTML("Tokens Reconocidos", outputTokens.getText()));
                writer.write(bloqueHTML("Errores Léxicos", outputErrorsLex.getText()));
                writer.write(bloqueHTML("Errores Sintácticos", outputErrorsSint.getText()));
                writer.write(bloqueHTML("Errores Semánticos", outputErrorsSem.getText()));
                writer.write(bloqueHTML("Código Intermedio", outputIntermedio.getText()));
                writer.write(bloqueHTML("Código Optimizado", outputOptimizado.getText()));
                writer.write(bloqueHTML("Código Objeto", outputObjeto.getText()));
                writer.write(bloqueHTML("Simulación SQL", outputSQL.getText()));
                writer.write("</body></html>");
                JOptionPane.showMessageDialog(this, "Análisis exportado exitosamente a:\n" + archivo.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar:\n" + ex.getMessage());
            }
        }
    }


    private String bloqueHTML(String titulo, String contenido) {
        return "<h2>" + titulo + "</h2><pre style='background:#f0f0f0;padding:10px;border:1px solid #ccc;'>" +
                contenido.replace("<", "&lt;").replace(">", "&gt;") +
                "</pre>";
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new MainWindow().setVisible(true));
    }
}
