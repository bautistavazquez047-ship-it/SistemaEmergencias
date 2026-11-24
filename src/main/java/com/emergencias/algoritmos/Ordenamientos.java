package com.emergencias.algoritmos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Ordenamientos implementados a mano. Ambos son estables: los elementos
 * que comparan igual conservan su orden relativo original.
 * Ordenan la lista recibida (trabajar sobre una copia si se quiere conservar el original).
 */
public final class Ordenamientos {

    private Ordenamientos() {
    }

    /**
     * Ordenamiento por inserción (algoritmo simple, iterativo).
     * Mejor caso O(n) (ya ordenada), promedio O(n^2), peor caso O(n^2). Espacio O(1).
     */
    public static <T> void insercion(List<T> lista, Comparator<? super T> criterio) {
        for (int i = 1; i < lista.size(); i++) {
            T clave = lista.get(i);
            int j = i - 1;
            while (j >= 0 && criterio.compare(lista.get(j), clave) > 0) {
                lista.set(j + 1, lista.get(j));
                j--;
            }
            lista.set(j + 1, clave);
        }
    }

    /**
     * Merge Sort (dividir y conquistar, recursivo).
     * Mejor, promedio y peor caso O(n log n). Espacio auxiliar O(n).
     */
    public static <T> void mergeSort(List<T> lista, Comparator<? super T> criterio) {
        if (lista.size() < 2) {
            return;
        }
        List<T> auxiliar = new ArrayList<>(lista);
        ordenar(lista, auxiliar, 0, lista.size() - 1, criterio);
    }

    private static <T> void ordenar(List<T> lista, List<T> auxiliar,
                                    int izquierda, int derecha, Comparator<? super T> criterio) {
        if (izquierda >= derecha) {
            return; // caso base: 0 o 1 elemento
        }
        int medio = izquierda + (derecha - izquierda) / 2;
        ordenar(lista, auxiliar, izquierda, medio, criterio);
        ordenar(lista, auxiliar, medio + 1, derecha, criterio);
        combinar(lista, auxiliar, izquierda, medio, derecha, criterio);
    }

    /** Mezcla las dos mitades ya ordenadas [izquierda..medio] y [medio+1..derecha]. */
    private static <T> void combinar(List<T> lista, List<T> auxiliar,
                                     int izquierda, int medio, int derecha, Comparator<? super T> criterio) {
        for (int k = izquierda; k <= derecha; k++) {
            auxiliar.set(k, lista.get(k));
        }
        int i = izquierda;
        int j = medio + 1;
        for (int k = izquierda; k <= derecha; k++) {
            if (i > medio) {
                lista.set(k, auxiliar.get(j++));
            } else if (j > derecha) {
                lista.set(k, auxiliar.get(i++));
            } else if (criterio.compare(auxiliar.get(j), auxiliar.get(i)) < 0) {
                lista.set(k, auxiliar.get(j++)); // la derecha solo gana si es estrictamente menor (estabilidad)
            } else {
                lista.set(k, auxiliar.get(i++));
            }
        }
    }
}
