package app.biblioteca.services;

import java.util.ArrayList;
import java.util.List;

import app.biblioteca.interfaces.RecursoDigital;
import app.biblioteca.models.EstadoRecurso;

public class GestorRecursos {
    private List<RecursoDigital> recursos;

    public GestorRecursos() {
        this.recursos = new ArrayList<>();
    }

    public void agregarRecurso(RecursoDigital recurso) {
        recursos.add(recurso);
        System.out.println("Recurso agregado correctamente: " + recurso.getTitulo());
    }

    public RecursoDigital buscarRecursoPorId(String identificador) {
        for (RecursoDigital recurso : recursos) {
            if (recurso.getIdentificador().equals(identificador)) {
                return recurso;
            }
        }
        return null;
    }

    public List<RecursoDigital> buscarRecursosPorTitulo(String titulo) {
        List<RecursoDigital> resultados = new ArrayList<>();

        for (RecursoDigital recurso : recursos) {
            if (recurso.getTitulo().toLowerCase().contains(titulo.toLowerCase())) {
                resultados.add(recurso);
            }
        }

        return resultados;
    }

    public List<RecursoDigital> listarRecursos() {
        return new ArrayList<>(recursos);
    }

    public List<RecursoDigital> listarRecursosDisponibles() {
        List<RecursoDigital> disponibles = new ArrayList<>();

        for (RecursoDigital recurso : recursos) {
            if (recurso.getEstado() == EstadoRecurso.DISPONIBLE) {
                disponibles.add(recurso);
            }
        }

        return disponibles;
    }

    public boolean eliminarRecurso(String identificador) {
        RecursoDigital recurso = buscarRecursoPorId(identificador);

        if (recurso != null) {
            recursos.remove(recurso);
            System.out.println("Recurso eliminado correctamente: " + recurso.getTitulo());
            return true;
        }

        System.out.println("No se encontró el recurso con identificador: " + identificador);
        return false;
    }
}