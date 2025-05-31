package org.umg.gui;

import com.formdev.flatlaf.FlatLightLaf;
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
import java.util.ArrayList;
import java.util.Map;

public class MainWindow extends JFrame {

    private JTextArea inputArea;
    private JTextArea outputTablaSimbolos;
    private JTextArea outputTokens;
    private JTextArea outputErrorsLex;
    private JTextArea outputErrorsSint;
    private JTextArea outputErrorsSem;
    private JTextArea outputIntermedio;
    private JTextArea outputOptimizado;
    private JTextArea outputObjeto;
    private JTextArea outputLenguaje;
    private JTextArea outputSQL;
    private JButton btnAnalizar;
    private JButton btnCargarArchivo;
    private JButton btnExportar;
    private JButton btnLimpiar;
    private AnalysisResults analysisResults;

    public MainWindow() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        configureWindow();
    }

    private void initializeComponents() {
        inputArea = createTextArea(true, "");
        outputTablaSimbolos = createTextArea(false, "");
        outputTokens = createTextArea(false, "");
        outputErrorsLex = createTextArea(false, "");
        outputErrorsSint = createTextArea(false, "");
        outputErrorsSem = createTextArea(false, "");
        outputIntermedio = createTextArea(false, "");
        outputOptimizado = createTextArea(false, "");
        outputObjeto = createTextArea(false, "");
        outputLenguaje = createTextArea(false, "");
        outputSQL = createTextArea(false, "");

        btnAnalizar = createButton("Analizar Código", "Ejecuta el análisis completo del código");
        btnCargarArchivo = createButton("Cargar Archivo", "Carga un archivo de código fuente");
        btnExportar = createButton("Exportar Reporte", "Exporta el análisis a HTML");
        btnLimpiar = createButton("Limpiar Todo", "Limpia todas las áreas de texto");

        analysisResults = new AnalysisResults();

    }

    //Configuración estándar
    private JTextArea createTextArea(boolean editable, String placeholder) {
        JTextArea area = new JTextArea();
        area.setEditable(editable);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBackground(editable ? new Color(214, 247, 250) : Color.white);
        area.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        area.setForeground(new Color(33, 37, 41));
        if (!placeholder.isEmpty()) {
            area.setText(placeholder);
            if (editable) {
                area.setForeground(new Color(4, 5, 6));
            }
        }
        return area;
    }

    private JButton createButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setPreferredSize(new Dimension(150, 40));
        button.setBackground(new Color(0, 123, 255));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 86, 179));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 123, 255));
            }
        });

        return button;
    }

    //Ventana Principal
    private void configureWindow() {
        setTitle("TITAN - Compilador");
        setSize(1400, 1000);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        getContentPane().setBackground(new Color(255, 255, 255));

    }

    //Layout
    private void setupLayout() {
        setLayout(new BorderLayout(15, 15));

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(248, 249, 250));

        JPanel topPanel = createSourceCodePanel();

        JPanel centerPanel = createResultsPanel();

        JPanel bottomPanel = createControlPanel();

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    //Panel de codigo fuente
    private JPanel createSourceCodePanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.setPreferredSize(new Dimension(0, 250));
        panel.setBackground(Color.WHITE);

        // Panel del código fuente
        JPanel panelCodigo = new JPanel(new BorderLayout());
        panelCodigo.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(108, 117, 125), 1),
                "Código Fuente", 0, 0,
                new Font("Segoe UI", Font.BOLD, 16),
                new Color(52, 58, 64)
        ));
        JScrollPane scrollCodigo = new JScrollPane(inputArea);
        scrollCodigo.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollCodigo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelCodigo.add(scrollCodigo, BorderLayout.CENTER);

        // Panel de la tabla de símbolos
        JPanel panelSimbolos = new JPanel(new BorderLayout());
        panelSimbolos.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(108, 117, 125), 1),
                "Tabla de Símbolos", 0, 0,
                new Font("Segoe UI", Font.BOLD, 16),
                new Color(52, 58, 64)
        ));

        JScrollPane scrollSimbolos = new JScrollPane(outputTablaSimbolos);
        scrollSimbolos.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollSimbolos.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelSimbolos.add(scrollSimbolos, BorderLayout.CENTER);

        panel.add(panelCodigo);
        panel.add(panelSimbolos);

        return panel;
    }

    //Panel de Resultados
    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 3, 10, 10));
        panel.setBackground(new Color(248, 249, 250));
        String[] titles = {
                "Tokens Reconocidos", "Errores Léxicos", "Errores Sintácticos",
                "Errores Semánticos", "Código Intermedio", "Código Optimizado",
                "Código Objeto", "Lenguaje Detectado", "Simulación SQL"
        };

        JTextArea[] areas = {
                outputTokens, outputErrorsLex, outputErrorsSint,
                outputErrorsSem, outputIntermedio, outputOptimizado,
                outputObjeto, outputLenguaje, outputSQL
        };

        for (int i = 0; i < titles.length; i++) {
            panel.add(createScrollablePanel(titles[i], areas[i]));
        }
        return panel;
    }

    //Panel Central
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(222, 226, 230)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setBackground(Color.WHITE);

        btnAnalizar.setBackground(new Color(40, 167, 69));
        btnAnalizar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnAnalizar.setBackground(new Color(33, 136, 56));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnAnalizar.setBackground(new Color(40, 167, 69));
            }
        });

        btnCargarArchivo.setBackground(new Color(23, 162, 184));
        btnCargarArchivo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnCargarArchivo.setBackground(new Color(19, 132, 150));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnCargarArchivo.setBackground(new Color(23, 162, 184));
            }
        });

        btnExportar.setBackground(new Color(255, 193, 7));
        btnExportar.setForeground(new Color(33, 37, 41));
        btnExportar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnExportar.setBackground(new Color(227, 172, 6));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnExportar.setBackground(new Color(255, 193, 7));
            }
        });

        btnLimpiar.setBackground(new Color(220, 53, 69));
        btnLimpiar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLimpiar.setBackground(new Color(200, 35, 51));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLimpiar.setBackground(new Color(220, 53, 69));
            }
        });

        panel.add(btnAnalizar);
        panel.add(btnCargarArchivo);
        panel.add(btnExportar);
        panel.add(btnLimpiar);

        return panel;
    }

    private JPanel createScrollablePanel(String title, JTextArea area) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                title,
                0, 0,
                new Font("Segoe UI", Font.BOLD, 13),
                new Color(73, 80, 87)
        ));
        panel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void setupEventHandlers() {
        btnAnalizar.addActionListener(this::performCompleteAnalysis);
        btnCargarArchivo.addActionListener(this::loadSourceFile);
        btnExportar.addActionListener(this::exportAnalysisReport);
        btnLimpiar.addActionListener(this::clearAllAreas);
    }

    private void clearAllAreas(ActionEvent e) {
        inputArea.setText("");
        clearOutputAreas();
        analysisResults.clear();
    }

    private void clearOutputAreas() {
        outputTokens.setText("");
        outputTablaSimbolos.setText("");
        outputErrorsLex.setText("");
        outputErrorsSint.setText("");
        outputErrorsSem.setText("");
        outputIntermedio.setText("");
        outputOptimizado.setText("");
        outputObjeto.setText("");
        outputLenguaje.setText("");
        outputSQL.setText("");
    }

    private void loadSourceFile(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivo de código fuente");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            loadFileContent(selectedFile);
        }
    }

    private void loadFileContent(File file) {
        try (java.util.Scanner scanner = new java.util.Scanner(file)) {
            StringBuilder content = new StringBuilder();
            while (scanner.hasNextLine()) {
                content.append(scanner.nextLine()).append("\n");
            }
            inputArea.setText(content.toString());
            showInfoMessage("Archivo cargado exitosamente: " + file.getName());
        } catch (Exception ex) {
            showErrorMessage("Error al cargar el archivo", ex);
        }
    }

    private void performCompleteAnalysis(ActionEvent e) {
        clearOutputAreas();
        String sourceCode = inputArea.getText().trim();

        try {
            // Detección de lenguaje
            performLanguageDetection(sourceCode);

            // Simulación SQL (si aplica)
            performSQLSimulation(sourceCode);

            // Análisis léxico
            performLexicalAnalysis(sourceCode);

            // Análisis sintáctico
            performSyntacticAnalysis();

            // Análisis semántico
            performSemanticAnalysis();

            // Generación de código (siempre se ejecuta)
            performCodeGeneration(sourceCode);

            // Detección y ejecución de HTML
            handleHTMLExecution(sourceCode);

        } catch (Exception ex) {
            showErrorMessage("Error durante el análisis", ex);
        }
    }

    private void performLanguageDetection(String sourceCode) {
        String detectedLanguage = LenguajeDetector.detectarLenguaje(sourceCode);
        analysisResults.detectedLanguage = detectedLanguage;
        outputLenguaje.setText("Lenguaje detectado: " + detectedLanguage);
    }

    private void performSQLSimulation(String sourceCode) {
        outputSQL.setText("");
        if (analysisResults.detectedLanguage != null &&
                (analysisResults.detectedLanguage.equals("T-SQL") ||
                        analysisResults.detectedLanguage.equals("PL/SQL"))) {
            SimuladorSQL simulator = new SimuladorSQL(outputSQL);
            simulator.procesar(sourceCode);
        }
    }

    private void performCodeGeneration(String sourceCode) {
        try {
            // Código intermedio
            if (!analysisResults.tokens.isEmpty()) {
                IntermediateCodeGenerator generator = new IntermediateCodeGenerator(analysisResults.tokens);
                generator.generar();
                analysisResults.intermediateCode = generator.getCodigoIntermedio();
                outputIntermedio.setText(analysisResults.intermediateCode);
            }

            // Código optimizado
            Optimizer optimizer = new Optimizer();
            optimizer.optimizar(analysisResults.intermediateCode);
            analysisResults.optimizedCode = optimizer.getCodigoOptimizado();
            outputOptimizado.setText(analysisResults.optimizedCode);

            // Código objeto
            ObjectCodeGenerator objectGenerator = new ObjectCodeGenerator();
            objectGenerator.generarDesdeIntermedio(analysisResults.optimizedCode);
            analysisResults.objectCode = objectGenerator.getCodigoObjeto();
            outputObjeto.setText(analysisResults.objectCode);

        } catch (Exception ex) {
            outputIntermedio.setText("Error en generación de código intermedio: " + ex.getMessage());
            outputOptimizado.setText("Error en optimización: " + ex.getMessage());
            outputObjeto.setText("Error en generación de código objeto: " + ex.getMessage());
        }
    }

    private void performLexicalAnalysis(String sourceCode) {
        if (sourceCode == null || sourceCode.isEmpty()) {
            outputErrorsLex.setText("No hay código fuente para analizar léxicamente.");
            return;
        }

        Lexer lexer = new Lexer();
        lexer.analizar(sourceCode);

        analysisResults.tokens = lexer.getTokens();
        analysisResults.lexicalErrors = lexer.getErrors();

        StringBuilder tokensOutput = new StringBuilder();
        for (Token token : analysisResults.tokens) {
            tokensOutput.append(token.toString()).append("\n");
        }
        outputTokens.setText(tokensOutput.toString());

        displayErrors(analysisResults.lexicalErrors, outputErrorsLex, "Sin errores léxicos.");
    }

    private void performSyntacticAnalysis() {
        if (analysisResults.tokens.isEmpty()) {
            outputErrorsSint.setText("No hay código fuente para analizar sintácticamente.");
            return;
        }

        Parser parser = new Parser(analysisResults.tokens);
        parser.analizar();
        analysisResults.syntacticErrors = parser.getErrores();

        displayErrors(analysisResults.syntacticErrors, outputErrorsSint, "Sin errores sintácticos.");
    }

    private void performSemanticAnalysis() {
        if (analysisResults.tokens == null || analysisResults.tokens.isEmpty()) {
            outputErrorsSem.setText("No hay código fuente para analizar semánticamente.");
            outputTablaSimbolos.setText("");
            return;
        }

        SemanticAnalyzer semantic = new SemanticAnalyzer(analysisResults.tokens);
        semantic.analizar();
        analysisResults.semanticErrors = semantic.getErrores();

        displayErrors(analysisResults.semanticErrors, outputErrorsSem, "Sin errores semánticos.");

        outputTablaSimbolos.setText("");
        if (semantic.getTablaSimbolos().isEmpty()) {
            outputTablaSimbolos.setText("No se detectaron símbolos.");
        } else {
            semantic.getTablaSimbolos().entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> outputTablaSimbolos.append(
                            "Variable: " + entry.getKey() + " | Tipo: " + entry.getValue() + "\n"));
        }
    }

    private void handleHTMLExecution(String sourceCode) {
        String lowerCode = sourceCode.toLowerCase();
        if (lowerCode.contains("<html") && lowerCode.contains("<body") && lowerCode.contains("</html>")) {
            int option = JOptionPane.showConfirmDialog(
                    this,
                    "Se detectó código HTML.\n¿Deseas ejecutarlo en el navegador?",
                    "Ejecutar HTML",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (option == JOptionPane.YES_OPTION) {
                executeHTML(sourceCode);
            }
        }
    }

    private void executeHTML(String htmlCode) {
        try {
            File tempFile = File.createTempFile("titan_preview_", ".html");
            tempFile.deleteOnExit();

            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(htmlCode);
            }

            Desktop.getDesktop().browse(tempFile.toURI());
        } catch (Exception ex) {
            showErrorMessage("Error al abrir el navegador", ex);
        }
    }

    private void displayErrors(List<ErrorLSSL> errors, JTextArea outputArea, String noErrorsMessage) {
        if (errors.isEmpty()) {
            outputArea.setText(noErrorsMessage);
        } else {
            StringBuilder errorOutput = new StringBuilder();
            for (ErrorLSSL error : errors) {
                errorOutput.append(error.toString()).append("\n");
            }
            outputArea.setText(errorOutput.toString());
        }
    }

    //Exportar Reporte
    private void exportAnalysisReport(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar reporte de análisis");
        chooser.setSelectedFile(new File("Reporte.html"));

        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            generateHTMLReport(file);
        }
    }

    //Generar Reporte
    private void generateHTMLReport(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(buildHTMLReport());
            showInfoMessage("Reporte exportado exitosamente a:\n" + file.getAbsolutePath());
        } catch (Exception ex) {
            showErrorMessage("Error al exportar el reporte", ex);
        }
    }

    //Contenido
    private String buildHTMLReport() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html><head>");
        html.append("<title>Reporte de Análisis - TITAN</title>");
        html.append("<meta charset='UTF-8'>");
        html.append("<style>");
        html.append("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }");
        html.append(".container { max-width: 1200px; margin: 0 auto; background: white; border-radius: 15px; padding: 30px; box-shadow: 0 10px 30px rgba(0,0,0,0.2); }");
        html.append("h1 { color: #2c3e50; text-align: center; font-size: 2.5em; margin-bottom: 10px; text-shadow: 2px 2px 4px rgba(0,0,0,0.1); }");
        html.append("h2 { color: #34495e; border-left: 4px solid #3498db; padding-left: 15px; font-size: 1.4em; margin-top: 25px; }");
        html.append("pre { background: linear-gradient(145deg, #f8f9fa, #e9ecef); padding: 20px; border: none; border-radius: 10px; overflow-x: auto; font-family: 'Consolas', 'Monaco', monospace; font-size: 13px; line-height: 1.5; box-shadow: inset 0 2px 4px rgba(0,0,0,0.1); }");
        html.append(".error { color: #e74c3c; font-weight: bold; }");
        html.append(".success { color: #27ae60; font-weight: bold; }");
        html.append(".header { text-align: center; margin-bottom: 30px; }");
        html.append(".date { color: #7f8c8d; font-style: italic; }");
        html.append(".section { margin: 20px 0; }");
        html.append("</style>");
        html.append("</head><body>");

        html.append("<div class='container'>");
        html.append("<div class='header'>");
        html.append("<h1>Reporte Compilador TITAN</h1>");
        html.append("</div>");

        html.append(createHTMLSection("Código Fuente", inputArea.getText()));
        html.append(createHTMLSection("Lenguaje Detectado", outputLenguaje.getText()));
        html.append(createHTMLSection("Tokens Reconocidos", outputTokens.getText()));
        html.append(createHTMLSection("Errores Léxicos", outputErrorsLex.getText()));
        html.append(createHTMLSection("Errores Sintácticos", outputErrorsSint.getText()));
        html.append(createHTMLSection("Errores Semánticos", outputErrorsSem.getText()));
        html.append(createHTMLSection("Tabla de Simbolos", outputTablaSimbolos.getText()));
        html.append(createHTMLSection("Código Intermedio", outputIntermedio.getText()));
        html.append(createHTMLSection("Código Optimizado", outputOptimizado.getText()));
        html.append(createHTMLSection("Código Objeto", outputObjeto.getText()));
        html.append(createHTMLSection("Simulación SQL", outputSQL.getText()));

        html.append("</div>");
        html.append("</body></html>");
        return html.toString();
    }

    private String createHTMLSection(String title, String content) {
        String escapedContent = content.replace("<", "&lt;").replace(">", "&gt;");
        return "<div class='section'><h2>" + title + "</h2>" +
                "<pre>" + escapedContent + "</pre></div>";
    }

    private void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorMessage(String message, Exception ex) {
        String fullMessage = message + ":\n" + ex.getMessage();
        JOptionPane.showMessageDialog(this, fullMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    //Clase interna para almacenar Analisis
    private static class AnalysisResults {
        String detectedLanguage;
        List<Token> tokens = new ArrayList<>();
        List<ErrorLSSL> lexicalErrors = new ArrayList<>();
        List<ErrorLSSL> syntacticErrors = new ArrayList<>();
        List<ErrorLSSL> semanticErrors = new ArrayList<>();
        String intermediateCode = "";
        String optimizedCode = "";
        String objectCode = "";

        void clear() {
            detectedLanguage = null;
            tokens.clear();
            lexicalErrors.clear();
            syntacticErrors.clear();
            semanticErrors.clear();
            intermediateCode = "";
            optimizedCode = "";
            objectCode = "";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 8);

            new MainWindow().setVisible(true);
        });
    }
}