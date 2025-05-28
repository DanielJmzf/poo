/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Main;

import Circuit.Circuito;
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
                JOptionPane.showMessageDialog(main, "No hay un circuito activo para guardar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!circuito.isModificado()) {
                JOptionPane.showMessageDialog(main, "El circuito no ha sido modificado desde el último guardado.", "Información", JOptionPane.INFORMATION_MESSAGE);
                return; 
            }

            if (circuito.getRutaArchivo() != null) {
                guardar(circuito, main);
                return;
            }
           
            guardarCircuitoComo(circuito, main);

        } catch (Exception excp) { 
            JOptionPane.showMessageDialog(main,
                "Ocurrió un error inesperado al intentar guardar el circuito:\n" + excp.getMessage(),
                "Error General de Guardado", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void guardarCircuitoComo(Circuito circuito, Main main) {
        if (circuito == null) {
            JOptionPane.showMessageDialog(main, "No hay circuito para guardar.", "Operación no posible", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar circuito como...");
        if (circuito.getRutaArchivo() != null) {
            fileChooser.setSelectedFile(new File(circuito.getRutaArchivo()));
        } else {
            String nombreSugerido = circuito.getNombre() != null && !circuito.getNombre().isEmpty() ? circuito.getNombre() : "MiCircuito";
            fileChooser.setSelectedFile(new File(nombreSugerido + ".cir"));
        }
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Archivos de Circuito (*.cir)", "cir");
        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false);
        int userSelection = fileChooser.showSaveDialog(main);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".cir")) {
                fileToSave = new File(filePath + ".cir");
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileToSave))) {
                   String fileNameWithoutExt = fileToSave.getName().replaceFirst("[.][^.]+$", "");
                circuito.setNombre(fileNameWithoutExt);
                circuito.setRutaArchivo(fileToSave.getAbsolutePath());

                oos.writeObject(circuito);
                circuito.setModificado(false); 

                JOptionPane.showMessageDialog(main, "Circuito guardado exitosamente en:\n" + fileToSave.getAbsolutePath(),"Guardado Exitoso", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException excp) {
                JOptionPane.showMessageDialog(main, "Error al guardar el archivo del circuito:\n" + excp.getMessage(),"Error de Escritura", JOptionPane.ERROR_MESSAGE);
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
            fileChooser.setAcceptAllFileFilterUsed(false);

            int userSelection = fileChooser.showOpenDialog(main);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToOpen = fileChooser.getSelectedFile();
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileToOpen))) {
                    Circuito circuitoCargado = (Circuito) ois.readObject();
                    if (circuitoCargado != null) {
                        circuitoCargado.setModificado(false);
                        circuitoCargado.setRutaArchivo(fileToOpen.getAbsolutePath());

                        return circuitoCargado;
                    } else {
                        JOptionPane.showMessageDialog(main, "El archivo de circuito está corrupto o vacío.",
                                                      "Error de Carga", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException | ClassNotFoundException excp) {
                    JOptionPane.showMessageDialog(main, "Error al leer o interpretar el archivo del circuito:\n" + excp.getMessage(),
                                                  "Error de Carga", JOptionPane.ERROR_MESSAGE);
                }
            }
            return null; // Si el usuario cancela o hay un error
        } catch (Exception excp) { // Captura genérica para problemas con JFileChooser u otros
            JOptionPane.showMessageDialog(main, "Ocurrió un error inesperado al intentar cargar el circuito:\n" + excp.getMessage(),
                                          "Error General de Carga", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    // Métodos Privados Ayudantes
    private static void guardar(Circuito circuito, Main main) {
        // Este método ahora asume que circuito.getRutaArchivo() NO es null
        if (circuito == null || circuito.getRutaArchivo() == null) {
             JOptionPane.showMessageDialog(main, "Error interno: Intento de guardado rápido sin ruta de archivo definida.",
                                          "Error de Guardado", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(circuito.getRutaArchivo()))) {
            oos.writeObject(circuito);
            circuito.setModificado(false);
            JOptionPane.showMessageDialog(main, "Circuito guardado en:\n" + circuito.getRutaArchivo(),"Guardado Rápido", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException excp) {
            JOptionPane.showMessageDialog(main,
                "Error al guardar el circuito en la ruta existente:\n" + excp.getMessage(),
                "Error de Escritura", JOptionPane.ERROR_MESSAGE);
        }
    }
}