package mx.unam.ciencias.edd.proyecto2.graficadores;

import java.util.Iterator;
import mx.unam.ciencias.edd.Lista;

/**
 * Clase concreta para graficar la estructura de datos Cola.
 */
public class GraficadorCola<T extends Comparable<T>> extends GraficadorLineal<T> {

    /**
     * Utilizamos una lista para representar internamente los datos de la
     * estructura, puesto que podemos iterar una Lista sin modificarla. Lo
     * anterior no se puede con nuestra implementación de Cola.
     */
    Lista<T> coleccion;

    /**
     * Constructor que recibe la lista con los datos que contiene la Cola que
     * queremos graficar.
     * @param coleccion la lista con la información a graficar.
     */
    public GraficadorCola(Lista<T> coleccion) {
        this.coleccion = coleccion;
    }

    /**
     * Regresa un iterador para iterar la estructura de datos. Necesitamos
     * implementarlo pues lo requiere la clase abstracta.
     */
    protected Iterator<T> getIterator() {
        return coleccion.iterator();
    }
}

