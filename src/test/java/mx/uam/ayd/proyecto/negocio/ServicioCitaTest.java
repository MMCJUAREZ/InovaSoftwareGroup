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
import mx.uam.ayd.proyecto.negocio.modelo.Cita;
import mx.uam.ayd.proyecto.negocio.modelo.TipoCita;

/**
 * Pruebas unitarias para ServicioCita, enfocadas en las validaciones de negocio
 * (horario, solapamiento, formatos, y fecha pasada).
 */

class ServicioCitaTest {

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private ServicioCorreo servicioCorreo;

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

        // 1. Tomamos el tiempo actual.
        LocalDateTime now = LocalDateTime.now();

        // 2. Sumamos 1 segundo para garantizar que esté en el futuro.
        LocalDateTime future = now.plusSeconds(1);

        // 3. Fijamos el día a Lunes para garantizar un día hábil.

        // Usamos 'with(DayOfWeek.MONDAY)' si 'future' no es Lunes, saltará a la siguiente semana.
        LocalDateTime nextMonday = future.with(DayOfWeek.MONDAY);

        // 4. Si el lunes ya pasó en esta semana, saltamos al siguiente.

        if(nextMonday.isBefore(future)) {

            nextMonday = nextMonday.plusWeeks(1);
        }

        // 5. Fijamos la hora a una hora segura (10:00 AM, limpia de segundos/nanos).

        return nextMonday.withHour(10).withMinute(0).withSecond(0).withNano(0);
    }

    // Pruebas de agendar cita

    @Test
    void agendarCita_ExitoSinCorreo() {

        // 1. Generar la fecha correcta

        LocalDateTime fechaHora = getHoraHabil();

        // 2. Configurar la cita en Mock

        Cita citaMock = new Cita();
        citaMock.setIdCita(1L);
        citaMock.setFechaHora(fechaHora);

        // Simula que no hay citas que se solapan

        when(citaRepository.findCitasOverlap(any(), any())).thenReturn(Collections.emptyList());

        // Simula que se guarda la cita y devuelve la versión con la fecha

        when(citaRepository.save(any(Cita.class))).thenReturn(citaMock);

        // 3. Ejecuta el servicio

        Cita resultado = servicioCita.agendarCita(fechaHora, TipoCita.Consulta, "Juan Pérez", "5512345678", false);

        // 4. Aserciones

        assertNotNull(resultado);
        assertEquals(fechaHora, resultado.getFechaHora()); // El objeto 'resultado' tiene la fecha
        verify(citaRepository, times(1)).save(any(Cita.class));
        verify(servicioCorreo, never()).enviarCorreo(any(), any(), any());
    }

    @Test
    void agendarCita_ExitoConCorreo() {
        LocalDateTime fechaHora = getHoraHabil();
        Cita citaMock = new Cita();
        citaMock.setIdCita(2L);

        when(citaRepository.findCitasOverlap(any(), any())).thenReturn(Collections.emptyList());
        when(citaRepository.save(any(Cita.class))).thenReturn(citaMock);

        servicioCita.agendarCita(fechaHora, TipoCita.Vacunacion, "María Test", "maria@correo.com", true);

        verify(citaRepository, times(1)).save(any(Cita.class));
        verify(servicioCorreo, times(1)).enviarCorreo(eq("maria@correo.com"), any(), any()); // Verifica que si se envió correo
    }

    @Test
    void agendarCita_FallaPorFechaPasada() {

        // Simular una hora en el pasado (hace una hora)

        LocalDateTime fechaHoraPasada = LocalDateTime.now().minusHours(1);

        assertThrows(IllegalArgumentException.class, () -> {

            servicioCita.agendarCita(fechaHoraPasada, TipoCita.Consulta, "Cliente Atrasado", "5512345678", false);

        }, "Debe fallar si la fecha/hora es anterior a la actual.");

        // Verifica que no se intentó guardar nada

        verify(citaRepository, never()).save(any(Cita.class));
    }

    @Test
    void agendarCita_FallaPorSolapamiento() {

        LocalDateTime fechaHora = getHoraHabil();
        Cita citaSolapada = new Cita();
        citaSolapada.setIdCita(10L);

        // Simular que ya hay una cita en ese rango

        when(citaRepository.findCitasOverlap(any(), any())).thenReturn(Arrays.asList(citaSolapada));

        assertThrows(IllegalArgumentException.class, () -> {

            servicioCita.agendarCita(fechaHora, TipoCita.Consulta, "Juan", "5512345678", false);

        }, "Debe fallar por solapamiento de horario.");
    }

    @Test
    void agendarCita_FallaPorHorarioInhabil_Domingo() {

        LocalDateTime fechaHora = getHoraHabil().with(DayOfWeek.SUNDAY);

        assertThrows(IllegalArgumentException.class, () -> {
            servicioCita.agendarCita(fechaHora, TipoCita.Consulta, "Juan", "5512345678", false);
        }, "Debe fallar si es Domingo.");
    }

    @Test
    void agendarCita_FallaPorHorarioInhabil_Tarde() {

        // 18:30, 30 minutos después de la hora de cierre (18:00)

        LocalDateTime fechaHora = getHoraHabil().with(LocalTime.of(18, 30));

        assertThrows(IllegalArgumentException.class, () -> {

            servicioCita.agendarCita(fechaHora, TipoCita.Consulta, "Juan", "5512345678", false);

        }, "Debe fallar si es después de la hora hábil.");
    }

    @Test
    void agendarCita_FallaPorDatosIncompletos() {
        LocalDateTime fechaHora = getHoraHabil();

        // Falta el nombre

        assertThrows(IllegalArgumentException.class, () -> {
            servicioCita.agendarCita(fechaHora, TipoCita.Consulta, "", "5512345678", false);
        }, "Debe fallar si el nombre está vacío.");

        // Falta el tipo

        assertThrows(IllegalArgumentException.class, () -> {
            servicioCita.agendarCita(fechaHora, null, "Juan", "5512345678", false);
        }, "Debe fallar si el tipo es nulo.");
    }

    @Test
    void agendarCita_FallaPorFormatoContactoInvalido() {
        LocalDateTime fechaHora = getHoraHabil();

        // Contacto inválido (no es correo ni 10 dígitos)

        assertThrows(IllegalArgumentException.class, () -> {
            servicioCita.agendarCita(fechaHora, TipoCita.Consulta, "Juan", "1234", false);
        }, "Debe fallar si el formato de contacto es incorrecto.");
    }

    // Pruebas de modificar cita

    @Test
    void modificarCita_Exito() {

        Cita citaExistente = new Cita();
        citaExistente.setIdCita(1L);
        citaExistente.setAtendida(false);
        citaExistente.setFechaHora(getHoraHabil().minusDays(1)); // Fecha vieja

        LocalDateTime nuevaFechaHora = getHoraHabil();

        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaExistente));

        // Simula que la búsqueda de solapamiento no encuentra otras citas

        when(citaRepository.findCitasOverlap(any(), any())).thenReturn(Collections.emptyList());
        when(citaRepository.save(any(Cita.class))).thenReturn(citaExistente);

        Cita resultado = servicioCita.modificarCita(1L, nuevaFechaHora, TipoCita.Estetica, "Nuevo Nombre", "new@mail.com");

        verify(citaRepository, times(1)).save(citaExistente);
        assertEquals(nuevaFechaHora, resultado.getFechaHora());
        assertEquals(TipoCita.Estetica, resultado.getTipo());
    }

    @Test
    void modificarCita_FallaCitaAtendida() {
        Cita citaExistente = new Cita();
        citaExistente.setIdCita(1L);
        citaExistente.setAtendida(true); // Cita ya atendida

        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaExistente));

        assertThrows(IllegalArgumentException.class, () -> {
            servicioCita.modificarCita(1L, getHoraHabil(), TipoCita.Consulta, "Juan", "contacto");
        }, "No debe permitir modificar una cita atendida.");
    }

    @Test
    void modificarCita_FallaModificarCitaAPasado() {
        Cita citaExistente = new Cita();
        citaExistente.setIdCita(1L);
        citaExistente.setAtendida(false);

        // Intentar modificar la cita a una hora pasada

        LocalDateTime fechaHoraPasada = LocalDateTime.now().minusHours(1);

        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaExistente));

        assertThrows(IllegalArgumentException.class, () -> {
            servicioCita.modificarCita(1L, fechaHoraPasada, TipoCita.Consulta, "Juan", "5512345678");
        }, "No debe permitir modificar la cita a una fecha/hora pasada.");
    }

    @Test
    void modificarCita_FallaSolapamientoConOtraCita() {
        Cita citaExistente = new Cita();
        citaExistente.setIdCita(1L);
        citaExistente.setAtendida(false);

        // Otra cita que se solapa y no es la que se está modificando

        Cita citaSolapada = new Cita();
        citaSolapada.setIdCita(2L);

        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaExistente));

        // El repositorio regresa una cita diferente que se solapa

        when(citaRepository.findCitasOverlap(any(), any())).thenReturn(Arrays.asList(citaSolapada));

        assertThrows(IllegalArgumentException.class, () -> {
            servicioCita.modificarCita(1L, getHoraHabil(), TipoCita.Consulta, "Juan", "5512345678");
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

    @Test
    void eliminarCita_FallaCitaAtendida() {

        Cita citaExistente = new Cita();
        citaExistente.setIdCita(1L);
        citaExistente.setAtendida(true);

        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaExistente));

        assertThrows(IllegalArgumentException.class, () -> {
            servicioCita.eliminarCita(1L);
        }, "No debe permitir eliminar una cita atendida.");
    }
}