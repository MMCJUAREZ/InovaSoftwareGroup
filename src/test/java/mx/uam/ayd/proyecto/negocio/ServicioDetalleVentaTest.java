package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import mx.uam.ayd.proyecto.datos.*;
import mx.uam.ayd.proyecto.negocio.modelo.*;

class ServicioDetalleVentaTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private DetalleVentaRepository detalleVentaRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ServicioDetalleVenta servicioDetalleVenta;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void newDetalleVenta_creaDetalleCorrectamente() {
        Producto producto = new Producto();
        producto.setCantidadStock(10);
        producto.setPrecio(30.0);
        producto.setNombre("Producto A");
        producto.setMarcaProducto(MarcaProducto.Catchow);
        producto.setTipoProducto(TipoProducto.Comida);
        producto.setUnidadProducto(UnidadProducto.Kg);

        Venta venta = new Venta();

        List<DetalleVenta> detallesVenta = new ArrayList<>();

        DetalleVenta detalle = servicioDetalleVenta.newDetalleVenta(producto, 5, venta, detallesVenta);

        assertNotNull(detalle);
        assertEquals(producto, detalle.getProducto());
        assertEquals(5, detalle.getCantidadVendida());
        assertEquals(venta, detalle.getVenta());
        assertEquals(producto.getPrecio() * 5, detalle.getSubtotal());
    }

    @Test
    void newDetalleVenta_lanzaException_siProductoDuplicado() {
        Producto producto = new Producto();
        producto.setCantidadStock(10);
        producto.setPrecio(20.0);

        Venta venta = new Venta();

        List<DetalleVenta> detallesVenta = new ArrayList<>();
        DetalleVenta detalleExistente = new DetalleVenta();
        detalleExistente.setProducto(producto);
        detallesVenta.add(detalleExistente);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            servicioDetalleVenta.newDetalleVenta(producto, 1, venta, detallesVenta);
        });
        assertEquals("El producto ya esta en la tabla", ex.getMessage());
    }

    @Test
    void newDetalleVenta_lanzaException_siProductoNulo() {
        Venta venta = new Venta();
        List<DetalleVenta> detallesVenta = new ArrayList<>();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioDetalleVenta.newDetalleVenta(null, 1, venta, detallesVenta);
        });
        assertEquals("El producto no puede ser nulo", ex.getMessage());
    }

    @Test
    void newDetalleVenta_lanzaException_siCantidadNoValida() {
        Producto producto = new Producto();
        producto.setCantidadStock(10);
        producto.setPrecio(20.0);

        Venta venta = new Venta();
        List<DetalleVenta> detallesVenta = new ArrayList<>();

        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> {
            servicioDetalleVenta.newDetalleVenta(producto, 0, venta, detallesVenta);
        });
        assertEquals("La cantidad no puede ser menor o igual a 0", ex1.getMessage());

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> {
            servicioDetalleVenta.newDetalleVenta(producto, -5, venta, detallesVenta);
        });
        assertEquals("La cantidad no puede ser menor o igual a 0", ex2.getMessage());
    }

    @Test
    void newDetalleVenta_lanzaException_siCantidadMayorQueStock() {
        Producto producto = new Producto();
        producto.setCantidadStock(3);
        producto.setPrecio(20.0);

        Venta venta = new Venta();
        List<DetalleVenta> detallesVenta = new ArrayList<>();

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            servicioDetalleVenta.newDetalleVenta(producto, 5, venta, detallesVenta);
        });
        assertEquals("La cantidad vendida no puede ser mayor al stock", ex.getMessage());
    }

    @Test
    void newDetalleVenta_lanzaException_siVentaNula() {
        Producto producto = new Producto();
        producto.setCantidadStock(5);
        producto.setPrecio(20.0);

        List<DetalleVenta> detallesVenta = new ArrayList<>();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioDetalleVenta.newDetalleVenta(producto, 1, null, detallesVenta);
        });
        assertEquals("La venta no puede ser nulo", ex.getMessage());
    }
}
