/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Componentes;

import Circuito.Circuito;
import Circuito.Pin;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Componente implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    
    public String id;
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
    
    public void setCircuito(Circuito circuito) {
        this.circuito = circuito;
        if (entradas != null) {
            for (Pin pin : entradas) {
                if (pin != null) pin.setComponente(this);
            }
        }
        if (salidas != null) {
            for (Pin pin : salidas) {
                if (pin != null) pin.setComponente(this);
            }
        }
    }
    
    public Circuito getCircuito() {
        return circuito;
    }
    
    public void inicializarPines(int numEntradas, int numSalidas) {
        this.entradas = new ArrayList<>();
        for (int i = 0; i < numEntradas; i++) {
            Pin pin = new Pin("entrada", this);
            entradas.add(pin);
        }
        
        this.salidas = new ArrayList<>();
        for (int i = 0; i < numSalidas; i++) {
            Pin pin = new Pin("salida", this);
            salidas.add(pin);
        }
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    public abstract void evaluar();
    public abstract void dibujar(java.awt.Graphics g);
    
    public void mover(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public Componente clone() {
        try {
            Componente cloned = (Componente) super.clone();
            cloned.entradas = new ArrayList<>();
            for (Pin pin : this.entradas) {
                if (pin != null) {
                    Pin clonedPin = new Pin(pin.getTipo(), cloned);
                    clonedPin.cambiarEstado(pin.obtenerEstado());
                    cloned.entradas.add(clonedPin);
                }
            }
            
            cloned.salidas = new ArrayList<>();
            for (Pin pin : this.salidas) {
                if (pin != null) {
                    Pin clonedPin = new Pin(pin.getTipo(), cloned);
                    clonedPin.cambiarEstado(pin.obtenerEstado());
                    cloned.salidas.add(clonedPin);
                }
            }
            
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
    
    // Getters y setters
    public String getId() { return id; }
    public int getX() { return x; }
    public int getY() { return y; }
    public List<Pin> getEntradas() { return entradas; }
    public List<Pin> getSalidas() { return salidas; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
}