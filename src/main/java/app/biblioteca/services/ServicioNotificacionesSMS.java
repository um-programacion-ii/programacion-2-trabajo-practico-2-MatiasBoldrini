package app.biblioteca.services;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import app.biblioteca.interfaces.ServicioNotificaciones;
import app.biblioteca.models.Usuario;

public class ServicioNotificacionesSMS implements ServicioNotificaciones {
    private Map<String, Boolean> notificacionesEnviadas;

    public ServicioNotificacionesSMS() {
        this.notificacionesEnviadas = new HashMap<>();
    }

    @Override
    public void enviarNotificacion(Usuario usuario, String mensaje) {
        // Simulación de envío de SMS
        String idNotificacion = UUID.randomUUID().toString();
        System.out.println("Enviando SMS a " + usuario.getNombre() + ": " + mensaje);
        notificacionesEnviadas.put(idNotificacion, true);
    }

    @Override
    public void enviarRecordatorio(Usuario usuario, String mensaje) {
        // Simulación de envío de SMS de recordatorio
        String idNotificacion = UUID.randomUUID().toString();
        System.out.println("RECORDATORIO por SMS a " + usuario.getNombre() + ": " + mensaje);
        notificacionesEnviadas.put(idNotificacion, true);
    }

    @Override
    public boolean notificacionEnviada(String idNotificacion) {
        return notificacionesEnviadas.getOrDefault(idNotificacion, false);
    }
}