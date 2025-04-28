package app.biblioteca.models;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Clase que representa una reserva de un recurso por un usuario
 */
public class Reserva implements Comparable<Reserva> {
    private String id;
    private Usuario usuario;
    private String idRecurso;
    private LocalDateTime fechaSolicitud;
    private boolean activa;
    private int prioridad;

    public Reserva(Usuario usuario, String idRecurso) {
        this.id = UUID.randomUUID().toString();
        this.usuario = usuario;
        this.idRecurso = idRecurso;
        this.fechaSolicitud = LocalDateTime.now();
        this.activa = true;
        this.prioridad = 1; // Prioridad normal por defecto
    }

    public String getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getIdRecurso() {
        return idRecurso;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
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

    /**
     * Compara dos reservas para determinar cuál tiene más prioridad.
     * Se comparan primero por prioridad y luego por fecha de solicitud.
     */
    @Override
    public int compareTo(Reserva otra) {
        // Primero comparamos por prioridad (mayor prioridad primero)
        int comparacionPrioridad = Integer.compare(otra.prioridad, this.prioridad);

        if (comparacionPrioridad != 0) {
            return comparacionPrioridad;
        }

        // Si la prioridad es igual, comparamos por fecha (más antigua primero)
        return this.fechaSolicitud.compareTo(otra.fechaSolicitud);
    }

    @Override
    public String toString() {
        return "Reserva [id=" + id + ", usuario=" + usuario.getNombre() + ", recurso=" + idRecurso +
                ", fecha=" + fechaSolicitud + ", activa=" + activa + ", prioridad=" + prioridad + "]";
    }
}