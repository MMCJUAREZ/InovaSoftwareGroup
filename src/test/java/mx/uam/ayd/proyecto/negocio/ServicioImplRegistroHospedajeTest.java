package mx.uam.ayd.proyecto.negocio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import mx.uam.ayd.proyecto.datos.RegistroHospedajeRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Hospedaje;
import mx.uam.ayd.proyecto.negocio.modelo.RegistroHospedaje;


/**
 * Pruebas unitarias para ServicioImplRegistroHospedaje
 *
 * @author Mitzi
 */
public class ServicioImplRegistroHospedajeTest {

    /* Dependencia simulada (mock) */
    private RegistroHospedajeRepository registroRepo;

    /* Servicio a probar */
    private ServicioImplRegistroHospedaje servicio;

    /**
     * Configura los mocks antes de cada prueba.
     */
    @BeforeEach
    public void setUp() {
        registroRepo = Mockito.mock(RegistroHospedajeRepository.class);
        servicio = new ServicioImplRegistroHospedaje(registroRepo);
    }

    /**
     * Dado un hospedaje válido dentro del rango
     * Cuando se registra el día
     * Entonces el registro se guarda exitosamente.
     */
    @Test
    public void dadoHospedajeValido_cuandoRegistrar_entoncesGuardaExitosamente() {
        // DADO
        Hospedaje hospedaje = new Hospedaje();
        hospedaje.setFechaEntrada(LocalDate.of(2025, 1, 1));
        hospedaje.setFechaSalida(LocalDate.of(2025, 1, 5));

        LocalDateTime fecha = LocalDateTime.of(2025, 1, 3, 10, 0);

        Mockito.when(registroRepo.existsByHospedajeAndFechaRegistroBetween(
                Mockito.eq(hospedaje),
                Mockito.any(LocalDateTime.class),
                Mockito.any(LocalDateTime.class)
        )).thenReturn(false);

        RegistroHospedaje simulado = new RegistroHospedaje();
        Mockito.when(registroRepo.save(Mockito.any())).thenReturn(simulado);

        // CUANDO
        RegistroHospedaje resultado = servicio.registrar(
                hospedaje,
                "Buena",
                "Estable",
                "Tranquilo",
                "Sin observaciones",
                fecha
        );

        // ENTONCES
        Assertions.assertNotNull(resultado);
        Mockito.verify(registroRepo).save(Mockito.any());
    }

    /**
     * Dado un hospedaje donde la fecha está fuera del rango
     * Cuando se intenta registrar
     * Entonces lanza IllegalArgumentException.
     */
    @Test
    public void dadoFechaFueraDeRango_cuandoRegistrar_entoncesLanzaExcepcion() {
        // DADO
        Hospedaje hospedaje = new Hospedaje();
        hospedaje.setFechaEntrada(LocalDate.of(2025, 1, 1));
        hospedaje.setFechaSalida(LocalDate.of(2025, 1, 5));

        LocalDateTime fecha = LocalDateTime.of(2025, 1, 10, 12, 0);

        // CUANDO + ENTONCES
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> servicio.registrar(
                        hospedaje,
                        "Comida",
                        "Salud",
                        "Comportamiento",
                        "Obs",
                        fecha
                )
        );
    }

    /**
     * Dado un hospedaje con registro ya existente ese día
     * Cuando se intenta registrar otro
     * Entonces lanza IllegalArgumentException.
     */
    @Test
    public void dadoRegistroDuplicado_cuandoRegistrar_entoncesLanzaExcepcion() {
        // DADO
        Hospedaje hospedaje = new Hospedaje();
        hospedaje.setFechaEntrada(LocalDate.of(2025, 1, 1));
        hospedaje.setFechaSalida(LocalDate.of(2025, 1, 5));

        LocalDate fecha = LocalDate.of(2025, 1, 3);
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.atTime(LocalTime.MAX);

        Mockito.when(registroRepo.existsByHospedajeAndFechaRegistroBetween(
                Mockito.eq(hospedaje),
                Mockito.eq(inicio),
                Mockito.eq(fin)
        )).thenReturn(true);

        LocalDateTime fechaRegistro = fecha.atTime(10, 0);

        // CUANDO + ENTONCES
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> servicio.registrar(
                        hospedaje,
                        "A",
                        "B",
                        "C",
                        "D",
                        fechaRegistro
                )
        );
    }

    /**
     * Dado un hospedaje válido
     * Cuando se listan sus registros
     * Entonces devuelve una lista ordenada sin fallos.
     */
    @Test
    public void dadoHospedaje_cuandoListar_entoncesRetornaLista() {
        // DADO
        Hospedaje hospedaje = new Hospedaje();
        List<RegistroHospedaje> listaSimulada = List.of(new RegistroHospedaje());

        Mockito.when(registroRepo.findByHospedajeOrderByFechaRegistroDesc(hospedaje))
                .thenReturn(listaSimulada);

        // CUANDO
        List<RegistroHospedaje> resultado = servicio.listarPorHospedaje(hospedaje);

        // ENTONCES
        Assertions.assertEquals(1, resultado.size());
    }

}
