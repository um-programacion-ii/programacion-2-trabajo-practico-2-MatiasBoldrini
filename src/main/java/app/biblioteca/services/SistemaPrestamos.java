package app.biblioteca.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import app.biblioteca.exceptions.RecursoNoDisponibleException;
import app.biblioteca.interfaces.Prestable;
import app.biblioteca.interfaces.RecursoDigital;
import app.biblioteca.interfaces.Renovable;
import app.biblioteca.models.EstadoRecurso;
import app.biblioteca.models.Prestamo;
import app.biblioteca.models.Usuario;

/**
 * Clase encargada de gestionar el sistema de préstamos
 */
public class SistemaPrestamos {

    private GestorRecursos gestorRecursos;
    private Map<String, Prestamo> prestamos;
    private ExecutorService executor;
    private Map<String, Integer> estadisticasRecursos;
    private Map<String, Integer> estadisticasUsuarios;

    public SistemaPrestamos(GestorRecursos gestorRecursos) {
        this.gestorRecursos = gestorRecursos;
        this.prestamos = new ConcurrentHashMap<>();
        this.executor = Executors.newFixedThreadPool(2);
        this.estadisticasRecursos = new HashMap<>();
        this.estadisticasUsuarios = new HashMap<>();
    }

    /**
     * Realiza un préstamo de un recurso a un usuario
     * 
     * @param idRecurso ID del recurso a prestar
     * @param usuario   Usuario que solicita el préstamo
     * @return Objeto Prestamo con la información del préstamo
     * @throws RecursoNoDisponibleException si el recurso no existe o no está
     *                                      disponible
     */
    public Prestamo prestarRecurso(String idRecurso, Usuario usuario) throws RecursoNoDisponibleException {
        // Sincronizamos el acceso al recurso
        synchronized (this) {
            RecursoDigital recurso = gestorRecursos.buscarPorId(idRecurso);

            if (recurso == null) {
                throw new RecursoNoDisponibleException("El recurso con ID " + idRecurso + " no existe");
            }

            if (!(recurso instanceof Prestable)) {
                throw new RecursoNoDisponibleException("El recurso con ID " + idRecurso + " no es prestable");
            }

            Prestable recursoPrestable = (Prestable) recurso;

            if (!recursoPrestable.estaDisponible()) {
                throw new RecursoNoDisponibleException("El recurso con ID " + idRecurso + " no está disponible");
            }

            // Creamos el préstamo
            final Prestamo prestamo = new Prestamo(usuario, idRecurso);

            // Actualizamos el estado del recurso
            recursoPrestable.prestar(usuario);
            recurso.actualizarEstado(EstadoRecurso.PRESTADO);

            // Guardamos el préstamo
            prestamos.put(prestamo.getId(), prestamo);

            // Actualizamos estadísticas (en segundo plano)
            executor.submit(() -> {
                actualizarEstadisticas(idRecurso, usuario.getId());
                usuario.agregarPrestamo(prestamo);
            });

            return prestamo;
        }
    }

    /**
     * Procesa la devolución de un recurso
     * 
     * @param idPrestamo ID del préstamo a devolver
     * @return true si la devolución fue exitosa
     * @throws RecursoNoDisponibleException si el préstamo no existe
     */
    public boolean devolverRecurso(String idPrestamo) throws RecursoNoDisponibleException {
        Prestamo prestamo = prestamos.get(idPrestamo);

        if (prestamo == null) {
            throw new RecursoNoDisponibleException("El préstamo con ID " + idPrestamo + " no existe");
        }

        if (!prestamo.isActivo()) {
            return false; // Ya fue devuelto
        }

        synchronized (this) {
            RecursoDigital recurso = gestorRecursos.buscarPorId(prestamo.getIdRecurso());

            if (recurso == null) {
                throw new RecursoNoDisponibleException("El recurso asociado no existe");
            }

            if (!(recurso instanceof Prestable)) {
                throw new RecursoNoDisponibleException("El recurso no es prestable");
            }

            Prestable recursoPrestable = (Prestable) recurso;

            // Finalizamos el préstamo
            prestamo.finalizarPrestamo();

            // Actualizamos el estado del recurso
            ((Prestable) recurso).devolver();
            recurso.actualizarEstado(EstadoRecurso.DISPONIBLE);

            return true;
        }
    }

    /**
     * Renueva un préstamo existente
     * 
     * @param idPrestamo    ID del préstamo a renovar
     * @param diasExtension Días adicionales para el préstamo
     * @return Nueva fecha de devolución si se pudo renovar
     * @throws RecursoNoDisponibleException si el préstamo no existe o no es
     *                                      renovable
     */
    public LocalDateTime renovarPrestamo(String idPrestamo, int diasExtension) throws RecursoNoDisponibleException {
        Prestamo prestamo = prestamos.get(idPrestamo);

        if (prestamo == null) {
            throw new RecursoNoDisponibleException("El préstamo con ID " + idPrestamo + " no existe");
        }

        if (!prestamo.isActivo()) {
            throw new RecursoNoDisponibleException("El préstamo ya fue devuelto");
        }

        synchronized (this) {
            RecursoDigital recurso = gestorRecursos.buscarPorId(prestamo.getIdRecurso());

            if (!(recurso instanceof Renovable)) {
                throw new RecursoNoDisponibleException("El recurso no es renovable");
            }

            Renovable recursoRenovable = (Renovable) recurso;

            if (!recursoRenovable.esRenovable()) {
                throw new RecursoNoDisponibleException("El recurso ya alcanzó el máximo de renovaciones");
            }

            // Renovamos el préstamo
            LocalDateTime nuevaFecha = recursoRenovable.renovar(diasExtension);
            prestamo.setFechaDevolucion(nuevaFecha);

            return nuevaFecha;
        }
    }

    /**
     * Busca préstamos activos de un usuario
     * 
     * @param idUsuario ID del usuario
     * @return Lista de préstamos activos del usuario
     */
    public List<Prestamo> buscarPrestamosActivosUsuario(String idUsuario) {
        return prestamos.values().stream()
                .filter(p -> p.getUsuario().getId().equals(idUsuario) && p.isActivo())
                .collect(Collectors.toList());
    }

    /**
     * Obtiene préstamos por vencer en los próximos días
     * 
     * @param dias Número de días a considerar
     * @return Lista de préstamos que vencen en los próximos días
     */
    public List<Prestamo> obtenerPrestamosPorVencer(int dias) {
        LocalDateTime fechaLimite = LocalDateTime.now().plusDays(dias);

        return prestamos.values().stream()
                .filter(p -> p.isActivo() &&
                        p.getFechaDevolucion().isBefore(fechaLimite) &&
                        !p.isVencido())
                .collect(Collectors.toList());
    }

    /**
     * Obtiene préstamos vencidos
     * 
     * @return Lista de préstamos vencidos
     */
    public List<Prestamo> obtenerPrestamosVencidos() {
        return prestamos.values().stream()
                .filter(p -> p.isActivo() && p.isVencido())
                .collect(Collectors.toList());
    }

    /**
     * Actualiza las estadísticas de préstamos
     */
    private void actualizarEstadisticas(String idRecurso, String idUsuario) {
        // Actualiza estadísticas de recursos
        estadisticasRecursos.put(idRecurso,
                estadisticasRecursos.getOrDefault(idRecurso, 0) + 1);

        // Actualiza estadísticas de usuarios
        estadisticasUsuarios.put(idUsuario,
                estadisticasUsuarios.getOrDefault(idUsuario, 0) + 1);
    }

    /**
     * Obtiene los recursos más prestados
     * 
     * @param limite Número máximo de recursos a devolver
     * @return Lista ordenada de IDs de recursos más prestados
     */
    public List<String> obtenerRecursosMasPrestados(int limite) {
        return estadisticasRecursos.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limite)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene los usuarios con más préstamos
     * 
     * @param limite Número máximo de usuarios a devolver
     * @return Lista ordenada de IDs de usuarios con más préstamos
     */
    public List<String> obtenerUsuariosMasActivos(int limite) {
        return estadisticasUsuarios.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limite)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los préstamos
     * 
     * @return Lista de todos los préstamos
     */
    public List<Prestamo> obtenerTodos() {
        return new ArrayList<>(prestamos.values());
    }

    /**
     * Finaliza los recursos utilizados por el sistema
     */
    public void finalizarRecursos() {
        executor.shutdown();
    }
}