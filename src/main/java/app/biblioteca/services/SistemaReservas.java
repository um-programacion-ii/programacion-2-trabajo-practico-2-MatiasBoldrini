package app.biblioteca.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import app.biblioteca.exceptions.RecursoNoDisponibleException;
import app.biblioteca.interfaces.RecursoDigital;
import app.biblioteca.models.EstadoRecurso;
import app.biblioteca.models.Reserva;
import app.biblioteca.models.Usuario;

/**
 * Clase que gestiona el sistema de reservas de la biblioteca
 */
public class SistemaReservas {

    private GestorRecursos gestorRecursos;
    private Map<String, BlockingQueue<Reserva>> colasReservas;
    private Map<String, Reserva> reservas;

    public SistemaReservas(GestorRecursos gestorRecursos) {
        this.gestorRecursos = gestorRecursos;
        this.colasReservas = new ConcurrentHashMap<>();
        this.reservas = new ConcurrentHashMap<>();
    }

    /**
     * Realiza una reserva de un recurso para un usuario
     * 
     * @param idRecurso ID del recurso a reservar
     * @param usuario   Usuario que solicita la reserva
     * @return Objeto Reserva con la información de la reserva
     * @throws RecursoNoDisponibleException si el recurso no existe
     */
    public synchronized Reserva reservarRecurso(String idRecurso, Usuario usuario) throws RecursoNoDisponibleException {
        RecursoDigital recurso = gestorRecursos.buscarPorId(idRecurso);

        if (recurso == null) {
            throw new RecursoNoDisponibleException("El recurso con ID " + idRecurso + " no existe");
        }

        // Crear la reserva
        Reserva reserva = new Reserva(usuario, idRecurso);

        // Obtener o crear la cola de reservas para este recurso
        BlockingQueue<Reserva> colaRecurso = colasReservas.computeIfAbsent(idRecurso,
                k -> new LinkedBlockingQueue<>());

        // Añadir la reserva a la cola
        colaRecurso.add(reserva);

        // Guardar la reserva en el mapa general
        reservas.put(reserva.getId(), reserva);

        // Si el recurso está disponible, marcarlo como reservado
        if (recurso.getEstado() == EstadoRecurso.DISPONIBLE) {
            recurso.actualizarEstado(EstadoRecurso.RESERVADO);
        }

        System.out.println("Reserva realizada: " + reserva);
        return reserva;
    }

    /**
     * Cancela una reserva existente
     * 
     * @param idReserva ID de la reserva a cancelar
     * @return true si la cancelación fue exitosa
     * @throws RecursoNoDisponibleException si la reserva no existe
     */
    public synchronized boolean cancelarReserva(String idReserva) throws RecursoNoDisponibleException {
        Reserva reserva = reservas.get(idReserva);

        if (reserva == null) {
            throw new RecursoNoDisponibleException("La reserva con ID " + idReserva + " no existe");
        }

        if (!reserva.isActiva()) {
            return false; // Ya fue cancelada
        }

        // Cancelar la reserva
        reserva.cancelar();

        // Eliminar la reserva de la cola
        BlockingQueue<Reserva> colaRecurso = colasReservas.get(reserva.getIdRecurso());
        if (colaRecurso != null) {
            colaRecurso.remove(reserva);

            // Si la cola está vacía y el recurso estaba reservado, marcarlo como disponible
            if (colaRecurso.isEmpty()) {
                RecursoDigital recurso = gestorRecursos.buscarPorId(reserva.getIdRecurso());
                if (recurso != null && recurso.getEstado() == EstadoRecurso.RESERVADO) {
                    recurso.actualizarEstado(EstadoRecurso.DISPONIBLE);
                }
            }
        }

        System.out.println("Reserva cancelada: " + reserva);
        return true;
    }

    /**
     * Obtiene la siguiente reserva en la cola para un recurso
     * 
     * @param idRecurso ID del recurso
     * @return La siguiente reserva o null si no hay reservas
     */
    public synchronized Reserva obtenerSiguienteReserva(String idRecurso) {
        BlockingQueue<Reserva> colaRecurso = colasReservas.get(idRecurso);

        if (colaRecurso == null || colaRecurso.isEmpty()) {
            return null;
        }

        // Obtenemos todas las reservas activas y las ordenamos por prioridad
        List<Reserva> reservasActivas = colaRecurso.stream()
                .filter(Reserva::isActiva)
                .sorted()
                .collect(Collectors.toList());

        return reservasActivas.isEmpty() ? null : reservasActivas.get(0);
    }

    /**
     * Procesa la siguiente reserva en la cola
     * 
     * @param idRecurso ID del recurso
     * @return La reserva procesada o null si no hay reservas
     */
    public synchronized Reserva procesarSiguienteReserva(String idRecurso) {
        BlockingQueue<Reserva> colaRecurso = colasReservas.get(idRecurso);

        if (colaRecurso == null || colaRecurso.isEmpty()) {
            return null;
        }

        // Obtenemos todas las reservas activas y las ordenamos por prioridad
        List<Reserva> reservasActivas = colaRecurso.stream()
                .filter(Reserva::isActiva)
                .sorted()
                .collect(Collectors.toList());

        if (reservasActivas.isEmpty()) {
            return null;
        }

        // Tomamos la reserva con mayor prioridad
        Reserva reserva = reservasActivas.get(0);

        // La marcamos como procesada
        reserva.cancelar();

        // La eliminamos de la cola
        colaRecurso.remove(reserva);

        return reserva;
    }

    /**
     * Comprueba si hay reservas activas para un recurso
     * 
     * @param idRecurso ID del recurso
     * @return true si hay reservas activas
     */
    public boolean hayReservasActivas(String idRecurso) {
        BlockingQueue<Reserva> colaRecurso = colasReservas.get(idRecurso);

        if (colaRecurso == null) {
            return false;
        }

        return colaRecurso.stream().anyMatch(Reserva::isActiva);
    }

    /**
     * Obtiene todas las reservas activas
     * 
     * @return Lista de reservas activas
     */
    public List<Reserva> obtenerReservasActivas() {
        return reservas.values().stream()
                .filter(Reserva::isActiva)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene reservas activas de un usuario
     * 
     * @param idUsuario ID del usuario
     * @return Lista de reservas activas del usuario
     */
    public List<Reserva> obtenerReservasUsuario(String idUsuario) {
        return reservas.values().stream()
                .filter(r -> r.isActiva() && r.getUsuario().getId().equals(idUsuario))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las reservas
     * 
     * @return Lista de todas las reservas
     */
    public List<Reserva> obtenerTodas() {
        return new ArrayList<>(reservas.values());
    }
}