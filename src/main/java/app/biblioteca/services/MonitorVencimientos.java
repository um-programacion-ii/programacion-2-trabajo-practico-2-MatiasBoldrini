package app.biblioteca.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import app.biblioteca.models.Prestamo;

public class MonitorVencimientos {
    private final SistemaPrestamos sistemaPrestamos;
    private final ServicioNotificacionManager notificacionManager;
    private ScheduledExecutorService scheduler;
    private static final int PERIODO_REVISION_HORAS = 24; // Revisar cada 24 horas

    public MonitorVencimientos(SistemaPrestamos sistemaPrestamos, ServicioNotificacionManager notificacionManager) {
        this.sistemaPrestamos = sistemaPrestamos;
        this.notificacionManager = notificacionManager;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void iniciar() {
        scheduler.scheduleAtFixedRate(
                this::revisarVencimientos,
                0, // Iniciar inmediatamente
                PERIODO_REVISION_HORAS,
                TimeUnit.HOURS);

        System.out.println("Monitor de vencimientos iniciado. Revisará cada " + PERIODO_REVISION_HORAS + " horas.");
    }

    private void revisarVencimientos() {
        try {
            System.out.println("Revisando vencimientos de préstamos...");

            // Obtener todos los préstamos activos
            List<Prestamo> prestamosActivos = sistemaPrestamos.listarPrestamosActivos();

            // Revisar cada préstamo
            for (Prestamo prestamo : prestamosActivos) {
                // Obtener los días hasta el vencimiento
                long diasHastaVencimiento = prestamo.diasHastaVencimiento();

                // Préstamos ya vencidos
                if (diasHastaVencimiento < 0) {
                    notificacionManager.enviarNotificacionVencimiento(prestamo);
                    continue;
                }

                // Préstamos que vencen pronto
                if (diasHastaVencimiento <= 3) {
                    notificacionManager.enviarNotificacionProximoVencimiento(prestamo, diasHastaVencimiento);
                }
            }

            // Contar y mostrar estadísticas
            long vencidos = prestamosActivos.stream()
                    .filter(Prestamo::estaVencido)
                    .count();

            long porVencer = prestamosActivos.stream()
                    .filter(p -> !p.estaVencido() && p.diasHastaVencimiento() <= 3)
                    .count();

            System.out.println(
                    "Revisión finalizada. Préstamos vencidos: " + vencidos + ", Por vencer pronto: " + porVencer);

        } catch (Exception e) {
            System.err.println("Error al revisar vencimientos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void detener() {
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
            System.out.println("Monitor de vencimientos detenido.");
        }
    }
}