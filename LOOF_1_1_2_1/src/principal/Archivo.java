/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package principal;

import Circuito.Circuito;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Archivo {
    
    public static void guardarCircuito(Circuito circuito, Main main) {
        try {
        if (circuito == null) {
            return;
        }
        
        if (!circuito.isModificado()) {
            return;
        }
        
        if (circuito.getRutaArchivo() != null) {
            guardarSilencioso(circuito, main);
            return;
        }
        
        guardarCircuitoComo(circuito, main);
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(main, 
                "Error al guardar el circuito:\n" + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static void guardarSilencioso(Circuito circuito, Main main) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(circuito.getRutaArchivo()))) {
            oos.writeObject(circuito);
            circuito.setModificado(false);
          
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(main, 
                "Error al guardar el circuito:\n" + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void guardarCircuitoComo(Circuito circuito, Main main) {
        if (circuito == null) return;
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar circuito como");
        
        if (circuito.getRutaArchivo() != null) {
            fileChooser.setSelectedFile(new File(circuito.getRutaArchivo()));
        } else {
            fileChooser.setSelectedFile(new File(circuito.getNombre() + ".cir"));
        }
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Archivos de Circuito (*.cir)", "cir");
        fileChooser.setFileFilter(filter);
        
        int userSelection = fileChooser.showSaveDialog(main);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".cir")) {
                fileToSave = new File(fileToSave.getPath() + ".cir");
            }
            
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileToSave))) {
                circuito.setNombre(fileToSave.getName().replace(".cir", ""));
                circuito.setRutaArchivo(fileToSave.getAbsolutePath());
                oos.writeObject(circuito);
                circuito.setModificado(false);
                JOptionPane.showMessageDialog(main, 
                    "Circuito guardado exitosamente", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(main, 
                    "Error al guardar el circuito:\n" + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public static Circuito cargarCircuito(Main main) {
        try {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Abrir circuito");
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Archivos de Circuito (*.cir)", "cir");
        fileChooser.setFileFilter(filter);
        
        int userSelection = fileChooser.showOpenDialog(main);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileToOpen))) {
                Circuito circuito = (Circuito) ois.readObject();
                circuito.setModificado(false);
                circuito.setRutaArchivo(fileToOpen.getAbsolutePath());
                return circuito;
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(main, 
                    "Error al cargar el circuito:\n" + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }catch (Exception ex) {
            JOptionPane.showMessageDialog(main, 
                "Error al cargar el circuito:\n" + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        
        
        
    }
}