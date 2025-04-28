package app.biblioteca.exceptions;

public class RecursoNoDisponibleException extends Exception {

    public RecursoNoDisponibleException(String mensaje) {
        super(mensaje);
    }

    public RecursoNoDisponibleException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}