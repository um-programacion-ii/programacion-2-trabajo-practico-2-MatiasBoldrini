package app.biblioteca.reports;

/**
 * Clase utilitaria para visualizar reportes en consola
 */
public class ReporteVisualizer {

    /**
     * Muestra un encabezado para los reportes
     * 
     * @param titulo Título del reporte
     */
    public void mostrarEncabezado(String titulo) {
        int longitud = titulo.length() + 10;

        System.out.println();
        System.out.println("=".repeat(longitud));
        System.out.println("     " + titulo);
        System.out.println("=".repeat(longitud));
    }

    /**
     * Muestra una línea separadora para los reportes
     * 
     * @param longitud Longitud de la línea
     */
    public void mostrarSeparador(int longitud) {
        System.out.println("-".repeat(longitud));
    }

    /**
     * Muestra un pie para los reportes
     * 
     * @param longitud Longitud de la línea
     */
    public void mostrarPie(int longitud) {
        System.out.println("=".repeat(longitud));
        System.out.println();
    }
}