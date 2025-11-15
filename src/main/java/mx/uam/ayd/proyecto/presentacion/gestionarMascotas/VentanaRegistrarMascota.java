package mx.uam.ayd.proyecto.presentacion.gestionarMascotas;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class VentanaRegistrarMascota {

    private Stage stage;
    private ControlGestionarMascotas control;

    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textFieldEspecie;
    @FXML
    private TextField textFieldRaza;
    @FXML
    private TextField textFieldEdad;
    @FXML
    private TextField textFieldSexo;
    @FXML
    private CheckBox checkBoxVacunas;

    public void muestra(ControlGestionarMascotas control) {
        this.control = control;
        
        if (stage == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-registrar-mascota.fxml"));
                loader.setController(this);
                VBox root = loader.load();
                Scene scene = new Scene(root);
                stage = new Stage();
                stage.setScene(scene);
                stage.setTitle("Registrar Nueva Mascota");
                stage.initModality(Modality.APPLICATION_MODAL);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        // Limpiar campos
        textFieldNombre.clear();
        textFieldEspecie.clear();
        textFieldRaza.clear();
        textFieldEdad.clear();
        textFieldSexo.clear();
        checkBoxVacunas.setSelected(false);
        
        stage.showAndWait();
    }

    @FXML
    private void handleGuardar() {
        try {
            int edad;
            try {
                // Validar que la edad sea un número
                edad = Integer.parseInt(textFieldEdad.getText());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("La edad debe ser un número válido");
            }

            control.registraMascota(
                textFieldNombre.getText(),
                    textFieldEspecie.getText(),
                textFieldRaza.getText(),
                edad,
                textFieldSexo.getText(),
                checkBoxVacunas.isSelected()
            );
            
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