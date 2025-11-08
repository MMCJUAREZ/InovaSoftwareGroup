package mx.uam.ayd.proyecto.presentacion.gestionarMascotas;

import java.io.IOException;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.Mascota;

@Component
public class VentanaGestionarMascotas {
    
    private Stage stage;
    private ControlGestionarMascotas control;
    private ObservableList<Mascota> mascotasData = FXCollections.observableArrayList();

    @FXML
    private TableView<Mascota> tableMascotas;
    @FXML
    private TableColumn<Mascota, Long> idColumn;
    @FXML
    private TableColumn<Mascota, String> nombreColumn;
    @FXML
    private TableColumn<Mascota, String> especieColumn;
    @FXML
    private TableColumn<Mascota, String> razaColumn;
    @FXML
    private TableColumn<Mascota, Integer> edadColumn;
    @FXML
    private TableColumn<Mascota, String> sexoColumn;
    @FXML
    private TableColumn<Mascota, Boolean> vacunasColumn;

    public void muestra(ControlGestionarMascotas control, Cliente cliente) {
        this.control = control;
        
        if (stage == null) {
            try {
                stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-gestionar-mascotas.fxml"));
                loader.setController(this);
                Scene scene = new Scene(loader.load(), 700, 500);
                stage.setScene(scene);

                // Configurar columnas
                idColumn.setCellValueFactory(new PropertyValueFactory<>("idMascota"));
                nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
                especieColumn.setCellValueFactory(new PropertyValueFactory<>("especie"));
                razaColumn.setCellValueFactory(new PropertyValueFactory<>("raza"));
                edadColumn.setCellValueFactory(new PropertyValueFactory<>("edad"));
                sexoColumn.setCellValueFactory(new PropertyValueFactory<>("sexo"));
                vacunasColumn.setCellValueFactory(new PropertyValueFactory<>("vacunasVigentes"));

                tableMascotas.setItems(mascotasData);
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        // Extraemos el nombre del cliente para ponerlo en el titulo
        stage.setTitle("Mascotas de: " + cliente.getNombreCompleto());
        control.actualizaListaMascotas();
        stage.show();
    }

    public void actualizaTabla(List<Mascota> mascotas) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.actualizaTabla(mascotas));
            return;
        }
        mascotasData.clear();
        mascotasData.addAll(mascotas);
    }

    @FXML
    private void handleRegistrarMascota() {
        control.solicitaRegistrarMascota();
    }

    @FXML
    private void handleEliminarMascota() {
        Mascota seleccionada = tableMascotas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            muestraAlerta(Alert.AlertType.WARNING, "Ninguna mascota seleccionada", "Por favor, selecciona una mascota de la tabla.");
        } else {
            control.solicitaEliminarMascota(seleccionada);
        }
    }

    @FXML
    private void handleCerrar() {
        stage.close();
    }
    
    public void muestraAlerta(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public boolean muestraConfirmacion(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }
}