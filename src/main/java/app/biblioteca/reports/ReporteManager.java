package app.biblioteca.reports;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import app.biblioteca.interfaces.RecursoDigital;
import app.biblioteca.models.CategoriaRecurso;
import app.biblioteca.models.Prestamo;
import app.biblioteca.models.Usuario;
import app.biblioteca.services.SistemaPrestamos;

/**
 * Clase encargada de generar diferentes tipos de reportes
 * sobre la actividad de la biblioteca
 */
public class ReporteManager {
    private final SistemaPrestamos sistemaPrestamos;

    public ReporteManager(SistemaPrestamos sistemaPrestamos) {
        this.sistemaPrestamos = sistemaPrestamos;
    }

    /**
     * Genera un reporte de los recursos más prestados
     * 
     * @param limit Límite de recursos a mostrar (top N)
     * @return Mapa ordenado de recursos y su cantidad de préstamos
     */
    public Map<RecursoDigital, Integer> generarReporteRecursosMasPrestados(int limit) {
        List<Prestamo> todosPrestamos = sistemaPrestamos.listarTodosPrestamos();

        // Crear mapa de conteo de préstamos por recurso
        Map<RecursoDigital, Integer> conteoRecursos = new HashMap<>();

        // Contar préstamos por recurso
        for (Prestamo prestamo : todosPrestamos) {
            RecursoDigital recurso = prestamo.getRecurso();
            conteoRecursos.put(recurso, conteoRecursos.getOrDefault(recurso, 0) + 1);
        }

        // Ordenar por número de préstamos (descendente)
        Map<RecursoDigital, Integer> ordenado = conteoRecursos.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(limit)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));

        return ordenado;
    }

    /**
     * Genera un reporte de recursos más prestados en un período específico
     * 
     * @param desde Fecha inicio del período
     * @param hasta Fecha fin del período
     * @param limit Límite de recursos a mostrar
     * @return Mapa ordenado de recursos y su cantidad de préstamos
     */
    public Map<RecursoDigital, Integer> generarReporteRecursosMasPrestadosPorPeriodo(
            LocalDateTime desde, LocalDateTime hasta, int limit) {

        List<Prestamo> prestamosEnPeriodo = sistemaPrestamos.listarTodosPrestamos().stream()
                .filter(p -> p.getFechaPrestamo().isAfter(desde) &&
                        p.getFechaPrestamo().isBefore(hasta))
                .collect(Collectors.toList());

        // Crear mapa de conteo de préstamos por recurso
        Map<RecursoDigital, Integer> conteoRecursos = new HashMap<>();

        // Contar préstamos por recurso
        for (Prestamo prestamo : prestamosEnPeriodo) {
            RecursoDigital recurso = prestamo.getRecurso();
            conteoRecursos.put(recurso, conteoRecursos.getOrDefault(recurso, 0) + 1);
        }

        // Ordenar por número de préstamos (descendente)
        Map<RecursoDigital, Integer> ordenado = conteoRecursos.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(limit)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));

        return ordenado;
    }

    /**
     * Imprime en consola el reporte de recursos más prestados
     * 
     * @param limit Límite de recursos a mostrar
     */
    public void mostrarReporteRecursosMasPrestados(int limit) {
        Map<RecursoDigital, Integer> reporte = generarReporteRecursosMasPrestados(limit);

        System.out.println("\n=== REPORTE DE RECURSOS MÁS PRESTADOS ===");
        System.out.println("------------------------------------------");

        int posicion = 1;
        for (Map.Entry<RecursoDigital, Integer> entry : reporte.entrySet()) {
            RecursoDigital recurso = entry.getKey();
            int cantidadPrestamos = entry.getValue();

            System.out.printf("%d. %s (ID: %s)\n",
                    posicion,
                    recurso.getTitulo(),
                    recurso.getIdentificador());
            System.out.printf("   Categoría: %s | Préstamos: %d\n",
                    recurso.getCategoria(),
                    cantidadPrestamos);
            System.out.println("   ------------------------------------------");

            posicion++;
        }

        if (reporte.isEmpty()) {
            System.out.println("No hay datos de préstamos para generar el reporte.");
        }
    }
}