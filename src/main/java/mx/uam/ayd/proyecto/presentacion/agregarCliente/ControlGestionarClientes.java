package mx.uam.ayd.proyecto.presentacion.agregarCliente;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javafx.scene.control.Alert;
import mx.uam.ayd.proyecto.negocio.ServicioCliente;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.presentacion.seleccionarMembresia.VentanaSeleccionarMembresia;
import mx.uam.ayd.proyecto.presentacion.gestionarMascotas.ControlGestionarMascotas;

@Component
public class ControlGestionarClientes {
    
    @Autowired
    private ServicioCliente servicioCliente;
    
    @Autowired
    private VentanaGestionarClientes ventanaGestionar;
    
    @Autowired
    private VentanaRegistrarCliente ventanaRegistrar;

    @Autowired
    private VentanaSeleccionarMembresia ventanaMembresia;

    @Autowired
    private ControlGestionarMascotas controlGestionarMascotas;

    /**
     * Inicia el caso de uso
     */
    public void inicia() {
        ventanaGestionar.muestra(this);
    }

    /**
     * Pide al servicio los clientes y los manda a la ventana
     */
    public void actualizaListaClientes() {
        List<Cliente> clientes = servicioCliente.recuperarCliente();
        ventanaGestionar.actualizaTabla(clientes);
    }

    /**
     * Muestra la ventana de registro
     */
    public void solicitaRegistrarCliente() {
        ventanaRegistrar.muestra(this);
    }

    /**
     * Intenta registrar un cliente (es llamado desde VentanaRegistrarCliente)
     */
    public void registraCliente(String nombre, String telefono, String correo, String direccion) {
        try {
            servicioCliente.registraCliente(nombre, telefono, correo, direccion);
            
            // Si tiene éxito
            ventanaRegistrar.cierra(); // cerramos el formulario
            ventanaGestionar.muestraAlerta(Alert.AlertType.INFORMATION, "Éxito", "Cliente registrado exitosamente");
            actualizaListaClientes(); // con esto actualizamos la tabla de manera automatica
            
        } catch (IllegalArgumentException ex) {
            throw ex;
        }
    }

    /**
     * Pide confirmación y elimina un cliente
     */
    public void solicitaEliminarCliente(Cliente cliente) {
        boolean confirmado = ventanaGestionar.muestraConfirmacion(
            "Confirmar Eliminación", 
            "¿Estás seguro de que deseas eliminar al cliente: " + cliente.getNombreCompleto() + "?"
        );
        
        if (confirmado) {
            try {
                servicioCliente.eliminaCliente(cliente.getIdCliente());
                ventanaGestionar.muestraAlerta(Alert.AlertType.INFORMATION, "Éxito", "Cliente eliminado exitosamente");
                actualizaListaClientes();
            } catch (IllegalArgumentException ex) {
                ventanaGestionar.muestraAlerta(Alert.AlertType.ERROR, "Error", ex.getMessage());
            }
        }
    }

    public void asignarMembresia(Cliente cliente){
        ventanaMembresia.initializeUI(this, cliente);
    }

    /**
     * Inicia el flujo para gestionar las mascotas del cliente seleccionado
     */
    public void gestionarMascotas(Cliente cliente) {
        controlGestionarMascotas.inicia(cliente);
    }

}