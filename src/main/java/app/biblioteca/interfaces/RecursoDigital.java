package app.biblioteca.interfaces;

import app.biblioteca.models.CategoriaRecurso;
import app.biblioteca.models.EstadoRecurso;

/**
 * Interfaz para todos los recursos digitales de la biblioteca
 */
public interface RecursoDigital {
    String getIdentificador();

    String getTitulo();

    String getAutor();

    EstadoRecurso getEstado();

    void actualizarEstado(EstadoRecurso estado);

    CategoriaRecurso getCategoria();

    void setCategoria(CategoriaRecurso categoria);
}