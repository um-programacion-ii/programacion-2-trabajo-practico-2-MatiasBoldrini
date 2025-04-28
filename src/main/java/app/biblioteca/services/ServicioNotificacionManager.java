package app.biblioteca.services;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.biblioteca.interfaces.ServicioNotificaciones;
import app.biblioteca.models.Prestamo;
import app.biblioteca.models.Reserva;
import app.biblioteca.models.Usuario;

/**
 * Gestor central de notificaciones que utiliza diferentes servicios de
 * notificación
 */
public class ServicioNotificacionManager {

    private ServicioNotificaciones servicioNotificaciones;
    private ExecutorService executorService;
    private DateTimeFormatter formatter;

    public ServicioNotificacionManager(ServicioNotificaciones servicioNotificaciones) {
        this.servicioNotificaciones = servicioNotificaciones;
        this.executorService = Executors.newFixedThreadPool(2);
        this.formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    }

    /**
     * Envía una notificación sobre un préstamo
     * 
     * @param prestamo Préstamo sobre el que notificar
     */
    public void enviarNotificacionPrestamo(Prestamo prestamo) {
        Usuario usuario = prestamo.getUsuario();
        String mensaje = "Se ha realizado un préstamo del recurso con ID " + prestamo.getIdRecurso() +
                ". Fecha de devolución: " + prestamo.getFechaDevolucion().format(formatter);

        enviarNotificacion(usuario, mensaje);
    }

    /**
     * Envía una notificación sobre una devolución
     * 
     * @param prestamo Préstamo devuelto
     */
    public void enviarNotificacionDevolucion(Prestamo prestamo) {
        Usuario usuario = prestamo.getUsuario();
        String mensaje = "Se ha registrado la devolución del recurso con ID " + prestamo.getIdRecurso();

        enviarNotificacion(usuario, mensaje);
    }

    /**
     * Envía una alerta por vencimiento de préstamo
     * 
     * @param prestamo      Préstamo por vencer
     * @param diasRestantes Días restantes para el vencimiento
     */
    public void enviarAlertaVencimiento(Prestamo prestamo, int diasRestantes) {
        Usuario usuario = prestamo.getUsuario();
        String asunto = "Alerta de vencimiento de préstamo";

        String mensaje;
        int prioridad;

        if (diasRestantes <= 0) {
            mensaje = "El préstamo del recurso con ID " + prestamo.getIdRecurso() +
                    " ha vencido hoy. Por favor, devuélvalo a la brevedad.";
            prioridad = 3; // Alta
        } else if (diasRestantes == 1) {
            mensaje = "El préstamo del recurso con ID " + prestamo.getIdRecurso() +
                    " vence mañana. Fecha de vencimiento: " +
                    prestamo.getFechaDevolucion().format(formatter);
            prioridad = 2; // Media
        } else {
            mensaje = "El préstamo del recurso con ID " + prestamo.getIdRecurso() +
                    " vence en " + diasRestantes + " días. Fecha de vencimiento: " +
                    prestamo.getFechaDevolucion().format(formatter);
            prioridad = 1; // Baja
        }

        enviarAlerta(usuario, asunto, mensaje, prioridad);
    }

    /**
     * Envía una notificación sobre disponibilidad de un recurso reservado
     * 
     * @param reserva Reserva cuyo recurso está disponible
     */
    public void enviarNotificacionDisponibilidad(Reserva reserva) {
        Usuario usuario = reserva.getUsuario();
        String asunto = "Recurso disponible para préstamo";
        String mensaje = "El recurso con ID " + reserva.getIdRecurso() +
                " que usted reservó está disponible para préstamo.";

        enviarAlerta(usuario, asunto, mensaje, 2);
    }

    /**
     * Envía una notificación genérica a un usuario
     * 
     * @param usuario Usuario destinatario
     * @param mensaje Mensaje a enviar
     */
    public void enviarNotificacion(Usuario usuario, String mensaje) {
        executorService.submit(() -> {
            if (servicioNotificaciones.isDisponible()) {
                servicioNotificaciones.enviarNotificacion(usuario, mensaje);
            } else {
                System.err.println("Servicio de notificaciones no disponible. " +
                        "No se pudo enviar notificación a " + usuario.getNombre());
            }
        });
    }

    /**
     * Envía una alerta a un usuario
     * 
     * @param usuario   Usuario destinatario
     * @param asunto    Asunto de la alerta
     * @param mensaje   Mensaje de la alerta
     * @param prioridad Nivel de prioridad (1-3)
     */
    public void enviarAlerta(Usuario usuario, String asunto, String mensaje, int prioridad) {
        executorService.submit(() -> {
            if (servicioNotificaciones.isDisponible()) {
                servicioNotificaciones.enviarAlerta(usuario, asunto, mensaje, prioridad);
            } else {
                System.err.println("Servicio de notificaciones no disponible. " +
                        "No se pudo enviar alerta a " + usuario.getNombre());
            }
        });
    }

    /**
     * Detiene el servicio de notificaciones
     */
    public void detener() {
        executorService.shutdown();
    }
}