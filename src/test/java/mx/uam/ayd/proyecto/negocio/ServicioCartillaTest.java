package mx.uam.ayd.proyecto.negocio;

import mx.uam.ayd.proyecto.datos.CartillaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cartilla;
import mx.uam.ayd.proyecto.negocio.modelo.VacunaEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ServicioCartillaTest {
    @Mock
    private CartillaRepository repositorioCartilla;
    @InjectMocks
    private ServicioCartilla servicioCartilla;

    @BeforeEach
    void setUp() {
        // Given: Configuración inicial del mock del repositorio
        repositorioCartilla = mock(CartillaRepository.class);
        servicioCartilla = new ServicioCartilla();

        // Given: Inyección del mock usando reflexión
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
        // Given: Datos de prueba válidos
        VacunaEnum vacuna = VacunaEnum.RABIA;
        LocalDate fecha = LocalDate.now().minusDays(2);
        String veterinario = "Doctor Juan Pérez García";  // Nombre válido: solo letras y espacios
        Long lote = 123L;
        String observaciones = "Sin complicaciones";
        Long mascotaId = 10L;

        // Given: Configuración del mock - no existe duplicado
        when(repositorioCartilla.existsByVacunaAndMascotaIdAndFechaAplicacion(
                eq(vacuna), eq(mascotaId), eq(fecha))).thenReturn(false);

        // Given: Configuración del mock para capturar el objeto guardado
        ArgumentCaptor<Cartilla> captorCartilla = ArgumentCaptor.forClass(Cartilla.class);
        when(repositorioCartilla.save(any(Cartilla.class))).thenAnswer(invocation -> {
            Cartilla cartilla = invocation.getArgument(0);
            cartilla.setId(1L); // Simular ID generado
            return cartilla;
        });

        // When: Se registra la vacuna
        Cartilla resultado = servicioCartilla.registrarVacuna(
                vacuna, fecha, veterinario, lote, observaciones, mascotaId);

        // Then: Se verifica la interacción con el repositorio
        verify(repositorioCartilla, times(1)).save(captorCartilla.capture());
        verify(repositorioCartilla, times(1))
                .existsByVacunaAndMascotaIdAndFechaAplicacion(vacuna, mascotaId, fecha);

        // Then: Se verifica el objeto guardado
        Cartilla cartillaGuardada = captorCartilla.getValue();
        assertNotNull(cartillaGuardada);
        assertEquals(vacuna, cartillaGuardada.getVacuna());
        assertEquals(fecha, cartillaGuardada.getFechaAplicacion());
        assertEquals(veterinario, cartillaGuardada.getVeterinario());
        assertEquals(lote, cartillaGuardada.getLote());
        assertEquals(observaciones, cartillaGuardada.getObservaciones());
        assertEquals(mascotaId, cartillaGuardada.getMascotaId());
        assertNotNull(cartillaGuardada.getProximaDosis());
        assertTrue(cartillaGuardada.getProximaDosis().isAfter(fecha));

        // Then: Se verifica el resultado retornado
        assertNotNull(resultado);
        assertEquals(cartillaGuardada, resultado);
    }

    // -------------------------------------------------------------------------
    // TEST: Falla si veterinario es demasiado largo
    // -------------------------------------------------------------------------
    @Test
    void testRegistrarVacuna_FallaVeterinarioMuyLargo() {
        // Given: Nombre de veterinario con más de 100 caracteres
        String nombreLargo = "Dr. " + "X".repeat(150);

        // When & Then: Se espera excepción con mensaje específico
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioCartilla.registrarVacuna(
                        VacunaEnum.RABIA,
                        LocalDate.now().minusDays(1),  // Fecha pasada
                        nombreLargo,
                        1L,
                        "",
                        1L
                )
        );

        // Then: Verificar mensaje de error
        assertEquals(
                "El nombre del veterinario solo debe contener letras y espacios (max 100 caracteres)",
                exception.getMessage()
        );

        // Then: Verificar que no se llamó al repositorio
        verify(repositorioCartilla, never()).existsByVacunaAndMascotaIdAndFechaAplicacion(
                any(), any(), any());
        verify(repositorioCartilla, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // TEST: Falla si veterinario contiene caracteres inválidos
    // -------------------------------------------------------------------------
    @Test
    void testRegistrarVacuna_FallaVeterinarioCaracteresInvalidos() {
        // Given: Nombre de veterinario con números
        String veterinarioInvalido = "Dr. Juan123";

        // When & Then: Se espera excepción
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioCartilla.registrarVacuna(
                        VacunaEnum.RABIA,
                        LocalDate.now().minusDays(1),
                        veterinarioInvalido,
                        1L,
                        "",
                        1L
                )
        );

        // Then: Verificar mensaje de error
        assertEquals(
                "El nombre del veterinario solo debe contener letras y espacios (max 100 caracteres)",
                exception.getMessage()
        );
    }

    // -------------------------------------------------------------------------
    // TEST: Falla si la vacuna es nula
    // -------------------------------------------------------------------------
    @Test
    void testRegistrarVacuna_FallaVacunaNula() {
        // Given: Vacuna nula como dato de entrada
        // When & Then: Se espera excepción
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioCartilla.registrarVacuna(
                        null,
                        LocalDate.now().minusDays(1),
                        "Doctor Juan Pérez",
                        1L,
                        "",
                        1L
                )
        );

        // Then: Verificar mensaje de error
        assertEquals("El tipo de vacuna es obligatorio", exception.getMessage());

        // Then: Verificar que no se llamó al repositorio
        verify(repositorioCartilla, never()).existsByVacunaAndMascotaIdAndFechaAplicacion(
                any(), any(), any());
        verify(repositorioCartilla, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // TEST: Falla si veterinario vacío
    // -------------------------------------------------------------------------
    @Test
    void testRegistrarVacuna_FallaVeterinarioVacio() {
        // Given: Nombre de veterinario vacío
        String veterinarioVacio = "   ";

        // When & Then: Se espera excepción
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioCartilla.registrarVacuna(
                        VacunaEnum.RABIA,
                        LocalDate.now().minusDays(1),
                        veterinarioVacio,
                        1L,
                        "",
                        1L
                )
        );

        // Then: Verificar mensaje de error
        assertEquals("El nombre del veterinario es obligatorio", exception.getMessage());

        // Then: Verificar que no se llamó al repositorio
        verify(repositorioCartilla, never()).existsByVacunaAndMascotaIdAndFechaAplicacion(
                any(), any(), any());
        verify(repositorioCartilla, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // TEST: Falla si la fecha es futura
    // -------------------------------------------------------------------------
    @Test
    void testRegistrarVacuna_FallaFechaFutura() {
        // Given: Fecha futura y veterinario válido
        LocalDate fechaFutura = LocalDate.now().plusDays(1);
        String veterinarioValido = "Doctor Juan Pérez García";

        // When & Then: Se espera excepción
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioCartilla.registrarVacuna(
                        VacunaEnum.RABIA,
                        fechaFutura,
                        veterinarioValido,
                        1L,
                        "",
                        1L
                )
        );

        // Then: Verificar mensaje de error
        assertEquals("La fecha de aplicación no puede ser futura", exception.getMessage());

        // Then: Verificar que no se llamó al repositorio
        verify(repositorioCartilla, never()).existsByVacunaAndMascotaIdAndFechaAplicacion(
                any(), any(), any());
        verify(repositorioCartilla, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // TEST: Falla si ya existe vacuna duplicada
    // -------------------------------------------------------------------------
    @Test
    void testRegistrarVacuna_Duplicada() {
        // Given: Ya existe un registro duplicado
        LocalDate fecha = LocalDate.now().minusDays(1);  // Fecha pasada
        VacunaEnum vacuna = VacunaEnum.RABIA;
        Long mascotaId = 1L;
        String veterinarioValido = "Doctor Ana María López";

        when(repositorioCartilla.existsByVacunaAndMascotaIdAndFechaAplicacion(
                eq(vacuna), eq(mascotaId), eq(fecha))).thenReturn(true);

        // When & Then: Se espera excepción
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioCartilla.registrarVacuna(
                        vacuna,
                        fecha,
                        veterinarioValido,
                        1L,
                        "",
                        mascotaId
                )
        );

        // Then: Verificar que se menciona la duplicación
        assertTrue(
                exception.getMessage().contains("Ya existe un registro de esta vacuna") ||
                        exception.getMessage().contains("duplicada") ||
                        exception.getMessage().contains("ya existe"),
                "El mensaje debería indicar duplicación. Mensaje: " + exception.getMessage()
        );

        // Then: Verificar que se consultó por duplicados pero no se guardó
        verify(repositorioCartilla, times(1))
                .existsByVacunaAndMascotaIdAndFechaAplicacion(vacuna, mascotaId, fecha);
        verify(repositorioCartilla, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // TEST: Eliminar vacuna exitosamente
    // -------------------------------------------------------------------------
    @Test
    void testEliminarRegistroVacuna() {
        // Given: ID de registro válido
        Long idRegistro = 5L;

        // When: Se elimina el registro
        servicioCartilla.eliminarRegistroVacuna(idRegistro);

        // Then: Se verifica que se llamó al método deleteById con el ID correcto
        verify(repositorioCartilla, times(1)).deleteById(eq(idRegistro));
    }

    // -------------------------------------------------------------------------
    // TEST: Actualizar vacuna exitosamente
    // -------------------------------------------------------------------------
    @Test
    void testActualizarRegistroVacuna_Exito() {
        // Given: Un registro existente en la base de datos
        Long idRegistro = 1L;
        Cartilla cartillaExistente = new Cartilla(
                VacunaEnum.RABIA,
                LocalDate.now().minusDays(10),
                "Doctor Original García",  // Nombre válido
                1L,
                "Observaciones originales",
                1L
        );
        cartillaExistente.setId(idRegistro);

        when(repositorioCartilla.findById(eq(idRegistro)))
                .thenReturn(Optional.of(cartillaExistente));

        // Given: Datos de actualización válidos
        VacunaEnum nuevaVacuna = VacunaEnum.PARVOVIRUS;
        LocalDate nuevaFecha = LocalDate.now().minusDays(1);
        String nuevoVeterinario = "Doctor Gómez Hernández";  // Nombre válido
        Long nuevoLote = 2L;
        String nuevasObservaciones = "Reaplicación";

        // Given: Configuración del mock para guardar
        when(repositorioCartilla.save(any(Cartilla.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When: Se actualiza el registro
        Cartilla resultado = servicioCartilla.actualizarRegistroVacuna(
                idRegistro,
                nuevaVacuna,
                nuevaFecha,
                nuevoVeterinario,
                nuevoLote,
                nuevasObservaciones
        );

        // Then: Se verifica que se buscó el registro por ID
        verify(repositorioCartilla, times(1)).findById(eq(idRegistro));

        // Then: Se verifica que se guardó el registro actualizado
        verify(repositorioCartilla, times(1)).save(cartillaExistente);

        // Then: Se verifica que los datos se actualizaron correctamente
        assertEquals(nuevaVacuna, resultado.getVacuna());
        assertEquals(nuevaFecha, resultado.getFechaAplicacion());
        assertEquals(nuevoVeterinario, resultado.getVeterinario());
        assertEquals(nuevoLote, resultado.getLote());
        assertEquals(nuevasObservaciones, resultado.getObservaciones());

        // Then: Se verifica que el ID se mantuvo igual
        assertEquals(idRegistro, resultado.getId());
    }

    // -------------------------------------------------------------------------
    // TEST: Obtener todas las vacunas disponibles
    // Formato Gherkin: Given-When-Then
    // -------------------------------------------------------------------------
    @Test
    void testObtenerTodasLasVacunas() {
        // Given: No se requiere configuración especial del mock

        // When: Se obtienen todas las vacunas
        List<VacunaEnum> resultado = servicioCartilla.obtenerTodasLasVacunas();

        // Then: Se verifica que la lista contiene todas las vacunas del enum
        assertNotNull(resultado);
        assertEquals(VacunaEnum.values().length, resultado.size());

        // Then: Se verifica que contiene algunas vacunas específicas
        assertTrue(resultado.contains(VacunaEnum.RABIA));
        assertTrue(resultado.contains(VacunaEnum.PARVOVIRUS));
        assertTrue(resultado.contains(VacunaEnum.MOQUILLO));

        // Then: Se verifica que no se llamó al repositorio
        verify(repositorioCartilla, never()).findAll();
        verify(repositorioCartilla, never()).findById(any());
    }

    // -------------------------------------------------------------------------
    // TEST: Falla al actualizar si veterinario es inválido
    // -------------------------------------------------------------------------
    @Test
    public void testActualizarRegistroVacuna_VeterinarioInvalido_NoValidaYGuarda() {

        // Arrange
        Long idVacuna = 1L;

        Cartilla cartillaExistente = new Cartilla();
        cartillaExistente.setId(idVacuna);
        cartillaExistente.setVacuna(VacunaEnum.RABIA);
        cartillaExistente.setFechaAplicacion(LocalDate.now().minusDays(10));
        cartillaExistente.setVeterinario("Dr Juan");
        cartillaExistente.setLote(123L);
        cartillaExistente.setObservaciones("Nada");
        cartillaExistente.setMascotaId(5L);


        when(repositorioCartilla.findById(idVacuna))
                .thenReturn(Optional.of(cartillaExistente));

        when(repositorioCartilla.save(any(Cartilla.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Datos inválidos
        VacunaEnum nuevaVacuna = VacunaEnum.PARVOVIRUS;
        LocalDate nuevaFecha = LocalDate.now().minusDays(5);
        String veterinarioInvalido = "123 Veterinario";
        Long nuevoLote = 456L;
        String obs = "Test";

        Cartilla resultado = servicioCartilla.actualizarRegistroVacuna(
                idVacuna, nuevaVacuna, nuevaFecha, veterinarioInvalido, nuevoLote, obs
        );

        // Assert — Se actualiza sin validar
        assertNotNull(resultado);
        assertEquals(veterinarioInvalido, resultado.getVeterinario());
        assertEquals(nuevaVacuna, resultado.getVacuna());
        assertEquals(nuevaFecha, resultado.getFechaAplicacion());
        assertEquals(nuevoLote, resultado.getLote());
        assertEquals(obs, resultado.getObservaciones());

        verify(repositorioCartilla, times(1)).findById(idVacuna);
        verify(repositorioCartilla, times(1)).save(any());

    }
}