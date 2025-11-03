package mx.uam.ayd.proyecto.presentacion.registroHospedaje;


import javafx.fxml.FXML;
import javafx.scene.control.*;
import mx.uam.ayd.proyecto.negocio.modelo.Mascota;
import org.springframework.stereotype.Component;
import mx.uam.ayd.proyecto.negocio.ServicioMascota;

/**
 * @file VentanaRegistroHospedaje.java
 * @brief Controlador de la interfaz gráfica para el registro de hospedaje.
 *
 * Esta clase pertenece a la capa de presentación (MVC) y actúa como el
 * controlador asociado al archivo FXML <code>ventana-registro-hospedaje.fxml</code>.
 *
 * <p>Permite registrar una mascota en el sistema, validando los campos del
 * formulario e interactuando con el servicio de negocio
 * {@link ServicioMascota} para persistir los datos.</p>
 *
 * <p>Utiliza la inyección de dependencias de Spring y la estructura de eventos
 * de JavaFX para manejar la interacción del usuario.</p>
 *
 * @author Mitzi
 * @date 2025-11-01
 */
@Component
public class VentanaRegistroHospedaje {

    @FXML private TextField txtNombre;
    @FXML private TextField txtRaza;
    @FXML private TextField txtEspecie;
    @FXML private TextField txtEdad;
    @FXML private ComboBox<String> cmbSexo;
    @FXML private CheckBox chkVacunas;

    private ServicioMascota servicioMascota;
    private ControlRegistroHospedaje controlRegistroHospedaje;

    public void setServicioMascota(ServicioMascota servicioMascota) {
        this.servicioMascota = servicioMascota;
    }

    public void setControlRegistroHospedaje(ControlRegistroHospedaje controlRegistroHospedaje){
        this.controlRegistroHospedaje = controlRegistroHospedaje;
    }

    @FXML
    void registrarMascota() {
        try {
            String nombre = txtNombre.getText();
            String raza = txtRaza.getText();
            String especie = txtEspecie.getText();
            String sexo = cmbSexo.getValue();
            boolean vacunas = chkVacunas.isSelected();

            if (nombre.isEmpty() || raza.isEmpty() || especie.isEmpty() || sexo == null) {
                mostrarAlerta("Campos incompletos", "Por favor, llena todos los campos obligatorios.");
                return;
            }

            int edad;
            try {
                edad = Integer.parseInt(txtEdad.getText());
            } catch (NumberFormatException e) {
                mostrarAlerta("Edad inválida", "Por favor, introduce un número válido para la edad.");
                return;
            }

            // Verificar que el servicio esté disponible
            if (servicioMascota == null) {
                mostrarAlerta("Error interno", "No se ha conectado el servicio de mascotas.");
                return;
            }

            Mascota mascota = servicioMascota.registrarMascota(nombre, raza, especie, edad, sexo, vacunas);
            mostrarAlerta("Éxito", "Mascota registrada correctamente:\nID: " + mascota.getIdMascota() + "\nNombre: " + mascota.getNombre());
            limpiarCampos();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Ocurrió un error al registrar la mascota: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtRaza.clear();
        txtEspecie.clear();
        txtEdad.clear();
        cmbSexo.setValue(null);
        chkVacunas.setSelected(false);
    }
}
