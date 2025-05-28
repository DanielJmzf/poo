/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Compuertas;

import Componentes.Componente;
import compuertas.renderers.CompuertaRenderer; // Asegúrate que esta importación sea correcta
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable; // Componente ya es Serializable, pero es bueno tenerlo presente

public abstract class Compuerta extends Componente {
    public final int ancho = 20; // Considera hacerlos protected si las subclases necesitan acceder directamente
    public final int alto = 40;  // Considera hacerlos protected
    protected transient CompuertaRenderer renderer; // Marcado como transient

    public Compuerta(int x, int y, int numEntradas, CompuertaRenderer rendererInicial) {
        super(x, y, numEntradas, 1); // Asumimos 1 salida para todas las compuertas
        this.renderer = rendererInicial;
    }

    /**
     * Establece el renderer para esta compuerta.
     * @param renderer El renderer a utilizar.
     */
    public void setRenderer(CompuertaRenderer renderer) {
        this.renderer = renderer;
    }

    /**
     * Método abstracto para que las subclases re-inicialicen su renderer específico
     * después de la deserialización, ya que el campo renderer es transient.
     */
    public abstract void reinitializeRenderer();

    /**
     * Método de serialización personalizado.
     * Los campos transient como 'renderer' no se serializan por defecto.
     * No necesitamos escribir nada especial aquí para 'renderer' porque es transient.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject(); // Serializa los campos no transient
    }

    /**
     * Método de deserialización personalizado.
     * Se llama después de que defaultReadObject ha completado.
     * Aquí re-inicializamos los campos transient.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject(); // Deserializa los campos no transient
        reinitializeRenderer(); // Re-inicializa el renderer que es transient
    }

    @Override
    public void dibujar(java.awt.Graphics g) {
        if (renderer != null) {
            renderer.dibujar(g, this);
        } else {
            // Opcional: Dibujar una representación por defecto si el renderer es null
            // Esto puede ayudar a identificar problemas si un renderer no se carga.
            g.setColor(java.awt.Color.GRAY);
            g.drawRect(getX(), getY(), ancho * 2, alto); // Usar getX(), getY() de Componente
            g.drawString("No Renderer", getX() + 5, getY() + 20);
            System.err.println("Advertencia: Renderer es null para la compuerta ID: " + getId());
        }
    }
    
    // Getters para ancho y alto si son necesarios externamente,
    // ya que los campos son public final, se pueden acceder directamente.
    // Si se cambian a protected, estos getters serían útiles.
    public int getAncho() { // Renombrado para evitar colisión con posible getAncho en Componente
        return ancho;
    }

    public int getAlto() { // Renombrado para evitar colisión con posible getAlto en Componente
        return alto;
    }
    
    // El método setPosicion ya debería estar en la clase Componente si es común a todos.
    // Si es específico para Compuerta y diferente de Componente.mover(), puede quedarse aquí.
    // public void setPosicion(int x, int y) {
    // this.x = x; // x e y son de la clase Componente, usar los setters o mover()
    // this.y = y;
    // mover(x,y); // Es mejor usar el método mover de la clase base si existe
    // }
}
