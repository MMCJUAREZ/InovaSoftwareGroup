package mx.uam.ayd.proyecto.negocio;

import java.time.LocalDateTime;

import mx.uam.ayd.proyecto.datos.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import mx.uam.ayd.proyecto.negocio.modelo.Mascota;
import mx.uam.ayd.proyecto.negocio.modelo.Notificacion;

@Service
public class ServicioNotificacion {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private JavaMailSender mailSender;


    public void enviarNotificacion(Mascota mascota, String mensaje) {
        if (mascota == null || mascota.getCliente() == null) {
            throw new IllegalArgumentException("Mascota o cliente no valido");
        }

        String destinatario = mascota.getCliente().getCorreoElectronico();

        // Esto va a guardar la notificacion en la base de datos H2
        Notificacion notificacion = new Notificacion();
        notificacion.setMascota(mascota);
        notificacion.setMansaje(mensaje);
        notificacion.setFechaEnvio(LocalDateTime.now());
        notificacion.setDestinatarioEmail(destinatario);
        notificacionRepository.save(notificacion);

        //Envia correo
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(destinatario);
        email.setSubject("Notificación sobre " + mascota.getNombre());
        email.setText(mensaje);
        mailSender.send(email);
    }
}
