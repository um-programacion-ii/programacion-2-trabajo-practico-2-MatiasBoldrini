package app.biblioteca.services;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import app.biblioteca.exceptions.RecursoNoDisponibleException;
import app.biblioteca.interfaces.RecursoDigital;
import app.biblioteca.models.EstadoRecurso;
import app.biblioteca.models.Reserva;
import app.biblioteca.models.Usuario;

public class SistemaReservas {
    private ConcurrentHashMap<String, BlockingQueue<Reserva>> reservasPorRecurso;
    private ConcurrentHashMap<String, Reserva> todasLasReservas;
    private GestorRecursos gestorRecursos;
    private ReentrantLock lock = new ReentrantLock();

    public SistemaReservas(GestorRecursos gestorRecursos) {
        this.reservasPorRecurso = new ConcurrentHashMap<>();
        this.todasLasReservas = new ConcurrentHashMap<>();
        this.gestorRecursos = gestorRecursos;
    }

    public Reserva reservarRecurso(String idRecurso, Usuario usuario, int prioridad)
            throws RecursoNoDisponibleException {
        RecursoDigital recurso = gestorRecursos.buscarRecursoPorId(idRecurso);

        if (recurso == null) {
            throw new RecursoNoDisponibleException("El recurso con ID " + idRecurso + " no existe");
        }

        lock.lock();
        try {
            // Crear la reserva
            Reserva reserva = new Reserva(recurso, usuario, prioridad);

            // Actualizar el estado del recurso si está disponible
            if (recurso.estaDisponible()) {
                recurso.actualizarEstado(EstadoRecurso.RESERVADO);
            }

            // Obtener o crear la cola de reservas para este recurso
            BlockingQueue<Reserva> colaReservas = reservasPorRecurso.computeIfAbsent(
                    idRecurso, k -> new LinkedBlockingQueue<>());

            // Añadir la reserva a la cola
            colaReservas.add(reserva);

            // Guardar la referencia a la reserva
            todasLasReservas.put(reserva.getId(), reserva);

            System.out.println("Reserva realizada: " + reserva);

            return reserva;
        } finally {
            lock.unlock();
        }
    }

    public boolean cancelarReserva(String idReserva) {
        Reserva reserva = todasLasReservas.get(idReserva);

        if (reserva == null || !reserva.isActiva()) {
            System.out.println("La reserva no existe o ya fue cancelada");
            return false;
        }

        lock.lock();
        try {
            // Marcar la reserva como inactiva
            reserva.cancelar();

            String idRecurso = reserva.getRecurso().getIdentificador();
            BlockingQueue<Reserva> colaReservas = reservasPorRecurso.get(idRecurso);

            if (colaReservas != null) {
                // Eliminar la reserva de la cola
                colaReservas.remove(reserva);

                // Si no hay más reservas, marcar el recurso como disponible
                if (colaReservas.isEmpty() && reserva.getRecurso().getEstado() == EstadoRecurso.RESERVADO) {
                    reserva.getRecurso().actualizarEstado(EstadoRecurso.DISPONIBLE);
                }
            }

            System.out.println("Reserva cancelada: " + reserva);

            return true;
        } finally {
            lock.unlock();
        }
    }

    public List<Reserva> listarReservasActivas() {
        return todasLasReservas.values().stream()
                .filter(Reserva::isActiva)
                .collect(Collectors.toList());
    }

    public List<Reserva> listarReservasPorUsuario(Usuario usuario) {
        return todasLasReservas.values().stream()
                .filter(r -> r.isActiva() && r.getUsuario().getId().equals(usuario.getId()))
                .collect(Collectors.toList());
    }

    public List<Reserva> listarReservasPorRecurso(String idRecurso) {
        BlockingQueue<Reserva> colaReservas = reservasPorRecurso.get(idRecurso);

        if (colaReservas == null) {
            return new ArrayList<>();
        }

        // Convertir la cola a lista ordenada por prioridad
        PriorityQueue<Reserva> colaOrdenada = new PriorityQueue<>(colaReservas);
        List<Reserva> result = new ArrayList<>();

        while (!colaOrdenada.isEmpty()) {
            Reserva r = colaOrdenada.poll();
            if (r.isActiva()) {
                result.add(r);
            }
        }

        return result;
    }

    public Reserva obtenerSiguienteReserva(String idRecurso) {
        BlockingQueue<Reserva> colaReservas = reservasPorRecurso.get(idRecurso);

        if (colaReservas == null || colaReservas.isEmpty()) {
            return null;
        }

        // Convertir la cola a cola de prioridad
        PriorityQueue<Reserva> colaOrdenada = new PriorityQueue<>(colaReservas);

        // Obtener la reserva de mayor prioridad
        return colaOrdenada.peek();
    }

    public void limpiarReservasExpiradas() {
        List<Reserva> expiradas = todasLasReservas.values().stream()
                .filter(r -> r.isActiva() && r.haExpirado())
                .collect(Collectors.toList());

        for (Reserva reserva : expiradas) {
            cancelarReserva(reserva.getId());
        }

        if (!expiradas.isEmpty()) {
            System.out.println("Se han eliminado " + expiradas.size() + " reservas expiradas");
        }
    }
}