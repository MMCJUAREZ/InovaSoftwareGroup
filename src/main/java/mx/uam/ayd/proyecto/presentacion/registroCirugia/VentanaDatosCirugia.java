package mx.uam.ayd.proyecto.presentacion.registroCirugia;

import org.springframework.stereotype.Component;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.modelo.Mascota;
import java.io.IOException;
import java.time.LocalDate;

/**
 * Controlador de la interfaz gráfica para el registro de datos de cirugía.
 *
 * <p>Esta clase pertenece a la capa de presentación y gestiona la ventana donde
 * el veterinario captura la información detallada de la cirugía y el historial clínico.
 * Recibe la mascota seleccionada en la ventana anterior y envía los datos capturados
 * al controlador para su validación y registro.</p>
 *
 * @author InovaSoftwareGroup
 * @since 2025-11-20
 */
@Component
public class VentanaDatosCirugia {

	private Stage stage;
	private ControlRegistroCirugia control;
	private Mascota mascotaActual;

	// Elementos de la interfaz gráfica inyectados desde el FXML
	@FXML private Label lblPaciente;
	@FXML private DatePicker dateFecha;
	@FXML private TextField txtTipo;
	@FXML private TextArea txtDescripcion;
	@FXML private TextArea txtConsultas;
	@FXML private TextArea txtTratamientos;
	@FXML private TextArea txtObservaciones;

	/**
	 * Inicializa y muestra la ventana de formulario.
	 *
	 * <p>Carga el archivo FXML si es la primera vez que se abre.
	 * Configura la etiqueta con el nombre del paciente y su dueño.
	 * Establece la fecha actual por defecto y limpia los campos anteriores.</p>
	 *
	 * @param control El controlador que gestiona el flujo de este caso de uso.
	 * @param mascota La mascota seleccionada previamente a la cual se le hará el registro.
	 */
	public void muestra(ControlRegistroCirugia control, Mascota mascota) {
		this.control = control;
		this.mascotaActual = mascota;

		if (stage == null) {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-datos-cirugia.fxml"));
				loader.setController(this);
				stage = new Stage();
				stage.setScene(new Scene(loader.load()));
				stage.setTitle("Registro de Cirugía");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		limpiar();
		// Se muestra información de contexto (Nombre mascota y dueño)
		lblPaciente.setText("Paciente: " + mascota.getNombre() + " (Dueño: " + mascota.getCliente().getNombreCompleto() + ")");
		dateFecha.setValue(LocalDate.now()); // Poner fecha de hoy por defecto

		stage.show();
	}

	/**
	 * Limpia el contenido de todos los campos de texto del formulario.
	 */
	private void limpiar() {
		txtTipo.clear();
		txtDescripcion.clear();
		txtConsultas.clear();
		txtTratamientos.clear();
		txtObservaciones.clear();
	}

	/**
	 * Método invocado al presionar el botón Guardar.
	 *
	 * <p>Recolecta la información de los componentes de la vista y delega
	 * la operación de registro al controlador.</p>
	 */
	@FXML
	private void handleGuardar() {
		control.registrarCirugia(
			mascotaActual,
			dateFecha.getValue(),
			txtTipo.getText(),
			txtDescripcion.getText(),
			txtConsultas.getText(),
			txtTratamientos.getText(),
			txtObservaciones.getText()
		);
	}

	/**
	 * Método invocado al presionar el botón Cancelar.
	 * Cierra la ventana sin realizar ninguna acción.
	 */
	@FXML
	private void handleCancelar() {
		stage.close();
	}

	/**
	 * Cierra la ventana programáticamente.
	 * Útil cuando la operación se completó exitosamente desde el control.
	 */
	public void cierra() {
		stage.close();
	}

	/**
	 * Muestra un cuadro de diálogo con un mensaje informativo o de error.
	 *
	 * @param type El tipo de alerta (INFORMATION, ERROR, WARNING, etc).
	 * @param title El título de la ventana de alerta.
	 * @param msg El mensaje a mostrar al usuario.
	 */
	public void muestraAlerta(Alert.AlertType type, String title, String msg) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setContentText(msg);
		alert.showAndWait();
	}
}