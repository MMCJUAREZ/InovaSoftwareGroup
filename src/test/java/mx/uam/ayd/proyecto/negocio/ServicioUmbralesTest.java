package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.datos.UmbralRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import mx.uam.ayd.proyecto.negocio.modelo.Umbral;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @class ServicioUmbralesTest
 * @brief Pruebas unitarias para la clase ServicioUmbrales.
 *
 * Esta clase valida el correcto funcionamiento de los métodos
 * de ServicioUmbrales utilizando mocks para simular las dependencias
 * UmbralRepository y ProductoRepository.
 *
 */
@ExtendWith(MockitoExtension.class)
class ServicioUmbralesTest {

    @Mock
    private UmbralRepository umbralRepository; /**< Mock para acceso a datos de umbrales. */

    @Mock
    private ProductoRepository productoRepository; /**< Mock para acceso a datos de productos. */

    @InjectMocks
    private ServicioUmbrales servicioUmbrales; /**< Instancia de servicio bajo prueba. */

    /**
     * @test Verifica que se recuperen productos con stock mayor a cero.
     */
    @Test
    public void testRecuperaConStockNoCero() {
        Producto p1 = new Producto();
        p1.setNombre("Producto A");
        Producto p2 = new Producto();
        p2.setNombre("Producto B");

        when(productoRepository.findByCantidadStockGreaterThan(0))
                .thenReturn(Arrays.asList(p1, p2));

        List<Producto> resultado = servicioUmbrales.recuperaConStockNoCero();
        assertEquals(2, resultado.size());
        verify(productoRepository).findByCantidadStockGreaterThan(0);
    }

    /**
     * @test Verifica que se obtenga un umbral a partir del ID del producto.
     */
    @Test
    public void testFindById() {
        Umbral umbral = new Umbral();
        when(umbralRepository.findByProductoIdProducto(1L)).thenReturn(umbral);

        Umbral resultado = servicioUmbrales.findById(1L);
        assertNotNull(resultado);
        assertEquals(umbral, resultado);
    }

    /**
     * @test Verifica el guardado exitoso de un umbral cuando el producto existe.
     */
    @Test
    public void testGuardaCambios_Exitoso() {
        Producto producto = new Producto();
        producto.setIdProducto(1L);

        Umbral umbral = new Umbral();
        umbral.setProducto(producto);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(umbralRepository.save(umbral)).thenReturn(umbral);

        Umbral resultado = servicioUmbrales.guardaCambios(umbral);
        assertNotNull(resultado);
        verify(umbralRepository).save(umbral);
    }

    /**
     * @test Verifica que se lance excepción si el producto no existe al guardar cambios.
     */
    @Test
    public void testGuardaCambios_ProductoNoExiste() {
        Producto producto = new Producto();
        producto.setIdProducto(1L);

        Umbral umbral = new Umbral();
        umbral.setProducto(producto);

        when(productoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            servicioUmbrales.guardaCambios(umbral);
        });
    }

    /**
     * @test Verifica que se lance excepción si se intenta guardar cambios con null.
     */
    @Test
    public void testGuardaCambios_Nulo() {
        assertThrows(IllegalArgumentException.class, () -> {
            servicioUmbrales.guardaCambios(null);
        });
    }

    /**
     * @test Verifica el guardado exitoso de un umbral nuevo cuando el producto no tiene umbral previo.
     */
    @Test
    public void testSave_Exitoso() {
        Producto producto = new Producto();
        producto.setIdProducto(1L);

        Umbral umbral = new Umbral();
        umbral.setProducto(producto);

        when(umbralRepository.findByProductoIdProducto(1L)).thenReturn(null);
        when(umbralRepository.save(umbral)).thenReturn(umbral);

        Umbral resultado = servicioUmbrales.save(umbral);
        assertNotNull(resultado);
        verify(umbralRepository).save(umbral);
    }

    /**
     * @test Verifica que no se pueda guardar un nuevo umbral si el producto ya tiene uno asignado.
     */
    @Test
    public void testSave_ProductoYaTieneUmbral() {
        Producto producto = new Producto();
        producto.setIdProducto(1L);

        Umbral umbralExistente = new Umbral();
        umbralExistente.setProducto(producto);

        when(umbralRepository.findByProductoIdProducto(1L)).thenReturn(umbralExistente);

        Umbral nuevoUmbral = new Umbral();
        nuevoUmbral.setProducto(producto);

        assertThrows(IllegalArgumentException.class, () -> {
            servicioUmbrales.save(nuevoUmbral);
        });
    }

    /**
     * @test Verifica que se lance excepción si se intenta guardar un umbral null.
     */
    @Test
    public void testSave_Nulo() {
        assertThrows(IllegalArgumentException.class, () -> {
            servicioUmbrales.save(null);
        });
    }

    /**
     * @test Verifica que manejarEdicionUmbral cree un nuevo umbral si no existe.
     */
    @Test
    public void testManejarEdicionUmbral_CreaNuevo() {
        Producto producto = new Producto();
        producto.setIdProducto(1L);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(umbralRepository.findByProductoIdProducto(1L)).thenReturn(null);

        servicioUmbrales.manejarEdicionUmbral(1L, 10);

        verify(umbralRepository).save(any(Umbral.class));
    }

    /**
     * @test Verifica que manejarEdicionUmbral actualice un umbral existente.
     */
    @Test
    public void testManejarEdicionUmbral_ActualizaExistente() {
        Producto producto = new Producto();
        producto.setIdProducto(1L);

        Umbral umbralExistente = new Umbral();
        umbralExistente.setProducto(producto);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(umbralRepository.findByProductoIdProducto(1L)).thenReturn(umbralExistente);

        servicioUmbrales.manejarEdicionUmbral(1L, 15);

        assertEquals(15, umbralExistente.getValorMinimo());
        verify(umbralRepository).save(umbralExistente);
    }

    /**
     * @test Verifica que se lance excepción si se intenta manejar un umbral de un producto inexistente.
     */
    @Test
    public void testManejarEdicionUmbral_ProductoNoExiste() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            servicioUmbrales.manejarEdicionUmbral(99L, 10);
        });
    }

    /**
     * @test Verifica la recuperación de un producto por su ID.
     */
    @Test
    public void testRecuperarProductoPorId() {
        Producto producto = new Producto();
        producto.setIdProducto(5L);

        when(productoRepository.findById(5L)).thenReturn(Optional.of(producto));

        Optional<Producto> resultado = servicioUmbrales.recuperarProductoPorId(5L);
        assertTrue(resultado.isPresent());
        assertEquals(5L, resultado.get().getIdProducto());
    }
}
