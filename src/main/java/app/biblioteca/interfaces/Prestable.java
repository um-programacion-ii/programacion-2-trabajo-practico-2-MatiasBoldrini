package app.biblioteca.interfaces;

import java.time.LocalDateTime;
import app.biblioteca.models.Usuario;

public interface Prestable {
    boolean estaDisponible();

    LocalDateTime getFechaDevolucion();

    void prestar(Usuario usuario);

    void devolver();
}