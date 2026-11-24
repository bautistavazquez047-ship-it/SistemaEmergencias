package com.emergencias.modelo;

import java.time.LocalDateTime;

/**
 * Emergencia relacionada con incendios.
 */
public class EmergenciaIncendio extends Emergencia {

    public EmergenciaIncendio(int codigo, String ubicacion, int gravedad, LocalDateTime fechaIngreso) {
        super(codigo, ubicacion, gravedad, fechaIngreso);
    }

    public EmergenciaIncendio(int codigo, String ubicacion, int gravedad) {
        this(codigo, ubicacion, gravedad, LocalDateTime.now());
    }

    @Override
    public String getTipo() {
        return "Incendio";
    }

    /** En un incendio la gravedad ya refleja el riesgo de propagación, así que define la prioridad. */
    @Override
    public int calcularPrioridad() {
        return getGravedad();
    }

    /** Los incendios se atienden con bomberos o con una unidad de rescate. */
    @Override
    public boolean admiteUnidad(TipoUnidad tipoUnidad) {
        return tipoUnidad == TipoUnidad.BOMBEROS || tipoUnidad == TipoUnidad.RESCATE;
    }
}
