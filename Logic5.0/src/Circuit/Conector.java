/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Circuit;

import Components.Componente;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane; // Aunque no se usa directamente, estaba en el original

public class Conector implements Serializable {
    private static final long serialVersionUID = 1L;

    private transient Pin pinSalida;
    private transient Pin pinEntrada;

    // Clase Interna Estática (si se usa para datos)
    public static class ConnectionData implements Serializable {
        private static final long serialVersionUID = 1L; // Buena práctica darle su propio serialVersionUID
        String idComponente;
        int indexPin;

        public ConnectionData(String idComponente, int indexPin) {
            this.idComponente = idComponente;
            this.indexPin = indexPin;
        }
    }

    // Constructor
    public Conector() {
        this.pinSalida = null;
        this.pinEntrada = null;
    }

    // Métodos Públicos Principales
    public boolean conectar(Pin pinOrigen, Pin pinDestino) {
        try {
            if (pinOrigen == null || pinDestino == null) {
                throw new Exception("Uno o ambos pines son nulos");
            }

            Pin salida = null;
            Pin entrada = null;

            if (pinOrigen.getTipo().equals("salida") && pinDestino.getTipo().equals("entrada")) {
                salida = pinOrigen;
                entrada = pinDestino;
            }
            else if (pinOrigen.getTipo().equals("entrada") && pinDestino.getTipo().equals("salida")) {
                salida = pinDestino;
                entrada = pinOrigen;
            }
            else {
                throw new Exception("Debe conectar un pin de salida con uno de entrada");
            }

            if (entrada.getConector() != null) {
                throw new Exception("El pin de entrada ya está conectado");
            }

            this.pinSalida = salida;
            this.pinEntrada = entrada;

            entrada.conectarA(this);
            salida.conectarA(this);

            return true;

        } catch (Exception e) {
            // Intentar mostrar el error a través del circuito del pin de entrada si existe
            if (pinEntrada != null && pinEntrada.getComponente() != null &&
                pinEntrada.getComponente().getCircuito() != null) {
                pinEntrada.getComponente().getCircuito().mostrarError(e.getMessage());
            } else if (pinSalida != null && pinSalida.getComponente() != null && // Fallback al pin de salida
                       pinSalida.getComponente().getCircuito() != null) {
                pinSalida.getComponente().getCircuito().mostrarError(e.getMessage());
            }
            // else { JOptionPane.showMessageDialog(null, e.getMessage(), "Error de Conexión", JOptionPane.ERROR_MESSAGE); } // Si no hay circuito
            return false;
        }
    }

    public void desconectar() {
        if (pinSalida != null) {
            pinSalida.desconectar(); // El pin se desvincula del conector
            pinSalida = null;
        }
        if (pinEntrada != null) {
            pinEntrada.desconectar(); // El pin se desvincula del conector
            pinEntrada = null;
        }
    }

    public void propagarEstado() {
        if (pinSalida == null || pinEntrada == null) {
            return; // No se puede propagar si falta algún pin
        }

        boolean estado = pinSalida.obtenerEstado();
        if (pinEntrada.obtenerEstado() != estado) { // Solo propagar si hay un cambio
            pinEntrada.cambiarEstado(estado);
            if (pinEntrada.getComponente() != null) {
                pinEntrada.getComponente().evaluar(); // Evaluar el componente que recibe la nueva señal
            }
        }
    }

    // Getters
    public Pin obtenerPinEntrada() {
        return pinEntrada;
    }

    public Pin obtenerPinSalida() {
        return pinSalida;
    }

    public List<Pin> obtenerPinesEntrada() { // Aunque solo hay uno, mantiene la interfaz si se quisiera expandir
        List<Pin> pines = new ArrayList<>();
        if (pinEntrada != null) {
            pines.add(pinEntrada);
        }
        return pines;
    }

    public ConnectionData getSalidaData() {
        if (pinSalida == null || pinSalida.getComponente() == null) {
            return null;
        }
        return new ConnectionData(
            pinSalida.getComponente().getId(),
            pinSalida.getComponente().getSalidas().indexOf(pinSalida)
        );
    }

    public ConnectionData getEntradaData() {
        if (pinEntrada == null || pinEntrada.getComponente() == null) {
            return null;
        }
        return new ConnectionData(
            pinEntrada.getComponente().getId(),
            pinEntrada.getComponente().getEntradas().indexOf(pinEntrada)
        );
    }

    // Métodos de Ayuda o Especializados
    public boolean agregarConexion(Pin pinEntrada) { // Parece un método para setear solo el pin de entrada
        if (pinEntrada == null || !"entrada".equals(pinEntrada.getTipo())) {
            return false; // Solo pines de entrada
        }
        if (pinEntrada.getConector() != null) {
            return false; // Pin ya conectado a otro conector
        }

        if (this.pinEntrada != null) { // Si ya había un pin de entrada, desconectarlo
            this.pinEntrada.desconectar();
        }

        this.pinEntrada = pinEntrada;
        pinEntrada.conectarA(this);
        return true;
    }

    public boolean conectarSinValidacion(Pin pinOrigen, Pin pinDestino) { // Asumo que esto es para reconstrucción o clonación
        try {
            if (pinOrigen == null || pinDestino == null) {
                return false;
            }

            // Determinar automáticamente qué pin es salida y cuál es entrada
            if (pinOrigen.getTipo().equals("salida") && pinDestino.getTipo().equals("entrada")) {
                this.pinSalida = pinOrigen;
                this.pinEntrada = pinDestino;
            }
            else if (pinOrigen.getTipo().equals("entrada") && pinDestino.getTipo().equals("salida")) {
                this.pinSalida = pinDestino;
                this.pinEntrada = pinOrigen;
            }
            else {
                return false; // No coinciden los tipos para una conexión válida
            }

            // Conectar sin verificar si ya está conectado (asume que es para un estado limpio)
            pinEntrada.conectarA(this);
            pinSalida.conectarA(this);

            return true;

        } catch (Exception e) {
            // En un método "sin validación", los errores deberían ser mínimos o manejados de otra forma
            return false;
        }
    }

    public boolean conectarForzado(Pin pinSalida, Pin pinEntrada) { // Para establecer pines directamente, útil en clonación/deserialización
        try {
            this.pinSalida = pinSalida;
            this.pinEntrada = pinEntrada;

            if (pinEntrada != null) pinEntrada.conectarA(this); // Asigna este conector al pin
            if (pinSalida != null) pinSalida.conectarA(this);  // Asigna este conector al pin

            return true;
        } catch (Exception e) {
            // Generalmente no debería fallar si los pines son válidos
            return false;
        }
    }

    public void removerConexion(Pin pinEntrada) { // Remueve la conexión solo si el pin de entrada coincide
        if (pinEntrada != null && pinEntrada.equals(this.pinEntrada)) {
            this.pinEntrada.desconectar();
            this.pinEntrada = null;
            // Considerar si también se debe limpiar this.pinSalida si solo queda un extremo
        }
    }

    public void reconstruir(Map<String, Componente> mapaComponentes,
                            String idSalida, int indexSalida,
                            String idEntrada, int indexEntrada) {

        if (idSalida != null && idEntrada != null) {
            Componente compSalida = mapaComponentes.get(idSalida);
            Componente compEntrada = mapaComponentes.get(idEntrada);

            if (compSalida != null && compEntrada != null &&
                indexSalida >= 0 && indexSalida < compSalida.getSalidas().size() && // Verificar límites
                indexEntrada >= 0 && indexEntrada < compEntrada.getEntradas().size()) { // Verificar límites

                this.pinSalida = compSalida.getSalidas().get(indexSalida);
                this.pinEntrada = compEntrada.getEntradas().get(indexEntrada);

                if (this.pinSalida != null && this.pinEntrada != null) {
                    this.pinSalida.conectarA(this);
                    this.pinEntrada.conectarA(this);
                }
            }
        }
    }

    public boolean contienePin(Pin pin) {
        return pin != null && (pin.equals(pinSalida) || pin.equals(pinEntrada));
    }
}