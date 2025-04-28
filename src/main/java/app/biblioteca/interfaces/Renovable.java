package app.biblioteca.interfaces;

import java.time.LocalDateTime;

public interface Renovable {
    boolean puedeRenovarse();

    LocalDateTime renovar();
}