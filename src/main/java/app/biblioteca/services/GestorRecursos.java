package app.biblioteca.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import app.biblioteca.exceptions.RecursoNoDisponibleException;
import app.biblioteca.interfaces.RecursoDigital;
import app.biblioteca.models.CategoriaRecurso;
import app.biblioteca.models.EstadoRecurso;

/**
 * Clase encargada de gestionar los recursos digitales de la biblioteca
 */
public class GestorRecursos {

    private Map<String, RecursoDigital> recursos;

    public GestorRecursos() {
        this.recursos = new HashMap<>();
    }

    /**
     * Agrega un nuevo recurso a la biblioteca
     * 
     * @param recurso Recurso digital a agregar
     */
    public void agregarRecurso(RecursoDigital recurso) {
        recursos.put(recurso.getIdentificador(), recurso);
        System.out.println("Recurso agregado: " + recurso.getTitulo());
    }

    /**
     * Busca un recurso por su identificador
     * 
     * @param identificador ID del recurso a buscar
     * @return Recurso encontrado o null si no existe
     */
    public RecursoDigital buscarPorId(String identificador) {
        return recursos.get(identificador);
    }

    /**
     * Busca recursos por título (búsqueda parcial, no sensible a mayúsculas)
     * 
     * @param titulo Título o parte del título a buscar
     * @return Lista de recursos que coinciden con la búsqueda
     */
    public List<RecursoDigital> buscarPorTitulo(String titulo) {
        return recursos.values().stream()
                .filter(r -> r.getTitulo().toLowerCase().contains(titulo.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Filtra recursos por categoría
     * 
     * @param categoria Categoría a filtrar
     * @return Lista de recursos de la categoría especificada
     */
    public List<RecursoDigital> filtrarPorCategoria(CategoriaRecurso categoria) {
        return recursos.values().stream()
                .filter(r -> r.getCategoria() == categoria)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene recursos disponibles para préstamo
     * 
     * @return Lista de recursos disponibles
     */
    public List<RecursoDigital> obtenerDisponibles() {
        return recursos.values().stream()
                .filter(r -> r.getEstado() == EstadoRecurso.DISPONIBLE)
                .collect(Collectors.toList());
    }

    /**
     * Elimina un recurso de la biblioteca
     * 
     * @param identificador ID del recurso a eliminar
     * @return true si se eliminó correctamente, false si no existía
     */
    public boolean eliminarRecurso(String identificador) {
        RecursoDigital recurso = recursos.remove(identificador);
        if (recurso != null) {
            System.out.println("Recurso eliminado: " + recurso.getTitulo());
            return true;
        }
        return false;
    }

    /**
     * Obtiene todos los recursos ordenados por título
     * 
     * @return Lista ordenada de recursos
     */
    public List<RecursoDigital> obtenerTodosOrdenados() {
        return recursos.values().stream()
                .sorted(Comparator.comparing(RecursoDigital::getTitulo))
                .collect(Collectors.toList());
    }

    /**
     * Actualiza el estado de un recurso
     * 
     * @param identificador ID del recurso a actualizar
     * @param estado        Nuevo estado del recurso
     * @throws RecursoNoDisponibleException si el recurso no existe
     */
    public void actualizarEstadoRecurso(String identificador, EstadoRecurso estado)
            throws RecursoNoDisponibleException {
        RecursoDigital recurso = buscarPorId(identificador);
        if (recurso == null) {
            throw new RecursoNoDisponibleException("El recurso con ID " + identificador + " no existe");
        }
        recurso.actualizarEstado(estado);
    }

    /**
     * Obtiene la cantidad total de recursos
     * 
     * @return Número total de recursos
     */
    public int cantidadTotal() {
        return recursos.size();
    }

    /**
     * Obtiene todos los recursos
     * 
     * @return Lista de todos los recursos
     */
    public List<RecursoDigital> obtenerTodos() {
        return new ArrayList<>(recursos.values());
    }
}