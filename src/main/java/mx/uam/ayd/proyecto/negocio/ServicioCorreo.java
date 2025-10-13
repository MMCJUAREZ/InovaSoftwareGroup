package mx.uam.ayd.proyecto.negocio;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class ServicioCorreo {

    private final JavaMailSender mailSender;

    public ServicioCorreo(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarCorreo(String destino, String asunto, String mensajeTexto) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destino);
        mensaje.setSubject(asunto);
        mensaje.setText(mensajeTexto);
        mailSender.send(mensaje);
        System.out.println("Correo enviado a " + destino);
    }
}
