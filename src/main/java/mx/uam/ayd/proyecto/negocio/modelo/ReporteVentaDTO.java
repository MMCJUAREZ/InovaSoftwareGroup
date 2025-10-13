package mx.uam.ayd.proyecto.negocio.modelo;

import java.time.LocalDate;
import mx.uam.ayd.proyecto.negocio.modelo.TipoProducto;
public class ReporteVentaDTO {

    private LocalDate fecha;
    private String nombreProducto;
    private TipoProducto tipoProducto;
    private Long cantidadVendida;
    private Double totalVenta;
    private String fechaFormateada;

    // Para consulta diaria
    public ReporteVentaDTO(LocalDate fecha, String nombreProducto,
                           TipoProducto tipoProducto,
                           Long cantidadVendida, Double totalVenta) {
        this.fecha = fecha;
        this.nombreProducto = nombreProducto;
        this.tipoProducto = tipoProducto;
        this.cantidadVendida = cantidadVendida;
        this.totalVenta = totalVenta;
    }

    // Para consulta mensual
    public ReporteVentaDTO(Integer year, Integer month, String nombreProducto,
                           TipoProducto tipoProducto, Long cantidadVendida, Double totalVenta) {
        if (year != null && month != null) {
            this.fecha = LocalDate.of(year, month, 1);  // día 1 del mes
        }
        this.nombreProducto = nombreProducto;
        this.tipoProducto = tipoProducto;
        this.cantidadVendida = cantidadVendida;
        this.totalVenta = totalVenta;
    }

    // Getters y Setters
    public String getFechaFormateada() {
        return fechaFormateada;
    }

    public void setFechaFormateada(String fechaFormateada) {
        this.fechaFormateada = fechaFormateada;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public TipoProducto getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(TipoProducto tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public Long getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(Long cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    public Double getTotalVenta() {
        return totalVenta;
    }

    public void setTotalVenta(Double totalVenta) {
        this.totalVenta = totalVenta;
    }

    @Override
    public String toString() {
        return "ReporteVentaDTO{" +
                "fecha=" + fecha +
                ", nombreProducto='" + nombreProducto + '\'' +
                ", cantidadVendida=" + cantidadVendida +
                ", totalVenta=" + totalVenta +
                '}';
    }
}