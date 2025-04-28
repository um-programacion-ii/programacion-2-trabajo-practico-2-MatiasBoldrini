package app.biblioteca.exceptions;

/**
 * Excepción que se lanza cuando un recurso no está disponible para préstamo
 */
public class RecursoNoDisponibleException extends Exception {

    public RecursoNoDisponibleException(String message) {
        super(message);
    }

    public RecursoNoDisponibleException(String message, Throwable cause) {
        super(message, cause);
    }
}