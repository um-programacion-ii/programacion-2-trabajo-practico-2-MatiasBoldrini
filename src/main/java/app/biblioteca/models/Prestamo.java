package app.biblioteca.models;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Clase que representa un préstamo de un recurso a un usuario
 */
public class Prestamo {
    private String id;
    private Usuario usuario;
    private String idRecurso;
    private LocalDateTime fechaPrestamo;
    private LocalDateTime fechaDevolucion;
    private boolean activo;

    public Prestamo(Usuario usuario, String idRecurso) {
        this.id = UUID.randomUUID().toString();
        this.usuario = usuario;
        this.idRecurso = idRecurso;
        this.fechaPrestamo = LocalDateTime.now();
        // Por defecto, los préstamos son por 14 días
        this.fechaDevolucion = fechaPrestamo.plusDays(14);
        this.activo = true;
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

    public LocalDateTime getFechaPrestamo() {
        return fechaPrestamo;
    }

    public LocalDateTime getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDateTime fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public void finalizarPrestamo() {
        this.activo = false;
    }

    public boolean isActivo() {
        return activo;
    }

    /**
     * Verifica si el préstamo está vencido
     * 
     * @return true si la fecha actual es posterior a la fecha de devolución
     */
    public boolean isVencido() {
        return LocalDateTime.now().isAfter(fechaDevolucion);
    }

    /**
     * Renueva el préstamo por un número adicional de días
     * 
     * @param dias Días adicionales para el préstamo
     * @return La nueva fecha de devolución
     */
    public LocalDateTime renovar(int dias) {
        this.fechaDevolucion = fechaDevolucion.plusDays(dias);
        return this.fechaDevolucion;
    }

    @Override
    public String toString() {
        return "Préstamo [id=" + id + ", usuario=" + usuario.getNombre() +
                ", recurso=" + idRecurso + ", fecha préstamo=" + fechaPrestamo +
                ", fecha devolución=" + fechaDevolucion + ", activo=" + activo + "]";
    }
}