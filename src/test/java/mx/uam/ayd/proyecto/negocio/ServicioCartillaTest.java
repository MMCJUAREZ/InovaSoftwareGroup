package mx.uam.ayd.proyecto.negocio;

import mx.uam.ayd.proyecto.datos.CartillaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cartilla;
import mx.uam.ayd.proyecto.negocio.modelo.VacunaEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServicioCartillaTest {

    private CartillaRepository repositorioCartilla;
    private ServicioCartilla servicioCartilla;

    @BeforeEach
    void setUp() {
        repositorioCartilla = mock(CartillaRepository.class);
        servicioCartilla = new ServicioCartilla();
        // Inyectamos el mock (por reflexión, ya que no usamos Spring aquí)
        try {
            var field = ServicioCartilla.class.getDeclaredField("repositorioCartilla");
            field.setAccessible(true);
            field.set(servicioCartilla, repositorioCartilla);
        } catch (Exception e) {
            fail("No se pudo inyectar el repositorio mock: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // TEST: Registrar vacuna exitosamente
    // -------------------------------------------------------------------------
    @Test
    void testRegistrarVacuna_Exito() {
        // Datos de prueba
        VacunaEnum vacuna = VacunaEnum.RABIA;
        LocalDate fecha = LocalDate.now().minusDays(2);
        String veterinario = "Dr. Pérez";
        Long lote = 123L;
        String observaciones = "Sin complicaciones";
        Long mascotaId = 10L;

        when(repositorioCartilla.existsByVacunaAndMascotaIdAndFechaAplicacion(
                vacuna, mascotaId, fecha)).thenReturn(false);

        ArgumentCaptor<Cartilla> captor = ArgumentCaptor.forClass(Cartilla.class);
        when(repositorioCartilla.save(any(Cartilla.class))).thenAnswer(i -> i.getArgument(0));

        // Acción
        Cartilla result = servicioCartilla.registrarVacuna(vacuna, fecha, veterinario, lote, observaciones, mascotaId);

        // Verificación
        verify(repositorioCartilla).save(captor.capture());
        Cartilla guardada = captor.getValue();

        assertEquals(vacuna, guardada.getVacuna());
        assertEquals(fecha, guardada.getFechaAplicacion());
        assertEquals("Dr. Pérez", guardada.getVeterinario());
        assertEquals(123L, guardada.getLote());
        assertEquals(mascotaId, guardada.getMascotaId());
        assertNotNull(guardada.getProximaDosis());
        assertTrue(guardada.getProximaDosis().isAfter(fecha));
        assertEquals(result, guardada);
    }

    // -------------------------------------------------------------------------
    // TEST: Falla si la vacuna es nula
    // -------------------------------------------------------------------------
    @Test
    void testRegistrarVacuna_FallaVacunaNula() {
        Exception e = assertThrows(IllegalArgumentException.class, () ->
                servicioCartilla.registrarVacuna(null, LocalDate.now(), "Dr", 1L, "", 1L));
        assertEquals("El tipo de vacuna es obligatorio", e.getMessage());
    }

    // -------------------------------------------------------------------------
    // TEST: Falla si veterinario vacío
    // -------------------------------------------------------------------------
    @Test
    void testRegistrarVacuna_FallaVeterinarioVacio() {
        Exception e = assertThrows(IllegalArgumentException.class, () ->
                servicioCartilla.registrarVacuna(VacunaEnum.RABIA, LocalDate.now(), "  ", 1L, "", 1L));
        assertEquals("El nombre del veterinario es obligatorio", e.getMessage());
    }

    // -------------------------------------------------------------------------
    // TEST: Falla si la fecha es futura
    // -------------------------------------------------------------------------
    @Test
    void testRegistrarVacuna_FallaFechaFutura() {
        LocalDate futura = LocalDate.now().plusDays(1);
        Exception e = assertThrows(IllegalArgumentException.class, () ->
                servicioCartilla.registrarVacuna(VacunaEnum.RABIA, futura, "Dr", 1L, "", 1L));
        assertEquals("La fecha de aplicación no puede ser futura", e.getMessage());
    }

    // -------------------------------------------------------------------------
    // TEST: Falla si ya existe vacuna duplicada
    // -------------------------------------------------------------------------
    @Test
    void testRegistrarVacuna_Duplicada() {
        LocalDate fecha = LocalDate.now();
        when(repositorioCartilla.existsByVacunaAndMascotaIdAndFechaAplicacion(
                VacunaEnum.RABIA, 1L, fecha)).thenReturn(true);

        Exception e = assertThrows(IllegalArgumentException.class, () ->
                servicioCartilla.registrarVacuna(VacunaEnum.RABIA, fecha, "Dr", 1L, "", 1L));
        assertTrue(e.getMessage().contains("Ya existe un registro de esta vacuna"));
    }

    // -------------------------------------------------------------------------
    // TEST: Eliminar vacuna
    // -------------------------------------------------------------------------
    @Test
    void testEliminarRegistroVacuna() {
        servicioCartilla.eliminarRegistroVacuna(5L);
        verify(repositorioCartilla).deleteById(5L);
    }

    // -------------------------------------------------------------------------
    // TEST: Actualizar vacuna
    // -------------------------------------------------------------------------
    @Test
    void testActualizarRegistroVacuna_Exito() {
        Cartilla existente = new Cartilla(VacunaEnum.RABIA, LocalDate.now(), "Dr", 1L, "", 1L);
        existente.setId(1L);
        when(repositorioCartilla.findById(1L)).thenReturn(Optional.of(existente));
        when(repositorioCartilla.save(any(Cartilla.class))).thenAnswer(i -> i.getArgument(0));

        Cartilla actualizada = servicioCartilla.actualizarRegistroVacuna(1L,
                VacunaEnum.PARVOVIRUS,
                LocalDate.now().minusDays(1),
                "Dr. Gómez",
                2L,
                "Reaplicación");

        assertEquals(VacunaEnum.PARVOVIRUS, actualizada.getVacuna());
        assertEquals("Dr. Gómez", actualizada.getVeterinario());
        assertEquals(2L, actualizada.getLote());
        assertEquals("Reaplicación", actualizada.getObservaciones());
        verify(repositorioCartilla).save(existente);
    }

    // -------------------------------------------------------------------------
    // TEST: Obtener todas las vacunas
    // -------------------------------------------------------------------------
    @Test
    void testObtenerTodasLasVacunas() {
        List<VacunaEnum> vacunas = servicioCartilla.obtenerTodasLasVacunas();
        assertTrue(vacunas.contains(VacunaEnum.RABIA));
        assertEquals(VacunaEnum.values().length, vacunas.size());
    }
}


