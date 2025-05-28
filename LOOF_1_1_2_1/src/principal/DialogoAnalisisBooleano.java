/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package principal; 

import Circuito.Circuito;
import Componentes.Componente; // Necesario para la lista de componentes seleccionados
import logicaExpresiones.BooleanExpressionParser;
import logicaExpresiones.ASTNode;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays; 
import java.util.Collections; 
import java.util.HashSet; 
import java.util.List;
import java.util.Map; 
import java.util.Set; 
import java.util.stream.Collectors; 
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
import javax.swing.table.DefaultTableModel; 

public class DialogoAnalisisBooleano extends JDialog {

    private Circuito circuitoReferencia; 
    private MiPanel panelDibujoReferencia;   

    private JTextField txtInputExpresion; 
    private JTextField txtInputVariables; 
    private JButton btnGenerarCircuito;    
    private JButton btnObtenerExpresion;   
    private JTextArea areaResultadoExpresion; 
    
    private JButton btnGenerarTablaVerdad;
    private JTable tablaVerdadVisual;   
    private DefaultTableModel modeloTablaVerdad;

    public DialogoAnalisisBooleano(JFrame owner, boolean modal, Circuito circuito, MiPanel panelDibujo) {
        super(owner, "Análisis Combinacional", modal); 
        this.circuitoReferencia = circuito;
        this.panelDibujoReferencia = panelDibujo;

        initComponentsDialogo(); 

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); 
        pack(); 
        setLocationRelativeTo(owner); 
    }

    private void initComponentsDialogo() {
        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel panelPestanaExpresion = crearPanelPestanaExpresion();
        tabbedPane.addTab("Expresión Lógica", null, panelPestanaExpresion, "Convertir entre expresión y circuito");

        JPanel panelPestanaTablaVerdad = crearPanelPestanaTablaVerdad();
        tabbedPane.addTab("Tabla de Verdad", null, panelPestanaTablaVerdad, "Generar tabla de verdad del circuito actual");
        
        JButton btnCerrarDialogo = new JButton("Cerrar");
        btnCerrarDialogo.addActionListener(e -> dispose()); 
        
        JPanel panelBotonesInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotonesInferior.add(btnCerrarDialogo);

        this.setLayout(new BorderLayout(10,10)); 
        this.add(tabbedPane, BorderLayout.CENTER);
        this.add(panelBotonesInferior, BorderLayout.SOUTH);
        this.getRootPane().setBorder(BorderFactory.createEmptyBorder(10,10,10,10)); 
    }

    private JPanel crearPanelPestanaExpresion() {
        JPanel panelPrincipalPestana = new JPanel(new BorderLayout(10, 15)); 
        panelPrincipalPestana.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel panelEntradaExp = new JPanel(new BorderLayout(5,5));
        panelEntradaExp.setBorder(BorderFactory.createTitledBorder("Generar Circuito desde Expresión"));
        JPanel panelCamposEntrada = new JPanel(new GridLayout(0, 2, 8, 8)); 
        
        JLabel lblExpresion = new JLabel("Expresión Booleana:", SwingConstants.RIGHT);
        panelCamposEntrada.add(lblExpresion);
        txtInputExpresion = new JTextField(35);
        txtInputExpresion.setToolTipText("Ej: A & B | !C (Operadores: & AND, | OR, ! NOT. Usar paréntesis)");
        panelCamposEntrada.add(txtInputExpresion);

        JLabel lblVariables = new JLabel("Variables (ej: A,B,C):", SwingConstants.RIGHT);
        panelCamposEntrada.add(lblVariables);
        txtInputVariables = new JTextField(20);
        panelCamposEntrada.add(txtInputVariables);
        
        panelEntradaExp.add(panelCamposEntrada, BorderLayout.CENTER);

        btnGenerarCircuito = new JButton("Crear Circuito desde Expresión"); 
        btnGenerarCircuito.addActionListener(this::accionGenerarCircuitoDesdeExpresionDialogo);
        JPanel panelBotonGenerar = new JPanel(new FlowLayout(FlowLayout.CENTER)); 
        panelBotonGenerar.add(btnGenerarCircuito);
        panelEntradaExp.add(panelBotonGenerar, BorderLayout.SOUTH);
        panelPrincipalPestana.add(panelEntradaExp, BorderLayout.NORTH);

        JPanel panelSalidaExp = new JPanel(new BorderLayout(5,5));
        panelSalidaExp.setBorder(BorderFactory.createTitledBorder("Obtener Expresión desde Circuito"));

        btnObtenerExpresion = new JButton("Obtener Expresión del Circuito Actual"); 
        btnObtenerExpresion.addActionListener(this::accionObtenerExpresionDesdeCircuitoDialogo); 
        JPanel panelBotonObtener = new JPanel(new FlowLayout(FlowLayout.CENTER)); 
        panelBotonObtener.add(btnObtenerExpresion);
        panelSalidaExp.add(panelBotonObtener, BorderLayout.NORTH);
        
        areaResultadoExpresion = new JTextArea(5, 35); 
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
        JPanel panelPrincipalTabla = new JPanel(new BorderLayout(10,10));
        panelPrincipalTabla.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        btnGenerarTablaVerdad = new JButton("Generar Tabla de Verdad del Circuito Actual");
        btnGenerarTablaVerdad.addActionListener(this::accionGenerarTablaVerdadDialogo);
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBoton.add(btnGenerarTablaVerdad);
        panelPrincipalTabla.add(panelBoton, BorderLayout.NORTH);

        modeloTablaVerdad = new DefaultTableModel();
        tablaVerdadVisual = new JTable(modeloTablaVerdad);
        tablaVerdadVisual.setFont(new Font("Monospaced", Font.PLAIN, 12));
        tablaVerdadVisual.setEnabled(false); 
        panelPrincipalTabla.add(new JScrollPane(tablaVerdadVisual), BorderLayout.CENTER);
        
        return panelPrincipalTabla;
    }

    private void accionGenerarCircuitoDesdeExpresionDialogo(ActionEvent evt) {
        String expresionStr = txtInputExpresion.getText().trim();
        String variablesStr = txtInputVariables.getText().trim().toUpperCase();

        if (expresionStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese una expresión booleana.", "Entrada Requerida", JOptionPane.WARNING_MESSAGE);
            txtInputExpresion.requestFocus();
            return;
        }
        if (variablesStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese las variables de entrada (ej: A,B,C).", "Entrada Requerida", JOptionPane.WARNING_MESSAGE);
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
                JOptionPane.showMessageDialog(this, "Variable inválida: '" + var + "'. Las variables deben ser letras únicas (A-Z).", "Variables Inválidas", JOptionPane.ERROR_MESSAGE);
                txtInputVariables.requestFocus();
                return;
            }
        }
        if (nombresVariablesList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se ingresaron variables válidas.", "Variables Inválidas", JOptionPane.ERROR_MESSAGE);
            txtInputVariables.requestFocus();
            return;
        }

        try {
            int resp = JOptionPane.showConfirmDialog(this.getOwner(), 
                             "Esto reemplazará el circuito actual en la ventana principal. ¿Desea continuar?", 
                             "Confirmar Nuevo Circuito", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (resp != JOptionPane.YES_OPTION) {
                return;
            }
            
            BooleanExpressionParser parser = new BooleanExpressionParser();
            ASTNode astRaiz = parser.parse(expresionStr, nombresVariablesSet); 

            if (circuitoReferencia != null && panelDibujoReferencia != null) {
                 circuitoReferencia.generarCircuitoDesdeAST(astRaiz, nombresVariablesList, panelDibujoReferencia);
                 JOptionPane.showMessageDialog(this, "Circuito generado exitosamente desde la expresión.", "Generación Completa", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error: Referencia al circuito o panel de dibujo es nula.", "Error Interno", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException e) { 
            JOptionPane.showMessageDialog(this, "Error en la expresión o variables: " + e.getMessage(), "Error de Entrada", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error inesperado al generar circuito: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); 
        }
    }

    private void accionObtenerExpresionDesdeCircuitoDialogo(ActionEvent evt) {
        if (circuitoReferencia == null || circuitoReferencia.getComponentes().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay circuito en el panel principal para analizar.", "Circuito Vacío", JOptionPane.WARNING_MESSAGE);
            areaResultadoExpresion.setText("");
            return;
        }
        try {
            String expresionGenerada = circuitoReferencia.generarExpresionBooleana(); 
            areaResultadoExpresion.setText(expresionGenerada);
        } catch (IllegalStateException e) { 
             JOptionPane.showMessageDialog(this, "Error al analizar circuito: " + e.getMessage(), "Error de Análisis", JOptionPane.ERROR_MESSAGE);
             areaResultadoExpresion.setText("Error al generar expresión: " + e.getMessage());
             e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error inesperado al obtener expresión: " + e.getMessage(), "Error Inesperado", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); 
            areaResultadoExpresion.setText("Error inesperado: " + e.getMessage());
        }
    }

    private void accionGenerarTablaVerdadDialogo(ActionEvent evt) {
        if (circuitoReferencia == null) {
             JOptionPane.showMessageDialog(this, "Referencia al circuito principal no encontrada.", "Error Interno", JOptionPane.ERROR_MESSAGE);
            if (modeloTablaVerdad != null) { modeloTablaVerdad.setRowCount(0); modeloTablaVerdad.setColumnCount(0); }
            return;
        }

        // Determinar qué conjunto de componentes analizar
        List<Componente> componentesAAnalizar;
        if (panelDibujoReferencia != null && !panelDibujoReferencia.getComponentesSeleccionados().isEmpty()) {
            componentesAAnalizar = panelDibujoReferencia.getComponentesSeleccionados();
            if (componentesAAnalizar.stream().noneMatch(c -> c instanceof Componentes.Led) || 
                componentesAAnalizar.stream().noneMatch(c -> c instanceof Componentes.Switch) ) {
                 JOptionPane.showMessageDialog(this, "La selección actual no contiene suficientes Switches (entradas) y LEDs (salidas) para generar una tabla de verdad coherente. Se analizará todo el circuito.", "Advertencia de Selección", JOptionPane.WARNING_MESSAGE);
                 componentesAAnalizar = circuitoReferencia.getComponentes(); // Fallback a todo el circuito
            }
        } else {
            componentesAAnalizar = circuitoReferencia.getComponentes();
        }

        if (componentesAAnalizar.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay componentes (o selección) para generar la tabla.", "Circuito Vacío", JOptionPane.WARNING_MESSAGE);
            if (modeloTablaVerdad != null) { modeloTablaVerdad.setRowCount(0); modeloTablaVerdad.setColumnCount(0); }
            return;
        }

        try {
            List<Map<String, Boolean>> tabla = circuitoReferencia.generarDatosTablaDeVerdad(componentesAAnalizar); 

            if (tabla == null || tabla.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se pudieron generar datos para la tabla de verdad.\nAsegúrese de que la selección (o el circuito) contenga Switches (entradas) y LEDs (salidas) conectados lógicamente.", "Tabla Vacía o Inválida", JOptionPane.INFORMATION_MESSAGE);
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
             JOptionPane.showMessageDialog(this, "Error al generar tabla de verdad: " + e.getMessage(), "Error de Análisis", JOptionPane.ERROR_MESSAGE);
             e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error inesperado al generar tabla de verdad: " + e.getMessage(), "Error Inesperado", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); 
        }
    }
}