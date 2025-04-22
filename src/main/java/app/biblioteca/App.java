package app.biblioteca;

import app.biblioteca.interfaces.ServicioNotificaciones;
import app.biblioteca.models.Audiolibro;
import app.biblioteca.models.Libro;
import app.biblioteca.models.Revista;
import app.biblioteca.models.Usuario;
import app.biblioteca.services.GestorRecursos;
import app.biblioteca.services.GestorUsuarios;
import app.biblioteca.services.ServicioNotificacionesEmail;

public class App {
    public static void main(String[] args) {
        // Inicializar gestores
        GestorUsuarios gestorUsuarios = new GestorUsuarios();
        GestorRecursos gestorRecursos = new GestorRecursos();

        // Inicializar servicio de notificaciones
        ServicioNotificaciones servicioNotificaciones = new ServicioNotificacionesEmail();

        // Crear algunos usuarios
        Usuario usuario1 = new Usuario("U001", "Juan Pérez", "juan@example.com");
        Usuario usuario2 = new Usuario("U002", "María López", "maria@example.com");

        // Registrar usuarios
        gestorUsuarios.registrarUsuario(usuario1);
        gestorUsuarios.registrarUsuario(usuario2);

        // Crear algunos recursos
        Libro libro1 = new Libro("L001", "Cien años de soledad", "Gabriel García Márquez", "9780307476463", 432);
        Revista revista1 = new Revista("R001", "National Geographic", "National Geographic Society", "1234-5678", 255);
        Audiolibro audiolibro1 = new Audiolibro("A001", "El Principito", "Antoine de Saint-Exupéry", "Juan Narrador",
                120);

        // Agregar recursos
        gestorRecursos.agregarRecurso(libro1);
        gestorRecursos.agregarRecurso(revista1);
        gestorRecursos.agregarRecurso(audiolibro1);

        // Probar funcionalidad de préstamo
        System.out.println("\n--- Prueba de préstamo ---");
        libro1.prestar(usuario1);

        // Enviar notificación
        servicioNotificaciones.enviarNotificacion(
                usuario1,
                "El libro '" + libro1.getTitulo() + "' debe ser devuelto antes del " + libro1.getFechaDevolucion());

        // Probar renovación
        System.out.println("\n--- Prueba de renovación ---");
        if (libro1.puedeRenovarse()) {
            libro1.renovar();
        }

        // Probar devolución
        System.out.println("\n--- Prueba de devolución ---");
        libro1.devolver();

        // Listar recursos disponibles
        System.out.println("\n--- Recursos disponibles ---");
        for (var recurso : gestorRecursos.listarRecursosDisponibles()) {
            System.out.println(recurso.getTitulo() + " - " + recurso.getEstado());
        }
    }
}