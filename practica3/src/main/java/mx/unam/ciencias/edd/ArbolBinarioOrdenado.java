package mx.unam.ciencias.edd;

import java.util.Iterator;

/**
 * <p>Clase para árboles binarios ordenados. Los árboles son genéricos, pero
 * acotados a la interfaz {@link Comparable}.</p>
 *
 * <p>Un árbol instancia de esta clase siempre cumple que:</p>
 * <ul>
 *   <li>Cualquier elemento en el árbol es mayor o igual que todos sus
 *       descendientes por la izquierda.</li>
 *   <li>Cualquier elemento en el árbol es menor o igual que todos sus
 *       descendientes por la derecha.</li>
 * </ul>
 */
public class ArbolBinarioOrdenado<T extends Comparable<T>>
    extends ArbolBinario<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Pila para recorrer los vértices en DFS in-order. */
        private Pila<Vertice> pila;

        /* Inicializa al iterador. */
        public Iterador() {
            pila = new Pila<Vertice>();
            if (raiz == null)
                return;

            pila.mete(raiz);
            Vertice vertice = raiz;

            while ((vertice = vertice.izquierdo) != null)
                pila.mete(vertice);
        }

        /* Nos dice si hay un elemento siguiente. */
        @Override public boolean hasNext() {
            return !pila.esVacia();
        }

        /* Regresa el siguiente elemento en orden DFS in-order. */
        @Override public T next() {
            Vertice vertice = pila.saca();

            if (vertice.derecho != null) {
                Vertice verticeAux = vertice.derecho;
                pila.mete(verticeAux);

                while ((verticeAux = verticeAux.izquierdo) != null)
                    pila.mete(verticeAux);
            }

            return vertice.elemento;
        }
    }

    /**
     * El vértice del último elemento agegado. Este vértice sólo se puede
     * garantizar que existe <em>inmediatamente</em> después de haber agregado
     * un elemento al árbol. Si cualquier operación distinta a agregar sobre el
     * árbol se ejecuta después de haber agregado un elemento, el estado de esta
     * variable es indefinido.
     */
    protected Vertice ultimoAgregado;

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros
     * de {@link ArbolBinario}.
     */
    public ArbolBinarioOrdenado() { super(); }

    /**
     * Construye un árbol binario ordenado a partir de una colección. El árbol
     * binario ordenado tiene los mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol
     *        binario ordenado.
     */
    public ArbolBinarioOrdenado(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Agrega un nuevo elemento al árbol. El árbol conserva su orden in-order.
     * @param elemento el elemento a agregar.
     */
    @Override public void agrega(T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException("Elemento no válido.");

        Vertice vertice = nuevoVertice(elemento);
        elementos++;

        if (raiz == null)
            raiz = vertice;
        else
            agrega(raiz, vertice);

        ultimoAgregado = vertice;
    }

    private void agrega(Vertice verticeActual, Vertice nuevoVertice) {
        if (nuevoVertice.elemento.compareTo(verticeActual.elemento) < 0) {
            if (verticeActual.izquierdo == null) {
                verticeActual.izquierdo = nuevoVertice;
                nuevoVertice.padre = verticeActual;
            }
            else {
                agrega(verticeActual.izquierdo, nuevoVertice);
            }
        } else {
            if (verticeActual.derecho == null) {
                verticeActual.derecho = nuevoVertice;
                nuevoVertice.padre = verticeActual;
            }
            else {
                agrega(verticeActual.derecho, nuevoVertice);
            }
        }
    }

    /**
     * Elimina un elemento. Si el elemento no está en el árbol, no hace nada; si
     * está varias veces, elimina el primero que encuentre (in-order). El árbol
     * conserva su orden in-order.
     * @param elemento el elemento a eliminar.
     */
    @Override public void elimina(T elemento) {
        Vertice vertice = vertice(busca(elemento));

        if (vertice == null)
            return;

        elementos--;

        if (vertice.izquierdo != null && vertice.derecho != null)
            vertice = intercambiaEliminable(vertice);

        eliminaVertice(vertice);
    }

    /**
     * Intercambia el elemento de un vértice con dos hijos distintos de
     * <code>null</code> con el elemento de un descendiente que tenga a lo más
     * un hijo.
     * @param vertice un vértice con dos hijos distintos de <code>null</code>.
     * @return el vértice descendiente con el que vértice recibido se
     *         intercambió. El vértice regresado tiene a lo más un hijo distinto
     *         de <code>null</code>.
     */
    protected Vertice intercambiaEliminable(Vertice vertice) {
        Vertice verticeMaximal = maximoEnSubarbol(vertice.izquierdo);
        T elementoMaximal = verticeMaximal.elemento;

        verticeMaximal.elemento = vertice.elemento;
        vertice.elemento = elementoMaximal;

        return verticeMaximal;
    }

    /**
     * Elimina un vértice que a lo más tiene un hijo distinto de
     * <code>null</code> subiendo ese hijo (si existe).
     * @param vertice el vértice a eliminar; debe tener a lo más un hijo
     *                distinto de <code>null</code>.
     */
    protected void eliminaVertice(Vertice vertice) {
        Vertice hijo = vertice.izquierdo != null ? vertice.izquierdo : vertice.derecho;

        if (vertice.padre == null)
            raiz = hijo;
        else
            if (vertice.padre.izquierdo == vertice)
                vertice.padre.izquierdo = hijo;
            else
                vertice.padre.derecho = hijo;

        if (hijo != null)
            hijo.padre = vertice.padre;
    }

    /**
     * Busca un elemento en el árbol recorriéndolo in-order. Si lo encuentra,
     * regresa el vértice que lo contiene; si no, regresa <code>null</code>.
     * @param elemento el elemento a buscar.
     * @return un vértice que contiene al elemento buscado si lo
     *         encuentra; <code>null</code> en otro caso.
     */
    @Override public VerticeArbolBinario<T> busca(T elemento) {
        return busca(elemento, raiz);
    }

    private VerticeArbolBinario<T> busca(T elemento, Vertice vertice) {
        if (vertice == null)
            return null;

        if (elemento.equals(vertice.elemento))
            return vertice;

        if (elemento.compareTo(vertice.elemento) < 0)
            return busca(elemento, vertice.izquierdo);

        return busca(elemento, vertice.derecho);
    }

    /**
     * Regresa el vértice que contiene el último elemento agregado al
     * árbol. Este método sólo se puede garantizar que funcione
     * <em>inmediatamente</em> después de haber invocado al método {@link
     * agrega}. Si cualquier operación distinta a agregar sobre el árbol se
     * ejecuta después de haber agregado un elemento, el comportamiento de este
     * método es indefinido.
     * @return el vértice que contiene el último elemento agregado al árbol, si
     *         el método es invocado inmediatamente después de agregar un
     *         elemento al árbol.
     */
    public VerticeArbolBinario<T> getUltimoVerticeAgregado() {
        return ultimoAgregado;
    }

    /**
     * Gira el árbol a la derecha sobre el vértice recibido. Si el vértice no
     * tiene hijo izquierdo, el método no hace nada.
     * @param vertice el vértice sobre el que vamos a girar.
     */
    public void giraDerecha(VerticeArbolBinario<T> vertice) {
        Vertice verticeActual = vertice(vertice);
        if (verticeActual.izquierdo == null)
            return;

        Vertice hijo = verticeActual.izquierdo;
        hijo.padre = verticeActual.padre;

        if (verticeActual.padre == null)
            raiz = hijo;
        else {
            if (verticeActual.padre.izquierdo == verticeActual)
                verticeActual.padre.izquierdo = hijo;
            else
                verticeActual.padre.derecho = hijo;
        }

        verticeActual.izquierdo = hijo.derecho;

        if (verticeActual.izquierdo != null)
            verticeActual.izquierdo.padre = verticeActual;

        hijo.derecho = verticeActual;
        verticeActual.padre = hijo;
    }

    /**
     * Gira el árbol a la izquierda sobre el vértice recibido. Si el vértice no
     * tiene hijo derecho, el método no hace nada.
     * @param vertice el vértice sobre el que vamos a girar.
     */
    public void giraIzquierda(VerticeArbolBinario<T> vertice) {
        Vertice verticeActual = vertice(vertice);
        if (verticeActual.derecho == null)
            return;

        Vertice hijo = verticeActual.derecho;
        hijo.padre = verticeActual.padre;

        if (verticeActual.padre == null)
            raiz = hijo;
        else {
            if (verticeActual.padre.derecho == verticeActual)
                verticeActual.padre.derecho = hijo;
            else
                verticeActual.padre.izquierdo = hijo;
        }

        verticeActual.derecho = hijo.izquierdo;

        if (verticeActual.derecho != null)
            verticeActual.derecho.padre = verticeActual;
        hijo.izquierdo = verticeActual;
        verticeActual.padre = hijo;
    }

    /**
     * Realiza un recorrido DFS <em>pre-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void dfsPreOrder(AccionVerticeArbolBinario<T> accion) {
        dfsPreOrder(accion, raiz);
    }

    private void dfsPreOrder(AccionVerticeArbolBinario<T> accion, Vertice vertice) {
        if (vertice == null)
            return;

        accion.actua(vertice);
        dfsPreOrder(accion, vertice.izquierdo);
        dfsPreOrder(accion, vertice.derecho);
    }

    /**
     * Realiza un recorrido DFS <em>in-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void dfsInOrder(AccionVerticeArbolBinario<T> accion) {
        dfsInOrder(accion, raiz);
    }

    private void dfsInOrder(AccionVerticeArbolBinario<T> accion, Vertice vertice) {
        if (vertice == null)
            return;

        dfsInOrder(accion, vertice.izquierdo);
        accion.actua(vertice);
        dfsInOrder(accion, vertice.derecho);
    }

    /**
     * Realiza un recorrido DFS <em>post-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void dfsPostOrder(AccionVerticeArbolBinario<T> accion) {
        dfsPostOrder(accion, raiz);
    }

    private void dfsPostOrder(AccionVerticeArbolBinario<T> accion, Vertice vertice) {
        if (vertice == null)
            return;

        dfsPostOrder(accion, vertice.izquierdo);
        dfsPostOrder(accion, vertice.derecho);
        accion.actua(vertice);
    }

    private Vertice maximoEnSubarbol(Vertice vertice) {
        if (vertice.derecho == null)
            return vertice;

        return maximoEnSubarbol(vertice.derecho);
    }

    /**
     * Regresa un iterador para iterar el árbol. El árbol se itera en orden.
     * @return un iterador para iterar el árbol.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }
}
