package com.emergencias.modelo;

/**
 * Ciclo de vida de una emergencia: PENDIENTE -> EN_ATENCION -> RESUELTA.
 */
public enum EstadoEmergencia {
    PENDIENTE("Pendiente"),
    EN_ATENCION("En atención"),
    RESUELTA("Resuelta");

    private final String etiqueta;

    EstadoEmergencia(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
