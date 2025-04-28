package app.biblioteca.reports;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import app.biblioteca.interfaces.RecursoDigital;
import app.biblioteca.models.CategoriaRecurso;
import app.biblioteca.models.Usuario;

/**
 * Clase utilitaria para mostrar reportes en la consola de forma visualmente
 * atractiva
 */
public class ReporteVisualizer {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final int ANCHO_TABLA = 80;

    /**
     * Imprime un encabezado de sección para el reporte
     * 
     * @param titulo Título de la sección
     */
    public static void imprimirEncabezado(String titulo) {
        System.out.println();
        imprimirLinea('=');
        System.out.println(centrarTexto(titulo, ANCHO_TABLA));
        imprimirLinea('=');
    }

    /**
     * Imprime un título de subsección
     * 
     * @param subtitulo Subtítulo
     */
    public static void imprimirSubtitulo(String subtitulo) {
        System.out.println();
        System.out.println(subtitulo);
        imprimirLinea('-');
    }

    /**
     * Imprime una línea separadora
     * 
     * @param caracter Carácter a utilizar para la línea
     */
    public static void imprimirLinea(char caracter) {
        System.out.println(new String(new char[ANCHO_TABLA]).replace('\0', caracter));
    }

    /**
     * Imprime el reporte de recursos más prestados
     * 
     * @param recursosConPrestamos Mapa de recursos y cantidad de préstamos
     */
    public static void imprimirReporteRecursos(Map<RecursoDigital, Integer> recursosConPrestamos) {
        if (recursosConPrestamos.isEmpty()) {
            System.out.println("No hay datos para mostrar en este reporte.");
            return;
        }

        // Encabezado de tabla
        System.out.printf("%-4s | %-40s | %-15s | %-10s\n",
                "POS", "TÍTULO", "CATEGORÍA", "PRÉSTAMOS");
        imprimirLinea('-');

        // Filas de datos
        int posicion = 1;
        for (Map.Entry<RecursoDigital, Integer> entry : recursosConPrestamos.entrySet()) {
            RecursoDigital recurso = entry.getKey();
            int prestamos = entry.getValue();

            // Recortar título si es muy largo
            String titulo = recurso.getTitulo();
            if (titulo.length() > 37) {
                titulo = titulo.substring(0, 37) + "...";
            }

            System.out.printf("%-4d | %-40s | %-15s | %-10d\n",
                    posicion,
                    titulo,
                    recurso.getCategoria().toString(),
                    prestamos);

            posicion++;
        }
    }

    /**
     * Imprime el reporte de usuarios más activos
     * 
     * @param usuariosConActividad Mapa de usuarios y su nivel de actividad
     */
    public static void imprimirReporteUsuarios(Map<Usuario, Integer> usuariosConActividad) {
        if (usuariosConActividad.isEmpty()) {
            System.out.println("No hay datos para mostrar en este reporte.");
            return;
        }

        // Encabezado de tabla
        System.out.printf("%-4s | %-30s | %-25s | %-10s\n",
                "POS", "NOMBRE", "EMAIL", "ACTIVIDAD");
        imprimirLinea('-');

        // Filas de datos
        int posicion = 1;
        for (Map.Entry<Usuario, Integer> entry : usuariosConActividad.entrySet()) {
            Usuario usuario = entry.getKey();
            int actividad = entry.getValue();

            // Recortar nombre si es muy largo
            String nombre = usuario.getNombre();
            if (nombre.length() > 27) {
                nombre = nombre.substring(0, 27) + "...";
            }

            // Recortar email si es muy largo
            String email = usuario.getEmail();
            if (email.length() > 22) {
                email = email.substring(0, 22) + "...";
            }

            System.out.printf("%-4d | %-30s | %-25s | %-10d\n",
                    posicion,
                    nombre,
                    email,
                    actividad);

            posicion++;
        }
    }

    /**
     * Imprime estadísticas por categoría
     * 
     * @param estadisticasPorCategoria Mapa de categorías y sus estadísticas
     */
    public static void imprimirEstadisticasPorCategoria(Map<CategoriaRecurso, Integer> estadisticasPorCategoria) {
        if (estadisticasPorCategoria.isEmpty()) {
            System.out.println("No hay datos para mostrar en este reporte.");
            return;
        }

        // Encabezado de tabla
        System.out.printf("%-25s | %-10s | %-30s\n",
                "CATEGORÍA", "PRÉSTAMOS", "GRÁFICO");
        imprimirLinea('-');

        // Obtener el valor máximo para escalar el gráfico
        int maxValor = estadisticasPorCategoria.values().stream()
                .max(Integer::compare)
                .orElse(0);

        // Filas de datos
        for (Map.Entry<CategoriaRecurso, Integer> entry : estadisticasPorCategoria.entrySet()) {
            CategoriaRecurso categoria = entry.getKey();
            int prestamos = entry.getValue();

            // Crear gráfico de barras simple
            int longitudBarra = maxValor > 0 ? (prestamos * 30) / maxValor : 0;
            String barra = new String(new char[longitudBarra]).replace('\0', '█');

            System.out.printf("%-25s | %-10d | %s\n",
                    categoria.toString(),
                    prestamos,
                    barra);
        }
    }

    /**
     * Imprime un mensaje de fecha y hora del reporte
     */
    public static void imprimirFechaGeneracion() {
        System.out.println();
        System.out.println("Reporte generado el: " + LocalDateTime.now().format(FORMATO_FECHA));
        System.out.println();
    }

    /**
     * Centra un texto en un ancho específico
     * 
     * @param texto Texto a centrar
     * @param ancho Ancho total
     * @return Texto centrado
     */
    private static String centrarTexto(String texto, int ancho) {
        int espaciosIzquierda = (ancho - texto.length()) / 2;
        int espaciosDerecha = ancho - texto.length() - espaciosIzquierda;

        return " ".repeat(espaciosIzquierda) + texto + " ".repeat(espaciosDerecha);
    }
}