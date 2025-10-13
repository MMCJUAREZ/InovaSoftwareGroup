package mx.uam.ayd.proyecto.negocio.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

/**
 * Entidad de negocio Grupo
 *
 * @author humbertocervantes
 *
 */
@Entity
public class DetalleVenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idDetalleVenta;

    private int cantidadVendida;
    private double subtotal;

    @ManyToOne
    @JoinColumn(name = "venta_id")
    private Venta venta;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    public Venta  getVenta() {
        return venta;
    }

    public void setVenta(){
        this.venta = new Venta();
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public long getIdDetalleVenta() {
        return idDetalleVenta;
    }

    public void setIdDetalleVenta(long idDetalleVenta) {
        this.idDetalleVenta = idDetalleVenta;
    }

    public int getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(int cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    public double getSubtotal() {return subtotal;}

    public void setSubtotal(double subtotal) {this.subtotal = subtotal;}

    public void setVenta(Venta venta){
        if(venta == null){
            throw new IllegalArgumentException("La venta no puede ser null");
        }
        this.venta = venta;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        DetalleVenta other = (DetalleVenta) obj;
        return java.util.Objects.equals(idDetalleVenta, other.idDetalleVenta);
    }

    @Override
    public int hashCode() {
        return (int) (31 * idDetalleVenta);
    }

    @Override
    public String toString() {
        return "DetalleVenta [idDetalleVenta=" + idDetalleVenta + ", cantidadVendida=" + cantidadVendida + ", subtotal=" + subtotal + "idVenta=" + (venta != null ? venta.getIdVenta() : "null") + "]";
    }
}