package app.biblioteca;

import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import app.biblioteca.exceptions.RecursoNoDisponibleException;
import app.biblioteca.interfaces.ServicioNotificaciones;
import app.biblioteca.models.Audiolibro;
import app.biblioteca.models.CategoriaRecurso;
import app.biblioteca.models.Libro;
import app.biblioteca.models.Prestamo;
import app.biblioteca.models.Reserva;
import app.biblioteca.models.Revista;
import app.biblioteca.models.Usuario;
import app.biblioteca.reports.ReporteRecursosMasPrestados;
import app.biblioteca.reports.ReporteUsuariosActivos;
import app.biblioteca.reports.ReporteVisualizer;
import app.biblioteca.services.GestorRecursos;
import app.biblioteca.services.GestorUsuarios;
import app.biblioteca.services.MonitorReservas;
import app.biblioteca.services.MonitorVencimientos;
import app.biblioteca.services.ServicioNotificacionManager;
import app.biblioteca.services.ServicioNotificacionesEmail;
import app.biblioteca.services.SistemaPrestamos;
import app.biblioteca.services.SistemaReservas;

public class App {
    public static void main(String[] args) {
        try {
            // Inicializar servicios principales
            GestorUsuarios gestorUsuarios = new GestorUsuarios();
            GestorRecursos gestorRecursos = new GestorRecursos();
            ServicioNotificaciones servicioNotificaciones = new ServicioNotificacionesEmail();
            ServicioNotificacionManager notificacionManager = new ServicioNotificacionManager(servicioNotificaciones);
            SistemaPrestamos sistemaPrestamos = new SistemaPrestamos(gestorRecursos);
            SistemaReservas sistemaReservas = new SistemaReservas(gestorRecursos);

            // Iniciar monitores
            MonitorVencimientos monitorVencimientos = new MonitorVencimientos(sistemaPrestamos, notificacionManager);
            monitorVencimientos.iniciar();

            MonitorReservas monitorReservas = new MonitorReservas(sistemaReservas, gestorRecursos, notificacionManager);
            monitorReservas.iniciar();

            // Crear usuarios y recursos
            System.out.println("==== Creación de usuarios y recursos ====");

            // Usuarios
            Usuario usuario1 = new Usuario("U001", "Juan Pérez", "juan@example.com");
            Usuario usuario2 = new Usuario("U002", "María López", "maria@example.com");
            Usuario usuario3 = new Usuario("U003", "Carlos Rodríguez", "carlos@example.com");
            Usuario usuario4 = new Usuario("U004", "Ana Martínez", "ana@example.com");

            gestorUsuarios.registrarUsuario(usuario1);
            gestorUsuarios.registrarUsuario(usuario2);
            gestorUsuarios.registrarUsuario(usuario3);
            gestorUsuarios.registrarUsuario(usuario4);

            // Recursos
            Libro libro1 = new Libro("L001", "Cien años de soledad", "Gabriel García Márquez", "9780307476463", 432);
            libro1.setCategoria(CategoriaRecurso.NOVELA);

            Libro libro2 = new Libro("L002", "1984", "George Orwell", "9780451524935", 328);
            libro2.setCategoria(CategoriaRecurso.CIENCIA_FICCION);

            Libro libro3 = new Libro("L003", "El Quijote", "Miguel de Cervantes", "9788420412146", 863);
            libro3.setCategoria(CategoriaRecurso.CLASICO);

            Revista revista1 = new Revista("R001", "National Geographic", "National Geographic Society", "1234-5678",
                    255);
            revista1.setCategoria(CategoriaRecurso.CIENCIA);

            Audiolibro audiolibro1 = new Audiolibro("A001", "El Principito", "Antoine de Saint-Exupéry",
                    "Juan Narrador", 120);
            audiolibro1.setCategoria(CategoriaRecurso.INFANTIL);

            gestorRecursos.agregarRecurso(libro1);
            gestorRecursos.agregarRecurso(libro2);
            gestorRecursos.agregarRecurso(libro3);
            gestorRecursos.agregarRecurso(revista1);
            gestorRecursos.agregarRecurso(audiolibro1);

            System.out.println("\n==== Demostración del sistema de préstamos ====");

            // Realizar varios préstamos para generar datos para los reportes
            try {
                // Préstamos de Juan (usuario1)
                Prestamo prestamo1 = sistemaPrestamos.prestarRecurso(libro1.getIdentificador(), usuario1);
                notificacionManager.enviarNotificacionPrestamo(prestamo1);

                Prestamo prestamo2 = sistemaPrestamos.prestarRecurso(libro3.getIdentificador(), usuario1);
                notificacionManager.enviarNotificacionPrestamo(prestamo2);

                // Préstamos de María (usuario2)
                Prestamo prestamo3 = sistemaPrestamos.prestarRecurso(libro2.getIdentificador(), usuario2);
                notificacionManager.enviarNotificacionPrestamo(prestamo3);

                Prestamo prestamo4 = sistemaPrestamos.prestarRecurso(revista1.getIdentificador(), usuario2);
                notificacionManager.enviarNotificacionPrestamo(prestamo4);

                // Préstamos de Carlos (usuario3)
                Prestamo prestamo5 = sistemaPrestamos.prestarRecurso(audiolibro1.getIdentificador(), usuario3);
                notificacionManager.enviarNotificacionPrestamo(prestamo5);

                // Devoluciones
                TimeUnit.SECONDS.sleep(1);

                // Juan devuelve un libro
                sistemaPrestamos.devolverRecurso(prestamo1.getId());
                notificacionManager.enviarNotificacionDevolucion(prestamo1);

                // María devuelve un libro
                sistemaPrestamos.devolverRecurso(prestamo3.getId());
                notificacionManager.enviarNotificacionDevolucion(prestamo3);

                // María realiza más préstamos para ser la más activa
                Prestamo prestamo6 = sistemaPrestamos.prestarRecurso(libro1.getIdentificador(), usuario2);
                notificacionManager.enviarNotificacionPrestamo(prestamo6);

                // Ana realiza un préstamo
                Prestamo prestamo7 = sistemaPrestamos.prestarRecurso(libro2.getIdentificador(), usuario4);
                notificacionManager.enviarNotificacionPrestamo(prestamo7);

            } catch (RecursoNoDisponibleException e) {
                System.err.println("Error: " + e.getMessage());
            }

            // Esperar a que se procesen todas las acciones
            TimeUnit.SECONDS.sleep(2);

            // Mostrar reportes
            System.out.println("\n==== Demostración del sistema de reportes ====");

            // Crear servicios de reportes
            ReporteRecursosMasPrestados reporteRecursos = new ReporteRecursosMasPrestados(sistemaPrestamos);
            ReporteUsuariosActivos reporteUsuarios = new ReporteUsuariosActivos(sistemaPrestamos);

            // Mostrar reporte de recursos más prestados
            reporteRecursos.mostrarReporte(5);

            // Esperar para mejor visualización
            TimeUnit.SECONDS.sleep(1);

            // Mostrar reporte de usuarios más activos
            reporteUsuarios.mostrarReporte(5);

            // Esperar para mejor visualización
            TimeUnit.SECONDS.sleep(1);

            // Mostrar reporte de puntuación de usuarios
            reporteUsuarios.mostrarReportePuntuacion(5);

            // Esperar para finalizar
            TimeUnit.SECONDS.sleep(2);

            // Detener los servicios
            System.out.println("\n==== Finalizando la aplicación ====");
            monitorVencimientos.detener();
            monitorReservas.detener();
            notificacionManager.detener();

            System.out.println("Aplicación finalizada correctamente.");

        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
}