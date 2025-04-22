package app.biblioteca.interfaces;

import app.biblioteca.models.CategoriaRecurso;
import app.biblioteca.models.EstadoRecurso;

public interface RecursoDigital {
    String getIdentificador();

    String getTitulo();

    EstadoRecurso getEstado();

    void actualizarEstado(EstadoRecurso estado);

    CategoriaRecurso getCategoria();

    void setCategoria(CategoriaRecurso categoria);

    boolean estaDisponible();
}