/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Circuit;

import Components.Componente;
import java.io.Serializable;
// import javax.swing.JOptionPane; // No se usa directamente en esta clase

public class Pin implements Serializable {
    private static final long serialVersionUID = 1L;
    private String tipo; // "entrada" o "salida"
    private boolean estado;
    private Componente componente; // El componente al que pertenece este pin
    private transient Conector conector; // El conector al que está unido este pin

    // Constructor
    public Pin(String tipo, Componente componente) {
        this.tipo = tipo;
        this.componente = componente;
        this.estado = false; // Estado inicial por defecto
        this.conector = null;
    }

    // Métodos de Lógica Principal / Modificación de Estado
    public void cambiarEstado(boolean nuevoEstado) {
        this.estado = nuevoEstado;
        // Si este es un pin de SALIDA y está conectado, propagar el nuevo estado.
        if (conector != null && "salida".equals(tipo)) {
            conector.propagarEstado();
        }
        // Si es un pin de ENTRADA y su estado cambia, el componente padre podría necesitar reevaluarse,
        // pero la propagación desde la fuente (a través de propagarEstado del conector) ya debería
        // haber invocado evaluar() en el componente de este pin de entrada.
        // No se necesita componente.evaluar() aquí directamente por un cambio de estado en pin de entrada,
        // ya que el cambio debe venir de una fuente externa.
    }

    public void conectarA(Conector conector) {
        try {
            if (conector == null) {
                throw new Exception("El conector no puede ser nulo."); // Mensaje claro
            }

            // Un pin de entrada solo puede tener una conexión.
            if ("entrada".equals(tipo) && this.conector != null && this.conector != conector) {
                // Comprobación adicional: si se intenta conectar a un conector diferente mientras ya está conectado.
                throw new Exception("El pin de entrada ya está conectado a otro conector."); // Mensaje claro
            }

            this.conector = conector;

            // Si este es un pin de ENTRADA, actualizar su estado según la salida a la que se conecta
            // y evaluar el componente al que pertenece.
            if ("entrada".equals(tipo)) {
                if (conector.obtenerPinSalida() != null) { // Asegurarse que el otro extremo del conector está definido
                    this.estado = conector.obtenerPinSalida().obtenerEstado();
                } else {
                    this.estado = false; // O un estado por defecto si el pin de salida no está listo
                }
                if (componente != null) {
                    componente.evaluar(); // El componente necesita reaccionar al nuevo estado de entrada
                }
            }
        } catch (Exception e) {
            if (componente != null && componente.getCircuito() != null) {
                componente.getCircuito().mostrarError(e.getMessage());
            }
            // else { JOptionPane.showMessageDialog(null, e.getMessage(), "Error en Pin", JOptionPane.ERROR_MESSAGE); }
        }
    }

    public void desconectar() {
        // Ajuste en los mensajes de depuración para mayor claridad
        System.out.println("[DEBUG] Pin.desconectar: Iniciando para pin de tipo '" + tipo +
                           "' en componente: " + (componente != null ? componente.getId() : "N/A"));

        if ("entrada".equals(tipo)) {
            this.estado = false; // Los pines de entrada desconectados suelen ser FALSE lógicamente
            System.out.println("[DEBUG] Pin.desconectar: Estado de entrada reestablecido a false.");

            if (componente != null) {
                System.out.println("[DEBUG] Pin.desconectar: Evaluando componente: " +
                                   (componente.getId() != null ? componente.getId() : "N/A"));
                componente.evaluar(); // Reevaluar el componente ya que una de sus entradas cambió
            }
        }
        // Para un pin de salida, su estado no cambia al desconectarse, pero ya no propaga.
        this.conector = null; // Eliminar la referencia al conector
    }

    // Getters y Setters
    public boolean obtenerEstado() {
        return estado;
    }

    public String getTipo() {
        return tipo;
    }

    public Componente getComponente() {
        return componente;
    }

    public void setComponente(Componente componente) { // Usado raramente, usualmente se define en constructor
        this.componente = componente;
    }

    public Conector getConector() {
        return conector;
    }

}