package com.emergencias.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase abstracta con los datos y comportamientos comunes de toda emergencia.
 * Cada subclase define su propio comportamiento (polimorfismo):
 * tipo, prioridad y qué unidades de atención admite.
 */
public abstract class Emergencia {

    public static final int GRAVEDAD_MIN = 1;
    public static final int GRAVEDAD_MAX = 5;
    public static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Contador global de ingresos: garantiza un orden estricto de llegada
    // incluso si dos emergencias tienen la misma fecha y hora.
    private static long contadorIngresos = 0;

    private final int codigo;
    private final String ubicacion;
    private final int gravedad;
    private final LocalDateTime fechaIngreso;
    private final long ordenIngreso;
    private EstadoEmergencia estado;
    private UnidadAtencion unidadAsignada;

    protected Emergencia(int codigo, String ubicacion, int gravedad, LocalDateTime fechaIngreso) {
        if (codigo <= 0) {
            throw new OperacionInvalidaException("El código debe ser un entero positivo.");
        }
        if (ubicacion == null || ubicacion.trim().isEmpty()) {
            throw new OperacionInvalidaException("No se puede registrar una emergencia sin ubicación.");
        }
        if (gravedad < GRAVEDAD_MIN || gravedad > GRAVEDAD_MAX) {
            throw new OperacionInvalidaException(
                    "La gravedad debe estar entre " + GRAVEDAD_MIN + " y " + GRAVEDAD_MAX + ".");
        }
        if (fechaIngreso == null) {
            throw new OperacionInvalidaException("La fecha y hora de ingreso no puede ser nula.");
        }
        this.codigo = codigo;
        this.ubicacion = ubicacion.trim();
        this.gravedad = gravedad;
        this.fechaIngreso = fechaIngreso;
        this.ordenIngreso = ++contadorIngresos;
        this.estado = EstadoEmergencia.PENDIENTE;
    }

    // ---------- Comportamiento propio de cada tipo (polimorfismo) ----------

    /** Nombre del tipo de emergencia, para mostrar. */
    public abstract String getTipo();

    /** Prioridad de atención (de 1 a 5): a mayor valor, antes se atiende. */
    public abstract int calcularPrioridad();

    /** Indica si este tipo de emergencia puede ser atendido por el tipo de unidad dado. */
    public abstract boolean admiteUnidad(TipoUnidad tipoUnidad);

    // ---------- Transiciones de estado ----------

    /** PENDIENTE -> EN_ATENCION. Valida estado, compatibilidad y que la unidad esté libre. */
    public void asignarUnidad(UnidadAtencion unidad) {
        if (unidad == null) {
            throw new OperacionInvalidaException("Debe indicar una unidad de atención.");
        }
        if (estado != EstadoEmergencia.PENDIENTE) {
            throw new OperacionInvalidaException(
                    "Solo se puede asignar una unidad a una emergencia pendiente (estado actual: "
                    + estado.getEtiqueta() + ").");
        }
        if (!admiteUnidad(unidad.getTipo())) {
            throw new OperacionInvalidaException(
                    "La unidad " + unidad + " no es compatible con una emergencia " + getTipo() + ".");
        }
        unidad.ocupar(); // falla si la unidad ya está ocupada; si falla, nada cambia
        this.unidadAsignada = unidad;
        this.estado = EstadoEmergencia.EN_ATENCION;
    }

    /** EN_ATENCION -> RESUELTA. Libera automáticamente la unidad asignada. */
    public void resolver() {
        if (estado == EstadoEmergencia.RESUELTA) {
            throw new OperacionInvalidaException("La emergencia " + codigo + " ya fue resuelta.");
        }
        if (estado == EstadoEmergencia.PENDIENTE) {
            throw new OperacionInvalidaException(
                    "No se puede resolver la emergencia " + codigo
                    + " porque está pendiente: primero hay que asignarle una unidad.");
        }
        unidadAsignada.liberar();
        this.estado = EstadoEmergencia.RESUELTA;
    }

    // ---------- Getters ----------

    public int getCodigo() {
        return codigo;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public int getGravedad() {
        return gravedad;
    }

    public LocalDateTime getFechaIngreso() {
        return fechaIngreso;
    }

    public long getOrdenIngreso() {
        return ordenIngreso;
    }

    public EstadoEmergencia getEstado() {
        return estado;
    }

    /** Unidad asignada (se conserva después de resolverla); null si nunca se asignó. */
    public UnidadAtencion getUnidadAsignada() {
        return unidadAsignada;
    }

    @Override
    public String toString() {
        return "Emergencia #" + codigo + " [" + getTipo() + ", gravedad " + gravedad
                + ", " + estado.getEtiqueta() + "]";
    }
}
