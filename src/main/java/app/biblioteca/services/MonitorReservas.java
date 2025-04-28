package app.biblioteca.services;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import app.biblioteca.interfaces.RecursoDigital;
import app.biblioteca.models.EstadoRecurso;
import app.biblioteca.models.Reserva;

/**
 * Monitor que verifica periódicamente las reservas y notifica cuando los recursos están disponibles
 */
public class MonitorReservas {
    
    private SistemaReservas sistemaReservas;
    private GestorRecursos gestorRecursos;
    private ServicioNotificacionManager notificacionManager;
    private ScheduledExecutorService scheduler;
    private boolean ejecutando;
    
    public MonitorReservas(SistemaReservas sistemaReservas, 
                          GestorRecursos gestorRecursos,
                          ServicioNotificacionManager notificacionManager) {
        this.sistemaReservas = sistemaReservas;
        this.gestorRecursos = gestorRecursos;
        this.notificacionManager = notificacionManager;
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.ejecutando = false;
    }
    
    /**
     * Inicia el monitor de reservas
     */
    public void iniciar() {
        if (!ejecutando) {
            ejecutando = true;
            
            // Programar la verificación cada 25 segundos (para demostración)
            // En un sistema real podría ser cada hora
            scheduler.scheduleAtFixedRate(this::verificarReservas, 10, 25, TimeUnit.SECONDS);
            
            System.out.println("Monitor de reservas iniciado");
        }
    }
    
    /**
     * Detiene el monitor de reservas
     */
    public void detener() {
        if (ejecutando) {
            ejecutando = false;
            scheduler.shutdown();
            
            System.out.println("Monitor de reservas detenido");
        }
    }
    
    /**
     * Verifica las reservas activas y notifica si los recursos están disponibles
     */
    private void verificarReservas() {
        try {
            System.out.println("\n==== Verificando reservas activas ====");
            
            // Obtener todas las reservas activas
            List<Reserva> reservasActivas = sistemaReservas.obtenerReservasActivas();
            
            if (reservasActivas.isEmpty()) {
                System.out.println("No hay reservas activas");
                return;
            }
            
            for (Reserva reserva : reservasActivas) {
                RecursoDigital recurso = gestorRecursos.buscarPorId(reserva.getIdRecurso());
                
                if (recurso != null && recurso.getEstado() == EstadoRecurso.DISPONIBLE) {
                    // El recurso está disponible, notificar al usuario
                    notificacionManager.enviarNotificacionDisponibilidad(reserva);
                    
                    // Marcar el recurso como reservado para que no se preste a otro usuario
                    recurso.actualizarEstado(EstadoRecurso.RESERVADO);
                    
                    System.out.println("Notificación enviada para la reserva: " + reserva.getId());
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error en la verificación de reservas: " + e.getMessage());
        }
    }
} 