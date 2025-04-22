package app.biblioteca.interfaces;

import app.biblioteca.models.Prestamo;
import app.biblioteca.models.Reserva;
import app.biblioteca.models.Usuario;

/**
 * Interfaz para servicios de notificaciones
 */
public interface ServicioNotificaciones {

    /**
     * Envía una notificación de nuevo préstamo al usuario
     */
    void enviarNotificacionPrestamo(Usuario usuario, Prestamo prestamo);

    /**
     * Envía una notificación de devolución al usuario
     */
    void enviarNotificacionDevolucion(Usuario usuario, Prestamo prestamo);

    /**
     * Envía una notificación de vencimiento al usuario
     */
    void enviarNotificacionVencimiento(Usuario usuario, Prestamo prestamo);

    /**
     * Envía una notificación de recordatorio al usuario
     */
    void enviarNotificacionRecordatorio(Usuario usuario, Prestamo prestamo, int diasRestantes);

    /**
     * Envía una notificación de nueva reserva al usuario
     */
    void enviarNotificacionReserva(Usuario usuario, Reserva reserva);

    /**
     * Envía una notificación de reserva cancelada al usuario
     */
    void enviarNotificacionReservaCancelada(Usuario usuario, Reserva reserva);

    /**
     * Envía una notificación de recurso disponible al usuario
     */
    void enviarNotificacionRecursoDisponible(Reserva reserva);
}