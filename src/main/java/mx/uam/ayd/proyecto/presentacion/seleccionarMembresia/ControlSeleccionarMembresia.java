package mx.uam.ayd.proyecto.presentacion.seleccionarMembresia;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import mx.uam.ayd.proyecto.negocio.ServicioCliente;
import mx.uam.ayd.proyecto.negocio.ServicioMembresia;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.TipoMembresia;
import mx.uam.ayd.proyecto.presentacion.agregarCliente.ControlGestionarClientes;
import mx.uam.ayd.proyecto.presentacion.agregarCliente.VentanaGestionarClientes;

@Component
public class ControlSeleccionarMembresia {
    
    @Autowired
    private final ServicioMembresia servicioMembresia;

    @Autowired
    private final ServicioCliente servicioCliente;

    @Autowired 
    private final VentanaSeleccionarMembresia ventanaMembresia;

    @Autowired
    private final VentanaGestionarClientes ventanaCliente;

    public ControlSeleccionarMembresia(VentanaSeleccionarMembresia ventanaMembresia,
                                        VentanaGestionarClientes ventanaCliente,
                                        ServicioMembresia servicioMembresia,
                                        ServicioCliente servicioCliente){
            this.ventanaMembresia = ventanaMembresia;
            this.ventanaCliente = ventanaCliente;
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
    public void asignarMembresia(Cliente cliente, TipoMembresia tipo){

        try{
            servicioCliente.asignarMembresia(cliente, tipo);
            ventanaMembresia.muestraDialogoConMensaje("Membresia asignada correctamente");
            termina();
        }catch(Exception e){
            ventanaMembresia.muestraDialogoConMensaje("No cumples con los requisitos");
        }
    }

    public void seleccionarMembresia(char c){
        servicioMembresia.seleccionarMembresia(c);
    }

    /**
     * Cierra la ventana de asignación.
     */
    private void termina(){
        ventanaMembresia.setVisible(false);
    }
}
