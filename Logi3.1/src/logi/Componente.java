/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Componente implements Serializable {
    private static final long serialVersionUID = 1L;
    
    protected String id;
    protected int x, y;
    protected transient List<Pin> entradas;
    protected transient List<Pin> salidas;
    protected transient Circuito circuito;

    public Componente(int x, int y, int numEntradas, int numSalidas) {
        this.x = x;
        this.y = y;
        this.id = java.util.UUID.randomUUID().toString();
        inicializarPines(numEntradas, numSalidas);
    }
    
    public void inicializarPines(int numEntradas, int numSalidas) {
        this.entradas = new ArrayList<>();
        for (int i = 0; i < numEntradas; i++) {
            Pin pin = new Pin("entrada", this);
            pin.setComponente(this);
            entradas.add(pin);
        }
        
        this.salidas = new ArrayList<>();
        for (int i = 0; i < numSalidas; i++) {
            Pin pin = new Pin("salida", this);
            pin.setComponente(this);
            salidas.add(pin);
        }
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    public List<Boolean> obtenerSalidas() {
        List<Boolean> estados = new ArrayList<>();
        for (Pin pin : salidas) {
            if (pin != null) {
                estados.add(pin.obtenerEstado());
            }
        }
        return estados;
    }
    
    public abstract void evaluar();
    
   public void conectarEntrada(int numEntrada, Conector conector) {
        try {
            if (numEntrada < 0 || numEntrada >= entradas.size()) {
                throw new IndexOutOfBoundsException("Número de entrada inválido");
            }
            if (conector == null) {
                throw new IllegalArgumentException("El conector no puede estar nulo");
            }
            conector.agregarConexion(entradas.get(numEntrada));
        } catch (Exception e) {
            if (circuito != null) {
                circuito.mostrarError(e.getMessage());
            }
        }
    }
    
    public void desconectarCable(Conector conector) {
        if (conector == null) return;
        
        for (Pin pin : entradas) {
            if (pin != null && conector.equals(pin.getConector())) {
                conector.removerConexion(pin);
            }
        }
    }
    
    public void mover(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public abstract void dibujar(java.awt.Graphics g);
    
    public String getId() { 
        return id; 
    }
    
    public int getX() { 
        return x; 
    }
    
    public int getY() { 
        return y; 
    }
    
    public List<Pin> getEntradas() { 
        return new ArrayList<>(entradas); 
    }
    
    public List<Pin> getSalidas() { 
        return new ArrayList<>(salidas); 
    }
        /**
     * Obtiene el circuito al que pertenece este componente
     * @return El circuito padre
     */
    public Circuito getCircuito() {
        return circuito;
    }
        /**
     * Establece el circuito al que pertenece este componente
     * @param circuito El circuito padre
     */
    public void setCircuito(Circuito circuito) {
        this.circuito = circuito;
        if (entradas != null) {
            for (Pin pin : entradas) {
                if (pin != null) {
                    pin.setComponente(this);
                }
            }
        }
        if (salidas != null) {
            for (Pin pin : salidas) {
                if (pin != null) {
                    pin.setComponente(this);
                }
            }
        }
    }
}