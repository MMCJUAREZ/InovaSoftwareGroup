package mx.uam.ayd.proyecto.presentacion.agregarCliente;
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
import mx.uam.ayd.proyecto.presentacion.seleccionarMembresia.ControlSeleccionarMembresia;

@Component
public class VentanaGestionarClientes {
    
    private Stage stage;
    private ControlGestionarClientes control;
    private ControlSeleccionarMembresia controlMembresia;
    private ObservableList<Cliente> clientesData = FXCollections.observableArrayList();

    @FXML
    private TableView<Cliente> tableClientes;
    @FXML
    private TableColumn<Cliente, Long> idColumn;
    @FXML
    private TableColumn<Cliente, String> nombreColumn;
    @FXML
    private TableColumn<Cliente, String> telefonoColumn;
    @FXML
    private TableColumn<Cliente, String> correoColumn;
    @FXML
    private TableColumn<Cliente, String> direccionColumn;

    /**
     * Muestra la ventana principal de gestión de clientes
     */
    public void muestra(ControlGestionarClientes control) {
        this.control = control;
        
        if (stage == null) {
            try {
                stage = new Stage();
                stage.setTitle("Gestionar Clientes");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-gestionar-clientes.fxml"));
                loader.setController(this);
                Scene scene = new Scene(loader.load(), 700, 500);
                stage.setScene(scene);

                idColumn.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
                nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
                telefonoColumn.setCellValueFactory(new PropertyValueFactory<>("telefono"));
                correoColumn.setCellValueFactory(new PropertyValueFactory<>("correoElectronico"));
                direccionColumn.setCellValueFactory(new PropertyValueFactory<>("direccion"));

                tableClientes.setItems(clientesData);
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        control.actualizaListaClientes();
        stage.show();
    }

    /**
     * Actualiza la tabla con la lista de clientes
     */
    public void actualizaTabla(List<Cliente> clientes) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.actualizaTabla(clientes));
            return;
        }
        
        clientesData.clear();
        clientesData.addAll(clientes);
    }

    @FXML
    private void handleRegistrarCliente() {
        control.solicitaRegistrarCliente();
    }

    @FXML
    private void handleEliminarCliente() {
        Cliente clienteSeleccionado = tableClientes.getSelectionModel().getSelectedItem();
        if (clienteSeleccionado == null) {
            muestraAlerta(Alert.AlertType.WARNING, "Ningún cliente seleccionado", "Por favor, selecciona un cliente de la tabla para eliminar.");
        } else {
            control.solicitaEliminarCliente(clienteSeleccionado);
        }
    }

    @FXML
    private void handleCerrar() {
        stage.close();
    }

    @FXML
    private void handleSeleccionarMembresia(){
        control.asignarMembresia();
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