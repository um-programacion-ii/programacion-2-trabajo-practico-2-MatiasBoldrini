package app.biblioteca.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import app.biblioteca.exceptions.RecursoNoDisponibleException;
import app.biblioteca.interfaces.RecursoDigital;
import app.biblioteca.models.EstadoRecurso;
import app.biblioteca.models.Prestamo;
import app.biblioteca.models.Usuario;

public class SistemaPrestamos {
    private ConcurrentHashMap<String, Prestamo> prestamos;
    private GestorRecursos gestorRecursos;

    public SistemaPrestamos(GestorRecursos gestorRecursos) {
        this.prestamos = new ConcurrentHashMap<>();
        this.gestorRecursos = gestorRecursos;
    }

    public synchronized Prestamo prestarRecurso(String idRecurso, Usuario usuario) throws RecursoNoDisponibleException {
        RecursoDigital recurso = gestorRecursos.buscarRecursoPorId(idRecurso);

        if (recurso == null) {
            throw new RecursoNoDisponibleException("El recurso con ID " + idRecurso + " no existe");
        }

        if (!recurso.estaDisponible()) {
            throw new RecursoNoDisponibleException("El recurso " + recurso.getTitulo() + " no está disponible");
        }

        // Calculamos la fecha de devolución
        LocalDateTime fechaDevolucion = calcularFechaDevolucion(recurso);

        // Creamos el préstamo
        Prestamo prestamo = new Prestamo(recurso, usuario, fechaDevolucion);

        // Actualizamos el estado del recurso
        recurso.actualizarEstado(EstadoRecurso.PRESTADO);

        // Guardamos el préstamo
        prestamos.put(prestamo.getId(), prestamo);

        System.out.println("Préstamo realizado: " + prestamo);

        return prestamo;
    }

    public synchronized boolean devolverRecurso(String idPrestamo) {
        Prestamo prestamo = prestamos.get(idPrestamo);

        if (prestamo == null || prestamo.isDevuelto()) {
            System.out.println("El préstamo no existe o ya fue devuelto");
            return false;
        }

        // Marcar el préstamo como devuelto
        prestamo.marcarComoDevuelto();

        // Actualizar el estado del recurso
        RecursoDigital recurso = prestamo.getRecurso();
        recurso.actualizarEstado(EstadoRecurso.DISPONIBLE);

        System.out.println("Recurso devuelto: " + recurso.getTitulo());

        return true;
    }

    public synchronized boolean renovarPrestamo(String idPrestamo) {
        Prestamo prestamo = prestamos.get(idPrestamo);

        if (prestamo == null || prestamo.isDevuelto()) {
            System.out.println("El préstamo no existe o ya fue devuelto");
            return false;
        }

        if (prestamo.estaVencido()) {
            System.out.println("El préstamo está vencido y no puede renovarse");
            return false;
        }

        // Calculamos nueva fecha de devolución
        LocalDateTime nuevaFecha = prestamo.getFechaDevolucion().plusDays(15);
        prestamo.setFechaDevolucion(nuevaFecha);

        System.out.println("Préstamo renovado hasta: " + nuevaFecha);

        return true;
    }

    private LocalDateTime calcularFechaDevolucion(RecursoDigital recurso) {
        // Por defecto, 15 días para cualquier recurso
        return LocalDateTime.now().plusDays(15);
    }

    public List<Prestamo> listarPrestamosActivos() {
        return prestamos.values().stream()
                .filter(p -> !p.isDevuelto())
                .collect(Collectors.toList());
    }

    public List<Prestamo> listarPrestamosPorUsuario(Usuario usuario) {
        return prestamos.values().stream()
                .filter(p -> p.getUsuario().getId().equals(usuario.getId()))
                .collect(Collectors.toList());
    }

    public List<Prestamo> listarPrestamosVencidos() {
        return prestamos.values().stream()
                .filter(p -> !p.isDevuelto() && p.estaVencido())
                .collect(Collectors.toList());
    }

    /**
     * Retorna una lista con todos los préstamos, tanto activos como devueltos
     * 
     * @return Lista de todos los préstamos
     */
    public List<Prestamo> listarTodosPrestamos() {
        return new ArrayList<>(prestamos.values());
    }
}