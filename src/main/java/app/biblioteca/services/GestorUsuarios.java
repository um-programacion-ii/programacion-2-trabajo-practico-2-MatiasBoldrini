package app.biblioteca.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import app.biblioteca.exceptions.UsuarioNoEncontradoException;
import app.biblioteca.models.Usuario;

/**
 * Clase encargada de gestionar los usuarios de la biblioteca
 */
public class GestorUsuarios {

    private Map<String, Usuario> usuarios;

    public GestorUsuarios() {
        this.usuarios = new HashMap<>();
    }

    /**
     * Registra un nuevo usuario en el sistema
     * 
     * @param usuario Usuario a registrar
     */
    public void registrarUsuario(Usuario usuario) {
        usuarios.put(usuario.getId(), usuario);
        System.out.println("Usuario registrado: " + usuario.getNombre());
    }

    /**
     * Busca un usuario por su identificador
     * 
     * @param id ID del usuario a buscar
     * @return Usuario encontrado
     * @throws UsuarioNoEncontradoException si el usuario no existe
     */
    public Usuario buscarPorId(String id) throws UsuarioNoEncontradoException {
        Usuario usuario = usuarios.get(id);
        if (usuario == null) {
            throw new UsuarioNoEncontradoException("El usuario con ID " + id + " no existe");
        }
        return usuario;
    }

    /**
     * Busca usuarios por nombre (búsqueda parcial, no sensible a mayúsculas)
     * 
     * @param nombre Nombre o parte del nombre a buscar
     * @return Lista de usuarios que coinciden con la búsqueda
     */
    public List<Usuario> buscarPorNombre(String nombre) {
        return usuarios.values().stream()
                .filter(u -> u.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Elimina un usuario del sistema
     * 
     * @param id ID del usuario a eliminar
     * @return true si se eliminó correctamente, false si no existía
     */
    public boolean eliminarUsuario(String id) {
        Usuario usuario = usuarios.remove(id);
        if (usuario != null) {
            System.out.println("Usuario eliminado: " + usuario.getNombre());
            return true;
        }
        return false;
    }

    /**
     * Obtiene todos los usuarios ordenados por nombre
     * 
     * @return Lista ordenada de usuarios
     */
    public List<Usuario> obtenerTodosOrdenados() {
        return usuarios.values().stream()
                .sorted(Comparator.comparing(Usuario::getNombre))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene usuarios ordenados por puntuación (mayor a menor)
     * 
     * @return Lista de usuarios ordenada por puntuación
     */
    public List<Usuario> obtenerPorPuntuacion() {
        return usuarios.values().stream()
                .sorted(Comparator.comparing(Usuario::getPuntuacion).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Obtiene la cantidad total de usuarios
     * 
     * @return Número total de usuarios
     */
    public int cantidadTotal() {
        return usuarios.size();
    }

    /**
     * Obtiene todos los usuarios
     * 
     * @return Lista de todos los usuarios
     */
    public List<Usuario> obtenerTodos() {
        return new ArrayList<>(usuarios.values());
    }
}