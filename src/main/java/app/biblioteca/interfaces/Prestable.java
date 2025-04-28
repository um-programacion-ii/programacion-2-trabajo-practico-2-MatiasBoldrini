package app.biblioteca.interfaces;

import java.time.LocalDateTime;

import app.biblioteca.models.Usuario;

/**
 * Interfaz para recursos que pueden ser prestados
 */
public interface Prestable {
    boolean estaDisponible();

    void prestar(Usuario usuario);

    void devolver();

    LocalDateTime getFechaDevolucion();

    void setFechaDevolucion(LocalDateTime fechaDevolucion);

    Usuario getUsuarioPrestamo();
}