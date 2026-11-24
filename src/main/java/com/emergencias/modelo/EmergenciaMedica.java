package com.emergencias.modelo;

import java.time.LocalDateTime;

/**
 * Emergencia que requiere asistencia sanitaria (incluye accidentes con heridos).
 */
public class EmergenciaMedica extends Emergencia {

    public EmergenciaMedica(int codigo, String ubicacion, int gravedad, LocalDateTime fechaIngreso) {
        super(codigo, ubicacion, gravedad, fechaIngreso);
    }

    public EmergenciaMedica(int codigo, String ubicacion, int gravedad) {
        this(codigo, ubicacion, gravedad, LocalDateTime.now());
    }

    @Override
    public String getTipo() {
        return "Médica";
    }

    /** En una emergencia médica el riesgo para la vida coincide con la gravedad. */
    @Override
    public int calcularPrioridad() {
        return getGravedad();
    }

    /** Las médicas se atienden con ambulancia o con una unidad de rescate. */
    @Override
    public boolean admiteUnidad(TipoUnidad tipoUnidad) {
        return tipoUnidad == TipoUnidad.AMBULANCIA || tipoUnidad == TipoUnidad.RESCATE;
    }
}
