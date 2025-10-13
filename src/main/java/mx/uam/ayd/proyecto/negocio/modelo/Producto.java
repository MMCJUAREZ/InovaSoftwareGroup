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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.OneToMany;

@Entity
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProducto;

    private String nombre;

    @Enumerated(EnumType.STRING)
    private TipoProducto tipoProducto;

    @Enumerated(EnumType.STRING)
    private UnidadProducto unidadProducto;

    @Enumerated(EnumType.STRING)
    private MarcaProducto marcaProducto;

    private double precio;

    private int cantidadStock;

    private LocalDate fechaCaducidad;

    // Relación bidireccional con Umbral
    @OneToOne(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Umbral umbral;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detallesVenta = new ArrayList<>();

    // Getters y setters

    public Umbral getUmbral() {
        return umbral;
    }

    public void setUmbral(Umbral umbral) {
        this.umbral = umbral;
        if (umbral != null) {
            umbral.setProducto(this);  // Mantener sincronía bidireccional
        }
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoProducto getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(TipoProducto tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public UnidadProducto getUnidadProducto() {
        return unidadProducto;
    }

    public void setUnidadProducto(UnidadProducto unidadProducto) {
        this.unidadProducto = unidadProducto;
    }

    public MarcaProducto getMarcaProducto() {
        return marcaProducto;
    }

    public void setMarcaProducto(MarcaProducto marcaProducto) {
        this.marcaProducto = marcaProducto;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getCantidadStock() {
        return cantidadStock;
    }

    public void setCantidadStock(int cantidadStock) {
        this.cantidadStock = cantidadStock;
    }

    public LocalDate getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(LocalDate fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    public List<DetalleVenta> getDetallesVenta() {
        return detallesVenta;
    }

    public void setDetallesVenta(List<DetalleVenta> detallesVenta) {
        this.detallesVenta = detallesVenta;
    }

    // Opcional: método para agregar umbral, puede quedar o eliminarse
    public boolean addUmbral(Umbral umbral) {
        if (umbral == null) {
            throw new IllegalArgumentException("El umbral no puede ser null");
        }
        if (this.umbral != null) {
            return false; // Ya hay un umbral asignado, no se puede agregar otro
        }
        setUmbral(umbral);
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Producto other = (Producto) obj;
        return idProducto != null && idProducto.equals(other.idProducto);
    }

    @Override
    public int hashCode() {
        return idProducto != null ? idProducto.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Producto [idProducto=" + idProducto + ", nombre=" + nombre + ", marca=" + marcaProducto + ", precio="
                + precio + ", Stock=" + cantidadStock +  "]";
    }
}
