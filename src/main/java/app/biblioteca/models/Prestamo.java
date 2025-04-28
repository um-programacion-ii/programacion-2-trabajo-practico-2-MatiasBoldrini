package app.biblioteca.models;

import java.time.LocalDateTime;
import java.util.UUID;

import app.biblioteca.interfaces.RecursoDigital;

public class Prestamo {
    private String id;
    private RecursoDigital recurso;
    private Usuario usuario;
    private LocalDateTime fechaPrestamo;
    private LocalDateTime fechaDevolucion;
    private boolean devuelto;

    public Prestamo(RecursoDigital recurso, Usuario usuario, LocalDateTime fechaDevolucion) {
        this.id = UUID.randomUUID().toString();
        this.recurso = recurso;
        this.usuario = usuario;
        this.fechaPrestamo = LocalDateTime.now();
        this.fechaDevolucion = fechaDevolucion;
        this.devuelto = false;
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

    public LocalDateTime getFechaPrestamo() {
        return fechaPrestamo;
    }

    public LocalDateTime getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDateTime fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public boolean isDevuelto() {
        return devuelto;
    }

    public void marcarComoDevuelto() {
        this.devuelto = true;
    }

    public boolean estaVencido() {
        return !devuelto && LocalDateTime.now().isAfter(fechaDevolucion);
    }

    public long diasHastaVencimiento() {
        if (devuelto) {
            return 0;
        }

        LocalDateTime ahora = LocalDateTime.now();
        if (ahora.isAfter(fechaDevolucion)) {
            // Ya está vencido, retorna días negativos
            return java.time.Duration.between(fechaDevolucion, ahora).toDays() * -1;
        } else {
            // Días restantes
            return java.time.Duration.between(ahora, fechaDevolucion).toDays();
        }
    }

    @Override
    public String toString() {
        return "Prestamo [id=" + id + ", recurso=" + recurso.getTitulo() + ", usuario=" + usuario.getNombre()
                + ", fechaPrestamo=" + fechaPrestamo + ", fechaDevolucion=" + fechaDevolucion + ", devuelto=" + devuelto
                + "]";
    }
}