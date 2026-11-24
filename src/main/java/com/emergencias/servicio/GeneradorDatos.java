package com.emergencias.servicio;

import com.emergencias.modelo.Emergencia;
import com.emergencias.modelo.EmergenciaIncendio;
import com.emergencias.modelo.EmergenciaMedica;
import com.emergencias.modelo.OperacionInvalidaException;
import com.emergencias.modelo.TipoUnidad;
import com.emergencias.modelo.UnidadAtencion;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

/**
 * Genera datos en memoria para las demostraciones y las pruebas de rendimiento.
 */
public final class GeneradorDatos {

    private static final String[] CALLES = {
        "Av. San Martín", "Belgrano", "Sarmiento", "Rivadavia",
        "Mitre", "Alem", "Moreno", "9 de Julio"
    };

    private GeneradorDatos() {
    }

    /**
     * Genera {@code cantidad} emergencias con códigos únicos (1..cantidad) en orden aleatorio,
     * gravedad y tipo aleatorios, y fechas de ingreso crecientes (un minuto entre cada una).
     * La misma semilla produce siempre los mismos datos.
     */
    public static ArrayList<Emergencia> generarEmergencias(int cantidad, long semilla) {
        Random azar = new Random(semilla);

        // Códigos 1..cantidad mezclados con Fisher-Yates.
        int[] codigos = new int[cantidad];
        for (int i = 0; i < cantidad; i++) {
            codigos[i] = i + 1;
        }
        for (int i = cantidad - 1; i > 0; i--) {
            int j = azar.nextInt(i + 1);
            int temporal = codigos[i];
            codigos[i] = codigos[j];
            codigos[j] = temporal;
        }

        LocalDateTime base = LocalDateTime.of(2026, 1, 1, 0, 0);
        ArrayList<Emergencia> lista = new ArrayList<>(cantidad);
        for (int i = 0; i < cantidad; i++) {
            String ubicacion = CALLES[azar.nextInt(CALLES.length)] + " " + (100 + azar.nextInt(2900));
            int gravedad = Emergencia.GRAVEDAD_MIN + azar.nextInt(Emergencia.GRAVEDAD_MAX);
            LocalDateTime ingreso = base.plusMinutes(i);
            if (azar.nextBoolean()) {
                lista.add(new EmergenciaMedica(codigos[i], ubicacion, gravedad, ingreso));
            } else {
                lista.add(new EmergenciaIncendio(codigos[i], ubicacion, gravedad, ingreso));
            }
        }
        return lista;
    }

    /**
     * Carga en la central 200 emergencias y 9 unidades de ejemplo.
     * Los códigos que ya existían se omiten.
     *
     * @return cantidad de emergencias que se cargaron
     */
    public static int cargarDatosDemo(CentralEmergencias central) {
        int cargadas = 0;
        for (Emergencia emergencia : generarEmergencias(200, 2026)) {
            try {
                central.registrarEmergencia(emergencia);
                cargadas++;
            } catch (OperacionInvalidaException e) {
                // código ya registrado: se omite
            }
        }
        cargarUnidades(central, "AMB-0", TipoUnidad.AMBULANCIA, 4);
        cargarUnidades(central, "BOM-0", TipoUnidad.BOMBEROS, 3);
        cargarUnidades(central, "RES-0", TipoUnidad.RESCATE, 2);
        return cargadas;
    }

    private static void cargarUnidades(CentralEmergencias central, String prefijo, TipoUnidad tipo, int cantidad) {
        for (int i = 1; i <= cantidad; i++) {
            try {
                central.registrarUnidad(new UnidadAtencion(prefijo + i, tipo));
            } catch (OperacionInvalidaException e) {
                // unidad ya registrada: se omite
            }
        }
    }
}
