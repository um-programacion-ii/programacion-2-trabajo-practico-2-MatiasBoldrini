package app.biblioteca;

import app.biblioteca.interfaces.ServicioNotificaciones;
import app.biblioteca.models.Audiolibro;
import app.biblioteca.models.CategoriaRecurso;
import app.biblioteca.models.Libro;
import app.biblioteca.models.Revista;
import app.biblioteca.models.Usuario;
import app.biblioteca.services.GestorRecursos;
import app.biblioteca.services.GestorUsuarios;
import app.biblioteca.services.ServicioBusqueda;
import app.biblioteca.services.ServicioNotificacionesEmail;

public class App {
    public static void main(String[] args) {
        // Inicializar gestores
        GestorUsuarios gestorUsuarios = new GestorUsuarios();
        GestorRecursos gestorRecursos = new GestorRecursos();

        // Inicializar servicios
        ServicioNotificaciones servicioNotificaciones = new ServicioNotificacionesEmail();
        ServicioBusqueda servicioBusqueda = new ServicioBusqueda(gestorRecursos);

        // Crear algunos usuarios
        Usuario usuario1 = new Usuario("U001", "Juan Pérez", "juan@example.com");
        Usuario usuario2 = new Usuario("U002", "María López", "maria@example.com");

        // Registrar usuarios
        gestorUsuarios.registrarUsuario(usuario1);
        gestorUsuarios.registrarUsuario(usuario2);

        // Crear algunos recursos
        Libro libro1 = new Libro("L001", "Cien años de soledad", "Gabriel García Márquez", "9780307476463", 432);
        libro1.setCategoria(CategoriaRecurso.NOVELA);

        Libro libro2 = new Libro("L002", "1984", "George Orwell", "9780451524935", 328);
        libro2.setCategoria(CategoriaRecurso.CIENCIA_FICCION);

        Revista revista1 = new Revista("R001", "National Geographic", "National Geographic Society", "1234-5678", 255);
        revista1.setCategoria(CategoriaRecurso.CIENCIA);

        Audiolibro audiolibro1 = new Audiolibro("A001", "El Principito", "Antoine de Saint-Exupéry", "Juan Narrador",
                120);
        audiolibro1.setCategoria(CategoriaRecurso.INFANTIL);

        // Agregar recursos
        gestorRecursos.agregarRecurso(libro1);
        gestorRecursos.agregarRecurso(libro2);
        gestorRecursos.agregarRecurso(revista1);
        gestorRecursos.agregarRecurso(audiolibro1);

        // Probar funcionalidad de préstamo
        System.out.println("\n--- Prueba de préstamo ---");
        libro1.prestar(usuario1);

        // Enviar notificación
        servicioNotificaciones.enviarNotificacion(
                usuario1,
                "El libro '" + libro1.getTitulo() + "' debe ser devuelto antes del " + libro1.getFechaDevolucion());

        // Probar búsqueda por título
        System.out.println("\n--- Búsqueda por título ---");
        var resultadosTitulo = servicioBusqueda.buscarPorTitulo("años");
        for (var recurso : resultadosTitulo) {
            System.out.println(recurso.getTitulo() + " - " + recurso.getCategoria());
        }

        // Probar búsqueda por categoría
        System.out.println("\n--- Búsqueda por categoría ---");
        var resultadosCategoria = servicioBusqueda.buscarPorCategoria(CategoriaRecurso.CIENCIA_FICCION);
        for (var recurso : resultadosCategoria) {
            System.out.println(recurso.getTitulo() + " - " + recurso.getCategoria());
        }

        // Probar listado ordenado por título
        System.out.println("\n--- Listado ordenado por título ---");
        var resultadosOrdenados = servicioBusqueda.listarOrdenadosPorTitulo();
        for (var recurso : resultadosOrdenados) {
            System.out.println(recurso.getTitulo());
        }

        // Probar filtrado de disponibles
        System.out.println("\n--- Recursos disponibles ---");
        var recursosDisponibles = servicioBusqueda.filtrarDisponibles();
        for (var recurso : recursosDisponibles) {
            System.out.println(recurso.getTitulo() + " - " + recurso.getEstado());
        }

        // Probar devolución
        System.out.println("\n--- Prueba de devolución ---");
        libro1.devolver();
    }
}