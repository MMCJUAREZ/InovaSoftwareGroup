package mx.uam.ayd.proyecto.presentacion.seleccionarMembresia;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import mx.uam.ayd.proyecto.negocio.ServicioCliente;
import mx.uam.ayd.proyecto.negocio.ServicioMembresia;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.TipoMembresia;


@Component
public class ControlSeleccionarMembresia {
    
    @Autowired
    private final ServicioMembresia servicioMembresia;

    @Autowired
    private final ServicioCliente servicioCliente;

    @Autowired 
    private final VentanaSeleccionarMembresia ventanaMembresia;


    public ControlSeleccionarMembresia(VentanaSeleccionarMembresia ventanaMembresia,
                                        ServicioMembresia servicioMembresia,
                                        ServicioCliente servicioCliente){
            this.ventanaMembresia = ventanaMembresia;
            this.servicioMembresia = servicioMembresia;
            this.servicioCliente = servicioCliente;
    }

    /**
     * Método que se ejecuta después de la construcción del bean
     * y realiza la conexión bidireccional entre el control y la ventana
     */
    @PostConstruct
    public void init(){
        ventanaMembresia.setControlSeleccionarMembresia(this);
    }


    /**
     * Inicia caso de uso para asignar membresia a un cliente
     * @param cliente a quien se le asigna
     * @param tipo de la membresia
     */

    public void asignarMembresia(TipoMembresia tipo, Cliente cliente){
        init();
        if(servicioCliente.asignarMembresia(tipo, cliente)){
            ventanaMembresia.muestraDialogoConMensaje("Membresia asignada exitosamente");
        }else{
            ventanaMembresia.muestraDialogoConMensaje("ERROR: El cliente no cuenta con el monto necesario");
        }
        termina();
    }


    /**
     * Cierra la ventana de asignación.
     */
    private void termina(){
        ventanaMembresia.setVisible(false);
    }
}
