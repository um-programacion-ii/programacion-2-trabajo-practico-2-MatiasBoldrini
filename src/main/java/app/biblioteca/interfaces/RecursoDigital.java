package app.biblioteca.interfaces;

import app.biblioteca.models.EstadoRecurso;

public interface RecursoDigital {
    String getIdentificador();

    String getTitulo();

    EstadoRecurso getEstado();

    void actualizarEstado(EstadoRecurso estado);
}