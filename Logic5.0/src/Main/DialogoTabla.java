/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Main;

import Circuit.Circuito;
import Components.Componente; // Necesario para la lista de componentes seleccionados
import logicaExpresion.Parser;
import logicaExpresion.Ast;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
// import java.util.Arrays; // No se usa directamente
// import java.util.Collections; // No se usa directamente
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
// import java.util.stream.Collectors; // No se usa directamente
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class DialogoTabla extends JDialog {

    // Referencias
    private Circuito circuitoReferencia;
    private MiPanel panelDibujoReferencia;

    // Componentes UI para Pestaña "Expresión Lógica"
    private JTextField txtInputExpresion;
    private JTextField txtInputVariables;
    private JButton btnGenerarCircuito;
    private JButton btnObtenerExpresion;
    private JTextArea areaResultadoExpresion;

    // Componentes UI para Pestaña "Tabla de Verdad"
    private JButton btnGenerarTablaVerdad;
    private JTable tablaVerdadVisual;
    private DefaultTableModel modeloTablaVerdad;

    // Constructor
    public DialogoTabla(JFrame owner, boolean modal, Circuito circuito, MiPanel panelDibujo) {
        super(owner, "Análisis Combinacional del Circuito", modal); // Título del diálogo
        this.circuitoReferencia = circuito;
        this.panelDibujoReferencia = panelDibujo;

        initComponentsDialogo(); // Inicializa y configura los componentes del diálogo

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        pack(); // Ajusta el tamaño del diálogo al contenido
        setLocationRelativeTo(owner); // Centra el diálogo respecto a la ventana principal
    }

    // Inicialización de Componentes de la UI
    private void initComponentsDialogo() {
        JTabbedPane tabbedPane = new JTabbedPane();

        // Crear y añadir pestañas
        JPanel panelPestanaExpresion = crearPanelPestanaExpresion(); // Método privado
        tabbedPane.addTab("Expresión ⇔ Circuito", null, panelPestanaExpresion, "Convertir entre expresión lógica y circuito gráfico");

        JPanel panelPestanaTablaVerdad = crearPanelPestanaTablaVerdad(); // Método privado
        tabbedPane.addTab("Tabla de Verdad", null, panelPestanaTablaVerdad, "Generar tabla de verdad para el circuito actual o selección");

        // Botón de cerrar
        JButton btnCerrarDialogo = new JButton("Cerrar");
        btnCerrarDialogo.addActionListener(e -> dispose()); // Cierra el diálogo

        JPanel panelBotonesInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotonesInferior.add(btnCerrarDialogo);

        // Configuración del layout principal del diálogo
        this.setLayout(new BorderLayout(10, 10)); // Espaciado entre componentes
        this.add(tabbedPane, BorderLayout.CENTER);
        this.add(panelBotonesInferior, BorderLayout.SOUTH);
        this.getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Margen interno
    }

    private JPanel crearPanelPestanaExpresion() {
        JPanel panelPrincipalPestana = new JPanel(new BorderLayout(10, 15)); // Mantener BorderLayout principal
        panelPrincipalPestana.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Más padding general

    // Definir una fuente para los TitledBorder
        Font titledBorderFont = UIManager.getFont("TitledBorder.font");
            if (titledBorderFont == null) {
                titledBorderFont = new Font("SansSerif", Font.BOLD, 13); // Fallback
            } else {
        // Hacerla un poco más grande y en negrita si se obtiene del UIManager
                titledBorderFont = titledBorderFont.deriveFont(Font.BOLD, titledBorderFont.getSize() + 1f);
        }

    // --- Sección: Generar Circuito desde Expresión ---
        JPanel panelEntradaExp = new JPanel(new BorderLayout(5, 10)); // Espaciado vertical añadido
        TitledBorder tbEntrada = BorderFactory.createTitledBorder("Generar Circuito a partir de Expresión Lógica");
        tbEntrada.setTitleFont(titledBorderFont);
        panelEntradaExp.setBorder(BorderFactory.createCompoundBorder(tbEntrada, BorderFactory.createEmptyBorder(10, 10, 10, 10)));


        JPanel panelCamposEntrada = new JPanel(new GridBagLayout()); // Usar GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Espaciado entre componentes del grid
        gbc.anchor = GridBagConstraints.LINE_START;

        // Etiqueta Expresión Booleana
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2; // Dar un poco de peso para el tamaño de la columna de etiquetas
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_END; // Alinear texto de la etiqueta a la derecha
        JLabel lblExpresion = new JLabel("Expresión Booleana:");
        panelCamposEntrada.add(lblExpresion, gbc);

        // Campo de Texto para Expresión Booleana
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.8; // Más peso para que el campo de texto se expanda
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.LINE_START;
        txtInputExpresion = new JTextField(35); // El tamaño de columna es preferible
        txtInputExpresion.setToolTipText("Ej: A & B | !C (Operadores: & AND, | OR, ! NOT. Usar paréntesis para precedencia)");
        panelCamposEntrada.add(txtInputExpresion, gbc);

        // Etiqueta Variables
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel lblVariables = new JLabel("Variables (ej: A,B,C):");
        panelCamposEntrada.add(lblVariables, gbc);

        // Campo de Texto para Variables
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.8;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.LINE_START;
        txtInputVariables = new JTextField(20);
        txtInputVariables.setToolTipText("Variables usadas en la expresión, separadas por coma o espacio. Deben ser letras únicas.");
        panelCamposEntrada.add(txtInputVariables, gbc);

        panelEntradaExp.add(panelCamposEntrada, BorderLayout.CENTER);

        btnGenerarCircuito = new JButton("Crear Circuito desde Expresión");
        btnGenerarCircuito.setToolTipText("Genera un nuevo diseño de circuito basado en la expresión ingresada.");
        btnGenerarCircuito.addActionListener(this::accionGenerarCircuitoDesdeExpresionDialogo);
        JPanel panelBotonGenerar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5)); // Padding vertical para el botón
        panelBotonGenerar.add(btnGenerarCircuito);
        panelEntradaExp.add(panelBotonGenerar, BorderLayout.SOUTH);
        panelPrincipalPestana.add(panelEntradaExp, BorderLayout.NORTH);


        // --- Sección: Obtener Expresión desde Circuito ---
        JPanel panelSalidaExp = new JPanel(new BorderLayout(5, 10)); // Espaciado vertical añadido
        TitledBorder tbSalida = BorderFactory.createTitledBorder("Obtener Expresión Lógica del Circuito Actual");
        tbSalida.setTitleFont(titledBorderFont);
        panelSalidaExp.setBorder(BorderFactory.createCompoundBorder(tbSalida, BorderFactory.createEmptyBorder(10, 10, 10, 10)));


        btnObtenerExpresion = new JButton("Obtener Expresión del Circuito");
        btnObtenerExpresion.setToolTipText("Analiza el circuito en el panel principal y muestra su expresión booleana.");
        btnObtenerExpresion.addActionListener(this::accionObtenerExpresionDesdeCircuitoDialogo);
        JPanel panelBotonObtener = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5)); // Padding vertical para el botón
        panelBotonObtener.add(btnObtenerExpresion);
        panelSalidaExp.add(panelBotonObtener, BorderLayout.NORTH);

        areaResultadoExpresion = new JTextArea(6, 35); // Ligeramente más alto
        areaResultadoExpresion.setEditable(false);
        areaResultadoExpresion.setLineWrap(true);
        areaResultadoExpresion.setWrapStyleWord(true);
        areaResultadoExpresion.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPaneResultado = new JScrollPane(areaResultadoExpresion);
        panelSalidaExp.add(scrollPaneResultado, BorderLayout.CENTER);
        panelPrincipalPestana.add(panelSalidaExp, BorderLayout.CENTER);

        return panelPrincipalPestana;
}

    private JPanel crearPanelPestanaTablaVerdad() {
        JPanel panelPrincipalTabla = new JPanel(new BorderLayout(10, 10));
        panelPrincipalTabla.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btnGenerarTablaVerdad = new JButton("Generar Tabla de Verdad (Circuito Actual o Selección)");
        btnGenerarTablaVerdad.setToolTipText("Calcula y muestra la tabla de verdad para el circuito o los componentes seleccionados.");
        btnGenerarTablaVerdad.addActionListener(this::accionGenerarTablaVerdadDialogo); 
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBoton.add(btnGenerarTablaVerdad);
        panelPrincipalTabla.add(panelBoton, BorderLayout.NORTH);

        modeloTablaVerdad = new DefaultTableModel();
        tablaVerdadVisual = new JTable(modeloTablaVerdad);
        tablaVerdadVisual.setFont(new Font("Monospaced", Font.PLAIN, 12));
        tablaVerdadVisual.setEnabled(false); 
        tablaVerdadVisual.getTableHeader().setReorderingAllowed(false);
        panelPrincipalTabla.add(new JScrollPane(tablaVerdadVisual), BorderLayout.CENTER);

        return panelPrincipalTabla;
    }

    // Manejadores de Acciones de los Botones
    private void accionGenerarCircuitoDesdeExpresionDialogo(ActionEvent evt) {
        String expresionStr = txtInputExpresion.getText().trim();
        String variablesStr = txtInputVariables.getText().trim().toUpperCase(); 

        if (expresionStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese una expresión booleana válida.",
                                          "Entrada Requerida", JOptionPane.WARNING_MESSAGE);
            txtInputExpresion.requestFocus();
            return;
        }
        if (variablesStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese las variables de entrada (ej: A, B, C).",
                                          "Entrada Requerida", JOptionPane.WARNING_MESSAGE);
            txtInputVariables.requestFocus();
            return;
        }

        List<String> nombresVariablesList = new ArrayList<>();
        Set<String> nombresVariablesSet = new HashSet<>(); 
        String[] varsArray = variablesStr.split("[\\s,]+");

        for (String var : varsArray) {
            if (var.length() == 1 && Character.isLetter(var.charAt(0))) {
                if (nombresVariablesSet.add(var)) { 
                    nombresVariablesList.add(var);
                }
            } else if (!var.isEmpty()) { 
                JOptionPane.showMessageDialog(this, "Nombre de variable inválido: '" + var + "'.\nLas variables deben ser letras únicas (A-Z, sin distinguir mayúsculas/minúsculas).",
                                              "Error en Variables", JOptionPane.ERROR_MESSAGE);
                txtInputVariables.requestFocus();
                return;
            }
        }
        if (nombresVariablesList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se ingresaron variables válidas para la expresión.",
                                          "Error en Variables", JOptionPane.ERROR_MESSAGE);
            txtInputVariables.requestFocus();
            return;
        }

        try {
            int resp = JOptionPane.showConfirmDialog(this.getOwner(),
                "Esta acción reemplazará el diseño actual del circuito en la ventana principal.\n¿Está seguro de que desea continuar?",
                "Confirmar Creación de Nuevo Circuito", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (resp != JOptionPane.YES_OPTION) {
                return; 
            }

            Parser parser = new Parser();
            Ast astRaiz = parser.parse(expresionStr, nombresVariablesSet); 

            if (circuitoReferencia != null && panelDibujoReferencia != null) {
                circuitoReferencia.generarCircuitoDesdeAST(astRaiz, nombresVariablesList, panelDibujoReferencia);
                JOptionPane.showMessageDialog(this, "El circuito ha sido generado exitosamente a partir de la expresión.",
                                              "Generación Completa", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error interno: La referencia al circuito o al panel de dibujo es nula.",
                                              "Error Crítico", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Error en la sintaxis de la expresión o en las variables proporcionadas:\n" + e.getMessage(),
                                          "Error de Entrada", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado al generar el circuito:\n" + e.getMessage(),
                                          "Error Inesperado", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); 
        }
    }

    private void accionObtenerExpresionDesdeCircuitoDialogo(ActionEvent evt) {
        if (circuitoReferencia == null || circuitoReferencia.getComponents().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay un circuito cargado o está vacío en el panel principal.",
                                          "Circuito No Disponible", JOptionPane.WARNING_MESSAGE);
            areaResultadoExpresion.setText(""); 
            return;
        }
        try {
            String expresionGenerada = circuitoReferencia.generarExpresionBooleana();
            areaResultadoExpresion.setText(expresionGenerada);
            areaResultadoExpresion.setCaretPosition(0); 
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, "No se pudo analizar el circuito para generar la expresión:\n" + e.getMessage(),
                                          "Error de Análisis de Circuito", JOptionPane.ERROR_MESSAGE);
            areaResultadoExpresion.setText("Error al generar expresión: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado al obtener la expresión:\n" + e.getMessage(),
                                          "Error Inesperado", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            areaResultadoExpresion.setText("Error inesperado: " + e.getMessage());
        }
    }

    private void accionGenerarTablaVerdadDialogo(ActionEvent evt) {
        if (circuitoReferencia == null) {
            JOptionPane.showMessageDialog(this, "Error interno: No se encontró la referencia al circuito principal.",
                                          "Error Crítico", JOptionPane.ERROR_MESSAGE);
            if (modeloTablaVerdad != null) { modeloTablaVerdad.setRowCount(0); modeloTablaVerdad.setColumnCount(0); }
            return;
        }

        List<Componente> componentesAAnalizar;
        String tituloAnalisis = "Tabla de Verdad del Circuito Completo";
        if (panelDibujoReferencia != null && !panelDibujoReferencia.getComponentsSeleccionados().isEmpty()) {
            componentesAAnalizar = panelDibujoReferencia.getComponentsSeleccionados();
            boolean tieneEntradas = componentesAAnalizar.stream().anyMatch(c -> c instanceof Components.Switch);
            boolean tieneSalidas = componentesAAnalizar.stream().anyMatch(c -> c instanceof Components.Led);

            if (!tieneEntradas || !tieneSalidas) {
                JOptionPane.showMessageDialog(this,
                    "La selección actual no contiene suficientes Switches (entradas) y LEDs (salidas) para generar una tabla de verdad significativa.\nSe procederá a analizar todo el circuito.",
                    "Advertencia de Selección", JOptionPane.WARNING_MESSAGE);
                componentesAAnalizar = circuitoReferencia.getComponents(); // Fallback a todo el circuito
            } else {
                tituloAnalisis = "Tabla de Verdad de la Selección Actual";
            }
        } else {
            componentesAAnalizar = circuitoReferencia.getComponents();
        }
        if (componentesAAnalizar.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay componentes en el circuito (o en la selección) para generar la tabla.","Circuito o Selección Vacía", JOptionPane.WARNING_MESSAGE);
            if (modeloTablaVerdad != null) { modeloTablaVerdad.setRowCount(0); modeloTablaVerdad.setColumnCount(0); }
            return;
        }

        try {
            List<Map<String, Boolean>> tabla = circuitoReferencia.generarDatosTablaDeVerdad(componentesAAnalizar);

            if (tabla == null || tabla.isEmpty()) {
                JOptionPane.showMessageDialog(this,"No se pudieron generar datos para la tabla de verdad.\nAsegúrese de que la selección (o el circuito completo) contenga Switches (entradas) y LEDs (salidas) conectados de forma lógica.","Tabla Vacía o Inválida", JOptionPane.INFORMATION_MESSAGE);
                if (modeloTablaVerdad != null) {
                    modeloTablaVerdad.setRowCount(0);
                    modeloTablaVerdad.setColumnCount(0);
                }
                return;
            }

            List<String> nombresEntradas = circuitoReferencia.getNombresDeSwitchesOrdenados(componentesAAnalizar);
            List<String> nombresSalidas = circuitoReferencia.getNombresDeLedsOrdenados(componentesAAnalizar);
            List<String> columnasOrdenadas = new ArrayList<>(nombresEntradas);
            columnasOrdenadas.addAll(nombresSalidas);
            modeloTablaVerdad.setColumnIdentifiers(columnasOrdenadas.toArray());
            modeloTablaVerdad.setRowCount(0); 

            for (Map<String, Boolean> filaMap : tabla) {
                Object[] filaDatos = new Object[columnasOrdenadas.size()];
                for (int i = 0; i < columnasOrdenadas.size(); i++) {

                    filaDatos[i] = filaMap.getOrDefault(columnasOrdenadas.get(i), false) ? "1" : "0";
                }
                modeloTablaVerdad.addRow(filaDatos);
            }
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, "No se pudo generar la tabla de verdad debido a una configuración inválida del circuito o selección:\n" + e.getMessage(),"Error de Análisis", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado al generar la tabla de verdad:\n" + e.getMessage(),
                                          "Error Inesperado", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}