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
 * @file VentanaDatosCirugia.java
 * @brief Controlador de la interfaz grafica para el registro de datos de cirugia.
 *
 * Esta clase pertenece a la capa de presentacion y gestiona la ventana donde
 * el veterinario captura la informacion detallada de la cirugia y el historial clinico.
 * Recibe la mascota seleccionada en la ventana anterior y envia los datos capturados
 * al controlador para su validacion y registro.
 *
 * @author InovaSoftwareGroup
 * @date 2025-11-20
 */
@Component
public class VentanaDatosCirugia {
    
    private Stage stage;
    private ControlRegistroCirugia control;
    private Mascota mascotaActual;

    // Elementos de la interfaz grafica inyectados desde el FXML
    @FXML private Label lblPaciente;
    @FXML private DatePicker dateFecha;
    @FXML private TextField txtTipo;
    @FXML private TextArea txtDescripcion;
    @FXML private TextArea txtConsultas;
    @FXML private TextArea txtTratamientos;
    @FXML private TextArea txtObservaciones;

    /**
     * @brief Inicializa y muestra la ventana de formulario.
     *
     * Carga el archivo FXML si es la primera vez que se abre.
     * Configura la etiqueta con el nombre del paciente y su dueno.
     * Establece la fecha actual por defecto y limpia los campos anteriores.
     *
     * @param control El controlador que gestiona el flujo de este caso de uso.
     * @param mascota La mascota seleccionada previamente a la cual se le hara el registro.
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
        // Se muestra informacion de contexto (Nombre mascota y dueno)
        lblPaciente.setText("Paciente: " + mascota.getNombre() + " (Dueño: " + mascota.getCliente().getNombreCompleto() + ")");
        dateFecha.setValue(LocalDate.now()); // Poner fecha de hoy por defecto
        
        stage.show();
    }

    /**
     * @brief Limpia el contenido de todos los campos de texto del formulario.
     */
    private void limpiar() {
        txtTipo.clear();
        txtDescripcion.clear();
        txtConsultas.clear();
        txtTratamientos.clear();
        txtObservaciones.clear();
    }

    /**
     * @brief Metodo invocado al presionar el boton Guardar.
     * * Recolecta la informacion de los componentes de la vista y delega
     * la operacion de registro al controlador.
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
     * @brief Metodo invocado al presionar el boton Cancelar.
     * Cierra la ventana sin realizar ninguna accion.
     */
    @FXML
    private void handleCancelar() {
        stage.close();
    }

    /**
     * @brief Cierra la ventana programaticamente.
     * Util cuando la operacion se completo exitosamente desde el control.
     */
    public void cierra() { stage.close(); }

    /**
     * @brief Muestra un cuadro de dialogo con un mensaje informativo o de error.
     *
     * @param type El tipo de alerta (INFORMATION, ERROR, WARNING, etc).
     * @param title El titulo de la ventana de alerta.
     * @param msg El mensaje a mostrar al usuario.
     */
    public void muestraAlerta(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}