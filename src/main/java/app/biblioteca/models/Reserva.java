package app.biblioteca.models;

import java.time.LocalDateTime;
import java.util.UUID;

import app.biblioteca.interfaces.RecursoDigital;

public class Reserva implements Comparable<Reserva> {
    private String id;
    private RecursoDigital recurso;
    private Usuario usuario;
    private LocalDateTime fechaReserva;
    private LocalDateTime fechaLimite;
    private boolean activa;
    private int prioridad;

    public Reserva(RecursoDigital recurso, Usuario usuario, int prioridad) {
        this.id = UUID.randomUUID().toString();
        this.recurso = recurso;
        this.usuario = usuario;
        this.fechaReserva = LocalDateTime.now();
        this.fechaLimite = fechaReserva.plusDays(7); // La reserva expira en 7 días
        this.activa = true;
        this.prioridad = prioridad;
    }

    public String getId() {
        return id;
    }

    public RecursoDigital getRecurso() {
        return recurso;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }

    public LocalDateTime getFechaLimite() {
        return fechaLimite;
    }

    public boolean isActiva() {
        return activa;
    }

    public void cancelar() {
        this.activa = false;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public boolean haExpirado() {
        return LocalDateTime.now().isAfter(fechaLimite);
    }

    @Override
    public int compareTo(Reserva otra) {
        // Primero comparamos por prioridad (mayor primero)
        int comparacionPrioridad = Integer.compare(otra.prioridad, this.prioridad);
        if (comparacionPrioridad != 0) {
            return comparacionPrioridad;
        }

        // Si tienen la misma prioridad, comparamos por fecha (más antigua primero)
        return this.fechaReserva.compareTo(otra.fechaReserva);
    }

    @Override
    public String toString() {
        return "Reserva [id=" + id + ", recurso=" + recurso.getTitulo() + ", usuario=" + usuario.getNombre()
                + ", fechaReserva=" + fechaReserva + ", fechaLimite=" + fechaLimite
                + ", activa=" + activa + ", prioridad=" + prioridad + "]";
    }
}