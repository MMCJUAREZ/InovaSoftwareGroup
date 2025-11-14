package mx.uam.ayd.proyecto.negocio;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @file TestCorreo.java
 * @brief Ejecuta una prueba automática de envío de correo al iniciar la aplicación.
 *
 * Esta clase se ejecuta automáticamente al arrancar Spring Boot y envía
 * un correo de prueba usando el servicio ServicioCorreo.
 */

@Component
public class TestCorreo  implements CommandLineRunner{

    /** Servicio engargado del envio de correo.*/
    private final ServicioCorreo servicioCorreo;

    /**
     * @brief Constructor que inyecta el servicio de correo.
     * @param servicioCorreo instancia de ServicioCorreo
     */
    public TestCorreo(ServicioCorreo servicioCorreo){
        this.servicioCorreo = servicioCorreo;
    }

    /**
     * @brief Método que se ejecuta al iniciar la aplicación.
     *
     * Envía un correo de prueba para verificar el correcto funcionamiento
     * del servicio de notificaciones.
     */
    @Override
    public void run(String... args) throws Exception{
        servicioCorreo.enviarCorreo(
                "kroketa.system@gmail.com",
                "Prueba de notificacion automatica",
                "Hola, este es un correo de prueba desde el sistema Kroketa  "
        );
    }

}
