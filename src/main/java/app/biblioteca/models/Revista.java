package app.biblioteca.models;

import java.time.LocalDateTime;
import app.biblioteca.interfaces.Prestable;

public class Revista extends RecursoBase implements Prestable {
    private String editorial;
    private String issn;
    private int numero;
    private LocalDateTime fechaDevolucion;
    private Usuario usuarioPrestamo;

    public Revista(String identificador, String titulo, String editorial, String issn, int numero) {
        super(identificador, titulo);
        this.editorial = editorial;
        this.issn = issn;
        this.numero = numero;
    }

    public String getEditorial() {
        return editorial;
    }

    public String getIssn() {
        return issn;
    }

    public int getNumero() {
        return numero;
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
            System.out.println("La revista no está disponible para préstamo");
            return;
        }

        this.usuarioPrestamo = usuario;
        this.fechaDevolucion = LocalDateTime.now().plusDays(7); // Las revistas tienen menor tiempo de préstamo
        this.estado = EstadoRecurso.PRESTADO;
        System.out.println("Revista prestada a " + usuario.getNombre() + " hasta " + fechaDevolucion);
    }

    @Override
    public void devolver() {
        if (this.estado != EstadoRecurso.PRESTADO) {
            System.out.println("Esta revista no está prestada");
            return;
        }

        this.usuarioPrestamo = null;
        this.fechaDevolucion = null;
        this.estado = EstadoRecurso.DISPONIBLE;
        System.out.println("Revista devuelta correctamente");
    }
}