package app.biblioteca.services;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import app.biblioteca.models.Prestamo;

/**
 * Monitor que verifica periódicamente los préstamos por vencer y envía alertas
 */
public class MonitorVencimientos {

    private SistemaPrestamos sistemaPrestamos;
    private ServicioNotificacionManager notificacionManager;
    private ScheduledExecutorService scheduler;
    private boolean ejecutando;

    public MonitorVencimientos(SistemaPrestamos sistemaPrestamos,
            ServicioNotificacionManager notificacionManager) {
        this.sistemaPrestamos = sistemaPrestamos;
        this.notificacionManager = notificacionManager;
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.ejecutando = false;
    }

    /**
     * Inicia el monitor de vencimientos
     */
    public void iniciar() {
        if (!ejecutando) {
            ejecutando = true;

            // Programar la verificación cada 30 segundos (para demostración)
            // En un sistema real podría ser diario
            scheduler.scheduleAtFixedRate(this::verificarVencimientos, 5, 30, TimeUnit.SECONDS);

            System.out.println("Monitor de vencimientos iniciado");
        }
    }

    /**
     * Detiene el monitor de vencimientos
     */
    public void detener() {
        if (ejecutando) {
            ejecutando = false;
            scheduler.shutdown();

            System.out.println("Monitor de vencimientos detenido");
        }
    }

    /**
     * Verifica los préstamos que están por vencer y envía alertas
     */
    private void verificarVencimientos() {
        try {
            System.out.println("\n==== Verificando préstamos por vencer ====");

            // Obtener préstamos por vencer en los próximos 3 días
            List<Prestamo> prestamosPorVencer = sistemaPrestamos.obtenerPrestamosPorVencer(3);

            for (Prestamo prestamo : prestamosPorVencer) {
                // Calcular días restantes hasta vencimiento
                long diasRestantes = LocalDateTime.now().until(prestamo.getFechaDevolucion(), ChronoUnit.DAYS);

                // Enviar alerta
                notificacionManager.enviarAlertaVencimiento(prestamo, (int) diasRestantes);
            }

            // Obtener préstamos vencidos
            List<Prestamo> prestamosVencidos = sistemaPrestamos.obtenerPrestamosVencidos();

            for (Prestamo prestamo : prestamosVencidos) {
                // Enviar alerta de vencimiento (prioridad alta)
                notificacionManager.enviarAlertaVencimiento(prestamo, 0);
            }

            if (prestamosPorVencer.isEmpty() && prestamosVencidos.isEmpty()) {
                System.out.println("No hay préstamos por vencer o vencidos");
            }

        } catch (Exception e) {
            System.err.println("Error en la verificación de vencimientos: " + e.getMessage());
        }
    }
}