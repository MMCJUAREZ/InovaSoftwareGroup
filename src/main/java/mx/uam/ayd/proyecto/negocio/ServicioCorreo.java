package mx.uam.ayd.proyecto.negocio;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.Mascota;
import mx.uam.ayd.proyecto.negocio.modelo.Hospedaje;

/**
 * @file ServicioCorreo.java
 * @brief Servicio para enviar correos electronicos.
 */

@Service
public class ServicioCorreo {

    private final JavaMailSender mailSender;

    /**
     * @brief Inyecta el componente de envio de correo.
     * @param mailSender
     */

    public ServicioCorreo(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * @brief Envia un correo simple.
     * @param destino destinatario correo de destino
     * @param asunto asunto del mensaje.
     * @param mensajeTexto mensaje contenido del mensaje
     */

    public void enviarCorreo(String destino, String asunto, String mensajeTexto) {
        try {

            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(destino);
            mensaje.setSubject(asunto);
            mensaje.setText(mensajeTexto);
            mailSender.send(mensaje);
            System.out.println("Correo enviado correctamente a " + destino);
        } catch (Exception e) {
            System.err.println("Error al enviar correo: " + e.getMessage());
        }
    }

    /**
     * Envia confirmacion personalizada de hospedaje
     */
    public void enviarCorreoConfirmacionHospedaje(Cliente cliente, Mascota mascota, Hospedaje hospedaje){

        String asunto = "Confirmacion de hospedaje de" + mascota.getNombre();
        String mensaje = "¡Hola" + cliente.getNombreCompleto() + "!\n\n " +
                "Tu mascota *" + mascota.getNombre()+ "* ha sido registrada correctamente para hospedaje. \n\n" +
                " **Fecha de entrada:** " + hospedaje.getFechaEntrada() + "`\n" +
                " **Fecha de salida:** " + hospedaje.getFechaSalida() + "\n" +
                " **Observaciones:**" +
                (hospedaje.getObservaciones() != null? hospedaje.getObservaciones() : "Sin observaciones") +
                "\n\n" +
                "Gracias por confiar en Kroketa para cuidar a tu compañero peludo \n\n" +
                "Atentamente, \n" +
                "Equipo Kroketa \uD83D\uDC36\uD83D\uDC31";

        enviarCorreo(cliente.getCorreoElectronico(), asunto, mensaje);

    }
}
