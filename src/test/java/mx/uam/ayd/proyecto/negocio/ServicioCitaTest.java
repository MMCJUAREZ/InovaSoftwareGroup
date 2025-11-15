package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import mx.uam.ayd.proyecto.datos.CitaRepository;
import mx.uam.ayd.proyecto.datos.VeterinarioRepository; // Nueva implementacion de repositorio
import mx.uam.ayd.proyecto.negocio.modelo.Cita;
import mx.uam.ayd.proyecto.negocio.modelo.TipoCita;
import mx.uam.ayd.proyecto.negocio.modelo.Veterinario; // Nueva implementacion de repositorio

/**
 * Pruebas unitarias para ServicioCita, enfocadas en las validaciones de negocio
 * (horario, solapamiento, formatos, fecha pasada, y conflicto por Veterinario).
 */

class ServicioCitaTest {

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private ServicioCorreo servicioCorreo;

    @Mock
    private VeterinarioRepository veterinarioRepository; // MOCK DE REPOSITORIO PARA CONSTRUCTOR

    @InjectMocks
    private ServicioCita servicioCita;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * @return Una fecha y hora hábil garantizada: Lunes (para evitar el fin de semana)
     * y 1 segundo en el futuro para pasar la validación de tiempo pasado.
     */

    private LocalDateTime getHoraHabil() {

        // Solución robusta: forzar Lunes a las 10:00 AM en el futuro

        LocalDateTime future = LocalDateTime.now().plusSeconds(1);
        LocalDateTime nextMonday = future.with(DayOfWeek.MONDAY).withHour(10).withMinute(0).withSecond(0).withNano(0);

        // Si ya pasó el Lunes 10 AM, saltamos a la siguiente semana

        if(nextMonday.isBefore(future)) {
            nextMonday = nextMonday.plusWeeks(1);
        }
        return nextMonday;
    }

    /**
     * @return Un Veterinario mockeado con el ID especificado.
     */

    private Veterinario getVeterinarioMock(Long id) {
        Veterinario vet = new Veterinario();
        vet.setIdVeterinario(id);
        vet.setNombreCompleto("Dr. Test " + id);
        return vet;
    }

    // Pruebas de agendar cita

    @Test
    void agendarCita_ExitoSinCorreo() {

        // Datos de prueba

        LocalDateTime fechaHora = getHoraHabil();
        Veterinario veterinarioMock = getVeterinarioMock(1L);

        // Mocks

        Cita citaMock = new Cita();
        citaMock.setIdCita(1L);
        citaMock.setFechaHora(fechaHora); // SINCRONIZACIÓN CLAVE

        when(citaRepository.findCitasOverlap(any(), any(), eq(1L))).thenReturn(Collections.emptyList()); // USANDO NUEVA QUERY
        when(citaRepository.save(any(Cita.class))).thenReturn(citaMock);

        // Ejecuta - Incluye los 3 nuevos argumentos (Veterinario, motivo, notas)

        Cita resultado = servicioCita.agendarCita(fechaHora, TipoCita.Consulta, "Juan Pérez", "5512345678", false,
                veterinarioMock, "Motivo: Revisión", "Notas");

        // Aserciones

        assertNotNull(resultado);
        assertEquals(fechaHora, resultado.getFechaHora());
        verify(citaRepository, times(1)).save(any(Cita.class));
        verify(servicioCorreo, never()).enviarCorreo(any(), any(), any());

    }

    @Test
    void agendarCita_ExitoConCorreo() {
        LocalDateTime fechaHora = getHoraHabil();
        Veterinario veterinarioMock = getVeterinarioMock(2L);

        Cita citaMock = new Cita();
        citaMock.setIdCita(2L);
        citaMock.setFechaHora(fechaHora);

        when(citaRepository.findCitasOverlap(any(), any(), eq(2L))).thenReturn(Collections.emptyList());
        when(citaRepository.save(any(Cita.class))).thenReturn(citaMock);

        servicioCita.agendarCita(fechaHora, TipoCita.Vacunacion, "María Test", "maria@correo.com", true,
                veterinarioMock, "Motivo: Vacunación", "Notas: Ninguna");

        verify(citaRepository, times(1)).save(any(Cita.class));
        verify(servicioCorreo, times(1)).enviarCorreo(eq("maria@correo.com"), any(), any());
    }

    @Test
    void agendarCita_FallaPorFechaPasada() {
        LocalDateTime fechaHoraPasada = LocalDateTime.now().minusHours(1);
        Veterinario veterinarioMock = getVeterinarioMock(3L);

        assertThrows(IllegalArgumentException.class, () -> {
            servicioCita.agendarCita(fechaHoraPasada, TipoCita.Consulta, "Cliente Atrasado", "5512345678", false,
                    veterinarioMock, "", "");
        }, "Debe fallar si la fecha/hora es anterior a la actual.");

        verify(citaRepository, never()).save(any(Cita.class));
    }

    // HU02 Prueba de conflicto por veterinario

    @Test
    void agendarCita_FallaPorConflictoVeterinario() {
        LocalDateTime fechaHora = getHoraHabil();
        Veterinario vet1 = getVeterinarioMock(1L);

        // Simular que el repositorio encuentra otra cita para el mismo veterinario

        Cita citaSolapada = new Cita();
        citaSolapada.setIdCita(10L);

        // Configuracion de mockito para que devuelva una cita solapada para el veterinario 1

        when(citaRepository.findCitasOverlap(any(), any(), eq(1L))).thenReturn(Arrays.asList(citaSolapada));

        assertThrows(IllegalArgumentException.class, () -> {
            // Intentar agendar una cita que se solapa para el mismo veterinario (ID 1)
            servicioCita.agendarCita(fechaHora, TipoCita.Consulta, "Cliente", "5512345678", false,
                    vet1, "motivo", "notas");
        }, "Debe fallar porque el veterinario seleccionado ya tiene un conflicto de agenda en esa fecha y hora.");

        verify(citaRepository, never()).save(any(Cita.class));
    }

    // Prueba de solapamiento de la HU-01 (adaptada para incluir veterinario)

    @Test
    void agendarCita_FallaPorSolapamientoSinVeterinario() {
        LocalDateTime fechaHora = getHoraHabil();
        Veterinario vet1 = getVeterinarioMock(1L);

        Cita citaSolapada = new Cita();
        citaSolapada.setIdCita(10L);

        // Simular que ya hay una cita en ese rango

        when(citaRepository.findCitasOverlap(any(), any(), eq(1L))).thenReturn(Arrays.asList(citaSolapada));

        assertThrows(IllegalArgumentException.class, () -> {
            servicioCita.agendarCita(fechaHora, TipoCita.Consulta, "Juan", "5512345678", false,
                    vet1, "", "");
        }, "Debe fallar por solapamiento de horario.");
    }

    @Test
    void agendarCita_FallaPorHorarioInhabil_Domingo() {
        LocalDateTime fechaHora = getHoraHabil().with(DayOfWeek.SUNDAY);
        Veterinario veterinarioMock = getVeterinarioMock(4L);

        assertThrows(IllegalArgumentException.class, () -> {
            servicioCita.agendarCita(fechaHora, TipoCita.Consulta, "Juan", "5512345678", false,
                    veterinarioMock, "", "");
        }, "Debe fallar si es Domingo.");
    }

    //  Pruebas de modificar cita

    @Test
    void modificarCita_Exito() {
        Cita citaExistente = new Cita();
        citaExistente.setIdCita(1L);
        citaExistente.setAtendida(false);
        citaExistente.setFechaHora(getHoraHabil().minusDays(1)); // Fecha vieja

        Veterinario vetNuevo = getVeterinarioMock(7L);
        LocalDateTime nuevaFechaHora = getHoraHabil();

        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaExistente));

        // Simula que la búsqueda de solapamiento no encuentra otras citas para el ID

        when(citaRepository.findCitasOverlap(any(), any(), eq(7L))).thenReturn(Collections.emptyList());
        when(citaRepository.save(any(Cita.class))).thenReturn(citaExistente);

        Cita resultado = servicioCita.modificarCita(1L, nuevaFechaHora, TipoCita.Estetica, "Nuevo Nombre", "new@mail.com",
                vetNuevo, "Nuevo Motivo", "Nueva Nota");

        verify(citaRepository, times(1)).save(citaExistente);
        assertEquals(nuevaFechaHora, resultado.getFechaHora());
        assertEquals(vetNuevo.getIdVeterinario(), resultado.getVeterinario().getIdVeterinario());
    }

    @Test
    void modificarCita_FallaModificarCitaAPasado() {
        Cita citaExistente = new Cita();
        citaExistente.setIdCita(1L);
        citaExistente.setAtendida(false);

        Veterinario vet = getVeterinarioMock(8L);
        LocalDateTime fechaHoraPasada = LocalDateTime.now().minusHours(1);

        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaExistente));

        assertThrows(IllegalArgumentException.class, () -> {
            servicioCita.modificarCita(1L, fechaHoraPasada, TipoCita.Consulta, "Juan", "5512345678",
                    vet, "", "");
        }, "No debe permitir modificar la cita a una fecha/hora pasada.");
    }

    @Test
    void modificarCita_FallaSolapamientoConOtraCita() {
        Cita citaExistente = new Cita();
        citaExistente.setIdCita(1L);
        citaExistente.setAtendida(false);

        Veterinario vet = getVeterinarioMock(9L);

        // Otra cita que se solapa y no es la que se está modificando

        Cita citaSolapada = new Cita();
        citaSolapada.setIdCita(2L);

        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaExistente));

        // El repositorio regresa una cita diferente que se solapa

        when(citaRepository.findCitasOverlap(any(), any(), eq(9L))).thenReturn(Arrays.asList(citaSolapada));

        assertThrows(IllegalArgumentException.class, () -> {
            servicioCita.modificarCita(1L, getHoraHabil(), TipoCita.Consulta, "Juan", "5512345678",
                    vet, "", "");
        }, "Debe fallar si se solapa con otra cita.");
    }

    // Pruebas de eliminar cita

    @Test
    void eliminarCita_Exito() {
        Cita citaExistente = new Cita();
        citaExistente.setIdCita(1L);
        citaExistente.setAtendida(false);

        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaExistente));

        servicioCita.eliminarCita(1L);

        verify(citaRepository, times(1)).delete(citaExistente);
    }
}