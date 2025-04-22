package app.biblioteca.models;

import app.biblioteca.interfaces.RecursoDigital;

public abstract class RecursoBase implements RecursoDigital {
    protected String identificador;
    protected String titulo;
    protected EstadoRecurso estado;
    protected CategoriaRecurso categoria;

    public RecursoBase(String identificador, String titulo) {
        this.identificador = identificador;
        this.titulo = titulo;
        this.estado = EstadoRecurso.DISPONIBLE;
        this.categoria = CategoriaRecurso.SIN_CATEGORIA;
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

    @Override
    public CategoriaRecurso getCategoria() {
        return categoria;
    }

    @Override
    public void setCategoria(CategoriaRecurso categoria) {
        this.categoria = categoria;
    }

    @Override
    public boolean estaDisponible() {
        return this.estado == EstadoRecurso.DISPONIBLE;
    }
}