package mx.uam.ayd.proyecto.negocio;

import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServicioInventario {

    private final ProductoRepository productoRepository;

    @Autowired
    public ServicioInventario(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }
    public List<Producto> recuperaProducto() {
        List <Producto> productos = new ArrayList<>();

        for(Producto producto:productoRepository.findAll()) {
            productos.add(producto);
        }

        return productos;
    }
    public void eliminaProducto(Long idproducto) {

        // Validar que no sean nulos o vacíos
        if (idproducto == null || idproducto <=0) {
            throw new IllegalArgumentException("El Id del producto no puede ser nulo o negativo");
        }

        // Buscar al Producto
        Producto producto = productoRepository.findById(idproducto).orElse(null);

        if (producto == null) {
            throw new IllegalArgumentException("No se encontró el producto");
        }

        // Eliminar al prodcuto de la base de datos
        productoRepository.delete(producto);
    }


}
