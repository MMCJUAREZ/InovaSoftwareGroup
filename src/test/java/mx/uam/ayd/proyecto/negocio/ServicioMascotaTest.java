package mx.uam.ayd.proyecto.negocio;

import mx.uam.ayd.proyecto.datos.MascotaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Mascota;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ServicioMascota.
 */
class ServicioMascotaTest {

    @Mock
    private MascotaRepository mascotaRepository;

    @InjectMocks
    private ServicioMascota servicioMascota;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
}
