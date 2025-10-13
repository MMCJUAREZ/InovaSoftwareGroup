package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import mx.uam.ayd.proyecto.datos.*;
import mx.uam.ayd.proyecto.negocio.modelo.*;

class ServicioVentaTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private DetalleVentaRepository detalleVentaRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ServicioVenta servicioVenta;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearVenta_debeCrearVentaNueva() {
        Venta venta = servicioVenta.crearVenta();
        assertNotNull(venta);
        assertNull(venta.getIdVenta());
        assertEquals(0, venta.getDetalleVentas().size());
    }

    @Test
    void actualizarStock_productoNoDebeSerNull(){
        Producto producto = null;
        int cantidadVendida = 10;

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioVenta.actualizarStock(producto, cantidadVendida);
        });
        assertEquals("Producto no puede ser nulo", ex.getMessage());
    }

    @Test
    void actualizarStock_cantidadVendidaNoDebeSerMayorACantidadStock(){
        Producto producto = new Producto();
        producto.setCantidadStock(5);
        producto.setIdProducto(1L);
        int cantidadVendida = 10;

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            servicioVenta.actualizarStock(producto, cantidadVendida);
        });
        assertEquals("La cantidad vendida no puede ser mayor al stock", ex.getMessage());
    }

    @Test
    void actualizarStock_cantidadNoDeberSerMenorA1(){
        Producto producto = new Producto();

        int cantidadVendida = -1;

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioVenta.actualizarStock(producto, cantidadVendida);
        });
        assertEquals("La cantidad vendida no puede ser menor a 0", ex.getMessage());
    }

    @Test
    void actualizarStock_debeGuardarProducto() {
        Producto producto = new Producto();
        int cantidadVendida = 5;
        producto.setNombre("Producto Test");
        producto.setCantidadStock(10);

        servicioVenta.actualizarStock(producto, cantidadVendida);

        verify(productoRepository, times(1)).save(producto);
    }

    @Test
    void guardarVenta_ventaNoDebeSerNull() {
        Venta venta = null;
        double montoTotal = 100.0;

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioVenta.guardarVenta(venta, montoTotal);
        });
        assertEquals("La venta no puede ser nulo", ex.getMessage());
    }

    @Test
    void guardarVenta_montoTotalNoDeberSerMenorA1() {
        Venta venta = new Venta();
        double montoTotal = -1;

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioVenta.guardarVenta(venta, montoTotal);
        });
        assertEquals("El monto total no puede ser menor o igual a 0", ex.getMessage());
    }

    @Test
    void guardarVenta_debeGuardarVenta() {
        Venta venta = new Venta();
        double montoTotal = 100.0;

        servicioVenta.guardarVenta(venta, montoTotal);

        verify(ventaRepository, times(1)).save(venta);
    }

    @Test
    void agregarDetallesVenta_detallesVentaNoDebeEstarVacio(){
        List<DetalleVenta> lista = new ArrayList<>();

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            servicioVenta.agregarDetallesVenta(lista);
        });
        assertEquals("La lista no puede estar vacia", ex.getMessage());
    }

    @Test
    void agregarDetallesVenta_noDebeHaberProductosRepetidos(){
        DetalleVenta detalle1 = new DetalleVenta();
        DetalleVenta detalle2 = new DetalleVenta();
        List<DetalleVenta> lista = new ArrayList<>();
        lista.add(detalle1);
        lista.add(detalle2);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            servicioVenta.agregarDetallesVenta(lista);
        });
        assertEquals("La lista de detalles contiene productos duplicados", ex.getMessage());
    }

    @Test
    void agregarDetallesVenta_noDebeGuardarDetallesVenta(){
        DetalleVenta detalle1 = new DetalleVenta();
        DetalleVenta detalle2 = new DetalleVenta();
        List<DetalleVenta> lista = new ArrayList<>();
        Producto producto1 = new Producto();

        producto1.setIdProducto(1L);
        producto1.setNombre("Producto Test");
        producto1.setCantidadStock(5);

        Producto producto2 = producto1;
        detalle1.setProducto(producto1);
        detalle2.setProducto(producto2);

        lista.add(detalle1);
        lista.add(detalle2);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            servicioVenta.agregarDetallesVenta(lista);
        });
        assertEquals("La lista de detalles contiene productos duplicados", ex.getMessage());
    }
}