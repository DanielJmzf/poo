/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package principal;

import Circuito.Circuito;
import Circuito.Conector;
import Circuito.Pin;
import Componentes.Componente;
import Compuertas.CompuertaAND;
import Compuertas.CompuertaNOT;
import Compuertas.CompuertaOR;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.BorderFactory;
import principal.MiPanel;
import Componentes.Led;
import Componentes.Led;
import Componentes.Switch;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;



/**
 *
 * @author jesus
 */
public class Main extends javax.swing.JFrame {
    private JToggleButton botonCopia;
private MiPanel p;
private Circuito circuito;
 private Componente componenteSeleccionado;

    
    public Main() {
      initComponents();
    circuito = new Circuito("Circuito 1");
    jPanel1.setLayout(new BorderLayout());
    p = new MiPanel();
    p.setCircuito(circuito);
    circuito.setPanelReferencia(p);

    jPanel1.add(p, BorderLayout.CENTER);
    jPanel1.revalidate();
    jPanel1.repaint();

    p.setBorder(BorderFactory.createEtchedBorder());
    p.setBackground(Color.white);
    jToggleButton1 = new javax.swing.JToggleButton();
    jToggleButton1.setText("Toggle Switch");
    jToggleButton1.setEnabled(false);

    // ¡Aquí está el cambio importante! Pasa la referencia de jToggleButton1 a MiPanel
    p.setToggleButtonCopia(jToggleButton1);
       setupKeyboardShortcuts();
        
    }
    
    private void setupKeyboardShortcuts() {
        // CTRL+Z para deshacer
        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK), "undo");
        getRootPane().getActionMap().put("undo", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (circuito != null && circuito.deshacer()) {
                    p.repaint();
                }
            }
        });
        
        // CTRL+Y para rehacer
        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK), "redo");
        getRootPane().getActionMap().put("redo", new AbstractAction() {
            
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (circuito != null && circuito.rehacer()) {
                    p.repaint();
                }
            }
        });
        
        // También agregar CTRL+SHIFT+Z como alternativa para rehacer
        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK), "redoAlternative");
        getRootPane().getActionMap().put("redoAlternative", getRootPane().getActionMap().get("redo"));
    }
public void actualizarControlesEntrada(Componente componente) {
         this.componenteSeleccionado = componente;
    jToggleButton1.setEnabled(componente instanceof Switch);
    if (componente instanceof Switch) {
        jToggleButton1.setSelected(((Switch)componente).getEstado());
    }
    }
 public void pegarEnPosicion(Point posicion) {
        if (p != null && circuito != null) {
            circuito.pegarDesdePortapapeles(posicion);
            p.repaint();
        }
    }
public void limpiarSeleccion() {
    componenteSeleccionado = null;
    jToggleButton1.setEnabled(false);
    p.setComponenteSeleccionado(null); 
    p.setConectorSeleccionado(null);   
    p.repaint(); 
}
  public void copiarSeleccion() {
    if (p != null && circuito != null) {
        List<Componente> seleccion = p.getComponentesSeleccionados();
        if (!seleccion.isEmpty()) {
            circuito.copiarAlPortapeles(seleccion);
            p.repaint();
        } else {
            circuito.mostrarError("Seleccione componentes para copiar");
        }
    }
}

public void pegarSeleccion() {
        if (p != null && circuito != null) {
            // Pegar en posición automática (buscará espacio libre)
            circuito.pegarDesdePortapapeles(null);
            p.repaint();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jToggleButton1 = new javax.swing.JToggleButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        menuItemAnalisisCombinacional = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 673, Short.MAX_VALUE)
        );

        jButton2.setText("NOT");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton1.setText("AND");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton3.setText("OR");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Switch");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("Led");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("Conectar");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setText("Seleccionar");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jToggleButton1.setText("Encender Switch");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        jButton8.setText("Eliminar");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setText("Copiar");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton10.setText("Pegar");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        menuItemAnalisisCombinacional.setText("Analisar Circuito");
        menuItemAnalisisCombinacional.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AnalisisCombinacionalActionPerformed(evt);
            }
        });

        jMenu1.setText("File");

        jMenuItem1.setText("Nuevo Circuito");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("Guardar");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem5.setText("Guardar Como");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);

        jMenuItem3.setText("Abrir Circuito");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuItem4.setText("Salir");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton3)
                        .addGap(251, 251, 251))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(99, 99, 99)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 68, Short.MAX_VALUE)
                        .addComponent(menuItemAnalisisCombinacional)
                        .addGap(84, 84, 84)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToggleButton1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton4)
                        .addGap(62, 62, 62)
                        .addComponent(jButton5)))
                .addGap(133, 133, 133)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton6)
                        .addGap(27, 27, 27)
                        .addComponent(jButton8))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton7)
                        .addGap(18, 18, 18)
                        .addComponent(jButton9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton10)))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton8)
                            .addComponent(jButton6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton7)
                            .addComponent(jButton9)
                            .addComponent(jButton10))
                        .addGap(3, 3, 3))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1)
                            .addComponent(jButton2)
                            .addComponent(jButton4)
                            .addComponent(jButton5)
                            .addComponent(menuItemAnalisisCombinacional))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToggleButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        circuito.agregarComponente("and", 50, 50);
        p.repaint();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        circuito.agregarComponente("not", 200, 50);
        p.repaint();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        circuito.agregarComponente("or", 150, 125);
        p.repaint();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        circuito.agregarComponente("switch", 50, 50);
         p.repaint();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
       circuito.agregarComponente("led", 50, 50);
        p.repaint();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
       p.setModo(MiPanel.Modo.CONEXION);
    limpiarSeleccion(); 
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
     p.setModo(MiPanel.Modo.SELECCION);
    limpiarSeleccion(); 
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        if (componenteSeleccionado instanceof Switch) {
        circuito.toggleSwitch((Switch)componenteSeleccionado);
        p.repaint();
    }

    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
     try {
        if (!p.getComponentesSeleccionados().isEmpty()) {
            circuito.eliminarSeleccion(p.getComponentesSeleccionados());
            p.limpiarSeleccion();
            p.repaint();
        } 
        else if (p.getConectorSeleccionado() != null) {
            circuito.eliminarConector(p.getConectorSeleccionado());
            p.limpiarSeleccion();
            p.repaint();
        } 
        else {
            throw new Exception("Seleccione componentes o conexiones primero");
        }
    } catch (Exception ex) {
        circuito.mostrarError(ex.getMessage());
    }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
       Archivo.guardarCircuito(circuito, this);
    p.repaint();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
       if (circuito != null && circuito.isModificado()) {
        int respuesta = JOptionPane.showConfirmDialog(
            this, 
            "¿Desea guardar el circuito actual antes de crear uno nuevo?", 
            "Guardar circuito", 
            JOptionPane.YES_NO_CANCEL_OPTION);
        
        if (respuesta == JOptionPane.YES_OPTION) {
            Archivo.guardarCircuito(circuito, this);
            // Si el usuario cancela el guardado, no continuamos
            if (circuito.isModificado()) return;
        } else if (respuesta == JOptionPane.CANCEL_OPTION) {
            return;
        }
    }
    
    // Crear nuevo circuito
    circuito = new Circuito("Nuevo Circuito");
    p.setCircuito(circuito);
    circuito.setPanelReferencia(p);
    p.repaint();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
                                                
    if (circuito != null && circuito.isModificado()) {
        int respuesta = JOptionPane.showConfirmDialog(
            this, 
            "¿Desea guardar el circuito actual antes de abrir otro?", 
            "Guardar circuito", 
            JOptionPane.YES_NO_CANCEL_OPTION);
        
        if (respuesta == JOptionPane.CANCEL_OPTION) {
            return;
        } else if (respuesta == JOptionPane.YES_OPTION) {
            Archivo.guardarCircuito(circuito, this);
            if (circuito.isModificado()) return;
        }
    }
    
    Circuito nuevoCircuito = Archivo.cargarCircuito(this);
    if (nuevoCircuito != null) {
        circuito = nuevoCircuito;
        p.setCircuito(circuito);
        circuito.setPanelReferencia(p);
        p.setModo(MiPanel.Modo.SELECCION);
        limpiarSeleccion();
        p.repaint();
        
        // Mensaje informativo
        JOptionPane.showMessageDialog(this, 
            "Circuito cargado. Puede modificarlo:\n" +
            "- Seleccione componentes para moverlos\n" +
            "- Use el botón Eliminar para borrar\n" +
            "- Use el modo Conexión para agregar conexiones", 
            "Instrucciones", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        if (circuito != null && circuito.isModificado()) {
        int respuesta = JOptionPane.showConfirmDialog(
            this, 
            "¿Desea guardar el circuito actual antes de salir?", 
            "Guardar circuito", 
            JOptionPane.YES_NO_CANCEL_OPTION);
        
        if (respuesta == JOptionPane.CANCEL_OPTION) {
            return;
        } else if (respuesta == JOptionPane.YES_OPTION) {
            Archivo.guardarCircuito(circuito, this);
            if (circuito.isModificado()) return;
        }
    }
    System.exit(0);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        Archivo.guardarCircuitoComo(circuito, this);
    p.repaint();
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        copiarSeleccion();
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        pegarSeleccion();
    }//GEN-LAST:event_jButton10ActionPerformed

    private void AnalisisCombinacionalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AnalisisCombinacionalActionPerformed
       // 'this' es la referencia al JFrame Main.
    // 'true' indica que el diálogo será modal (bloquea la ventana principal mientras está abierto).
    // 'circuito' es tu instancia actual del objeto Circuito.
    // 'p' es tu instancia actual de MiPanel (el panel de dibujo).

    // Verifica que las referencias no sean nulas antes de pasarlas
    if (this.circuito == null) {
        JOptionPane.showMessageDialog(this, "El objeto Circuito no está inicializado.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    if (this.p == null) { // 'p' es tu MiPanel
        JOptionPane.showMessageDialog(this, "El panel de dibujo (MiPanel) no está inicializado.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Crear y mostrar el nuevo diálogo (Asegúrate de que DialogoAnalisisBooleano.java ya exista)
    DialogoAnalisisBooleano dialogo = new DialogoAnalisisBooleano(this, true, this.circuito, this.p);
    dialogo.setVisible(true); // Esto mostrará el diálogo

    // Cuando el diálogo se cierre (si es modal), el código continuará aquí.
    // Es buena idea repintar el panel principal por si el circuito cambió.
    if (this.p != null) {
        this.p.repaint();
    }
    }//GEN-LAST:event_AnalisisCombinacionalActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JButton menuItemAnalisisCombinacional;
    // End of variables declaration//GEN-END:variables

    
}
