package com.emergencias.modelo;

/**
 * Recurso que puede atender una emergencia. Solo puede estar asignada
 * a una emergencia a la vez: mientras está ocupada no puede reasignarse.
 */
public class UnidadAtencion {

    private final String codigo;
    private final TipoUnidad tipo;
    private boolean disponible;

    public UnidadAtencion(String codigo, TipoUnidad tipo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new OperacionInvalidaException("El código de la unidad no puede estar vacío.");
        }
        if (tipo == null) {
            throw new OperacionInvalidaException("La unidad debe tener un tipo.");
        }
        this.codigo = codigo.trim().toUpperCase();
        this.tipo = tipo;
        this.disponible = true; // toda unidad nueva queda lista para asignarse
    }

    public String getCodigo() {
        return codigo;
    }

    public TipoUnidad getTipo() {
        return tipo;
    }

    public boolean isDisponible() {
        return disponible;
    }

    /** Marca la unidad como ocupada. Falla si ya lo estaba. */
    public void ocupar() {
        if (!disponible) {
            throw new OperacionInvalidaException(
                    "La unidad " + codigo + " está ocupada y no puede asignarse.");
        }
        disponible = false;
    }

    /** Libera la unidad para que pueda atender otra emergencia. */
    public void liberar() {
        disponible = true;
    }

    @Override
    public String toString() {
        return codigo + " (" + tipo.getEtiqueta() + ")";
    }
}
