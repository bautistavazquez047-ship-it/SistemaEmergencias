package com.emergencias.modelo;

/**
 * Tipos de unidad de atención que maneja la central.
 */
public enum TipoUnidad {
    AMBULANCIA("Ambulancia"),
    BOMBEROS("Bomberos"),
    RESCATE("Rescate");

    private final String etiqueta;

    TipoUnidad(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
