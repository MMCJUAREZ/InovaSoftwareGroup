package mx.uam.ayd.proyecto.negocio.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.OneToMany;

/**
 * @brief Representa un producto dentro del sistema de la veterinaria.
 *
 * Esta clase modela la entidad Producto en la base de datos. Incluye información
 * sobre su tipo, marca, unidad de medida, precio, cantidad en inventario, fecha
 * de caducidad y su relación con otras entidades como {@link Umbral} y {@link DetalleVenta}.
 *
 * Cada producto puede tener un umbral de stock (para control de inventario)
 * y estar asociado a múltiples detalles de venta.
 */
@Entity
public class Producto {

    /** @brief Identificador único del producto. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProducto;

    /** @brief Nombre del producto. */
    private String nombre;

    /** @brief Tipo del producto (por ejemplo: medicamento, alimento, accesorio, etc.). */
    @Enumerated(EnumType.STRING)
    private TipoProducto tipoProducto;

    /** @brief Unidad de medida del producto (por ejemplo: pieza, litro, kilogramo). */
    @Enumerated(EnumType.STRING)
    private UnidadProducto unidadProducto;

    /** @brief Marca del producto. */
    @Enumerated(EnumType.STRING)
    private MarcaProducto marcaProducto;

    /** @brief Uso veterinario del producto, aplicable solo a medicamentos. */
    @Enumerated(EnumType.STRING)
    private UsoVeterinario usoVeterinario;

    /** @brief Precio unitario del producto. */
    private double precio;

    /** @brief Cantidad disponible en el inventario. */
    private int cantidadStock;

    /** @brief Fecha de caducidad del producto (si aplica). */
    private LocalDate fechaCaducidad;

    /**
     * @brief Relación uno a uno con la entidad {@link Umbral}.
     *
     * Indica el nivel mínimo de stock que debe mantenerse para este producto.
     * Se elimina automáticamente cuando se elimina el producto.
     */
    @OneToOne(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Umbral umbral;

    /**
     * @brief Relación uno a muchos con la entidad {@link DetalleVenta}.
     *
     * Cada producto puede aparecer en varios detalles de venta.
     */
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detallesVenta = new ArrayList<>();

    /**
     * @brief Obtiene el umbral asociado al producto.
     * @return Instancia de {@link Umbral} asociada al producto.
     */
    public Umbral getUmbral() {
        return umbral;
    }

    /**
     * @brief Asigna un umbral al producto.
     * @param umbral Objeto {@link Umbral} que se asociará al producto.
     */
    public void setUmbral(Umbral umbral) {
        this.umbral = umbral;
        if (umbral != null) {
            umbral.setProducto(this);
        }
    }

    /**
     * @brief Obtiene el identificador único del producto.
     * @return Identificador del producto.
     */
    public Long getIdProducto() {
        return idProducto;
    }

    /**
     * @brief Asigna el identificador del producto.
     * @param idProducto Identificador del producto.
     */
    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    /**
     * @brief Obtiene el nombre del producto.
     * @return Nombre del producto.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @brief Asigna el nombre del producto.
     * @param nombre Nombre a establecer.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @brief Obtiene el tipo de producto.
     * @return Tipo de producto.
     */
    public TipoProducto getTipoProducto() {
        return tipoProducto;
    }

    /**
     * @brief Asigna el tipo de producto.
     * @param tipoProducto Tipo de producto.
     */
    public void setTipoProducto(TipoProducto tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    /**
     * @brief Obtiene la unidad de medida del producto.
     * @return Unidad de medida.
     */
    public UnidadProducto getUnidadProducto() {
        return unidadProducto;
    }

    /**
     * @brief Asigna la unidad de medida del producto.
     * @param unidadProducto Unidad de medida.
     */
    public void setUnidadProducto(UnidadProducto unidadProducto) {
        this.unidadProducto = unidadProducto;
    }

    /**
     * @brief Obtiene la marca del producto.
     * @return Marca del producto.
     */
    public MarcaProducto getMarcaProducto() {
        return marcaProducto;
    }

    /**
     * @brief Asigna la marca del producto.
     * @param marcaProducto Marca a establecer.
     */
    public void setMarcaProducto(MarcaProducto marcaProducto) {
        this.marcaProducto = marcaProducto;
    }

    /**
     * @brief Obtiene el uso veterinario del producto.
     * @return Uso veterinario (solo aplica a medicamentos).
     */
    public UsoVeterinario getUsoVeterinario() {
        return usoVeterinario;
    }

    /**
     * @brief Asigna el uso veterinario del producto.
     * @param usoVeterinario Uso veterinario.
     */
    public void setUsoVeterinario(UsoVeterinario usoVeterinario) {
        this.usoVeterinario = usoVeterinario;
    }

    /**
     * @brief Obtiene el precio unitario del producto.
     * @return Precio del producto.
     */
    public double getPrecio() {
        return precio;
    }

    /**
     * @brief Asigna el precio unitario del producto.
     * @param precio Precio del producto.
     */
    public void setPrecio(double precio) {
        this.precio = precio;
    }

    /**
     * @brief Obtiene la cantidad en inventario.
     * @return Cantidad de stock.
     */
    public int getCantidadStock() {
        return cantidadStock;
    }

    /**
     * @brief Asigna la cantidad en inventario.
     * @param cantidadStock Cantidad disponible.
     */
    public void setCantidadStock(int cantidadStock) {
        this.cantidadStock = cantidadStock;
    }

    /**
     * @brief Obtiene la fecha de caducidad del producto.
     * @return Fecha de caducidad.
     */
    public LocalDate getFechaCaducidad() {
        return fechaCaducidad;
    }

    /**
     * @brief Asigna la fecha de caducidad del producto.
     * @param fechaCaducidad Fecha de caducidad.
     */
    public void setFechaCaducidad(LocalDate fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    /**
     * @brief Obtiene la lista de detalles de venta asociados al producto.
     * @return Lista de {@link DetalleVenta}.
     */
    public List<DetalleVenta> getDetallesVenta() {
        return detallesVenta;
    }

    /**
     * @brief Asigna la lista de detalles de venta del producto.
     * @param detallesVenta Lista de {@link DetalleVenta}.
     */
    public void setDetallesVenta(List<DetalleVenta> detallesVenta) {
        this.detallesVenta = detallesVenta;
    }

    // ====================== MÉTODOS ADICIONALES ======================

    /**
     * @brief Agrega un umbral al producto si aún no tiene uno asignado.
     * @param umbral Umbral a agregar.
     * @return true si se asignó correctamente, false si ya existe uno.
     * @throws IllegalArgumentException Si el parámetro es nulo.
     */
    public boolean addUmbral(Umbral umbral) {
        if (umbral == null) {
            throw new IllegalArgumentException("El umbral no puede ser null");
        }
        if (this.umbral != null) {
            return false; ///< Ya hay un umbral asignado.
        }
        setUmbral(umbral);
        return true;
    }

    /**
     * @brief Compara dos productos por su identificador.
     * @param obj Objeto a comparar.
     * @return true si ambos productos tienen el mismo ID, false en caso contrario.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Producto other = (Producto) obj;
        return idProducto != null && idProducto.equals(other.idProducto);
    }

    /**
     * @brief Genera un código hash basado en el ID del producto.
     * @return Valor hash del producto.
     */
    @Override
    public int hashCode() {
        return idProducto != null ? idProducto.hashCode() : 0;
    }

    /**
     * @brief Devuelve una representación en texto del producto.
     * @return Cadena con la información básica del producto.
     */
    @Override
    public String toString() {
        if(tipoProducto.equals(TipoProducto .Medicamento)){
            return "nombre: " + nombre + ", Presentacion: " + unidadProducto + ", marca: " + marcaProducto;
        }
        return "Producto [idProducto=" + idProducto + ", nombre=" + nombre + ", marca=" + marcaProducto + ", precio=" + precio + ", Stock=" + cantidadStock + "]";
    }
}
