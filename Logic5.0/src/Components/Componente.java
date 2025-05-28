/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Components;

import Circuit.Circuito;
import Circuit.Pin;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Componente implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;

    public String id; // Público para acceso directo, considerar hacerlo protegido con getter/setter
    protected int x, y;
    protected transient List<Pin> entradas;
    protected transient List<Pin> salidas;
    protected transient Circuito circuito;

    // Constructor
    public Componente(int x, int y, int numEntradas, int numSalidas) {
        this.x = x;
        this.y = y;
        this.id = java.util.UUID.randomUUID().toString(); // Generar ID único
        inicializarPines(numEntradas, numSalidas); // Llamada al método de inicialización de pines
    }

    // Métodos Abstractos (a ser implementados por subclases)
    public abstract void evaluar();
    public abstract void dibujar(java.awt.Graphics g);

    // Métodos Públicos Principales
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

    public void mover(int x, int y) {
        this.x = x;
        this.y = y;
        // Considerar si se necesita llamar a circuito.setModificado(true); aquí
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public List<Pin> getEntradas() {
        return entradas;
    }

    public List<Pin> getSalidas() {
        return salidas;
    }

    public Circuito getCircuito() {
        return circuito;
    }

    public void setCircuito(Circuito circuito) {
        this.circuito = circuito;
        // Asegurar que los pines también conozcan a este componente
        // Esto es importante especialmente después de la deserialización o clonación
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

    // Método Clone
    @Override
    public Componente clone() {
        try {
            Componente cloned = (Componente) super.clone();
            // Clonar listas de pines para que el clon tenga sus propias instancias de pines
            cloned.entradas = new ArrayList<>();
            if (this.entradas != null) { // Comprobar si la lista original no es nula
                for (Pin pin : this.entradas) {
                    if (pin != null) {
                        Pin clonedPin = new Pin(pin.getTipo(), cloned); // Nuevo pin asociado al clon
                        clonedPin.cambiarEstado(pin.obtenerEstado()); // Copiar estado del pin original
                        cloned.entradas.add(clonedPin);
                    }
                }
            }

            cloned.salidas = new ArrayList<>();
            if (this.salidas != null) { // Comprobar si la lista original no es nula
                for (Pin pin : this.salidas) {
                    if (pin != null) {
                        Pin clonedPin = new Pin(pin.getTipo(), cloned); // Nuevo pin asociado al clon
                        clonedPin.cambiarEstado(pin.obtenerEstado()); // Copiar estado del pin original
                        cloned.salidas.add(clonedPin);
                    }
                }
            }
            // El ID debería ser único para el clon, se puede regenerar si es necesario
            // cloned.id = java.util.UUID.randomUUID().toString(); // Opcional: si cada clon debe tener un ID nuevo
            // El circuito del clon se establecerá cuando se añada a un circuito
            cloned.circuito = null;

            return cloned;
        } catch (CloneNotSupportedException e) {
            // Esto no debería suceder ya que Componente implementa Cloneable
            throw new AssertionError("Clonación no soportada, pero Componente implementa Cloneable.",e);
        }
    }

    // Métodos de Serialización (Personalizados para campos transient si fuera necesario,
    // pero aquí `defaultWriteObject` y `defaultReadObject` son suficientes
    // ya que `inicializarPines` y `setCircuito` se encargan de reinicializar los `transient`)
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        // No es necesario escribir los pines explícitamente si son transient
        // y se reconstruyen en readObject o al llamar a inicializarPines/setCircuito.
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // Los campos transient (entradas, salidas, circuito) serán null aquí.
        // Necesitan ser reinicializados.
        // La reinicialización de pines (entradas, salidas) generalmente se hace
        // en la clase Circuito durante su propio readObject, donde conoce el
        // número de entradas/salidas para cada tipo de componente.
        // El campo 'circuito' también se establece desde la clase Circuito.
        // Si esta clase Componente necesitara reconstruir sus pines por sí misma,
        // necesitaría saber cuántos tiene, lo cual depende de la subclase.
        // Por ahora, se asume que la clase Circuito maneja esto.
    }
}