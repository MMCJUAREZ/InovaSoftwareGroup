package mx.uam.ayd.proyecto.presentacion.seleccionarMembresia;

import org.springframework.stereotype.Component;
import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.TipoMembresia;
import mx.uam.ayd.proyecto.presentacion.agregarCliente.ControlGestionarClientes;


@Component
public class VentanaSeleccionarMembresia {
    
    private Stage stage;
    private boolean initialized = false; 
    private ControlSeleccionarMembresia controlMembresia;
    private ControlGestionarClientes controlClientes;
    
    private Cliente cliente;

    /**
     * Define el ID del paciente al que se asociará la captura.
     * @param pacienteID identificador del paciente
     */
    public void setcliente(Cliente cliente) {
        this.cliente=cliente;
    }

    public VentanaSeleccionarMembresia(){
        //Constructor vacio
    }

    /**
     * Asigna el controlador que gestionará las acciones de esta ventana.
     * @param controlSeleccionarMembresia controlador de la ventana
     */
    public void setControlSeleccionarMembresia(ControlSeleccionarMembresia controlMembresia) {
        this.controlMembresia = controlMembresia;
    }    

    public void initializeUI(ControlGestionarClientes controlClientes,Cliente cliente){
        this.cliente= cliente;
        this.controlClientes = controlClientes;

        if (initialized) {
            return;
        }
        if (stage == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-seleccionar-membresia.fxml"));
                loader.setController(this);
                VBox root = loader.load(); 
                Scene scene = new Scene(root);
                stage = new Stage();
                stage.setScene(scene);
                stage.setTitle("Membresia");
                stage.initModality(Modality.APPLICATION_MODAL);
                initialized = true;
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Muestra un diálogo informativo con el mensaje indicado.
     * @param mensaje texto a mostrar
     */
    public void muestraDialogoConMensaje(String mensaje) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestraDialogoConMensaje(mensaje));
            return;
        }

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Cambia la visibilidad de la ventana.
     * @param visible {@code true} para mostrar; {@code false} para ocultar
     */
    public void setVisible(boolean visible) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.setVisible(visible));
            return;
        }

        if (!initialized) {
            if (visible) {
                initializeUI(controlClientes, cliente);
            } else {
                return;
            }
        }

        if (visible) {
            stage.show();
        } else {
            stage.hide();
        }
    }   

    @FXML
    private void handleSeleccionarStandard() {
        controlMembresia.asignarMembresia(TipoMembresia.Standard, cliente);
        stage.close();
    }

    @FXML
    private void handleSeleccionarPlatinum() {
        stage.close();
    }


}
