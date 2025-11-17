package mx.uam.ayd.proyecto.presentacion.seleccionarMembresia;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import mx.uam.ayd.proyecto.negocio.ServicioCliente;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.TipoMembresia;
import mx.uam.ayd.proyecto.presentacion.agregarCliente.ControlGestionarClientes;


@Component
public class ControlSeleccionarMembresia {
    

    @Autowired
    private final ServicioCliente servicioCliente;

    @Autowired 
    private final VentanaSeleccionarMembresia ventanaMembresia;

    @Autowired
    private final ControlGestionarClientes controlCliente;


    public ControlSeleccionarMembresia(VentanaSeleccionarMembresia ventanaMembresia,
                                        ServicioCliente servicioCliente,
                                        ControlGestionarClientes controlCliente){
            this.ventanaMembresia = ventanaMembresia;
            this.servicioCliente = servicioCliente;
            this.controlCliente = controlCliente;
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
        try {
            servicioCliente.asignarMembresia(tipo, cliente);
            ventanaMembresia.muestraDialogoConMensaje("Membresía asignada exitosamente.");
            controlCliente.actualizaListaClientes();
        } catch (IllegalArgumentException ex) {
            ventanaMembresia.muestraDialogoConMensaje(ex.getMessage());
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
