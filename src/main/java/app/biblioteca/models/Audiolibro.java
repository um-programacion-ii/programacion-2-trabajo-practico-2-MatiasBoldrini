package app.biblioteca.models;

import java.time.LocalDateTime;
import app.biblioteca.interfaces.Prestable;
import app.biblioteca.interfaces.Renovable;

public class Audiolibro extends RecursoBase implements Prestable, Renovable {
    private String autor;
    private String narrador;
    private int duracionMinutos;
    private LocalDateTime fechaDevolucion;
    private Usuario usuarioPrestamo;
    private int renovacionesRealizadas;
    private static final int MAX_RENOVACIONES = 1;

    public Audiolibro(String identificador, String titulo, String autor, String narrador, int duracionMinutos) {
        super(identificador, titulo);
        this.autor = autor;
        this.narrador = narrador;
        this.duracionMinutos = duracionMinutos;
        this.renovacionesRealizadas = 0;
    }

    public String getAutor() {
        return autor;
    }

    public String getNarrador() {
        return narrador;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
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
            System.out.println("El audiolibro no está disponible para préstamo");
            return;
        }

        this.usuarioPrestamo = usuario;
        this.fechaDevolucion = LocalDateTime.now().plusDays(10);
        this.estado = EstadoRecurso.PRESTADO;
        this.renovacionesRealizadas = 0;
        System.out.println("Audiolibro prestado a " + usuario.getNombre() + " hasta " + fechaDevolucion);
    }

    @Override
    public void devolver() {
        if (this.estado != EstadoRecurso.PRESTADO) {
            System.out.println("Este audiolibro no está prestado");
            return;
        }

        this.usuarioPrestamo = null;
        this.fechaDevolucion = null;
        this.estado = EstadoRecurso.DISPONIBLE;
        System.out.println("Audiolibro devuelto correctamente");
    }

    @Override
    public boolean puedeRenovarse() {
        return this.estado == EstadoRecurso.PRESTADO && renovacionesRealizadas < MAX_RENOVACIONES;
    }

    @Override
    public LocalDateTime renovar() {
        if (!puedeRenovarse()) {
            System.out.println("No se puede renovar este audiolibro");
            return fechaDevolucion;
        }

        this.fechaDevolucion = this.fechaDevolucion.plusDays(10);
        this.renovacionesRealizadas++;
        System.out.println("Audiolibro renovado hasta " + fechaDevolucion);

        return fechaDevolucion;
    }
}