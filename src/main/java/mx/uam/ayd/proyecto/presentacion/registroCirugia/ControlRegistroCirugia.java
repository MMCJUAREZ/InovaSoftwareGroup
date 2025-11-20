package mx.uam.ayd.proyecto.presentacion.registroCirugia;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioCliente;
import mx.uam.ayd.proyecto.negocio.ServicioMascota;
import mx.uam.ayd.proyecto.negocio.ServicioCirugia;
import mx.uam.ayd.proyecto.negocio.modelo.Mascota;
import javafx.scene.control.Alert;
import java.time.LocalDate;

/**
 * @file ControlRegistroCirugia.java
 * @brief Controlador principal para el flujo de registro de cirugias.
 *
 * Esta clase coordina la interaccion entre la capa de presentacion (ventanas)
 * y la capa de negocio (servicios) para la Historia de Usuario de Registro de Cirugias.
 * Gestiona la navegacion entre la seleccion de paciente y el formulario de datos,
 * ademas de manejar las respuestas exitosas o errores de validacion.
 *
 * @author InovaSoftwareGroup
 * @date 2025-11-20
 */
@Component
public class ControlRegistroCirugia {

    @Autowired
    private ServicioCliente servicioCliente;
    @Autowired
    private ServicioMascota servicioMascota;
    @Autowired
    private ServicioCirugia servicioCirugia;

    @Autowired
    private VentanaSeleccionPaciente ventanaSeleccion;
    @Autowired
    private VentanaDatosCirugia ventanaDatos;

    /**
     * @brief Inicia la historia de usuario.
     * * Muestra la primera ventana para que el usuario seleccione al cliente
     * y a la mascota. Pasa las referencias de los servicios necesarios para
     * llenar las listas desplegables.
     */
    public void inicia() {
        ventanaSeleccion.muestra(this, servicioCliente, servicioMascota);
    }

    /**
     * @brief Metodo de transicion llamado cuando se selecciona un paciente.
     *
     * Se ejecuta desde la VentanaSeleccionPaciente una vez que el usuario
     * ha elegido una mascota valida. Cierra la ventana de seleccion y abre
     * el formulario de registro de cirugia.
     *
     * @param mascota La mascota seleccionada por el usuario.
     */
    public void mascotaSeleccionada(Mascota mascota) {
        ventanaSeleccion.cierra();
        ventanaDatos.muestra(this, mascota);
    }

    /**
     * @brief Coordina el registro de la cirugia en el sistema.
     *
     * Recibe todos los datos capturados en la VentanaDatosCirugia e invoca
     * al servicio de negocio para persistir la informacion.
     * Maneja las excepciones de negocio (IllegalArgumentException) mostrando
     * alertas de error, o mensajes de exito si todo sale bien.
     *
     * @param mascota La mascota a la que se realiza la cirugia.
     * @param fecha Fecha del procedimiento.
     * @param tipo Tipo de cirugia.
     * @param desc Descripcion detallada.
     * @param consultas Notas de consultas asociadas.
     * @param trat Tratamientos aplicados.
     * @param obs Observaciones generales.
     */
    public void registrarCirugia(Mascota mascota, LocalDate fecha, String tipo, String desc, 
                                 String consultas, String trat, String obs) {
        try {
            // Intenta realizar el registro en la capa de negocio
            servicioCirugia.registrarCirugia(mascota, fecha, tipo, desc, consultas, trat, obs);
            
            // Si no hubo error, notifica exito y cierra la ventana
            ventanaDatos.muestraAlerta(Alert.AlertType.INFORMATION, "Éxito", "Cirugía registrada correctamente");
            ventanaDatos.cierra();
            
        } catch (IllegalArgumentException e) {
            // Captura errores de validacion (datos vacios, fechas invalidas, etc)
            ventanaDatos.muestraAlerta(Alert.AlertType.ERROR, "Error de validación", e.getMessage());
        } catch (Exception e) {
            // Captura cualquier otro error inesperado
            ventanaDatos.muestraAlerta(Alert.AlertType.ERROR, "Error", "Error al guardar: " + e.getMessage());
        }
    }
}