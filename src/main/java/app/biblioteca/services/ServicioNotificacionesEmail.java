package app.biblioteca.services;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import app.biblioteca.interfaces.ServicioNotificaciones;
import app.biblioteca.models.Usuario;

public class ServicioNotificacionesEmail implements ServicioNotificaciones {
    private Map<String, Boolean> notificacionesEnviadas;

    public ServicioNotificacionesEmail() {
        this.notificacionesEnviadas = new HashMap<>();
    }

    @Override
    public void enviarNotificacion(Usuario usuario, String mensaje) {
        // Simulación de envío de correo electrónico
        String idNotificacion = UUID.randomUUID().toString();
        System.out.println("Enviando email a " + usuario.getEmail() + ": " + mensaje);
        notificacionesEnviadas.put(idNotificacion, true);
    }

    @Override
    public void enviarRecordatorio(Usuario usuario, String mensaje) {
        // Simulación de envío de correo de recordatorio
        String idNotificacion = UUID.randomUUID().toString();
        System.out.println("RECORDATORIO por email a " + usuario.getEmail() + ": " + mensaje);
        notificacionesEnviadas.put(idNotificacion, true);
    }

    @Override
    public boolean notificacionEnviada(String idNotificacion) {
        return notificacionesEnviadas.getOrDefault(idNotificacion, false);
    }
}