package mx.uam.ayd.proyecto.datos;
import mx.uam.ayd.proyecto.negocio.modelo.TipoProducto;
import mx.uam.ayd.proyecto.negocio.modelo.Venta;
import mx.uam.ayd.proyecto.negocio.modelo.ReporteVentaDTO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface VentaRepository extends CrudRepository<Venta, Long> {

    List<Venta> findByFecha(LocalDate fecha);

    @Query("SELECT new mx.uam.ayd.proyecto.negocio.modelo.ReporteVentaDTO(" +
            "v.fecha, p.nombre, p.tipoProducto, SUM(d.cantidadVendida), SUM(d.cantidadVendida * p.precio)) " +
            "FROM Venta v JOIN v.detallesVenta d JOIN d.producto p " +
            "WHERE (:desde IS NULL OR v.fecha >= :desde) " +
            "AND (:hasta IS NULL OR v.fecha <= :hasta) " +
            "AND (:tipoProducto IS NULL OR p.tipoProducto = :tipoProducto) " +
            "GROUP BY v.fecha, p.nombre, p.tipoProducto " +
            "ORDER BY v.fecha, p.nombre")
    List<ReporteVentaDTO> obtenerReporteVentasDiario(
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta,
            @Param("tipoProducto") TipoProducto tipoProducto);

    @Query("SELECT new mx.uam.ayd.proyecto.negocio.modelo.ReporteVentaDTO(" +
            "FUNCTION('YEAR', v.fecha), " +            // año
            "FUNCTION('MONTH', v.fecha), " +           // mes
            "p.nombre, " +
            "p.tipoProducto, " +
            "SUM(d.cantidadVendida), " +
            "SUM(d.cantidadVendida * p.precio)) " +
            "FROM Venta v JOIN v.detallesVenta d JOIN d.producto p " +
            "WHERE (:desde IS NULL OR v.fecha >= :desde) " +
            "AND (:hasta IS NULL OR v.fecha <= :hasta) " +
            "AND (:tipoProducto IS NULL OR p.tipoProducto = :tipoProducto) " +
            "GROUP BY FUNCTION('YEAR', v.fecha), FUNCTION('MONTH', v.fecha), p.nombre, p.tipoProducto " +
            "ORDER BY FUNCTION('YEAR', v.fecha), FUNCTION('MONTH', v.fecha), p.nombre")
    List<ReporteVentaDTO> obtenerReporteVentasMensual(
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta,
            @Param("tipoProducto") TipoProducto tipoProducto);
}