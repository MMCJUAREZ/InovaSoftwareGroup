package mx.uam.ayd.proyecto.presentacion.generarReceta;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

/**
 * @class Medicacion
 * @brief Modelo que representa un elemento dentro de una receta veterinaria.
 *
 * Contiene la información necesaria para generar una línea dentro de la receta:
 * nombre del producto, dosis, intervalo, duración y notas adicionales.
 */
public class DatosReceta {

    /** Nombre del producto recetado. */
    private Producto producto;

    /** Dosis recomendada. */
    private IntegerProperty dosis = new SimpleIntegerProperty();

    /** Intervalo de administración. */
    private IntegerProperty cada = new SimpleIntegerProperty();

    /** Duración del tratamiento. */
    private IntegerProperty hasta = new SimpleIntegerProperty();

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
    public DatosReceta(Producto producto, int dosis, int cada, int hasta, String nota) {
        this.producto = producto;
        this.dosis.set(dosis);
        this.cada.set(cada);
        this.hasta.set(hasta);
        this.nota = nota;
    }

    /** @return Nombre del producto recetado. */
    public Producto getProducto() {
        return producto;
    }

    /** @param producto Nombre del producto recetado. */
    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    /** @return Nombre del producto recetado. */
    public String getNombreProducto() {
        return producto.getNombre();
    }

    /** @return La dosis recomendada. */
    public int getDosis() {
        return dosis.get();
    }

    /** @param dosis Dosis recomendada. */
    public void setDosis(int dosis) {
        this.dosis.set(dosis);
    }

    /** @return Intervalo de administración. */
    public int getCada() {
        return cada.get();
    }

    /** @param cada Intervalo de administración. */
    public void setCada(int cada) {
        this.cada.set(cada);
    }

    /** @return Duración del tratamiento. */
    public int getHasta() {
        return hasta.get();
    }

    /** @param hasta Duración del tratamiento. */
    public void setHasta(int hasta) {
        this.hasta.set(hasta);
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
