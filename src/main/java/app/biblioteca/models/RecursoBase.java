package app.biblioteca.models;

import app.biblioteca.interfaces.RecursoDigital;

public abstract class RecursoBase implements RecursoDigital {
    protected String identificador;
    protected String titulo;
    protected EstadoRecurso estado;

    public RecursoBase(String identificador, String titulo) {
        this.identificador = identificador;
        this.titulo = titulo;
        this.estado = EstadoRecurso.DISPONIBLE;
    }

    @Override
    public String getIdentificador() {
        return identificador;
    }

    @Override
    public String getTitulo() {
        return titulo;
    }

    @Override
    public EstadoRecurso getEstado() {
        return estado;
    }

    @Override
    public void actualizarEstado(EstadoRecurso estado) {
        this.estado = estado;
    }
}