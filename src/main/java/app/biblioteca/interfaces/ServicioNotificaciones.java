package app.biblioteca.interfaces;

import app.biblioteca.models.Usuario;

/**
 * Interfaz para servicios de notificaciones
 */
public interface ServicioNotificaciones {
    void enviarNotificacion(Usuario usuario, String mensaje);

    void enviarAlerta(Usuario usuario, String asunto, String mensaje, int prioridad);

    boolean isDisponible();
}