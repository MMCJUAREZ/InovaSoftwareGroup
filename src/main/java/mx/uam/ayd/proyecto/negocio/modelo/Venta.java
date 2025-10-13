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
import jakarta.persistence.OneToMany;

/**
 * Entidad de negocio Grupo
 *
 * @author humbertocervantes
 *
 */
@Entity
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVenta;
    private LocalDate fecha;
    private double montoTotal;

    @OneToMany(mappedBy = "venta", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<DetalleVenta> detallesVenta = new ArrayList<>();

    public Long getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Long idVenta) {
        this.idVenta = idVenta;
    }

    public List<DetalleVenta> getDetalleVentas() {
        return detallesVenta;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(double montoTotal) {
        this.montoTotal = montoTotal;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {this.fecha = fecha;}

    public boolean addDetalleVenta(DetalleVenta detalleVenta) {
        if (detalleVenta == null) {
            throw new IllegalArgumentException("El detalleVenta no puede ser null");
        }

        if (detallesVenta.contains(detalleVenta)) {
            return false;
        }

        return detallesVenta.add(detalleVenta);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Venta other = (Venta) obj;
        return java.util.Objects.equals(idVenta, other.idVenta);
    }

    @Override
    public int hashCode() {
        return (int) (31 * idVenta);
    }

    @Override
    public String toString() {
        return "Venta [idVenta=" + idVenta + ", fecha=" + fecha + ", montoTotal=" + montoTotal + "]";
    }
}