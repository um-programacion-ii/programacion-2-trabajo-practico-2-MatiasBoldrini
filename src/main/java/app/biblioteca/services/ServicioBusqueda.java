package app.biblioteca.services;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import app.biblioteca.interfaces.RecursoDigital;
import app.biblioteca.models.CategoriaRecurso;

public class ServicioBusqueda {
    private GestorRecursos gestorRecursos;

    public ServicioBusqueda(GestorRecursos gestorRecursos) {
        this.gestorRecursos = gestorRecursos;
    }

    public List<RecursoDigital> buscarPorTitulo(String titulo) {
        return gestorRecursos.listarRecursos().stream()
                .filter(recurso -> recurso.getTitulo().toLowerCase().contains(titulo.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<RecursoDigital> buscarPorCategoria(CategoriaRecurso categoria) {
        return gestorRecursos.listarRecursos().stream()
                .filter(recurso -> recurso.getCategoria() == categoria)
                .collect(Collectors.toList());
    }

    public List<RecursoDigital> buscarPorTituloYCategoria(String titulo, CategoriaRecurso categoria) {
        return gestorRecursos.listarRecursos().stream()
                .filter(recurso -> recurso.getTitulo().toLowerCase().contains(titulo.toLowerCase()))
                .filter(recurso -> recurso.getCategoria() == categoria)
                .collect(Collectors.toList());
    }

    public List<RecursoDigital> listarOrdenadosPorTitulo() {
        return gestorRecursos.listarRecursos().stream()
                .sorted(Comparator.comparing(RecursoDigital::getTitulo))
                .collect(Collectors.toList());
    }

    public List<RecursoDigital> listarOrdenadosPorCategoria() {
        return gestorRecursos.listarRecursos().stream()
                .sorted(Comparator.comparing(recurso -> recurso.getCategoria().name()))
                .collect(Collectors.toList());
    }

    public List<RecursoDigital> filtrarDisponibles() {
        return gestorRecursos.listarRecursos().stream()
                .filter(recurso -> recurso.estaDisponible()) // Asumiendo que RecursoDigital tiene el método
                                                             // estaDisponible
                .collect(Collectors.toList());
    }

    public List<RecursoDigital> filtrarPorCategoriaDisponibles(CategoriaRecurso categoria) {
        return gestorRecursos.listarRecursos().stream()
                .filter(recurso -> recurso.getCategoria() == categoria)
                .filter(recurso -> recurso.estaDisponible())
                .collect(Collectors.toList());
    }
}