package app.biblioteca.models;

/**
 * Interfaz que define los métodos comunes para todos los recursos digitales
 * Siguiendo el principio OCP (Open/Closed Principle)
 */
public interface RecursoDigital {
    /**
     * Obtiene el identificador único del recurso
     * 
     * @return Identificador único
     */
    String getIdentificador();

    /**
     * Obtiene el título del recurso
     * 
     * @return Título del recurso
     */
    String getTitulo();

    /**
     * Obtiene el autor o creador del recurso
     * 
     * @return Autor o creador
     */
    String getAutor();

    /**
     * Obtiene el tipo de recurso (ej: Libro, Revista, Audiolibro)
     * 
     * @return Tipo de recurso
     */
    String getTipo();

    /**
     * Establece la categoría del recurso
     * 
     * @param categoria La categoría a asignar
     */
    void setCategoria(CategoriaRecurso categoria);

    /**
     * Obtiene la categoría del recurso
     * 
     * @return Categoría del recurso
     */
    CategoriaRecurso getCategoria();
}