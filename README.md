# Sistema de Gestión de Emergencias y Asignación de Recursos

Trabajo Integrador Final – Programación II 2026 (Proyecto 1)
**Carrera:** Técnico Superior en Desarrollo de Software
**Alumno:** Bautista Vazquez
**Lenguaje:** Java (aplicación de consola, modalidad individual)

## Descripción

Una central de emergencias necesita registrar incidentes, administrar sus unidades de atención
(ambulancias, bomberos, rescate), asignarlas a las emergencias pendientes y saber en todo momento
qué atender primero. La aplicación resuelve eso con un menú de consola e integra programación
orientada a objetos, estructuras de datos (`ArrayList`), búsquedas y ordenamientos programados a
mano, validaciones, manejo de excepciones y análisis de eficiencia.

## Funcionalidades

| Requisito | Opción del menú |
|-----------|-----------------|
| RF01 Registrar emergencias | 1 |
| RF02 Registrar unidades de atención | 2 |
| RF03 Asignar recursos | 3 |
| RF04 Consultar emergencias | 4 |
| RF05 Localizar una emergencia por código (búsqueda binaria) | 5 |
| RF06 Ver emergencias por prioridad (Merge Sort) | 6 |
| RF07 Consultar unidades disponibles | 7 |
| RF08 Resolver emergencias (libera la unidad) | 8 |
| RF09 Estadísticas (pendientes / en atención / resueltas) | 9 |
| RF10 Pruebas de rendimiento con `System.nanoTime()` | 10 |
| Cargar 200 emergencias y 9 unidades de ejemplo | 11 |

## Requisitos

- JDK 8 o superior (probado con JDK 21.0.9). No usa librerías externas ni Maven.

## Cómo compilar y ejecutar

Desde la carpeta raíz del proyecto.

**Linux / macOS**
```bash
find src -name "*.java" > fuentes.txt
javac -encoding UTF-8 -d out @fuentes.txt
java -cp out com.emergencias.Main
```

**Windows (cmd)**
```bat
dir /s /b src\*.java > fuentes.txt
javac -encoding UTF-8 -d out @fuentes.txt
chcp 65001
java -Dfile.encoding=UTF-8 -cp out com.emergencias.Main
```
(`chcp 65001` y `-Dfile.encoding=UTF-8` sirven para que los acentos se vean bien en la consola.)

**VS Code:** instalar la extensión *Extension Pack for Java*, abrir la carpeta `SistemaEmergencias`
y ejecutar `Main.java` con el botón *Run* que aparece sobre el método `main`. Si VS Code no detecta
`src/main/java` como carpeta de fuentes, crear `.vscode/settings.json` con:
```json
{ "java.project.sourcePaths": ["src/main/java"] }
```
También se puede compilar y ejecutar con los comandos de arriba desde la terminal integrada.

Para probar rápido: elegir la opción **11** (datos de ejemplo) y luego la 6, la 5 o la 10.

## Estructura

```text
SistemaEmergencias/
├── .gitignore
├── README.md
├── docs/
│   ├── diagrama-clases.png
│   ├── explicacion-uml.txt
│   └── informe-tecnico.pdf
└── src/main/java/com/emergencias/
    ├── Main.java              menú de consola
    ├── modelo/                Emergencia (abstracta), EmergenciaMedica, EmergenciaIncendio,
    │                          UnidadAtencion, TipoUnidad, EstadoEmergencia,
    │                          OperacionInvalidaException
    ├── servicio/              CentralEmergencias, GeneradorDatos, PruebaRendimiento
    └── algoritmos/            Busquedas, Ordenamientos, Comparadores
```

## Decisiones de diseño

- **Herencia y polimorfismo:** `Emergencia` es abstracta y declara `getTipo()`, `calcularPrioridad()`
  y `admiteUnidad(TipoUnidad)`; `EmergenciaMedica` y `EmergenciaIncendio` los implementan a su manera.
- **Compatibilidad de unidades:** médica → ambulancia o rescate; incendio → bomberos o rescate.
- **Prioridad:** coincide con la gravedad (1 a 5). Se ordena de mayor a menor y, ante igual gravedad,
  primero la que ingresó antes (fecha y hora; si coinciden, orden de llegada).
- **Lista siempre ordenada por código:** `CentralEmergencias` inserta cada emergencia en su lugar,
  así la validación de códigos duplicados y la localización por código usan búsqueda binaria.
- **Estados:** `PENDIENTE → EN_ATENCION → RESUELTA`. No se puede resolver una emergencia pendiente
  ni resolver dos veces la misma.
- **Errores:** las reglas del dominio lanzan `OperacionInvalidaException`; el menú la captura y
  vuelve a mostrarse sin cerrar el programa. Los datos numéricos mal escritos se vuelven a pedir.
- **Restricciones respetadas:** no se usa `Collections.sort`, `Arrays.sort`, `Collections.binarySearch`,
  `indexOf`/`contains` ni streams para buscar u ordenar.

## Algoritmos y complejidad

| Algoritmo | Mejor caso | Caso promedio | Peor caso | Notas |
|-----------|-----------|---------------|-----------|-------|
| Búsqueda lineal | O(1) | O(n) | O(n) | no requiere datos ordenados |
| Búsqueda binaria | O(1) | O(log n) | O(log n) | requiere datos ordenados por código |
| Ordenamiento por inserción | O(n) | O(n²) | O(n²) | simple, estable, espacio O(1) |
| Merge Sort | O(n log n) | O(n log n) | O(n log n) | recursivo, estable, espacio O(n) |

## Pruebas de rendimiento (opción 10)

Mide con `System.nanoTime()` sobre 100, 1.000 y 10.000 emergencias generadas en memoria:
búsqueda lineal vs. binaria (código existente e inexistente) e inserción vs. Merge Sort sobre
copias idénticas. La preparación de datos se mide aparte, cada prueba se repite 10 veces y se
informa el promedio. Antes de medir se hace una pasada de calentamiento que se descarta.
Los tiempos varían según la computadora y lo que esté corriendo en ese momento.