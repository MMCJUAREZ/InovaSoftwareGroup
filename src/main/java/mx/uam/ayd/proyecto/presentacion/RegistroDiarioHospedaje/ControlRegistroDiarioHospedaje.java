package mx.uam.ayd.proyecto.presentacion.RegistroDiarioHospedaje;


import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Control encargado de iniciar y mostrar la ventana
 * para registrar el seguimineto diario de un hospedaje.
 */
@Component
public class ControlRegistroDiarioHospedaje {

    @Autowired
    private VentanaRegistroDiarioHospedaje ventanaRegistroDiarioHospedaje;

    /**
     * Inicia la ventana sin un hospedaje específico.
     * (Útil solo en casos de prueba o ventanas incompletas)
     */
    public void inicia() {

        // Mostrar ventana
        ventanaRegistroDiarioHospedaje.muestra();
    }
}
