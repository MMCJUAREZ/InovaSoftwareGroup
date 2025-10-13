package mx.uam.ayd.proyecto.datos;

import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import mx.uam.ayd.proyecto.negocio.modelo.Umbral;

import java.util.List;

public interface UmbralRepository extends CrudRepository<Umbral, Long> {
    Umbral findByProductoIdProducto(Long idProducto);

    @Query("SELECT u FROM Umbral u WHERE u.producto.cantidadStock > 1")
    List<Umbral> findUmbralsConStockPositivo();

    Umbral findByIdUmbral(Long idUmbral);
}