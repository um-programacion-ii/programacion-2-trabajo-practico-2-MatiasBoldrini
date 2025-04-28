package app.biblioteca.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.biblioteca.models.Usuario;

public class GestorUsuarios {
    private Map<String, Usuario> usuarios;

    public GestorUsuarios() {
        this.usuarios = new HashMap<>();
    }

    public void registrarUsuario(Usuario usuario) {
        if (usuarios.containsKey(usuario.getId())) {
            System.out.println("Ya existe un usuario con el ID: " + usuario.getId());
            return;
        }

        usuarios.put(usuario.getId(), usuario);
        System.out.println("Usuario registrado correctamente: " + usuario.getNombre());
    }

    public Usuario buscarUsuarioPorId(String id) {
        return usuarios.get(id);
    }

    public List<Usuario> listarUsuarios() {
        return new ArrayList<>(usuarios.values());
    }

    public void actualizarUsuario(Usuario usuario) {
        if (!usuarios.containsKey(usuario.getId())) {
            System.out.println("No existe un usuario con el ID: " + usuario.getId());
            return;
        }

        usuarios.put(usuario.getId(), usuario);
        System.out.println("Usuario actualizado correctamente: " + usuario.getNombre());
    }

    public void eliminarUsuario(String id) {
        if (!usuarios.containsKey(id)) {
            System.out.println("No existe un usuario con el ID: " + id);
            return;
        }

        Usuario usuario = usuarios.remove(id);
        System.out.println("Usuario eliminado correctamente: " + usuario.getNombre());
    }
}