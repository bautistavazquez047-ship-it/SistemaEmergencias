package com.emergencias.algoritmos;

import com.emergencias.modelo.Emergencia;
import java.util.List;

/**
 * Búsquedas implementadas a mano por código de emergencia.
 */
public final class Busquedas {

    private Busquedas() {
    }

    /**
     * Búsqueda lineal: recorre la lista de principio a fin.
     * No requiere datos ordenados.
     * Mejor caso O(1), promedio O(n), peor caso O(n).
     *
     * @return posición del elemento o -1 si no existe
     */
    public static int busquedaLineal(List<Emergencia> lista, int codigo) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getCodigo() == codigo) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Búsqueda binaria: en cada paso descarta la mitad de la lista.
     * REQUIERE que la lista esté ordenada ascendentemente por código.
     * Mejor caso O(1), promedio O(log n), peor caso O(log n).
     *
     * @return posición del elemento; si no existe devuelve -(puntoDeInsercion) - 1,
     *         donde puntoDeInsercion es la posición en la que debería insertarse
     *         para mantener la lista ordenada (siempre es un valor negativo).
     */
    public static int busquedaBinaria(List<Emergencia> lista, int codigo) {
        int izquierda = 0;
        int derecha = lista.size() - 1;
        while (izquierda <= derecha) {
            int medio = izquierda + (derecha - izquierda) / 2;
            int actual = lista.get(medio).getCodigo();
            if (actual == codigo) {
                return medio;
            }
            if (actual < codigo) {
                izquierda = medio + 1;
            } else {
                derecha = medio - 1;
            }
        }
        return -(izquierda + 1);
    }
}
