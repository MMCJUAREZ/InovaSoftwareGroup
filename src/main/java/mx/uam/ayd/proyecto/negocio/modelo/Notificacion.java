package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/*Para que vean la interaccion del mensaje en el correo, hay que abirir el gmail
el correo es: kroketa.system@gmail.com y la contraseña es:
KroketaSystem2025, para que se vea el mensaje hay que registrar una mascota al hospedaje y llega el correo.*/

/**
 * @file Notificacion.java
 * @brief Representa una notificación enviada al dueño de una mascota.
 *
 * Esta entidad almacena el mensaje, la fecha de envío, el destinatario
 * y la mascota asociada a la notificación.
 */
@Data
@Entity
public class Notificacion {

    /** Identificador único de la notificación. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNotificacion;

    /** Contenido del mensaje enviado. */
    private String mansaje;

    /** Fecha y hora en que se envió la notificación. */
    private LocalDateTime fechaEnvio;

    /** Correo electrónico del destinatario. */
    private String destinatarioEmail;

    /**
     * Mascota asociada a la notificación.
     * Una mascota puede tener varias notificaciones.
     */
    @ManyToOne
    @JoinColumn(name = "id_mascota")
    private Mascota mascota;
}
