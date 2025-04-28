package app.biblioteca.models;

import app.biblioteca.interfaces.Renovable;

/**
 * Clase que representa un libro digital
 */
public class Libro extends RecursoBase implements Renovable {
    private String isbn;
    private int numeroPaginas;
    private int renovacionesRealizadas;
    private final int maximoRenovaciones = 2;

    public Libro(String identificador, String titulo, String autor, String isbn, int numeroPaginas) {
        super(identificador, titulo, autor);
        this.isbn = isbn;
        this.numeroPaginas = numeroPaginas;
        this.renovacionesRealizadas = 0;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getNumeroPaginas() {
        return numeroPaginas;
    }

    @Override
    public boolean esRenovable() {
        return renovacionesRealizadas < maximoRenovaciones;
    }

    @Override
    public int getMaximoRenovaciones() {
        return maximoRenovaciones;
    }

    @Override
    public int getRenovacionesRealizadas() {
        return renovacionesRealizadas;
    }

    @Override
    public void incrementarRenovaciones() {
        renovacionesRealizadas++;
    }

    @Override
    public java.time.LocalDateTime renovar(int diasExtension) {
        if (esRenovable()) {
            incrementarRenovaciones();
            java.time.LocalDateTime nuevaFecha = getFechaDevolucion().plusDays(diasExtension);
            setFechaDevolucion(nuevaFecha);
            return nuevaFecha;
        }
        return getFechaDevolucion();
    }

    @Override
    public String toString() {
        return "Libro [id=" + getIdentificador() + ", título=" + getTitulo() +
                ", autor=" + getAutor() + ", ISBN=" + isbn +
                ", páginas=" + numeroPaginas + ", estado=" + getEstado() +
                ", categoría=" + getCategoria() + "]";
    }
}