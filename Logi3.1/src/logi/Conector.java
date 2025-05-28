/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Conector implements Serializable {
    private static final long serialVersionUID = 1L;
    private transient Pin pinSalida;
    private transient Pin pinEntrada; 

    public Conector() {
        this.pinSalida = null;
        this.pinEntrada = null;
    }

    
    public boolean conectar(Pin pinOrigen, Pin pinDestino) {
    try {
        if (pinOrigen == null || pinDestino == null) {
            throw new Exception("Uno o más pines son nulos");
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
            throw new Exception("Debe conectar obligatoriamente un pin de salida con uno de entrada");
        }
        if (entrada.getConector() != null) {
            throw new Exception("El pin de entrada ya está conectado");
        }
        this.pinSalida = salida;
        this.pinEntrada = entrada;
        entrada.conectar(this);
        salida.conectar(this);
        return true;
    } catch (Exception e) {
        if (pinEntrada != null && pinEntrada.getComponente() != null &&
            pinEntrada.getComponente().getCircuito() != null) {
            pinEntrada.getComponente().getCircuito().mostrarError(e.getMessage());
        }
        return false;
    }
}

    
    public void desconectar() {
    if (pinSalida != null) {
        pinSalida.desconectar();
        pinSalida = null;
    }
    if (pinEntrada != null) {
        pinEntrada.desconectar();
        pinEntrada = null;
    }
}
    
    public void propagarEstado() {
        if (pinSalida == null || pinEntrada == null) {
            return;
        }
        boolean estado = pinSalida.obtenerEstado();
        if (pinEntrada.obtenerEstado() != estado) {
            pinEntrada.cambiarEstado(estado);
            if (pinEntrada.getComponente() != null) {
                pinEntrada.getComponente().evaluar();
            }
        }
    }

    
    public Pin obtenerPinEntrada() {
        return pinEntrada;
    }

   
    public Pin obtenerPinSalida() {
        return pinSalida;
    }
    
    public List<Pin> obtenerPinesEntrada() {
        List<Pin> pines = new ArrayList<>();
        if (pinEntrada != null) {
            pines.add(pinEntrada);
        }
        return pines;
    }
public boolean agregarConexion(Pin pinEntrada) {
        if (pinEntrada == null || !"entrada".equals(pinEntrada.getTipo())) {
            return false;
        }
        if (pinEntrada.getConector() != null) {
            return false;
        }
        if (this.pinEntrada != null) {
            this.pinEntrada.desconectar();
        }
        this.pinEntrada = pinEntrada;
        pinEntrada.conectar(this);
        return true;
    }
public void removerConexion(Pin pinEntrada) {
        if (pinEntrada != null && pinEntrada.equals(this.pinEntrada)) {
            this.pinEntrada.desconectar();
            this.pinEntrada = null;
        }
    }
   
    public void reconstruir(Map<String, Componente> mapaComponentes, String idSalida, int indexSalida, String idEntrada, int indexEntrada) {
    if (idSalida != null && idEntrada != null) {
        Componente compSalida = mapaComponentes.get(idSalida);
        Componente compEntrada = mapaComponentes.get(idEntrada);
        if (compSalida != null && compEntrada != null && indexSalida < compSalida.getSalidas().size() && indexEntrada < compEntrada.getEntradas().size()) {  
            this.pinSalida = compSalida.getSalidas().get(indexSalida);
            this.pinEntrada = compEntrada.getEntradas().get(indexEntrada);
            if (this.pinSalida != null && this.pinEntrada != null) {
                this.pinSalida.conectar(this);
                this.pinEntrada.conectar(this);
            }
        }
    }
}

    
    public static class ConnectionData implements Serializable {
        private static final long serialVersionUID = 1L;
        String idComponente;
        int indexPin;
        
        public ConnectionData(String idComponente, int indexPin) {
            this.idComponente = idComponente;
            this.indexPin = indexPin;
        }
    }
    
    public boolean contienePin(Pin pin) {
        return pin != null && (pin.equals(pinSalida) || pin.equals(pinEntrada));
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
}