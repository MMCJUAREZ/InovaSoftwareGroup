package mx.uam.ayd.proyecto.negocio;

import mx.uam.ayd.proyecto.datos.HospedajeRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.Hospedaje;
import mx.uam.ayd.proyecto.negocio.modelo.Mascota;
import mx.uam.ayd.proyecto.negocio.ServicioCorreo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ServicioHospedaje.
 */
class ServicioHospedajeTest {

    @Mock
    private HospedajeRepository hospedajeRepository;

    @InjectMocks
    private ServicioHospedaje servicioHospedaje;

    private Cliente cliente;
    private Mascota mascota;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cliente = new Cliente();
        mascota = new Mascota();
    }

    @Test
    void testRegistrar_Exito() {
        Hospedaje hospedajeMock = new Hospedaje();
        when(hospedajeRepository.save(any(Hospedaje.class))).thenReturn(hospedajeMock);

        Hospedaje resultado = servicioHospedaje.registrar(
                cliente, mascota, LocalDate.now().plusDays(1), LocalDate.now().plusDays(3), "Ninguna");

        assertNotNull(resultado);
        verify(hospedajeRepository, times(1)).save(any(Hospedaje.class));
    }

    @Test
    void testRegistrar_FechaEntradaAnteriorAHoy() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                servicioHospedaje.registrar(cliente, mascota,
                        LocalDate.now().minusDays(1), LocalDate.now().plusDays(2), ""));
        assertEquals("La fecha de entrada no puede ser anterior a hoy.", ex.getMessage());
    }

    @Test
    void testRegistrar_FechaSalidaInvalida() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                servicioHospedaje.registrar(cliente, mascota,
                        LocalDate.now().plusDays(2), LocalDate.now().plusDays(1), ""));
        assertEquals("La fecha de salida debe ser posterior a la fecha de entrada.", ex.getMessage());
    }

    @Test
    void testEliminarHospedaje_Exito() {
        Hospedaje hospedaje = new Hospedaje();
        hospedaje.setFechaEntrada(LocalDate.now().plusDays(2));
        when(hospedajeRepository.findById(1L)).thenReturn(Optional.of(hospedaje));

        servicioHospedaje.eliminarHospedaje(1L);

        verify(hospedajeRepository, times(1)).delete(hospedaje);
    }

    @Test
    void testEliminarHospedaje_NoExiste() {
        when(hospedajeRepository.findById(99L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                servicioHospedaje.eliminarHospedaje(99L));
        assertTrue(ex.getMessage().contains("No existe el registro de hospedaje"));
    }

    @Test
    void testEliminarHospedaje_FechaPasada() {
        Hospedaje hospedaje = new Hospedaje();
        hospedaje.setFechaEntrada(LocalDate.now().minusDays(1));
        when(hospedajeRepository.findById(2L)).thenReturn(Optional.of(hospedaje));

        Exception ex = assertThrows(IllegalStateException.class, () ->
                servicioHospedaje.eliminarHospedaje(2L));
        assertEquals("No se puede eliminar el registro, la mascota ya ingresó (la fecha de entrada ya pasó).", ex.getMessage());
    }
}