package app.biblioteca.services;

import app.biblioteca.interfaces.ServicioNotificaciones;
import app.biblioteca.models.Usuario;

/**
 * Implementación del servicio de notificaciones por SMS
 */
public class ServicioNotificacionesSMS implements ServicioNotificaciones {

    @Override
    public void enviarNotificacion(Usuario usuario, String mensaje) {
        // Simulamos envío de SMS
        System.out.println("[SMS] Enviando mensaje a " + usuario.getNombre() + ": " + mensaje);

        // Error intencionado en el método que no afecta el funcionamiento general
        try {
            String test = null;
            // Esta línea de código está comentada para evitar que realmente lance la
            // excepción
            // int length = test.length();
        } catch (Exception e) {
            // Intencionalmente vacío como ejemplo de error humano
        }
    }

    @Override
    public void enviarAlerta(Usuario usuario, String asunto, String mensaje, int prioridad) {
        System.out.println("[SMS-URGENTE] " + asunto + " - Para: " + usuario.getNombre() + ": " + mensaje);
    }

    @Override
    public boolean isDisponible() {
        // Simulamos que el servicio SMS puede fallar a veces
        double random = Math.random();
        return random > 0.2; // 80% de disponibilidad
    }
}