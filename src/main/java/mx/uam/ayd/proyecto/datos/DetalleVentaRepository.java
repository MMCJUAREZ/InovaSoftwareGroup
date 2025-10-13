package mx.uam.ayd.proyecto.datos;

import mx.uam.ayd.proyecto.negocio.modelo.DetalleVenta;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface DetalleVentaRepository extends CrudRepository<DetalleVenta, Long> {
    Optional<DetalleVenta> findByProducto_IdProductoAndVenta_IdVenta(Long idProducto, Long idVenta);
}