package app.biblioteca.models;

import java.time.LocalDateTime;

import app.biblioteca.interfaces.Renovable;

/**
 * Clase que representa un audiolibro digital
 */
public class Audiolibro extends RecursoBase implements Renovable {
    private String narrador;
    private int duracionMinutos;
    private int renovacionesRealizadas;
    private final int maximoRenovaciones = 1;

    public Audiolibro(String identificador, String titulo, String autor, String narrador, int duracionMinutos) {
        super(identificador, titulo, autor);
        this.narrador = narrador;
        this.duracionMinutos = duracionMinutos;
        this.renovacionesRealizadas = 0;
    }

    public String getNarrador() {
        return narrador;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    @Override
    public void prestar(Usuario usuario) {
        super.prestar(usuario);
        // Los audiolibros se prestan por 10 días
        LocalDateTime fechaDevolucion = LocalDateTime.now().plusDays(10);
        setFechaDevolucion(fechaDevolucion);
    }

    @Override
    public boolean esRenovable() {
        return renovacionesRealizadas < maximoRenovaciones;
    }

    @Override
    public LocalDateTime renovar(int diasExtension) {
        if (esRenovable()) {
            incrementarRenovaciones();
            LocalDateTime nuevaFecha = getFechaDevolucion().plusDays(diasExtension);
            setFechaDevolucion(nuevaFecha);
            return nuevaFecha;
        }
        return getFechaDevolucion();
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
    public String toString() {
        return "Audiolibro [id=" + getIdentificador() + ", título=" + getTitulo() +
                ", autor=" + getAutor() + ", narrador=" + narrador +
                ", duración=" + duracionMinutos + " min, estado=" + getEstado() +
                ", categoría=" + getCategoria() + "]";
    }
}