package com.emergencias.algoritmos;

import com.emergencias.modelo.Emergencia;
import java.util.Comparator;

/**
 * Criterios de comparación entre emergencias. Se escriben con comparaciones
 * simples (sin Integer.compare ni métodos de ordenamiento de la biblioteca).
 */
public final class Comparadores {

    private Comparadores() {
    }

    /** Orden ascendente por código identificador (necesario para la búsqueda binaria). */
    public static final Comparator<Emergencia> POR_CODIGO = (a, b) -> {
        if (a.getCodigo() < b.getCodigo()) {
            return -1;
        }
        if (a.getCodigo() > b.getCodigo()) {
            return 1;
        }
        return 0;
    };

    /**
     * Orden de atención: primero la mayor prioridad (de más grave a menos grave).
     * Ante igual prioridad, primero la que ingresó antes.
     */
    public static final Comparator<Emergencia> POR_PRIORIDAD = (a, b) -> {
        int prioridadA = a.calcularPrioridad();
        int prioridadB = b.calcularPrioridad();
        if (prioridadA != prioridadB) {
            return prioridadA > prioridadB ? -1 : 1;
        }
        if (a.getFechaIngreso().isBefore(b.getFechaIngreso())) {
            return -1;
        }
        if (a.getFechaIngreso().isAfter(b.getFechaIngreso())) {
            return 1;
        }
        // Misma fecha y hora: decide el orden de llegada.
        if (a.getOrdenIngreso() < b.getOrdenIngreso()) {
            return -1;
        }
        if (a.getOrdenIngreso() > b.getOrdenIngreso()) {
            return 1;
        }
        return 0;
    };
}
