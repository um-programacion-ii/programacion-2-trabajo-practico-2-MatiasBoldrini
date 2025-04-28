package app.biblioteca.services;

import app.biblioteca.interfaces.ServicioNotificaciones;
import app.biblioteca.models.Usuario;

/**
 * Implementación del servicio de notificaciones por email
 */
public class ServicioNotificacionesEmail implements ServicioNotificaciones {

    @Override
    public void enviarNotificacion(Usuario usuario, String mensaje) {
        System.out.println("[EMAIL] Enviando notificación a " + usuario.getNombre() +
                " (" + usuario.getEmail() + "): " + mensaje);
    }

    @Override
    public void enviarAlerta(Usuario usuario, String asunto, String mensaje, int prioridad) {
        String nivelPrioridad = "";

        switch (prioridad) {
            case 1:
                nivelPrioridad = "BAJA";
                break;
            case 2:
                nivelPrioridad = "MEDIA";
                break;
            case 3:
                nivelPrioridad = "ALTA";
                break;
            default:
                nivelPrioridad = "INFORMATIVA";
        }

        System.out.println("[EMAIL-ALERTA " + nivelPrioridad + "] " + asunto + " - Para: " +
                usuario.getNombre() + " (" + usuario.getEmail() + "): " + mensaje);
    }

    @Override
    public boolean isDisponible() {
        // Simulamos que el servicio está disponible
        return true;
    }
}