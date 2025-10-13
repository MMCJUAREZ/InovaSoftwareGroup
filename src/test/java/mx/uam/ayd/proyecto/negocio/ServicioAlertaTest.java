package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.*; // Métodos para hacer verificaciones en las pruebas
import static org.mockito.Mockito.*; // Métodos para simular (mockear) objetos y comportamientos

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import mx.uam.ayd.proyecto.datos.AlertaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Alerta;
import mx.uam.ayd.proyecto.negocio.modelo.Umbral;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

/**
 * Clase de pruebas unitarias para el servicio de alertas.
 * Aquí se usan mocks para simular dependencias y se verifica el comportamiento del servicio.
 */
public class ServicioAlertaTest {

    private ServicioAlerta servicioAlerta;

    // Simulación del repositorio para no depender de la base de datos real
    @Mock
    private AlertaRepository alertaRepository;

    @BeforeEach
    public void setup() {
        // Inicializa los mocks antes de cada prueba
        MockitoAnnotations.openMocks(this);
        servicioAlerta = new ServicioAlerta(alertaRepository);
    }

    @Test
    public void crearAlerta_CuandoUmbralNoTieneAlerta_CreaNuevaAlerta() {
        // Simulamos un umbral sin alerta asociada
        Umbral umbral = mock(Umbral.class);
        when(umbral.getAlerta()).thenReturn(null);

        // Capturamos cuando se asigne la alerta al umbral para que se simule correctamente
        doAnswer(invocation -> {
            Alerta arg = invocation.getArgument(0);
            when(umbral.getAlerta()).thenReturn(arg);
            return null;
        }).when(umbral).setAlerta(any(Alerta.class));

        String correo = "correo@test.com";
        String mensaje = "Mensaje de prueba";
        LocalDateTime fecha = LocalDateTime.now();

        // Simulamos que guardar una alerta en el repositorio devuelve la misma alerta
        when(alertaRepository.save(any(Alerta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Ejecutamos el método que estamos probando
        Alerta alerta = servicioAlerta.crearAlerta(umbral, correo, mensaje, fecha);

        // Verificaciones
        assertNotNull(alerta);
        assertEquals(mensaje, alerta.getMensajePersonalizado());
        assertTrue(alerta.isEnviadoPorCorreo());
        assertEquals(fecha, alerta.getFechaHoraEnvio());
        assertEquals(umbral, alerta.getUmbral());
        verify(umbral).setAlerta(alerta);
        verify(alertaRepository).save(alerta);
    }

    @Test
    public void crearAlerta_CuandoUmbralTieneAlerta_LanzaException() {
        // Simulamos un umbral que ya tiene una alerta
        Umbral umbral = mock(Umbral.class);
        when(umbral.getAlerta()).thenReturn(new Alerta());

        // Verificamos que lanzar una alerta en este caso produce una excepción
        assertThrows(IllegalArgumentException.class, () -> {
            servicioAlerta.crearAlerta(umbral, "correo@test.com", "msg", LocalDateTime.now());
        });
    }

    @Test
    public void editarAlerta_CuandoExisteAlerta_ActualizaCampos() {
        // Creamos una alerta simulada en la base de datos
        Alerta alertaGuardada = new Alerta();
        alertaGuardada.setMensajePersonalizado("Mensaje viejo");
        alertaGuardada.setEnviadoPorCorreo(false);
        alertaGuardada.setFechaHoraEnvio(LocalDateTime.now().minusDays(1));

        long id = 1L;
        when(alertaRepository.findById(id)).thenReturn(Optional.of(alertaGuardada));
        when(alertaRepository.save(any(Alerta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Datos nuevos para actualizar la alerta
        String correoNuevo = "nuevo@correo.com";
        String mensajeNuevo = "Mensaje nuevo";
        LocalDateTime fechaNueva = LocalDateTime.now();

        // Ejecutamos la edición
        Alerta alertaEditada = servicioAlerta.editarAlerta(id, correoNuevo, mensajeNuevo, fechaNueva);

        // Verificamos cambios
        assertEquals(mensajeNuevo, alertaEditada.getMensajePersonalizado());
        assertTrue(alertaEditada.isEnviadoPorCorreo());
        assertEquals(fechaNueva, alertaEditada.getFechaHoraEnvio());
    }

    @Test
    public void editarAlerta_CuandoNoExisteAlerta_LanzaException() {
        // Simulamos que no se encuentra la alerta
        when(alertaRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Esperamos una excepción
        assertThrows(IllegalArgumentException.class, () -> {
            servicioAlerta.editarAlerta(999L, "correo@test.com", "msg", LocalDateTime.now());
        });
    }

    @Test
    public void crearAlertaSiNecesaria_CuandoStockMenorQueMinimoYNoHayAlerta_CreaAlerta() {
        // Simulamos un producto con poco stock
        Producto producto = mock(Producto.class);
        when(producto.getCantidadStock()).thenReturn(5);
        when(producto.getNombre()).thenReturn("ProductoX");

        // Simulamos un umbral sin alerta
        Umbral umbral = mock(Umbral.class);
        when(umbral.getValorMinimo()).thenReturn(10);
        when(umbral.getAlerta()).thenReturn(null);

        // Simulamos asignación de alerta
        doAnswer(invocation -> {
            Alerta arg = invocation.getArgument(0);
            when(umbral.getAlerta()).thenReturn(arg);
            return null;
        }).when(umbral).setAlerta(any(Alerta.class));

        when(alertaRepository.save(any(Alerta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Ejecutamos
        Alerta alerta = servicioAlerta.crearAlertaSiNecesaria(producto, umbral);

        // Verificamos que se creó correctamente
        assertNotNull(alerta);
        assertTrue(alerta.getMensajePersonalizado().contains("ProductoX"));
    }

    @Test
    public void crearAlertaSiNecesaria_CuandoStockMenorQueMinimoYYaHayAlerta_RetornaAlertaExistente() {
        Producto producto = mock(Producto.class);
        when(producto.getCantidadStock()).thenReturn(5);

        Umbral umbral = mock(Umbral.class);
        when(umbral.getValorMinimo()).thenReturn(10);

        // Ya existe una alerta asociada
        Alerta alertaExistente = new Alerta();
        when(umbral.getAlerta()).thenReturn(alertaExistente);

        // Ejecutamos
        Alerta alerta = servicioAlerta.crearAlertaSiNecesaria(producto, umbral);

        // Debe devolver la misma alerta existente
        assertEquals(alertaExistente, alerta);
    }

    @Test
    public void crearAlertaSiNecesaria_CuandoStockNoEsMenorQueMinimo_NoCreaAlerta() {
        // Simulamos un producto con stock suficiente
        Producto producto = mock(Producto.class);
        when(producto.getCantidadStock()).thenReturn(15);

        Umbral umbral = mock(Umbral.class);
        when(umbral.getValorMinimo()).thenReturn(10);

        // Ejecutamos y esperamos null
        Alerta alerta = servicioAlerta.crearAlertaSiNecesaria(producto, umbral);

        assertNull(alerta);
    }
}