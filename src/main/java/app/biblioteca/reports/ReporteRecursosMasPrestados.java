package app.biblioteca.reports;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.biblioteca.interfaces.RecursoDigital;
import app.biblioteca.services.GestorRecursos;
import app.biblioteca.services.SistemaPrestamos;

/**
 * Reporte que muestra los recursos más prestados
 */
public class ReporteRecursosMasPrestados {

    private SistemaPrestamos sistemaPrestamos;
    private GestorRecursos gestorRecursos;
    private ExecutorService executor;

    public ReporteRecursosMasPrestados(SistemaPrestamos sistemaPrestamos) {
        this.sistemaPrestamos = sistemaPrestamos;
        this.executor = Executors.newSingleThreadExecutor();
    }

    public ReporteRecursosMasPrestados(SistemaPrestamos sistemaPrestamos, GestorRecursos gestorRecursos) {
        this.sistemaPrestamos = sistemaPrestamos;
        this.gestorRecursos = gestorRecursos;
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Genera y muestra el reporte de los recursos más prestados.
     * 
     * @param limite Límite de recursos a mostrar
     */
    public void mostrarReporte(int limite) {
        CompletableFuture.supplyAsync(() -> {
            System.out.println("\n======= REPORTE DE RECURSOS MÁS PRESTADOS =======");
            System.out.println("-------------------------------------------------");

            List<String> recursosMasPrestados = sistemaPrestamos.obtenerRecursosMasPrestados(limite);

            if (recursosMasPrestados.isEmpty()) {
                System.out.println("No hay datos suficientes para generar el reporte.");
                return false;
            }

            System.out.println("Ranking | ID Recurso | Título");
            System.out.println("-------------------------------------------------");

            int ranking = 1;
            for (String idRecurso : recursosMasPrestados) {
                String titulo = "N/A";

                // Si tenemos acceso al gestor de recursos, obtenemos el título
                if (gestorRecursos != null) {
                    RecursoDigital recurso = gestorRecursos.buscarPorId(idRecurso);
                    if (recurso != null) {
                        titulo = recurso.getTitulo();
                    }
                }

                System.out.printf("%-7d | %-10s | %s%n", ranking++, idRecurso, titulo);
            }

            System.out.println("-------------------------------------------------");
            return true;
        }, executor);
    }

    /**
     * Cierra los recursos del reporte
     */
    public void cerrar() {
        executor.shutdown();
    }
}