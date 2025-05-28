/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logi;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import javax.swing.JPanel;

class pAreaCliente extends JPanel{
    public pAreaCliente() {
        super();
    }
@Override
    public Dimension getSize() {
    return super.getSize();
    }
    
    

    public void add(MiPanel p) {
        this.add(p);
        this.revalidate();
        this.repaint();    }
    
@Override
    public void revalidate() {
        super.revalidate();   
    }
    
    
@Override
    public void repaint() {
        super.repaint();
    }
    
    

    public void addComponentListener(ComponentAdapter componentAdapter) {
    super.addComponentListener(componentAdapter);
    }
    
}
