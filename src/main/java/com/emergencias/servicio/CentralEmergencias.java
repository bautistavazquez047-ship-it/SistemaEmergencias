package com.emergencias.servicio;

import com.emergencias.algoritmos.Busquedas;
import com.emergencias.algoritmos.Comparadores;
import com.emergencias.algoritmos.Ordenamientos;
import com.emergencias.modelo.Emergencia;
import com.emergencias.modelo.EstadoEmergencia;
import com.emergencias.modelo.OperacionInvalidaException;
import com.emergencias.modelo.UnidadAtencion;
import java.util.ArrayList;
import java.util.List;

/**
 * Administra las emergencias, las unidades de atención y las asignaciones.
 *
 * Decisión de diseño: la lista de emergencias se mantiene SIEMPRE ordenada por código
 * (se inserta en su posición con búsqueda binaria). Así, tanto la validación de códigos
 * duplicados como la localización de una emergencia cuestan O(log n).
 */
public class CentralEmergencias {

    private final ArrayList<Emergencia> emergencias = new ArrayList<>(); // ordenada por código
    private final ArrayList<UnidadAtencion> unidades = new ArrayList<>();

    // ---------- RF01 / RF02: registros ----------

    public void registrarEmergencia(Emergencia emergencia) {
        if (emergencia == null) {
            throw new OperacionInvalidaException("La emergencia no puede ser nula.");
        }
        int posicion = Busquedas.busquedaBinaria(emergencias, emergencia.getCodigo());
        if (posicion >= 0) {
            throw new OperacionInvalidaException(
                    "Ya existe una emergencia con el código " + emergencia.getCodigo() + ".");
        }
        emergencias.add(-posicion - 1, emergencia); // punto de inserción que devolvió la búsqueda
    }

    public void registrarUnidad(UnidadAtencion unidad) {
        if (unidad == null) {
            throw new OperacionInvalidaException("La unidad no puede ser nula.");
        }
        if (buscarUnidad(unidad.getCodigo()) != null) {
            throw new OperacionInvalidaException(
                    "Ya existe una unidad con el código " + unidad.getCodigo() + ".");
        }
        unidades.add(unidad);
    }

    // ---------- RF05: localizar ----------

    /** Búsqueda binaria por código. Devuelve null si no existe. */
    public Emergencia buscarEmergencia(int codigo) {
        int posicion = Busquedas.busquedaBinaria(emergencias, codigo);
        return posicion >= 0 ? emergencias.get(posicion) : null;
    }

    /** Búsqueda lineal (hay pocas unidades). Devuelve null si no existe. */
    public UnidadAtencion buscarUnidad(String codigo) {
        if (codigo == null) {
            return null;
        }
        String buscado = codigo.trim();
        for (int i = 0; i < unidades.size(); i++) {
            if (unidades.get(i).getCodigo().equalsIgnoreCase(buscado)) {
                return unidades.get(i);
            }
        }
        return null;
    }

    // ---------- RF03 / RF08: asignar y resolver ----------

    public void asignarUnidad(int codigoEmergencia, String codigoUnidad) {
        Emergencia emergencia = buscarEmergencia(codigoEmergencia);
        if (emergencia == null) {
            throw new OperacionInvalidaException("No existe una emergencia con el código " + codigoEmergencia + ".");
        }
        UnidadAtencion unidad = buscarUnidad(codigoUnidad);
        if (unidad == null) {
            throw new OperacionInvalidaException("No existe una unidad con el código " + codigoUnidad + ".");
        }
        emergencia.asignarUnidad(unidad); // valida estado, compatibilidad y disponibilidad
    }

    public void resolverEmergencia(int codigoEmergencia) {
        Emergencia emergencia = buscarEmergencia(codigoEmergencia);
        if (emergencia == null) {
            throw new OperacionInvalidaException("No existe una emergencia con el código " + codigoEmergencia + ".");
        }
        emergencia.resolver(); // valida estado y libera la unidad
    }

    // ---------- RF04 / RF06 / RF07: consultas ----------

    /** Copia de las emergencias ordenada por código. */
    public List<Emergencia> listarEmergencias() {
        return new ArrayList<>(emergencias);
    }

    /** Copia de las emergencias de más graves a menos graves; a igual gravedad, la que ingresó antes. */
    public List<Emergencia> listarPorPrioridad() {
        List<Emergencia> copia = new ArrayList<>(emergencias);
        Ordenamientos.mergeSort(copia, Comparadores.POR_PRIORIDAD);
        return copia;
    }

    public List<UnidadAtencion> unidadesDisponibles() {
        List<UnidadAtencion> disponibles = new ArrayList<>();
        for (int i = 0; i < unidades.size(); i++) {
            if (unidades.get(i).isDisponible()) {
                disponibles.add(unidades.get(i));
            }
        }
        return disponibles;
    }

    // ---------- RF09: estadísticas ----------

    public int contarPorEstado(EstadoEmergencia estado) {
        int cantidad = 0;
        for (int i = 0; i < emergencias.size(); i++) {
            if (emergencias.get(i).getEstado() == estado) {
                cantidad++;
            }
        }
        return cantidad;
    }

    public int totalEmergencias() {
        return emergencias.size();
    }
}
