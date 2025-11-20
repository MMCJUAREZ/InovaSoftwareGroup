package mx.uam.ayd.proyecto.negocio;

import mx.uam.ayd.proyecto.datos.CirugiaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cirugia;
import mx.uam.ayd.proyecto.negocio.modelo.Mascota;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicioCirugiaTest {

    @Mock
    private CirugiaRepository cirugiaRepository;

    @InjectMocks
    private ServicioCirugia servicioCirugia;

    private Mascota mascotaMock;

    @BeforeEach
    void setUp() {
        // Preparamos una mascota dummy para las pruebas
        mascotaMock = new Mascota();
        mascotaMock.setIdMascota(1L);
        mascotaMock.setNombre("Firulais");
    }

    @Test
    void testRegistrarCirugia_Exito() {
        // Datos válidos
        LocalDate fechaHoy = LocalDate.now();
        String tipo = "Esterilización";
        String descripcion = "Procedimiento de rutina.";
        String consultas = "Revisión general previa.";
        String tratamientos = "Antibióticos y reposo.";
        String observaciones = "Todo salió bien.";

        // Simulamos que el repositorio guarda y devuelve el objeto
        when(cirugiaRepository.save(any(Cirugia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Ejecución
        Cirugia resultado = servicioCirugia.registrarCirugia(
                mascotaMock, fechaHoy, tipo, descripcion, consultas, tratamientos, observaciones
        );

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(mascotaMock, resultado.getMascota());
        assertEquals(fechaHoy, resultado.getFecha());
        assertEquals(tipo, resultado.getTipoCirugia());
        
        // Verificamos que se llamó al repositorio 1 vez
        verify(cirugiaRepository, times(1)).save(any(Cirugia.class));
    }

    @Test
    void testRegistrarCirugia_MascotaNula() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> 
            servicioCirugia.registrarCirugia(null, LocalDate.now(), "Tipo", "Desc", "Cons", "Trat", "Obs")
        );
        assertEquals("La mascota es obligatoria.", ex.getMessage());
    }

    @Test
    void testRegistrarCirugia_FechaNula() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> 
            servicioCirugia.registrarCirugia(mascotaMock, null, "Tipo", "Desc", "Cons", "Trat", "Obs")
        );
        assertEquals("La fecha es obligatoria.", ex.getMessage());
    }
    
    @Test
    void testRegistrarCirugia_FechaPasada() {
        // Fecha de ayer (inválida)
        LocalDate ayer = LocalDate.now().minusDays(1);
        
        Exception ex = assertThrows(IllegalArgumentException.class, () -> 
            servicioCirugia.registrarCirugia(mascotaMock, ayer, "Tipo", "Desc", "Cons", "Trat", "Obs")
        );
        assertEquals("La fecha no puede ser anterior al día de hoy.", ex.getMessage());
    }

    @Test
    void testRegistrarCirugia_CamposVacios() {
        LocalDate hoy = LocalDate.now();
        
        // Prueba Tipo vacío
        assertThrows(IllegalArgumentException.class, () -> 
            servicioCirugia.registrarCirugia(mascotaMock, hoy, "", "Desc", "Cons", "Trat", "Obs")
        );

        // Prueba Consultas vacías
        assertThrows(IllegalArgumentException.class, () -> 
            servicioCirugia.registrarCirugia(mascotaMock, hoy, "Tipo", "Desc", "", "Trat", "Obs")
        );
        
        // Prueba Tratamientos vacíos
        assertThrows(IllegalArgumentException.class, () -> 
            servicioCirugia.registrarCirugia(mascotaMock, hoy, "Tipo", "Desc", "Cons", "   ", "Obs")
        );
    }

    @Test
    void testRegistrarCirugia_CaracteresInvalidos() {
        LocalDate hoy = LocalDate.now();
        // El caracter '@' o '#' no está permitido en tu REGEX
        String tipoInvalido = "Cirugía #1"; 

        Exception ex = assertThrows(IllegalArgumentException.class, () -> 
            servicioCirugia.registrarCirugia(mascotaMock, hoy, tipoInvalido, "Desc", "Cons", "Trat", "Obs")
        );
        
        // Verifica que el mensaje mencione el campo y el error de caracteres
        assertTrue(ex.getMessage().contains("Tipo de cirugía contiene caracteres inválidos"));
    }

    @Test
    void testRegistrarCirugia_LongitudExcedida() {
        LocalDate hoy = LocalDate.now();
        // Creamos un string de 101 caracteres (el límite es 100 para Tipo)
        String textoLargo = "A".repeat(101);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> 
            servicioCirugia.registrarCirugia(mascotaMock, hoy, textoLargo, "Desc", "Cons", "Trat", "Obs")
        );
        
        assertTrue(ex.getMessage().contains("Tipo de cirugía excede el máximo"));
    }
}