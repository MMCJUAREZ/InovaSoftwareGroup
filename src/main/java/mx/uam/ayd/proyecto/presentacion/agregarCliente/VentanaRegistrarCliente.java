package mx.uam.ayd.proyecto.presentacion.agregarCliente;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class VentanaRegistrarCliente {

    private Stage stage;
    private ControlGestionarClientes control;

    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textFieldTelefono;
    @FXML
    private TextField textFieldCorreo;
    @FXML
    private TextField textFieldDireccion;

    public VentanaRegistrarCliente() {
        // Constructor vacío
    }

    public void muestra(ControlGestionarClientes control) {
        this.control = control;
        
        if (stage == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-registrar-cliente.fxml"));
                loader.setController(this);
                VBox root = loader.load();
                Scene scene = new Scene(root);
                stage = new Stage();
                stage.setScene(scene);
                stage.setTitle("Registrar Nuevo Cliente");
                stage.initModality(Modality.APPLICATION_MODAL);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        // Limpiar campos antes de mostrar
        textFieldNombre.clear();
        textFieldTelefono.clear();
        textFieldCorreo.clear();
        textFieldDireccion.clear();
        
        stage.showAndWait();
    }

    @FXML
    private void handleGuardar() {
        try {
            // Llamamos al control principal para que intente registrar
            control.registraCliente(
                textFieldNombre.getText(),
                textFieldTelefono.getText(),
                textFieldCorreo.getText(),
                textFieldDireccion.getText()
            );
            
            // Si tiene éxito, el control el mensaje y cerrara la ventana
            
        } catch (IllegalArgumentException ex) {
            muestraAlerta(Alert.AlertType.ERROR, "Error de Validación", ex.getMessage());
        }
    }

    @FXML
    private void handleCancelar() {
        stage.close();
    }
    
    public void cierra() {
        stage.close();
    }

    public void muestraAlerta(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}