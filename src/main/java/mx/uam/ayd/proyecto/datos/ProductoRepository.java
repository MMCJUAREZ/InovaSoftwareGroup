package mx.uam.ayd.proyecto.datos;

import mx.uam.ayd.proyecto.negocio.modelo.*;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends CrudRepository <Producto, Long> {

    public Producto findByNombre(String nombre);

    Optional<Producto> findByNombreAndTipoProductoAndMarcaProducto(String nombre, TipoProducto tipoProducto, MarcaProducto marcaProducto);

    List<Producto> findByTipoProducto(TipoProducto tipoProducto);

    public Producto findByIdProducto(Long idProducto);

    //Se agrega esta parte para la implementacion con el umbral
    List<Producto> findByCantidadStockGreaterThan(int cantidad);

    //Nos permitira filtrar los productos que nos interesan cou un usoVeterinario y unidad especifico.
    List<Producto> findByUsoVeterinarioAndUnidadProducto(UsoVeterinario usoVeterinario, UnidadProducto unidadProducto);

    //Nos permitira filtrar los productos que nos interesan cou un usoVeterinario
    List<Producto> findByUsoVeterinario(UsoVeterinario usoVeterinario);
}
