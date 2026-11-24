package com.emergencias;

import com.emergencias.modelo.Emergencia;
import com.emergencias.modelo.EmergenciaIncendio;
import com.emergencias.modelo.EmergenciaMedica;
import com.emergencias.modelo.EstadoEmergencia;
import com.emergencias.modelo.OperacionInvalidaException;
import com.emergencias.modelo.TipoUnidad;
import com.emergencias.modelo.UnidadAtencion;
import com.emergencias.servicio.CentralEmergencias;
import com.emergencias.servicio.GeneradorDatos;
import com.emergencias.servicio.PruebaRendimiento;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Punto de entrada: menú de consola de la central de emergencias.
 * Main solo se ocupa de leer datos y mostrar resultados; las reglas están en el modelo y el servicio.
 */
public class Main {

    private static final Scanner ENTRADA = new Scanner(System.in);
    private static final CentralEmergencias CENTRAL = new CentralEmergencias();

    public static void main(String[] args) {
        int opcion = -1;
        try {
            do {
                mostrarMenu();
                opcion = leerEntero("Opción: ");
                try {
                    ejecutar(opcion);
                } catch (OperacionInvalidaException e) {
                    System.out.println("[ERROR] " + e.getMessage());
                }
            } while (opcion != 0);
        } catch (NoSuchElementException e) {
            System.out.println();
            System.out.println("Se cerró la entrada de datos. Fin del programa.");
        }
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("==========================================");
        System.out.println("   CENTRAL DE EMERGENCIAS");
        System.out.println("==========================================");
        System.out.println(" 1. Registrar emergencia");
        System.out.println(" 2. Registrar unidad de atención");
        System.out.println(" 3. Asignar unidad a una emergencia");
        System.out.println(" 4. Consultar emergencias");
        System.out.println(" 5. Buscar emergencia por código");
        System.out.println(" 6. Ver emergencias por prioridad");
        System.out.println(" 7. Ver unidades disponibles");
        System.out.println(" 8. Resolver emergencia");
        System.out.println(" 9. Ver estadísticas");
        System.out.println("10. Pruebas de rendimiento");
        System.out.println("11. Cargar datos de ejemplo (200 emergencias)");
        System.out.println(" 0. Salir");
        System.out.println("------------------------------------------");
    }

    private static void ejecutar(int opcion) {
        switch (opcion) {
            case 1:
                registrarEmergencia();
                break;
            case 2:
                registrarUnidad();
                break;
            case 3:
                asignarUnidad();
                break;
            case 4:
                mostrarEmergencias(CENTRAL.listarEmergencias());
                break;
            case 5:
                buscarEmergencia();
                break;
            case 6:
                mostrarEmergencias(CENTRAL.listarPorPrioridad());
                break;
            case 7:
                mostrarUnidades(CENTRAL.unidadesDisponibles());
                break;
            case 8:
                resolverEmergencia();
                break;
            case 9:
                mostrarEstadisticas();
                break;
            case 10:
                PruebaRendimiento.ejecutar();
                break;
            case 11:
                cargarDatosDemo();
                break;
            case 0:
                System.out.println("Hasta luego.");
                break;
            default:
                System.out.println("[ERROR] Opción inexistente. Elija un número del menú.");
        }
    }

    // ---------- Operaciones del menú ----------

    private static void registrarEmergencia() {
        System.out.println("\n-- Registrar emergencia --");
        int tipo = leerEntero("Tipo (1 = Médica, 2 = Incendio): ");
        if (tipo != 1 && tipo != 2) {
            throw new OperacionInvalidaException("Tipo de emergencia inválido: elija 1 o 2.");
        }
        int codigo = leerEntero("Código (entero positivo): ");
        String ubicacion = leerTexto("Ubicación: ");
        int gravedad = leerEntero("Gravedad (1 a 5): ");

        Emergencia emergencia = (tipo == 1)
                ? new EmergenciaMedica(codigo, ubicacion, gravedad)
                : new EmergenciaIncendio(codigo, ubicacion, gravedad);
        CENTRAL.registrarEmergencia(emergencia);
        System.out.println("[OK] Emergencia registrada. Estado inicial: Pendiente.");
    }

    private static void registrarUnidad() {
        System.out.println("\n-- Registrar unidad de atención --");
        String codigo = leerTexto("Código de la unidad (ej. AMB-01): ");
        int tipo = leerEntero("Tipo (1 = Ambulancia, 2 = Bomberos, 3 = Rescate): ");
        if (tipo < 1 || tipo > TipoUnidad.values().length) {
            throw new OperacionInvalidaException("Tipo de unidad inválido: elija 1, 2 o 3.");
        }
        CENTRAL.registrarUnidad(new UnidadAtencion(codigo, TipoUnidad.values()[tipo - 1]));
        System.out.println("[OK] Unidad registrada y disponible.");
    }

    private static void asignarUnidad() {
        System.out.println("\n-- Asignar unidad a una emergencia --");
        System.out.println("Unidades disponibles:");
        mostrarUnidades(CENTRAL.unidadesDisponibles());
        int codigoEmergencia = leerEntero("Código de la emergencia: ");
        String codigoUnidad = leerTexto("Código de la unidad: ");
        CENTRAL.asignarUnidad(codigoEmergencia, codigoUnidad);
        System.out.println("[OK] Unidad " + codigoUnidad.toUpperCase() + " asignada a la emergencia "
                + codigoEmergencia + ". Estado: En atención.");
    }

    private static void buscarEmergencia() {
        System.out.println("\n-- Buscar emergencia --");
        int codigo = leerEntero("Código: ");
        Emergencia emergencia = CENTRAL.buscarEmergencia(codigo);
        if (emergencia == null) {
            System.out.println("No existe una emergencia con el código " + codigo + ".");
        } else {
            mostrarDetalle(emergencia);
        }
    }

    private static void resolverEmergencia() {
        System.out.println("\n-- Resolver emergencia --");
        int codigo = leerEntero("Código de la emergencia: ");
        CENTRAL.resolverEmergencia(codigo);
        UnidadAtencion unidad = CENTRAL.buscarEmergencia(codigo).getUnidadAsignada();
        System.out.println("[OK] Emergencia " + codigo + " resuelta. Unidad " + unidad.getCodigo() + " liberada.");
    }

    private static void mostrarEstadisticas() {
        System.out.println("\n-- Estadísticas --");
        System.out.println("Emergencias registradas: " + CENTRAL.totalEmergencias());
        System.out.println("  Pendientes:   " + CENTRAL.contarPorEstado(EstadoEmergencia.PENDIENTE));
        System.out.println("  En atención:  " + CENTRAL.contarPorEstado(EstadoEmergencia.EN_ATENCION));
        System.out.println("  Resueltas:    " + CENTRAL.contarPorEstado(EstadoEmergencia.RESUELTA));
    }

    private static void cargarDatosDemo() {
        int cargadas = GeneradorDatos.cargarDatosDemo(CENTRAL);
        System.out.println("[OK] Se cargaron " + cargadas + " emergencias de ejemplo y las unidades AMB-01..04, "
                + "BOM-01..03 y RES-01..02 (los códigos repetidos se omiten).");
    }

    // ---------- Presentación ----------

    private static void mostrarEmergencias(List<Emergencia> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay emergencias registradas.");
            return;
        }
        System.out.printf("%-7s %-9s %-24s %-8s %-17s %-12s %-8s%n",
                "Código", "Tipo", "Ubicación", "Gravedad", "Ingreso", "Estado", "Unidad");
        for (Emergencia e : lista) {
            String unidad = e.getUnidadAsignada() == null ? "-" : e.getUnidadAsignada().getCodigo();
            System.out.printf("%-7d %-9s %-24.24s %-8d %-17s %-12s %-8s%n",
                    e.getCodigo(), e.getTipo(), e.getUbicacion(), e.getGravedad(),
                    e.getFechaIngreso().format(Emergencia.FORMATO_FECHA), e.getEstado().getEtiqueta(), unidad);
        }
        System.out.println("Total: " + lista.size());
    }

    private static void mostrarDetalle(Emergencia e) {
        String unidad = e.getUnidadAsignada() == null ? "Sin asignar" : e.getUnidadAsignada().toString();
        System.out.printf("%-20s%d%n", "Código:", e.getCodigo());
        System.out.printf("%-20s%s%n", "Tipo:", e.getTipo());
        System.out.printf("%-20s%s%n", "Ubicación:", e.getUbicacion());
        System.out.printf("%-20s%d%n", "Gravedad:", e.getGravedad());
        System.out.printf("%-20s%d%n", "Prioridad:", e.calcularPrioridad());
        System.out.printf("%-20s%s%n", "Ingreso:", e.getFechaIngreso().format(Emergencia.FORMATO_FECHA));
        System.out.printf("%-20s%s%n", "Estado:", e.getEstado().getEtiqueta());
        System.out.printf("%-20s%s%n", "Unidad asignada:", unidad);
    }

    private static void mostrarUnidades(List<UnidadAtencion> unidades) {
        if (unidades.isEmpty()) {
            System.out.println("No hay unidades disponibles.");
            return;
        }
        for (UnidadAtencion u : unidades) {
            System.out.println(" - " + u);
        }
        System.out.println("Total disponibles: " + unidades.size());
    }

    // ---------- Lectura de datos (los errores de formato no cierran el programa) ----------

    private static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = ENTRADA.nextLine().trim();
            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Ingrese un número entero válido.");
            }
        }
    }

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return ENTRADA.nextLine().trim();
    }
}
