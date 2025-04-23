package app.biblioteca.models;

/**
 * Clase que representa un usuario de la biblioteca
 */
public class Usuario {
    private String id;
    private String nombre;
    private String email;
    private int prestamosActivos;

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
        this.prestamosActivos = 0;
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
     * Obtiene la cantidad de préstamos activos
     * 
     * @return Número de préstamos activos
     */
    public int getPrestamosActivos() {
        return prestamosActivos;
    }

    /**
     * Incrementa el contador de préstamos activos
     */
    public void incrementarPrestamos() {
        this.prestamosActivos++;
    }

    /**
     * Decrementa el contador de préstamos activos
     */
    public void decrementarPrestamos() {
        if (this.prestamosActivos > 0) {
            this.prestamosActivos--;
        }
    }

    @Override
    public String toString() {
        return nombre + " (" + id + ")";
    }
}