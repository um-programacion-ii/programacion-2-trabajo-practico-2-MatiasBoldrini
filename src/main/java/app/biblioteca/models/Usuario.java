package app.biblioteca.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa a un usuario de la biblioteca
 */
public class Usuario {
    private String id;
    private String nombre;
    private String email;
    private List<Prestamo> historialPrestamos;
    private int puntuacion;

    /**
     * Constructor para crear un usuario
     * 
     * @param id     Identificador único del usuario
     * @param nombre Nombre completo del usuario
     * @param email  Correo electrónico del usuario
     */
    public Usuario(String id, String nombre, String email) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.historialPrestamos = new ArrayList<>();
        this.puntuacion = 0;
    }

    /**
     * Obtiene el ID del usuario
     * 
     * @return Identificador único del usuario
     */
    public String getId() {
        return id;
    }

    /**
     * Obtiene el nombre del usuario
     * 
     * @return Nombre completo del usuario
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene el email del usuario
     * 
     * @return Correo electrónico del usuario
     */
    public String getEmail() {
        return email;
    }

    /**
     * Obtiene el historial de préstamos del usuario
     * 
     * @return Lista de préstamos realizados por el usuario
     */
    public List<Prestamo> getHistorialPrestamos() {
        return historialPrestamos;
    }

    /**
     * Agrega un préstamo al historial del usuario
     * 
     * @param prestamo Préstamo a agregar al historial
     */
    public void agregarPrestamo(Prestamo prestamo) {
        this.historialPrestamos.add(prestamo);
        this.puntuacion += 10; // Incrementamos puntuación por cada préstamo
    }

    /**
     * Obtiene la puntuación del usuario
     * 
     * @return Puntuación actual del usuario
     */
    public int getPuntuacion() {
        return puntuacion;
    }

    /**
     * Incrementa la puntuación del usuario
     * 
     * @param puntos Puntos a sumar a la puntuación actual
     */
    public void incrementarPuntuacion(int puntos) {
        this.puntuacion += puntos;
    }

    /**
     * Verifica si el usuario tiene préstamos activos
     * 
     * @return true si el usuario tiene al menos un préstamo activo
     */
    public boolean tienePrestamosActivos() {
        for (Prestamo prestamo : historialPrestamos) {
            if (prestamo.isActivo()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Devuelve el número de préstamos activos del usuario
     * 
     * @return cantidad de préstamos activos
     */
    public int numeroPrestamosActivos() {
        int contador = 0;
        for (Prestamo prestamo : historialPrestamos) {
            if (prestamo.isActivo()) {
                contador++;
            }
        }
        return contador;
    }

    /**
     * Devuelve el total de préstamos realizados por el usuario
     * 
     * @return total de préstamos históricos
     */
    public int totalPrestamos() {
        return historialPrestamos.size();
    }

    @Override
    public String toString() {
        return "Usuario [id=" + id + ", nombre=" + nombre + ", email=" + email + ", préstamos activos=" +
                numeroPrestamosActivos() + ", puntuación=" + puntuacion + "]";
    }
}