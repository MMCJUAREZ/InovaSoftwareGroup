package mx.uam.ayd.proyecto.negocio;

import mx.uam.ayd.proyecto.datos.MascotaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.Mascota;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ServicioMascota.
 */
class ServicioMascotaTest {

    @Mock
    private MascotaRepository mascotaRepository;

    @InjectMocks
    private ServicioMascota servicioMascota;

    private Cliente clienteMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        clienteMock = new Cliente();
        clienteMock.setIdCliente(1L);
        clienteMock.setNombreCompleto("Juan Perez");
    }

    @Test
    void testRegistrarMascota_Exito() {
        Mascota mascotaMock = new Mascota();
        when(mascotaRepository.save(any(Mascota.class))).thenReturn(mascotaMock);

        Mascota resultado = servicioMascota.registrarMascota(
                "Firulais", "Labrador", "Perro", 3, "Macho", true);

        assertNotNull(resultado);
        verify(mascotaRepository, times(1)).save(any(Mascota.class));
    }

    @Test
    void testRegistrarMascota_NombreInvalido() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                servicioMascota.registrarMascota("", "Labrador", "Perro", 3, "Macho", true));
        assertEquals("El nombre de la mascota es obligatorio.", ex.getMessage());
    }

    @Test
    void testRegistrarMascota_EdadInvalida() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                servicioMascota.registrarMascota("Luna", "Poodle", "Perro", 0, "Hembra", true));
        assertEquals("La edad debe ser mayor a cero.", ex.getMessage());
    }

    @Test
    void testRegistrarMascota_SexoInvalido() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                servicioMascota.registrarMascota("Milo", "Gato Persa", "Gato", 2, "", true));
        assertEquals("El sexo de la mascota es obligatorio.", ex.getMessage());
    }

    @Test
    void testRegistraMascota_ConCliente_Exito() {
        Mascota mascotaGuardada = new Mascota();
        mascotaGuardada.setCliente(clienteMock);
        mascotaGuardada.setNombre("Bobby");

        // Simulamos que el save funciona
        when(mascotaRepository.save(any(Mascota.class))).thenReturn(mascotaGuardada);

        Mascota resultado = servicioMascota.registraMascota(
                clienteMock, "Bobby", "Schnauzer", "Perro", 5, "Macho", false
        );

        assertNotNull(resultado);
        assertEquals("Bobby", resultado.getNombre());
        assertEquals(clienteMock, resultado.getCliente()); // Verifica que el cliente se asignó
        verify(mascotaRepository, times(1)).save(any(Mascota.class));
    }

    @Test
    void testRegistraMascota_ConCliente_ClienteNulo() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                servicioMascota.registraMascota(
                        null, "Bobby", "Schnauzer", "Perro", 5, "Macho", false
                ));
        assertEquals("No se puede registrar una mascota sin un cliente", ex.getMessage());
    }
    
    @Test
    void testRecuperaMascotas_Exito() {
        List<Mascota> listaMock = Collections.singletonList(new Mascota());
        
        when(mascotaRepository.findByCliente(clienteMock)).thenReturn(listaMock);
        
        List<Mascota> resultado = servicioMascota.recuperaMascotas(clienteMock);
        
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(mascotaRepository, times(1)).findByCliente(clienteMock);
    }
    
    @Test
    void testEliminaMascota_Exito() {
        Long idMascota = 1L;
        
        when(mascotaRepository.existsById(idMascota)).thenReturn(true);
        doNothing().when(mascotaRepository).deleteById(idMascota);
        
        assertDoesNotThrow(() -> servicioMascota.eliminaMascota(idMascota));
        
        verify(mascotaRepository, times(1)).deleteById(idMascota);
    }
    
    @Test
    void testEliminaMascota_NoExiste() {
        Long idMascota = 99L;
        
        when(mascotaRepository.existsById(idMascota)).thenReturn(false);
        
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                servicioMascota.eliminaMascota(idMascota));
        
        assertEquals("La mascota con ID " + idMascota + " no existe", ex.getMessage());
        
        verify(mascotaRepository, never()).deleteById(idMascota);
    }
}
