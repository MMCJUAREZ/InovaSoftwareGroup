package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import mx.uam.ayd.proyecto.negocio.modelo.UnidadProducto;
import mx.uam.ayd.proyecto.negocio.modelo.MarcaProducto;
import mx.uam.ayd.proyecto.negocio.modelo.TipoProducto;
import mx.uam.ayd.proyecto.negocio.modelo.UsoVeterinario;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ServicioProductoTest {

    @Mock
    private ProductoRepository productoRepository;
    @InjectMocks
    private ServicioProducto servicioProducto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        servicioProducto = new ServicioProducto(productoRepository);
    }

    /*@Test
    public void agregarProducto_no_medicamento_guardaProducto() {
        String nombre = "Sobre de gato";
        TipoProducto tipo = TipoProducto.Comida;
        MarcaProducto marca = MarcaProducto.HILLS;

        double precio = 12.0;
        int cantidad = 10;
        UnidadProducto unidad = UnidadProducto.Pieza;
        LocalDate fechaCaducidad = LocalDate.now().plusWeeks(2);

        when(productoRepository.findByNombreAndTipoProductoAndMarcaProducto(nombre, tipo, marca))
                .thenReturn(Optional.empty());
        when(productoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Producto resultado = servicioProducto.agregarProducto(nombre, tipo, marca, precio, cantidad, unidad, fechaCaducidad, null);

        assertNotNull(resultado);
        assertEquals(nombre, resultado.getNombre());
        assertEquals(tipo, resultado.getTipoProducto());
        assertEquals(marca, resultado.getMarcaProducto());
        assertEquals(precio, resultado.getPrecio());
        assertEquals(cantidad, resultado.getCantidadStock());
        assertEquals(unidad, resultado.getUnidadProducto());
        assertEquals(fechaCaducidad, resultado.getFechaCaducidad());

        verify(productoRepository).save(any());
    }*/

    @Test
    public void agregarProducto_productoExistente_lanzaExcepcion() {
        String nombre = "Sobre de gato";
        TipoProducto tipo = TipoProducto.Comida;
        MarcaProducto marca = MarcaProducto.HILLS;

        when(productoRepository.findByNombreAndTipoProductoAndMarcaProducto(nombre, tipo, marca))
                .thenReturn(Optional.of(new Producto()));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            servicioProducto.agregarProducto(nombre, tipo, marca, 12, 10, UnidadProducto.Pieza, null, null);
        });

        assertEquals("El producto ya existe en la base de datos.", ex.getMessage());
    }

    @Test
    public void agregarProducto_nombreNulo_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioProducto.agregarProducto(null, TipoProducto.Comida, MarcaProducto.HILLS, 12, 10, UnidadProducto.Pieza, null, null);
        });
        assertEquals("El nombre del producto no puede ser nulo o vacío", ex.getMessage());
    }

    @Test
    public void agregarProducto_nombreVacio_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioProducto.agregarProducto(" ", TipoProducto.Comida, MarcaProducto.HILLS, 12, 10, UnidadProducto.Pieza, null, null);
        });
        assertEquals("El nombre del producto no puede ser nulo o vacío", ex.getMessage());
    }

    @Test
    public void agregarProducto_tipoProductoNulo_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioProducto.agregarProducto("Sobre de gato", null, MarcaProducto.HILLS, 12, 10, UnidadProducto.Pieza, null, null);
        });
        assertEquals("El tipo de producto no puede ser nulo", ex.getMessage());
    }

    @Test
    public void agregarProducto_marcaProductoNula_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioProducto.agregarProducto("Sobre de gato", TipoProducto.Comida, null, 12, 10, UnidadProducto.Pieza, null, null);
        });
        assertEquals("La marca del producto no puede ser nula", ex.getMessage());
    }

    @Test
    public void agregarProducto_precioInvalido_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioProducto.agregarProducto("Sobre de gato", TipoProducto.Comida, MarcaProducto.HILLS, 0, 10, UnidadProducto.Pieza, null, null);
        });
        assertEquals("El precio debe ser mayor a cero", ex.getMessage());
    }

    @Test
    public void agregarProducto_unidadProductoNula_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioProducto.agregarProducto("Sobre de gato", TipoProducto.Comida, MarcaProducto.HILLS, 12, 10, null, null, null);
        });
        assertEquals("La unidad del producto no puede ser nula", ex.getMessage());
    }

    @Test
    public void agregarProducto_fechaCaducidadMuyCercana_lanzaExcepcion() {
        LocalDate fechaCaducidad = LocalDate.now().plusDays(3); // menos de una semana

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioProducto.agregarProducto("Sobre de gato", TipoProducto.Comida, MarcaProducto.HILLS, 12, 10, UnidadProducto.Pieza, fechaCaducidad, null);
        });
        assertEquals("La fecha no puede ser anterior a una semana a partir de hoy", ex.getMessage());
    }

    @Test
    public void agregarProducto_usoVeterinarioNula_lanzaExcepcion() {
        LocalDate fechaCaducidad = LocalDate.now().plusWeeks(3);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioProducto.agregarProducto("Meloxivet", TipoProducto.Medicamento, MarcaProducto.MSD, 12, 10, UnidadProducto.Inyeccion, fechaCaducidad, null);
        });
        assertEquals("El uso veterinario no puede ser nulo para medicamentos", ex.getMessage());
    }

    @Test
    public void agregarProducto_fechaCaducidadNula_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioProducto.agregarProducto("Meloxivet", TipoProducto.Medicamento, MarcaProducto.MSD, 12, 10, UnidadProducto.Inyeccion, null, UsoVeterinario.Gato);
        });
        assertEquals("La fecha de caducidad no puede ser nula en medicamentos o comida", ex.getMessage());
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
