package mx.uam.ayd.proyecto.presentacion.gestionarMascotas;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javafx.scene.control.Alert;
import mx.uam.ayd.proyecto.negocio.ServicioMascota;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.Mascota;
import mx.uam.ayd.proyecto.negocio.ServicioNotificacion;

@Component
public class ControlGestionarMascotas {
    
    @Autowired
    private ServicioMascota servicioMascota;

    @Autowired
    private VentanaGestionarMascotas ventanaGestionar;
    
    @Autowired
    private VentanaRegistrarMascota ventanaRegistrar;

    private Cliente clientePropietario;

    @Autowired
    private ServicioNotificacion servicioNotificacion;


    public void enviarNotificacionPrueba(Mascota mascota) {
        String mensaje = "Tu mascota " + mascota.getNombre() + " ha sido alimentada y se encuentra bien 😊";
        servicioNotificacion.enviarNotificacion(mascota, mensaje);
    }

    /**
     * Inicia el caso de uso para un cliente específico
     */
    public void inicia(Cliente cliente) {
        this.clientePropietario = cliente;
        ventanaGestionar.muestra(this, cliente);
    }

    /**
     * Pide al servicio las mascotas y las manda a la ventana
     */
    public void actualizaListaMascotas() {
        List<Mascota> mascotas = servicioMascota.recuperaMascotas(clientePropietario);
        ventanaGestionar.actualizaTabla(mascotas);
    }

    /**
     * Muestra la ventana de registro
     */
    public void solicitaRegistrarMascota() {
        ventanaRegistrar.muestra(this);
    }

    /**
     * Intenta registrar una mascota
     */
    public void registraMascota(String nombre, String especie, String raza, int edad, String sexo, boolean vacunas) {
        try {
            // Usa el método sobrecargado
            servicioMascota.registraMascota(clientePropietario, nombre, especie, raza, edad, sexo, vacunas);
            
            ventanaRegistrar.cierra();
            ventanaGestionar.muestraAlerta(Alert.AlertType.INFORMATION, "Éxito", "Mascota registrada exitosamente");
            actualizaListaMascotas();
            
        } catch (IllegalArgumentException ex) {
            throw ex;
        }
    }

    /**
     * Pide confirmación y elimina una mascota
     */
    public void solicitaEliminarMascota(Mascota mascota) {
        boolean confirmado = ventanaGestionar.muestraConfirmacion(
            "Confirmar Eliminación", 
            "¿Estás seguro de que deseas eliminar a: " + mascota.getNombre() + "?"
        );
        
        if (confirmado) {
            try {
                servicioMascota.eliminaMascota(mascota.getIdMascota());
                ventanaGestionar.muestraAlerta(Alert.AlertType.INFORMATION, "Éxito", "Mascota eliminada exitosamente");
                actualizaListaMascotas();
            } catch (IllegalArgumentException ex) {
                ventanaGestionar.muestraAlerta(Alert.AlertType.ERROR, "Error", ex.getMessage());
            }
        }
    }
}