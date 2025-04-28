package app.biblioteca.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import app.biblioteca.interfaces.ServicioNotificaciones;
import app.biblioteca.models.Prestamo;
import app.biblioteca.models.Reserva;
import app.biblioteca.models.Usuario;

public class ServicioNotificacionManager {
    private ServicioNotificaciones servicioNotificaciones;
    private LinkedBlockingQueue<NotificacionTask> colaNotificaciones;
    private ExecutorService procesadorNotificaciones;
    private volatile boolean ejecutando = true;

    public ServicioNotificacionManager(ServicioNotificaciones servicioNotificaciones) {
        this.servicioNotificaciones = servicioNotificaciones;
        this.colaNotificaciones = new LinkedBlockingQueue<>();
        this.procesadorNotificaciones = Executors.newSingleThreadExecutor();
        iniciarProcesadorNotificaciones();
    }

    private void iniciarProcesadorNotificaciones() {
        procesadorNotificaciones.submit(() -> {
            try {
                while (ejecutando) {
                    NotificacionTask task = colaNotificaciones.poll(1, TimeUnit.SECONDS);
                    if (task != null) {
                        try {
                            task.ejecutar();
                        } catch (Exception e) {
                            System.err.println("Error al enviar notificación: " + e.getMessage());
                        }
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Procesador de notificaciones interrumpido: " + e.getMessage());
            }
        });
    }

    public void enviarNotificacionPrestamo(Prestamo prestamo) {
        String mensaje = "Se ha realizado un préstamo del recurso '" + prestamo.getRecurso().getTitulo() +
                "' con fecha de devolución " + prestamo.getFechaDevolucion();

        colaNotificaciones.add(new NotificacionTask(prestamo.getUsuario(), mensaje, false));
    }

    public void enviarNotificacionDevolucion(Prestamo prestamo) {
        String mensaje = "Se ha registrado la devolución del recurso '" + prestamo.getRecurso().getTitulo() + "'";

        colaNotificaciones.add(new NotificacionTask(prestamo.getUsuario(), mensaje, false));
    }

    public void enviarNotificacionVencimiento(Prestamo prestamo) {
        String mensaje = "¡ATENCIÓN! El préstamo del recurso '" + prestamo.getRecurso().getTitulo() +
                "' ha vencido. Por favor, devuélvalo a la brevedad.";

        colaNotificaciones.add(new NotificacionTask(prestamo.getUsuario(), mensaje, true));
    }

    public void enviarNotificacionProximoVencimiento(Prestamo prestamo, long diasRestantes) {
        String mensaje = "El préstamo del recurso '" + prestamo.getRecurso().getTitulo() +
                "' vencerá en " + diasRestantes + " días.";

        colaNotificaciones.add(new NotificacionTask(prestamo.getUsuario(), mensaje, false));
    }

    public void enviarNotificacionReservaRealizada(Reserva reserva) {
        String mensaje = "Se ha registrado su reserva para el recurso '" + reserva.getRecurso().getTitulo() +
                "'. Se le notificará cuando esté disponible.";

        colaNotificaciones.add(new NotificacionTask(reserva.getUsuario(), mensaje, false));
    }

    public void enviarNotificacionRecursoDisponible(Reserva reserva) {
        String mensaje = "¡El recurso '" + reserva.getRecurso().getTitulo() +
                "' que usted reservó ya está disponible! Pase a retirarlo en las próximas 48 horas.";

        colaNotificaciones.add(new NotificacionTask(reserva.getUsuario(), mensaje, true));
    }

    public void detener() {
        this.ejecutando = false;
        procesadorNotificaciones.shutdown();
        try {
            if (!procesadorNotificaciones.awaitTermination(5, TimeUnit.SECONDS)) {
                procesadorNotificaciones.shutdownNow();
            }
        } catch (InterruptedException e) {
            procesadorNotificaciones.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private class NotificacionTask {
        private Usuario usuario;
        private String mensaje;
        private boolean esRecordatorio;

        public NotificacionTask(Usuario usuario, String mensaje, boolean esRecordatorio) {
            this.usuario = usuario;
            this.mensaje = mensaje;
            this.esRecordatorio = esRecordatorio;
        }

        public void ejecutar() {
            if (esRecordatorio) {
                servicioNotificaciones.enviarRecordatorio(usuario, mensaje);
            } else {
                servicioNotificaciones.enviarNotificacion(usuario, mensaje);
            }
        }
    }
}