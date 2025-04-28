package app.biblioteca.exceptions;

/**
 * Excepción que se lanza cuando un usuario no se encuentra en el sistema
 */
public class UsuarioNoEncontradoException extends Exception {

    public UsuarioNoEncontradoException(String message) {
        super(message);
    }

    public UsuarioNoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}