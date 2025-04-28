package app.biblioteca.models;

import java.time.LocalDateTime;

/**
 * Clase que representa una revista digital
 */
public class Revista extends RecursoBase {
    private String issn;
    private int numeroEdicion;

    public Revista(String identificador, String titulo, String autor, String issn, int numeroEdicion) {
        super(identificador, titulo, autor);
        this.issn = issn;
        this.numeroEdicion = numeroEdicion;
    }

    public String getIssn() {
        return issn;
    }

    public int getNumeroEdicion() {
        return numeroEdicion;
    }

    @Override
    public void prestar(Usuario usuario) {
        super.prestar(usuario);
        // Las revistas se prestan por menos tiempo: 7 días
        LocalDateTime fechaDevolucion = LocalDateTime.now().plusDays(7);
        setFechaDevolucion(fechaDevolucion);
    }

    @Override
    public String toString() {
        return "Revista [id=" + getIdentificador() + ", título=" + getTitulo() +
                ", editor=" + getAutor() + ", ISSN=" + issn +
                ", edición=" + numeroEdicion + ", estado=" + getEstado() +
                ", categoría=" + getCategoria() + "]";
    }
}