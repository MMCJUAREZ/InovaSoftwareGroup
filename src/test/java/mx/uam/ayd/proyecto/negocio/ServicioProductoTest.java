package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import mx.uam.ayd.proyecto.datos.*;
import mx.uam.ayd.proyecto.negocio.modelo.*;

public class ServicioProductoTest {

    @Mock
    private ProductoRepository productoRepository;

    private ServicioProducto servicioProducto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        servicioProducto = new ServicioProducto(productoRepository);
    }

    @Test
    public void agregarProducto_valido_guardaProducto() {
        String nombre = "Sobre de gato";
        TipoProducto tipo = TipoProducto.Comida;
        MarcaProducto marca = MarcaProducto.Whiskas;
        double precio = 12.0;
        int cantidad = 10;
        UnidadProducto unidad = UnidadProducto.Pieza;
        LocalDate fechaCaducidad = LocalDate.now().plusWeeks(2);

        when(productoRepository.findByNombreAndTipoProductoAndMarcaProducto(nombre, tipo, marca))
                .thenReturn(Optional.empty());
        when(productoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Producto resultado = servicioProducto.agregarProducto(nombre, tipo, marca, precio, cantidad, unidad, fechaCaducidad);

        assertNotNull(resultado);
        assertEquals(nombre, resultado.getNombre());
        assertEquals(tipo, resultado.getTipoProducto());
        assertEquals(marca, resultado.getMarcaProducto());
        assertEquals(precio, resultado.getPrecio());
        assertEquals(cantidad, resultado.getCantidadStock());
        assertEquals(unidad, resultado.getUnidadProducto());
        assertEquals(fechaCaducidad, resultado.getFechaCaducidad());

        verify(productoRepository).save(any());
    }

    @Test
    public void agregarProducto_productoExistente_lanzaExcepcion() {
        String nombre = "Sobre de gato";
        TipoProducto tipo = TipoProducto.Comida;
        MarcaProducto marca = MarcaProducto.Whiskas;

        when(productoRepository.findByNombreAndTipoProductoAndMarcaProducto(nombre, tipo, marca))
                .thenReturn(Optional.of(new Producto()));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            servicioProducto.agregarProducto(nombre, tipo, marca, 12, 10, UnidadProducto.Pieza, null);
        });

        assertEquals("El producto ya existe en la base de datos.", ex.getMessage());
    }

    @Test
    public void agregarProducto_nombreNulo_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioProducto.agregarProducto(null, TipoProducto.Comida, MarcaProducto.Whiskas, 12, 10, UnidadProducto.Pieza, null);
        });
        assertEquals("El nombre del producto no puede ser nulo o vacío", ex.getMessage());
    }

    @Test
    public void agregarProducto_nombreVacio_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioProducto.agregarProducto(" ", TipoProducto.Comida, MarcaProducto.Whiskas, 12, 10, UnidadProducto.Pieza, null);
        });
        assertEquals("El nombre del producto no puede ser nulo o vacío", ex.getMessage());
    }

    @Test
    public void agregarProducto_tipoProductoNulo_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioProducto.agregarProducto("Leche", null, MarcaProducto.Whiskas, 12, 10, UnidadProducto.Pieza, null);
        });
        assertEquals("El tipo de producto no puede ser nulo", ex.getMessage());
    }

    @Test
    public void agregarProducto_marcaProductoNula_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioProducto.agregarProducto("Leche", TipoProducto.Comida, null, 12, 10, UnidadProducto.Pieza, null);
        });
        assertEquals("La marca del producto no puede ser nula", ex.getMessage());
    }

    @Test
    public void agregarProducto_precioInvalido_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioProducto.agregarProducto("Leche", TipoProducto.Comida, MarcaProducto.Whiskas, 0, 10, UnidadProducto.Pieza, null);
        });
        assertEquals("El precio debe ser mayor a cero", ex.getMessage());
    }

    @Test
    public void agregarProducto_unidadProductoNula_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioProducto.agregarProducto("Leche", TipoProducto.Comida, MarcaProducto.Whiskas, 12, 10, null, null);
        });
        assertEquals("La unidad del producto no puede ser nula", ex.getMessage());
    }

    @Test
    public void agregarProducto_fechaCaducidadMuyCercana_lanzaExcepcion() {
        LocalDate fechaCaducidad = LocalDate.now().plusDays(3); // menos de una semana

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioProducto.agregarProducto("Leche", TipoProducto.Comida, MarcaProducto.Whiskas, 12, 10, UnidadProducto.Pieza, fechaCaducidad);
        });
        assertEquals("La fecha no puede ser anterior a partir de una semana", ex.getMessage());
    }

    // Pruebas para recuperaProductos

    @Test
    public void recuperaProductos_conProductos_devuelveLista() {
        Producto p1 = new Producto();
        p1.setNombre("Sobre de gato");
        Producto p2 = new Producto();
        p2.setNombre("Correa");

        when(productoRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<Producto> productos = servicioProducto.recuperaProductos();

        assertEquals(2, productos.size());
        assertTrue(productos.contains(p1));
        assertTrue(productos.contains(p2));
    }
}
