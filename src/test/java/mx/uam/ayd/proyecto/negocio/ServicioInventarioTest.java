package mx.uam.ayd.proyecto.negocio;

import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import mx.uam.ayd.proyecto.negocio.modelo.Umbral;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServicioInventarioTest {
    
    @Mock

    private ProductoRepository productoRepository;
    
    @InjectMocks

    private ServicioInventario servicioInventario;


    @Test
    void testInventario() {
        // Caso 1: No hay productos guardados, regresa lista vacía
        List<Producto> productos = servicioInventario.recuperaProducto();
        assertEquals(0, productos.size());

        // Caso 2: Hay productos guardados, regresa lista con productos
        ArrayList<Producto> lista = new ArrayList<>();

        Producto producto1 = new Producto();
        producto1.setNombre("croqueta");
        producto1.setPrecio(500);
        producto1.setCantidadStock(20);

        Producto producto2 = new Producto();
        producto2.setNombre("croqueta");
        producto2.setPrecio(500);
        producto2.setCantidadStock(20);

        lista.add(producto1);
        lista.add(producto2);

        when(productoRepository.findAll()).thenReturn(lista);

        productos = servicioInventario.recuperaProducto();
        assertEquals(2, productos.size());
    }


    @Test
   void testeliminaProducto() {
        // Crear producto
        Producto producto = new Producto();
        producto.setNombre("Leche");
        producto.setPrecio(15.0);
        producto.setCantidadStock(10);

        // Crear umbral
        Umbral umbral = new Umbral();
        umbral.setValorMinimo(2);

        // Asociar
        producto.setUmbral(umbral); // <- setea también producto en umbral por dentro

        // Guardar producto (se guarda también el umbral automáticamente)
        producto = productoRepository.save(producto);
        Long idProducto = producto.getIdProducto();
        System.out.println("Producto creado con ID: " + idProducto);
        // Verifica que exista
        Optional<Producto> existente = productoRepository.findById(idProducto);
        assert existente.isPresent();
        // Eliminar producto
        productoRepository.delete(existente.get());
        // Verificar que ya no existe
        boolean sigueExistiendo = productoRepository.existsById(idProducto);
        assert !sigueExistiendo : "El producto no fue eliminado correctamente";

        System.out.println("Producto y su umbral eliminados correctamente.");
    }
} 