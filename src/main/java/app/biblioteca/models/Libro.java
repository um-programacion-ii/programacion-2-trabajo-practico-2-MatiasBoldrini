package app.biblioteca.models;

import java.time.LocalDateTime;
import app.biblioteca.interfaces.Prestable;
import app.biblioteca.interfaces.Renovable;

public class Libro extends RecursoBase implements Prestable, Renovable {
    private String autor;
    private String isbn;
    private int numPaginas;
    private LocalDateTime fechaDevolucion;
    private Usuario usuarioPrestamo;
    private int renovacionesRealizadas;
    private static final int MAX_RENOVACIONES = 2;

    public Libro(String identificador, String titulo, String autor, String isbn, int numPaginas) {
        super(identificador, titulo);
        this.autor = autor;
        this.isbn = isbn;
        this.numPaginas = numPaginas;
        this.renovacionesRealizadas = 0;
    }

    public String getAutor() {
        return autor;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getNumPaginas() {
        return numPaginas;
    }

    @Override
    public boolean estaDisponible() {
        return this.estado == EstadoRecurso.DISPONIBLE;
    }

    @Override
    public LocalDateTime getFechaDevolucion() {
        return fechaDevolucion;
    }

    @Override
    public void prestar(Usuario usuario) {
        if (!estaDisponible()) {
            System.out.println("El libro no está disponible para préstamo");
            return;
        }

        this.usuarioPrestamo = usuario;
        this.fechaDevolucion = LocalDateTime.now().plusDays(15);
        this.estado = EstadoRecurso.PRESTADO;
        this.renovacionesRealizadas = 0;
        System.out.println("Libro prestado a " + usuario.getNombre() + " hasta " + fechaDevolucion);
    }

    @Override
    public void devolver() {
        if (this.estado != EstadoRecurso.PRESTADO) {
            System.out.println("Este libro no está prestado");
            return;
        }

        this.usuarioPrestamo = null;
        this.fechaDevolucion = null;
        this.estado = EstadoRecurso.DISPONIBLE;
        System.out.println("Libro devuelto correctamente");
    }

    @Override
    public boolean puedeRenovarse() {
        return this.estado == EstadoRecurso.PRESTADO && renovacionesRealizadas < MAX_RENOVACIONES;
    }

    @Override
    public LocalDateTime renovar() {
        if (!puedeRenovarse()) {
            System.out.println("No se puede renovar este libro");
            return fechaDevolucion;
        }

        this.fechaDevolucion = this.fechaDevolucion.plusDays(15);
        this.renovacionesRealizadas++;
        System.out.println("Libro renovado hasta " + fechaDevolucion);

        return fechaDevolucion;
    }
}