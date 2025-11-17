package mx.uam.ayd.proyecto.negocio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @brief Pruebas unitarias para la clase ServicioCorreo.
 *
 * Verifica que el servicio construya y envíe correctamente
 * un correo utilizando JavaMailSender mediante Mockito.
 */
public class ServicioCorreoTest {

    private JavaMailSender mailSender;
    private ServicioCorreo servicioCorreo;

    /**
     * @brief Configuración previa a cada prueba.
     *
     * Crea un mock de JavaMailSender y lo inyecta en el servicio,
     * evitando envíos reales de correo.
     */
    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        servicioCorreo = new ServicioCorreo(mailSender);
    }

    /**
     * @brief Verifica que enviarCorreo() construya correctamente
     *        el mensaje y llame al método send() del mailSender.
     */
    @Test
    void testEnviarCorreo() {

        String destino = "test@gmail.com";
        String asunto = "Asunto de prueba";
        String mensaje = "Mensaje de prueba";

        // Captura del mensaje enviado
        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        servicioCorreo.enviarCorreo(destino, asunto, mensaje);

        // Verifica que send() se haya llamado exactamente una vez
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage enviado = captor.getValue();

        assertEquals(destino, enviado.getTo()[0]);
        assertEquals(asunto, enviado.getSubject());
        assertEquals(mensaje, enviado.getText());
    }
}