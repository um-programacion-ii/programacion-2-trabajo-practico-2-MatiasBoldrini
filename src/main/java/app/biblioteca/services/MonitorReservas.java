package app.biblioteca.services;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import app.biblioteca.interfaces.RecursoDigital;
import app.biblioteca.models.EstadoRecurso;
import app.biblioteca.models.Reserva;

public class MonitorReservas {
    private final SistemaReservas sistemaReservas;
    private final GestorRecursos gestorRecursos;
    private final ServicioNotificacionManager notificacionManager;
    private ScheduledExecutorService scheduler;
    private static final int PERIODO_REVISION_MINUTOS = 30;

    public MonitorReservas(SistemaReservas sistemaReservas, GestorRecursos gestorRecursos,
            ServicioNotificacionManager notificacionManager) {
        this.sistemaReservas = sistemaReservas;
        this.gestorRecursos = gestorRecursos;
        this.notificacionManager = notificacionManager;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void iniciar() {
        scheduler.scheduleAtFixedRate(
                this::procesarReservas,
                0, // Iniciar inmediatamente
                PERIODO_REVISION_MINUTOS,
                TimeUnit.MINUTES);

        System.out.println("Monitor de reservas iniciado. Revisará cada " + PERIODO_REVISION_MINUTOS + " minutos.");
    }

    private void procesarReservas() {
        try {
            System.out.println("Procesando reservas...");

            // Primero, limpiar reservas expiradas
            sistemaReservas.limpiarReservasExpiradas();

            // Luego, procesar recursos que se hayan vuelto disponibles
            List<RecursoDigital> recursosDisponibles = gestorRecursos.listarRecursosDisponibles();

            for (RecursoDigital recurso : recursosDisponibles) {
                // Verificar si hay reservas para este recurso
                Reserva siguienteReserva = sistemaReservas.obtenerSiguienteReserva(recurso.getIdentificador());

                if (siguienteReserva != null) {
                    // Marcar recurso como reservado
                    recurso.actualizarEstado(EstadoRecurso.RESERVADO);

                    // Notificar al usuario
                    notificacionManager.enviarNotificacionRecursoDisponible(siguienteReserva);

                    System.out.println("Recurso '" + recurso.getTitulo() + "' notificado a usuario "
                            + siguienteReserva.getUsuario().getNombre());
                }
            }

            // Contar y mostrar estadísticas
            int reservasActivas = sistemaReservas.listarReservasActivas().size();

            System.out.println("Procesamiento finalizado. Reservas activas: " + reservasActivas);

        } catch (Exception e) {
            System.err.println("Error al procesar reservas: " + e.getMessage());
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
            System.out.println("Monitor de reservas detenido.");
        }
    }
}