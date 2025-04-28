package app.biblioteca.models;

import java.time.LocalDateTime;

import app.biblioteca.interfaces.Prestable;
import app.biblioteca.interfaces.RecursoDigital;

/**
 * Clase base abstracta para todos los recursos digitales
 */
public abstract class RecursoBase implements RecursoDigital, Prestable {

    private String identificador;
    private String titulo;
    private String autor;
    private EstadoRecurso estado;
    private CategoriaRecurso categoria;
    private LocalDateTime fechaDevolucion;
    private Usuario usuarioPrestamo;

    public RecursoBase(String identificador, String titulo, String autor) {
        this.identificador = identificador;
        this.titulo = titulo;
        this.autor = autor;
        this.estado = EstadoRecurso.DISPONIBLE;
        this.categoria = CategoriaRecurso.OTRO;
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
    public String getAutor() {
        return autor;
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

    @Override
    public void prestar(Usuario usuario) {
        this.usuarioPrestamo = usuario;
        this.estado = EstadoRecurso.PRESTADO;

        // Por defecto, el préstamo es por 14 días
        LocalDateTime ahora = LocalDateTime.now();
        this.fechaDevolucion = ahora.plusDays(14);
    }

    @Override
    public void devolver() {
        this.usuarioPrestamo = null;
        this.fechaDevolucion = null;
        this.estado = EstadoRecurso.DISPONIBLE;
    }

    @Override
    public LocalDateTime getFechaDevolucion() {
        return fechaDevolucion;
    }

    @Override
    public void setFechaDevolucion(LocalDateTime fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    @Override
    public Usuario getUsuarioPrestamo() {
        return usuarioPrestamo;
    }

    @Override
    public String toString() {
        return "Recurso [id=" + identificador + ", título=" + titulo + ", autor=" + autor +
                ", estado=" + estado + ", categoría=" + categoria + "]";
    }
}