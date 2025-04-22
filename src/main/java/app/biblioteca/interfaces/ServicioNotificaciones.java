package app.biblioteca.interfaces;

import app.biblioteca.models.Usuario;

public interface ServicioNotificaciones {
    void enviarNotificacion(Usuario usuario, String mensaje);

    void enviarRecordatorio(Usuario usuario, String mensaje);

    boolean notificacionEnviada(String idNotificacion);
}