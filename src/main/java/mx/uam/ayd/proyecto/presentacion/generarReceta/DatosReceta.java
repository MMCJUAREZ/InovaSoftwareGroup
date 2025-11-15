package mx.uam.ayd.proyecto.presentacion.generarReceta;

import mx.uam.ayd.proyecto.negocio.modelo.Producto;

/**
 * @class DatosReceta
 * @brief Modelo que representa un elemento dentro de una receta veterinaria.
 *
 * Contiene la información necesaria para generar una línea dentro de la receta:
 * nombre del producto, dosis, intervalo, duración y notas adicionales.
 */
public class DatosReceta {

    /** Nombre del producto recetado. */
    private String producto;

    /** Dosis recomendada. */
    private String dosis;

    /** Intervalo de administración. */
    private String cada;

    /** Duración del tratamiento. */
    private String hasta;

    /** Nota o instrucción adicional del tratamiento. */
    private String nota;

    /**
     * @brief Constructor vacío.
     *
     * Permite la creación de objetos sin inicializar todos los campos,
     */
    public DatosReceta() {}

    /**
     * @brief Constructor completo.
     *
     * Inicializa el objeto tomando el nombre del producto desde la entidad {@link Producto}.
     *
     * @param producto Objeto de tipo Producto del cual se extrae el nombre.
     * @param dosis Dosis recomendada.
     * @param cada Intervalo de administración.
     * @param hasta Duración o fecha límite del tratamiento.
     * @param nota Notas adicionales.
     */
    public DatosReceta(Producto producto, String dosis, String cada, String hasta, String nota) {
        this.producto = producto.getNombre();
        this.dosis = dosis;
        this.cada = cada;
        this.hasta = hasta;
        this.nota = nota;
    }

    /** @return Nombre del producto recetado. */
    public String getProducto() {
        return producto;
    }

    /** @param producto Nombre del producto recetado. */
    public void setProducto(String producto) {
        this.producto = producto;
    }

    /** @return La dosis recomendada. */
    public String getDosis() {
        return dosis;
    }

    /** @param dosis Dosis recomendada. */
    public void setDosis(String dosis) {
        this.dosis = dosis;
    }

    /** @return Intervalo de administración. */
    public String getCada() {
        return cada;
    }

    /** @param cada Intervalo de administración. */
    public void setCada(String cada) {
        this.cada = cada;
    }

    /** @return Duración del tratamiento. */
    public String getHasta() {
        return hasta;
    }

    /** @param hasta Duración del tratamiento. */
    public void setHasta(String hasta) {
        this.hasta = hasta;
    }

    /** @return Nota adicional de la receta. */
    public String getNota() {
        return nota;
    }

    /** @param nota Nota adicional de la receta. */
    public void setNota(String nota) {
        this.nota = nota;
    }
}
