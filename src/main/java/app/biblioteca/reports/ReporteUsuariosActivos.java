package app.biblioteca.reports;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.biblioteca.exceptions.UsuarioNoEncontradoException;
import app.biblioteca.models.Prestamo;
import app.biblioteca.models.Usuario;
import app.biblioteca.services.GestorUsuarios;
import app.biblioteca.services.SistemaPrestamos;

/**
 * Clase que genera reportes de usuarios más activos en el sistema
 */
public class ReporteUsuariosActivos {

    private SistemaPrestamos sistemaPrestamos;
    private GestorUsuarios gestorUsuarios;
    private ExecutorService executor;
    private ReporteVisualizer visualizer;

    public ReporteUsuariosActivos(SistemaPrestamos sistemaPrestamos) {
        this.sistemaPrestamos = sistemaPrestamos;
        this.executor = Executors.newSingleThreadExecutor();
        this.visualizer = new ReporteVisualizer();
    }

    public ReporteUsuariosActivos(SistemaPrestamos sistemaPrestamos, GestorUsuarios gestorUsuarios) {
        this.sistemaPrestamos = sistemaPrestamos;
        this.gestorUsuarios = gestorUsuarios;
        this.executor = Executors.newSingleThreadExecutor();
        this.visualizer = new ReporteVisualizer();
    }

    /**
     * Genera un mapa con los usuarios y la cantidad de préstamos realizados
     */
    public Map<Usuario, Integer> generarReporte() {
        List<Prestamo> prestamos = sistemaPrestamos.listarTodosPrestamos();
        Map<Usuario, Integer> conteoUsuarios = new HashMap<>();

        for (Prestamo prestamo : prestamos) {
            Usuario usuario = prestamo.getUsuario();
            conteoUsuarios.put(usuario, conteoUsuarios.getOrDefault(usuario, 0) + 1);
        }

        return conteoUsuarios;
    }

    /**
     * Genera un mapa con los usuarios y la cantidad de préstamos realizados en un
     * período específico
     */
    public Map<Usuario, Integer> generarReportePeriodo(LocalDateTime desde, LocalDateTime hasta) {
        List<Prestamo> prestamos = sistemaPrestamos.listarTodosPrestamos()
                .stream()
                .filter(p -> p.getFechaPrestamo().isAfter(desde) && p.getFechaPrestamo().isBefore(hasta))
                .collect(Collectors.toList());

        Map<Usuario, Integer> conteoUsuarios = new HashMap<>();

        for (Prestamo prestamo : prestamos) {
            Usuario usuario = prestamo.getUsuario();
            conteoUsuarios.put(usuario, conteoUsuarios.getOrDefault(usuario, 0) + 1);
        }

        return conteoUsuarios;
    }

    /**
     * Genera un mapa con la puntuación de cada usuario basada en diversos factores
     */
    public Map<Usuario, Double> generarReportePuntuacion() {
        List<Prestamo> prestamos = sistemaPrestamos.listarTodosPrestamos();
        Map<Usuario, Double> puntuacionUsuarios = new HashMap<>();
        Map<Usuario, Integer> devolucionesATiempo = new HashMap<>();
        Map<Usuario, Integer> devolucionesConRetraso = new HashMap<>();
        Map<Usuario, Integer> totalPrestamos = new HashMap<>();

        for (Prestamo prestamo : prestamos) {
            Usuario usuario = prestamo.getUsuario();

            // Contar préstamos totales
            totalPrestamos.put(usuario, totalPrestamos.getOrDefault(usuario, 0) + 1);

            // Solo contar devoluciones para préstamos ya devueltos
            if (prestamo.getFechaDevolucion() != null) {
                if (prestamo.getFechaDevolucion().isBefore(prestamo.getFechaVencimiento())) {
                    // Devolución a tiempo
                    devolucionesATiempo.put(usuario, devolucionesATiempo.getOrDefault(usuario, 0) + 1);
                } else {
                    // Devolución con retraso
                    devolucionesConRetraso.put(usuario, devolucionesConRetraso.getOrDefault(usuario, 0) + 1);
                }
            }
        }

        // Calcular puntuación para cada usuario
        for (Usuario usuario : totalPrestamos.keySet()) {
            int total = totalPrestamos.getOrDefault(usuario, 0);
            int devTiempo = devolucionesATiempo.getOrDefault(usuario, 0);
            int devRetraso = devolucionesConRetraso.getOrDefault(usuario, 0);

            // Fórmula de puntuación: (préstamos totales + devoluciones a tiempo -
            // devoluciones con retraso)
            double puntuacion = total + devTiempo - (devRetraso * 1.5);
            puntuacionUsuarios.put(usuario, Math.max(0, puntuacion)); // Mínimo 0 puntos
        }

        return puntuacionUsuarios;
    }

    /**
     * Muestra el reporte de usuarios más activos
     */
    public void mostrarReporte(int limite) {
        CompletableFuture.supplyAsync(() -> {
            System.out.println("\n======= REPORTE DE USUARIOS MÁS ACTIVOS =======");
            System.out.println("------------------------------------------------");

            List<String> usuariosMasActivos = sistemaPrestamos.obtenerUsuariosMasActivos(limite);

            if (usuariosMasActivos.isEmpty()) {
                System.out.println("No hay datos suficientes para generar el reporte.");
                return false;
            }

            System.out.println("Ranking | ID Usuario | Nombre");
            System.out.println("------------------------------------------------");

            int ranking = 1;
            for (String idUsuario : usuariosMasActivos) {
                String nombre = "N/A";

                // Si tenemos acceso al gestor de usuarios, obtenemos el nombre
                if (gestorUsuarios != null) {
                    try {
                        Usuario usuario = gestorUsuarios.buscarPorId(idUsuario);
                        nombre = usuario.getNombre();
                    } catch (UsuarioNoEncontradoException e) {
                        // El usuario no existe, mantenemos el nombre como N/A
                    }
                }

                System.out.printf("%-7d | %-10s | %s%n", ranking++, idUsuario, nombre);
            }

            System.out.println("------------------------------------------------");
            return true;
        }, executor);
    }

    /**
     * Muestra el reporte de puntuación de usuarios
     */
    public void mostrarReportePuntuacion(int limite) {
        // Solo mostrar si tenemos acceso al gestor de usuarios
        if (gestorUsuarios == null) {
            System.out.println("No se puede generar el reporte de puntuación sin acceso al gestor de usuarios.");
            return;
        }

        CompletableFuture.supplyAsync(() -> {
            System.out.println("\n======= REPORTE DE PUNTUACIÓN DE USUARIOS =======");
            System.out.println("--------------------------------------------------");

            List<Usuario> usuariosPorPuntuacion = gestorUsuarios.obtenerPorPuntuacion();

            if (usuariosPorPuntuacion.isEmpty()) {
                System.out.println("No hay datos suficientes para generar el reporte.");
                return false;
            }

            System.out.println("Ranking | ID Usuario | Nombre             | Puntos");
            System.out.println("--------------------------------------------------");

            int ranking = 1;
            int count = 0;
            for (Usuario usuario : usuariosPorPuntuacion) {
                if (count++ >= limite)
                    break;

                System.out.printf("%-7d | %-10s | %-18s | %d%n",
                        ranking++, usuario.getId(), usuario.getNombre(), usuario.getPuntuacion());
            }

            System.out.println("--------------------------------------------------");
            return true;
        }, executor);
    }

    /**
     * Cierra los recursos del reporte
     */
    public void cerrar() {
        executor.shutdown();
    }
}