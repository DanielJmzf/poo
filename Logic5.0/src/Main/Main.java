/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Main;

import Components.Led;
import Components.Switch;
import Components.Componente;
import Circuit.Circuito;
import Gates.And;
import Gates.Not;
import Gates.Or;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;


public class Main extends javax.swing.JFrame {
private MiPanel p;
private Circuito circuito;
 private Componente componenteSeleccionado;

    
    public Main() {
        initComponents();
        circuito = new Circuito("Circuito"); 
        jPanel1.setLayout(new BorderLayout()); 
        p = new MiPanel();
        p.setCircuit(circuito);
        circuito.setPanelReferencia(p);
    
        jPanel1.add(p, BorderLayout.CENTER); 
        jPanel1.revalidate();
        jPanel1.repaint();
    
        p.setBorder(BorderFactory.createEtchedBorder());
        p.setBackground(Color.white);
        BotonENCEn = new javax.swing.JToggleButton();
        BotonENCEn.setText("Toggle Switch");
        BotonENCEn.setEnabled(false);

       
        
    }
    public void actualizarControlesEntrada(Componente componente) {
         this.componenteSeleccionado = componente;
    BotonENCEn.setEnabled(componente instanceof Switch);
    if (componente instanceof Switch) {
        BotonENCEn.setSelected(((Switch)componente).getEstado());
    }
    }
    public void limpiarSeleccion() {
    componenteSeleccionado = null;
    BotonENCEn.setEnabled(false);
    p.setComponenteSeleccionado(null); 
    p.setConectorSeleccionado(null);   
    p.repaint(); 
}
  

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        BotonAND = new javax.swing.JButton();
        BotonNOT = new javax.swing.JButton();
        BotonOR = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        BotonSWITCH = new javax.swing.JButton();
        BotonLED = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        BotonCONECTAR = new javax.swing.JButton();
        BotonSELEC = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        BotonELIM = new javax.swing.JButton();
        BotonENCEn = new javax.swing.JToggleButton();
        BotonTABLA = new javax.swing.JToggleButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
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
            .addGap(0, 502, Short.MAX_VALUE)
        );

        jPanel2.setBackground(new java.awt.Color(204, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        BotonAND.setBackground(new java.awt.Color(204, 204, 255));
        BotonAND.setText("AND");
        BotonAND.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonANDActionPerformed(evt);
            }
        });

        BotonNOT.setBackground(new java.awt.Color(204, 204, 255));
        BotonNOT.setText("NOT");
        BotonNOT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonNOTActionPerformed(evt);
            }
        });

        BotonOR.setBackground(new java.awt.Color(204, 204, 255));
        BotonOR.setText("OR");
        BotonOR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonORActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Sitka Text", 0, 14)); // NOI18N
        jLabel1.setText("COMPUERTAS");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(BotonAND)
                        .addComponent(BotonNOT)
                        .addComponent(BotonOR)))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(BotonAND)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BotonNOT)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BotonOR)
                .addGap(15, 15, 15))
        );

        jPanel3.setBackground(new java.awt.Color(255, 204, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        BotonSWITCH.setBackground(new java.awt.Color(255, 255, 51));
        BotonSWITCH.setText("Switch");
        BotonSWITCH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonSWITCHActionPerformed(evt);
            }
        });

        BotonLED.setBackground(new java.awt.Color(255, 255, 51));
        BotonLED.setText("Led");
        BotonLED.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonLEDActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Sitka Text", 0, 14)); // NOI18N
        jLabel2.setText("OTROS");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(BotonSWITCH)
                            .addComponent(BotonLED)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jLabel2)))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BotonSWITCH)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BotonLED)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(204, 204, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        BotonCONECTAR.setBackground(new java.awt.Color(153, 255, 153));
        BotonCONECTAR.setText("Conectar");
        BotonCONECTAR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonCONECTARActionPerformed(evt);
            }
        });

        BotonSELEC.setBackground(new java.awt.Color(102, 255, 102));
        BotonSELEC.setText("Seleccionar");
        BotonSELEC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonSELECActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Sitka Text", 0, 14)); // NOI18N
        jLabel3.setText("OPCIONES");

        BotonELIM.setBackground(new java.awt.Color(255, 102, 102));
        BotonELIM.setText("Eliminar");
        BotonELIM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonELIMActionPerformed(evt);
            }
        });

        BotonENCEn.setBackground(new java.awt.Color(153, 255, 153));
        BotonENCEn.setText("Encender Switch");
        BotonENCEn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonENCEnActionPerformed(evt);
            }
        });

        BotonTABLA.setBackground(new java.awt.Color(153, 255, 153));
        BotonTABLA.setText("Expresión");
        BotonTABLA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonTABLAActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(128, 128, 128))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BotonCONECTAR, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BotonSELEC))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(BotonELIM)
                        .addGap(18, 18, 18)
                        .addComponent(BotonTABLA)
                        .addGap(0, 12, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(BotonENCEn)
                        .addContainerGap())))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BotonCONECTAR)
                    .addComponent(BotonELIM)
                    .addComponent(BotonTABLA))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BotonSELEC)
                    .addComponent(BotonENCEn))
                .addGap(36, 36, 36))
        );

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

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(80, 80, 80)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(274, Short.MAX_VALUE))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(12, 12, 12)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BotonANDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonANDActionPerformed
        circuito.agregarComponente(new And(50, 50));
        p.repaint();
    }//GEN-LAST:event_BotonANDActionPerformed

    private void BotonNOTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonNOTActionPerformed
        circuito.agregarComponente(new Not(200, 50));
        p.repaint();
    }//GEN-LAST:event_BotonNOTActionPerformed

    private void BotonORActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonORActionPerformed
        circuito.agregarComponente(new Or(150, 125));
        p.repaint();
    }//GEN-LAST:event_BotonORActionPerformed

    private void BotonSWITCHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonSWITCHActionPerformed
        Switch sw = new Switch(50, 50);
        if (p.getCircuit()!= null) {
            p.getCircuit().agregarComponente(sw);
        }
        p.repaint();
    }//GEN-LAST:event_BotonSWITCHActionPerformed

    private void BotonLEDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonLEDActionPerformed
        Led led = new Led(50, 50);
        if (p.getCircuit()!= null) {
            p.getCircuit().agregarComponente(led);
        }
        p.repaint();
    }//GEN-LAST:event_BotonLEDActionPerformed

    private void BotonCONECTARActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonCONECTARActionPerformed
        p.setModo(MiPanel.Modo.CONEXION);
        limpiarSeleccion();
    }//GEN-LAST:event_BotonCONECTARActionPerformed

    private void BotonSELECActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonSELECActionPerformed
        p.setModo(MiPanel.Modo.SELECCION);
        limpiarSeleccion();
    }//GEN-LAST:event_BotonSELECActionPerformed

    private void BotonELIMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonELIMActionPerformed
        if (p.getComponenteSeleccionado() != null) {
            System.out.println("Eliminando componente: " + p.getComponenteSeleccionado().getId());
            circuito.eliminarComponente(p.getComponenteSeleccionado());
            limpiarSeleccion();
            p.repaint();
        }
        else if (p.getConectorSeleccionado() != null) {
            System.out.println("Eliminando conector: "
                + p.getConectorSeleccionado().obtenerPinSalida().getComponente().getId()
                + " y "
                + p.getConectorSeleccionado().obtenerPinesEntrada().get(0).getComponente().getId());
            circuito.eliminarConector(p.getConectorSeleccionado());
            limpiarSeleccion();
            p.repaint();
        }
        else {
            JOptionPane.showMessageDialog(this, "Seleccione que quiere eliminar", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_BotonELIMActionPerformed

    private void BotonENCEnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonENCEnActionPerformed
        if (componenteSeleccionado instanceof Switch) {
            ((Switch)componenteSeleccionado).toggle();
            p.repaint();
        }
    }//GEN-LAST:event_BotonENCEnActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed

        if (circuito != null && circuito.isModificado()) {
            int respuesta = JOptionPane.showConfirmDialog(
                this, "¿Desea guardar el circuito actual antes de abrir otro?", "Guardar circuito", JOptionPane.YES_NO_CANCEL_OPTION);
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
            p.setCircuit(circuito);
            circuito.setPanelReferencia(p);
            p.setModo(MiPanel.Modo.SELECCION);
            limpiarSeleccion();
            p.repaint();
            JOptionPane.showMessageDialog(this, """
                                                Circuito cargado.
                                               """,
                "Informacion",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        Archivo.guardarCircuitoComo(circuito, this);
        p.repaint();
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        Archivo.guardarCircuito(circuito, this);
        p.repaint();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        if (circuito != null && circuito.isModificado()) {
            int respuesta = JOptionPane.showConfirmDialog(this, "¿Desea guardar el circuito antes de crear otro?", "Guardar circuito",
                JOptionPane.YES_NO_CANCEL_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                Archivo.guardarCircuito(circuito, this);
                if (circuito.isModificado()) return;
            } else if (respuesta == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        circuito = new Circuito("Nuevo Circuito");
        p.setCircuit(circuito);
        circuito.setPanelReferencia(p);
        p.repaint();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void BotonTABLAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonTABLAActionPerformed

    if (this.circuito == null) {
        JOptionPane.showMessageDialog(this, "El objeto Circuito no está inicializado.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    if (this.p == null) { 
        JOptionPane.showMessageDialog(this, "El panel de dibujo (MiPanel) no está inicializado.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    DialogoTabla dialogo = new DialogoTabla(this, true, this.circuito, this.p);
    dialogo.setVisible(true); // Esto mostrará el diálogo
    if (this.p != null) {
        this.p.repaint();
    }    }//GEN-LAST:event_BotonTABLAActionPerformed

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
    private javax.swing.JButton BotonAND;
    private javax.swing.JButton BotonCONECTAR;
    private javax.swing.JButton BotonELIM;
    private javax.swing.JToggleButton BotonENCEn;
    private javax.swing.JButton BotonLED;
    private javax.swing.JButton BotonNOT;
    private javax.swing.JButton BotonOR;
    private javax.swing.JButton BotonSELEC;
    private javax.swing.JButton BotonSWITCH;
    private javax.swing.JToggleButton BotonTABLA;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    // End of variables declaration//GEN-END:variables

    
}
