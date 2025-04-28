package app.biblioteca.reports;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import app.biblioteca.interfaces.RecursoDigital;
import app.biblioteca.models.Prestamo;
import app.biblioteca.services.SistemaPrestamos;

/**
 * Clase especializada en generar reportes de recursos más prestados
 */
public class ReporteRecursosMasPrestados {
    private final SistemaPrestamos sistemaPrestamos;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ReporteRecursosMasPrestados(SistemaPrestamos sistemaPrestamos) {
        this.sistemaPrestamos = sistemaPrestamos;
    }

    /**
     * Genera el reporte de recursos más prestados de todos los tiempos
     * 
     * @param limite Cantidad máxima de recursos a incluir
     * @return Mapa ordenado de recursos y su cantidad de préstamos
     */
    public Map<RecursoDigital, Integer> generarReporte(int limite) {
        List<Prestamo> prestamos = sistemaPrestamos.listarTodosPrestamos();
        return contarYOrdenarPrestamos(prestamos, limite);
    }

    /**
     * Genera el reporte de recursos más prestados en un período específico
     * 
     * @param desde  Fecha de inicio
     * @param hasta  Fecha de fin
     * @param limite Cantidad máxima de recursos a incluir
     * @return Mapa ordenado de recursos y su cantidad de préstamos
     */
    public Map<RecursoDigital, Integer> generarReportePorPeriodo(
            LocalDateTime desde, LocalDateTime hasta, int limite) {

        List<Prestamo> prestamosEnPeriodo = sistemaPrestamos.listarTodosPrestamos().stream()
                .filter(p -> !p.getFechaPrestamo().isBefore(desde) &&
                        !p.getFechaPrestamo().isAfter(hasta))
                .collect(Collectors.toList());

        return contarYOrdenarPrestamos(prestamosEnPeriodo, limite);
    }

    /**
     * Cuenta y ordena los préstamos por recurso
     * 
     * @param prestamos Lista de préstamos a analizar
     * @param limite    Límite de resultados
     * @return Mapa ordenado
     */
    private Map<RecursoDigital, Integer> contarYOrdenarPrestamos(List<Prestamo> prestamos, int limite) {
        // Agrupar los préstamos por recurso y contar
        Map<RecursoDigital, Long> conteoPrestamos = prestamos.stream()
                .collect(Collectors.groupingBy(Prestamo::getRecurso, Collectors.counting()));

        // Convertir a Map<RecursoDigital, Integer> y ordenar por cantidad (descendente)
        Map<RecursoDigital, Integer> resultado = conteoPrestamos.entrySet().stream()
                .sorted(Map.Entry.<RecursoDigital, Long>comparingByValue().reversed())
                .limit(limite)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().intValue(),
                        (e1, e2) -> e1,
                        LinkedHashMap::new));

        return resultado;
    }

    /**
     * Muestra el reporte en la consola con formato amigable
     * 
     * @param limite Cantidad máxima de recursos a mostrar
     */
    public void mostrarReporte(int limite) {
        Map<RecursoDigital, Integer> reporte = generarReporte(limite);
        mostrarTablaResultados(reporte, "REPORTE DE RECURSOS MÁS PRESTADOS (TODOS LOS TIEMPOS)", limite);
    }

    /**
     * Muestra el reporte por período en la consola con formato amigable
     * 
     * @param desde  Fecha de inicio
     * @param hasta  Fecha de fin
     * @param limite Cantidad máxima de recursos a mostrar
     */
    public void mostrarReportePorPeriodo(LocalDateTime desde, LocalDateTime hasta, int limite) {
        Map<RecursoDigital, Integer> reporte = generarReportePorPeriodo(desde, hasta, limite);

        String titulo = String.format("REPORTE DE RECURSOS MÁS PRESTADOS (%s - %s)",
                desde.format(FORMATO_FECHA),
                hasta.format(FORMATO_FECHA));

        mostrarTablaResultados(reporte, titulo, limite);
    }

    /**
     * Muestra una tabla formateada con los resultados del reporte
     * 
     * @param reporte Datos del reporte
     * @param titulo  Título a mostrar
     * @param limite  Cantidad máxima a mostrar
     */
    private void mostrarTablaResultados(Map<RecursoDigital, Integer> reporte, String titulo, int limite) {
        System.out.println("\n=== " + titulo + " ===");
        System.out.println("-------------------------------------------------------");
        System.out.printf("%-5s %-30s %-15s %-10s\n",
                "POS", "TÍTULO", "CATEGORÍA", "PRÉSTAMOS");
        System.out.println("-------------------------------------------------------");

        if (reporte.isEmpty()) {
            System.out.println("No hay datos de préstamos para generar el reporte.");
            return;
        }

        int posicion = 1;
        for (Map.Entry<RecursoDigital, Integer> entry : reporte.entrySet()) {
            RecursoDigital recurso = entry.getKey();
            Integer cantidadPrestamos = entry.getValue();

            // Acortar título si es muy largo
            String titulo_acortado = recurso.getTitulo();
            if (titulo_acortado.length() > 27) {
                titulo_acortado = titulo_acortado.substring(0, 27) + "...";
            }

            System.out.printf("%-5d %-30s %-15s %-10d\n",
                    posicion,
                    titulo_acortado,
                    recurso.getCategoria(),
                    cantidadPrestamos);

            posicion++;

            // Limitamos a la cantidad especificada
            if (posicion > limite) {
                break;
            }
        }

        System.out.println("-------------------------------------------------------");
    }
}