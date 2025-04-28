package app.biblioteca.interfaces;

import java.time.LocalDateTime;

/**
 * Interfaz para recursos que pueden ser renovados
 */
public interface Renovable {
    boolean esRenovable();

    LocalDateTime renovar(int diasExtension);

    int getMaximoRenovaciones();

    int getRenovacionesRealizadas();

    void incrementarRenovaciones();
}