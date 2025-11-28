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
 * Controlador principal para el flujo de registro de cirugías.
 *
 * <p>Esta clase coordina la interacción entre la capa de presentación (ventanas)
 * y la capa de negocio (servicios) para la Historia de Usuario de Registro de Cirugías.
 * Gestiona la navegación entre la selección de paciente y el formulario de datos,
 * además de manejar las respuestas exitosas o errores de validación.</p>
 *
 * @author InovaSoftwareGroup
 * @since 2025-11-20
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
	 * Inicia la historia de usuario.
	 *
	 * <p>Muestra la primera ventana para que el usuario seleccione al cliente
	 * y a la mascota. Pasa las referencias de los servicios necesarios para
	 * llenar las listas desplegables.</p>
	 */
	public void inicia() {
		ventanaSeleccion.muestra(this, servicioCliente, servicioMascota);
	}

	/**
	 * Método de transición llamado cuando se selecciona un paciente.
	 *
	 * <p>Se ejecuta desde la VentanaSeleccionPaciente una vez que el usuario
	 * ha elegido una mascota válida. Cierra la ventana de selección y abre
	 * el formulario de registro de cirugía.</p>
	 *
	 * @param mascota La mascota seleccionada por el usuario.
	 */
	public void mascotaSeleccionada(Mascota mascota) {
		ventanaSeleccion.cierra();
		ventanaDatos.muestra(this, mascota);
	}

	/**
	 * Coordina el registro de la cirugía en el sistema.
	 *
	 * <p>Recibe todos los datos capturados en la VentanaDatosCirugia e invoca
	 * al servicio de negocio para persistir la información.
	 * Maneja las excepciones de negocio (IllegalArgumentException) mostrando
	 * alertas de error, o mensajes de éxito si todo sale bien.</p>
	 *
	 * @param mascota La mascota a la que se realiza la cirugía.
	 * @param fecha Fecha del procedimiento.
	 * @param tipo Tipo de cirugía.
	 * @param desc Descripción detallada.
	 * @param consultas Notas de consultas asociadas.
	 * @param trat Tratamientos aplicados.
	 * @param obs Observaciones generales.
	 */
	public void registrarCirugia(Mascota mascota, LocalDate fecha, String tipo, String desc,
								 String consultas, String trat, String obs) {
		try {
			// Intenta realizar el registro en la capa de negocio
			servicioCirugia.registrarCirugia(mascota, fecha, tipo, desc, consultas, trat, obs);

			// Si no hubo error, notifica éxito y cierra la ventana
			ventanaDatos.muestraAlerta(Alert.AlertType.INFORMATION, "Éxito", "Cirugía registrada correctamente");
			ventanaDatos.cierra();

		} catch (IllegalArgumentException e) {
			// Captura errores de validación (datos vacíos, fechas inválidas, etc)
			ventanaDatos.muestraAlerta(Alert.AlertType.ERROR, "Error de validación", e.getMessage());
		} catch (Exception e) {
			// Captura cualquier otro error inesperado
			ventanaDatos.muestraAlerta(Alert.AlertType.ERROR, "Error", "Error al guardar: " + e.getMessage());
		}
	}
}