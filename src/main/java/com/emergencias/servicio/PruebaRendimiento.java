package com.emergencias.servicio;

import com.emergencias.algoritmos.Busquedas;
import com.emergencias.algoritmos.Comparadores;
import com.emergencias.algoritmos.Ordenamientos;
import com.emergencias.modelo.Emergencia;
import java.util.ArrayList;
import java.util.List;

/**
 * RF10: mide con System.nanoTime() las búsquedas (lineal y binaria) y los ordenamientos
 * (inserción y Merge Sort) sobre los mismos datos, con 100, 1.000 y 10.000 registros.
 *
 * Metodología:
 *  - Los datos se generan en memoria y su preparación se mide por separado.
 *  - Cada prueba se repite REPETICIONES veces (10, el mínimo pedido es 5) y se informa el promedio.
 *  - Búsquedas: cada repetición ejecuta LLAMADAS_POR_MEDICION búsquedas seguidas y se
 *    divide por esa cantidad, porque una sola búsqueda dura menos que la resolución del reloj.
 *  - Ordenamientos: ambos algoritmos trabajan sobre copias idénticas de los datos originales,
 *    y las copias se hacen fuera de la medición.
 *  - Calentamiento: se hace una pasada completa previa cuyos tiempos se descartan, para que el
 *    compilador JIT de la JVM ya haya optimizado el código cuando empieza la medición real.
 */
public final class PruebaRendimiento {

    private static final int[] TAMANIOS = {100, 1000, 10000};
    private static final int REPETICIONES = 10;
    private static final int LLAMADAS_POR_MEDICION = 1000;
    private static final long SEMILLA = 42;

    // Se acumulan los resultados de las búsquedas aquí para que la JVM no pueda descartarlas.
    private static int sumidero;

    private PruebaRendimiento() {
    }

    /** Tiempos promedio (en ns) de una pasada completa, un valor por cada tamaño. */
    private static final class Resultados {
        final long[] generacion = new long[TAMANIOS.length];
        final long[] ordenPrevio = new long[TAMANIOS.length];
        final long[] linealExiste = new long[TAMANIOS.length];
        final long[] binariaExiste = new long[TAMANIOS.length];
        final long[] linealNoExiste = new long[TAMANIOS.length];
        final long[] binariaNoExiste = new long[TAMANIOS.length];
        final long[] simple = new long[TAMANIOS.length];
        final long[] merge = new long[TAMANIOS.length];
        boolean mismoResultado = true;
    }

    public static void ejecutar() {
        System.out.println();
        System.out.println("=== PRUEBAS DE RENDIMIENTO (System.nanoTime) ===");
        System.out.printf("Repeticiones por prueba: %d | búsquedas por medición: %d | semilla de datos: %d%n",
                REPETICIONES, LLAMADAS_POR_MEDICION, SEMILLA);
        System.out.println("Calentando la JVM (pasada previa que no se registra)...");
        medirTodo();
        System.out.println("Midiendo...");
        Resultados r = medirTodo();

        imprimirTabla("Preparación de los datos (promedio)",
                "Generar datos (ns)", "Ordenar por código (ns)", r.generacion, r.ordenPrevio, false);
        imprimirTabla("Búsqueda de un código EXISTENTE (promedio por búsqueda)",
                "Búsqueda lineal (ns)", "Búsqueda binaria (ns)", r.linealExiste, r.binariaExiste, true);
        imprimirTabla("Búsqueda de un código INEXISTENTE (promedio por búsqueda)",
                "Búsqueda lineal (ns)", "Búsqueda binaria (ns)", r.linealNoExiste, r.binariaNoExiste, true);
        imprimirTabla("Ordenamiento por prioridad (promedio)",
                "Inserción (ns)", "Merge Sort (ns)", r.simple, r.merge, true);

        System.out.println();
        System.out.println("Verificación: ambos ordenamientos dieron el mismo resultado en todas las pruebas: "
                + (r.mismoResultado ? "SÍ" : "NO"));
        System.out.println("Nota: la búsqueda binaria se midió sobre datos ya ordenados; el costo de ordenarlos "
                + "figura en la tabla de preparación.");
    }

    // ---------- Una pasada completa de mediciones ----------

    private static Resultados medirTodo() {
        Resultados res = new Resultados();

        for (int k = 0; k < TAMANIOS.length; k++) {
            int n = TAMANIOS[k];

            // ---- Preparación de los datos (se mide por separado) ----
            long sumaGeneracion = 0;
            long sumaOrdenPrevio = 0;
            ArrayList<Emergencia> datos = null;
            ArrayList<Emergencia> porCodigo = null;
            for (int r = 0; r < REPETICIONES; r++) {
                long inicio = System.nanoTime();
                datos = GeneradorDatos.generarEmergencias(n, SEMILLA);
                sumaGeneracion += System.nanoTime() - inicio;

                ArrayList<Emergencia> copia = new ArrayList<>(datos);
                inicio = System.nanoTime();
                Ordenamientos.mergeSort(copia, Comparadores.POR_CODIGO); // la binaria necesita datos ordenados
                sumaOrdenPrevio += System.nanoTime() - inicio;
                porCodigo = copia;
            }
            res.generacion[k] = sumaGeneracion / REPETICIONES;
            res.ordenPrevio[k] = sumaOrdenPrevio / REPETICIONES;

            // ---- Búsquedas: mismo identificador para ambos algoritmos ----
            System.gc(); // limpia memoria antes de medir, para que un GC no caiga dentro de una medición
            int existente = datos.get(n / 2).getCodigo(); // un código que está en los datos
            int inexistente = n + 1;                      // los códigos van de 1 a n
            res.linealExiste[k] = medirLineal(datos, existente);
            res.binariaExiste[k] = medirBinaria(porCodigo, existente);
            res.linealNoExiste[k] = medirLineal(datos, inexistente);
            res.binariaNoExiste[k] = medirBinaria(porCodigo, inexistente);

            // ---- Ordenamientos sobre copias idénticas ----
            System.gc();
            long sumaSimple = 0;
            long sumaMerge = 0;
            for (int r = 0; r < REPETICIONES; r++) {
                ArrayList<Emergencia> copiaSimple = new ArrayList<>(datos);
                ArrayList<Emergencia> copiaMerge = new ArrayList<>(datos);

                long inicio = System.nanoTime();
                Ordenamientos.insercion(copiaSimple, Comparadores.POR_PRIORIDAD);
                sumaSimple += System.nanoTime() - inicio;

                inicio = System.nanoTime();
                Ordenamientos.mergeSort(copiaMerge, Comparadores.POR_PRIORIDAD);
                sumaMerge += System.nanoTime() - inicio;

                if (!mismoOrden(copiaSimple, copiaMerge)) {
                    res.mismoResultado = false;
                }
            }
            res.simple[k] = sumaSimple / REPETICIONES;
            res.merge[k] = sumaMerge / REPETICIONES;
        }
        return res;
    }

    // ---------- Mediciones ----------

    private static long medirLineal(List<Emergencia> lista, int codigo) {
        long total = 0;
        for (int r = 0; r < REPETICIONES; r++) {
            int acumulado = 0;
            long inicio = System.nanoTime();
            for (int i = 0; i < LLAMADAS_POR_MEDICION; i++) {
                acumulado += Busquedas.busquedaLineal(lista, codigo);
            }
            total += System.nanoTime() - inicio;
            sumidero += acumulado;
        }
        return total / ((long) REPETICIONES * LLAMADAS_POR_MEDICION);
    }

    private static long medirBinaria(List<Emergencia> lista, int codigo) {
        long total = 0;
        for (int r = 0; r < REPETICIONES; r++) {
            int acumulado = 0;
            long inicio = System.nanoTime();
            for (int i = 0; i < LLAMADAS_POR_MEDICION; i++) {
                acumulado += Busquedas.busquedaBinaria(lista, codigo);
            }
            total += System.nanoTime() - inicio;
            sumidero += acumulado;
        }
        return total / ((long) REPETICIONES * LLAMADAS_POR_MEDICION);
    }

    private static boolean mismoOrden(List<Emergencia> a, List<Emergencia> b) {
        if (a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); i++) {
            if (a.get(i) != b.get(i)) {
                return false;
            }
        }
        return true;
    }

    // ---------- Presentación ----------

    private static void imprimirTabla(String titulo, String columnaA, String columnaB,
                                      long[] a, long[] b, boolean mostrarRelacion) {
        System.out.println();
        System.out.println(titulo);
        if (mostrarRelacion) {
            System.out.printf("%-10s %24s %24s %14s%n", "Cantidad", columnaA, columnaB, "Veces (A/B)");
        } else {
            System.out.printf("%-10s %24s %24s%n", "Cantidad", columnaA, columnaB);
        }
        for (int k = 0; k < TAMANIOS.length; k++) {
            if (mostrarRelacion) {
                String relacion = b[k] == 0 ? "-" : String.format("%.1fx", (double) a[k] / b[k]);
                System.out.printf("%-10s %24s %24s %14s%n",
                        String.format("%,d", TAMANIOS[k]), String.format("%,d", a[k]),
                        String.format("%,d", b[k]), relacion);
            } else {
                System.out.printf("%-10s %24s %24s%n",
                        String.format("%,d", TAMANIOS[k]), String.format("%,d", a[k]), String.format("%,d", b[k]));
            }
        }
    }
}
