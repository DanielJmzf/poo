/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Gates;

import Components.Componente;
import Render.GatesRenderer; // Asumo que este es el import correcto
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class Compuerta extends Componente {
    public final int ancho = 20; // Ancho lógico, el dibujo podría usar ancho*2
    public final int alto = 40;
    protected transient GatesRenderer renderer;

    // Constructor
    public Compuerta(int x, int y, int numEntradas, GatesRenderer rendererInicial) {
        super(x, y, numEntradas, 1); // Llama al constructor de Componente, asumiendo 1 salida
        this.renderer = rendererInicial;
    }

    // Métodos Abstractos (deben ser implementados por subclases concretas)
    /**
     * Reinicializa el renderer. Típicamente llamado después de la deserialización
     * para asegurar que el campo transient 'renderer' no sea null.
     */
    public abstract void reinitializeRenderer();

    // Métodos Públicos
    @Override
    public void dibujar(java.awt.Graphics g) {
        if (renderer != null) {
            renderer.dibujar(g, this); // Delega el dibujado al renderer específico
        } else {
            // Dibujado por defecto si no hay renderer (o como fallback)
            g.setColor(java.awt.Color.GRAY);
            g.drawRect(getX(), getY(), ancho * 2, alto); // Usar getX(), getY() de la superclase
            g.drawString("No Renderer", getX() + 5, getY() + alto / 2 + 5); // Centrar un poco mejor el texto
            // Mensaje de advertencia mejorado
            System.err.println("Advertencia: El renderer es nulo para la compuerta tipo '" +
                               this.getClass().getSimpleName() + "' con ID: " + getId() +
                               ". Se utilizará un dibujado por defecto.");
        }
    }

    // Getters y Setters
    public void setRenderer(GatesRenderer renderer) {
        this.renderer = renderer;
    }

    // No hay un getRenderer(), pero podría ser útil si se necesita externamente.

    public int getAncho() { // Devuelve el ancho lógico
        return ancho;
    }

    public int getAlto() { // Devuelve el alto
        return alto;
    }

    // Métodos de Serialización
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        // El campo 'renderer' es transient, no se serializa.
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // El campo 'renderer' será null después de defaultReadObject.
        // Se llama a reinitializeRenderer para que las subclases puedan
        // instanciar su renderer específico.
        reinitializeRenderer();
    }
}