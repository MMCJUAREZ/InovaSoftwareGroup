package mx.uam.ayd.proyecto.presentacion.Inventario;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DataFormat;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import mx.uam.ayd.proyecto.presentacion.agregarProducto.ControlAgregarProducto;
import mx.uam.ayd.proyecto.presentacion.modificarProducto.ControlModificarProducto;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component

public class VentanaInventario {

    private final ControlModificarProducto controlModificarProducto;
    //ObservableList<Producto> data = FXCollections.observableArrayList();
    private Stage stage;

    @FXML
    private TableView<Producto> tableProducto;

    @FXML
    private TableColumn<Producto, Long> idColumn;

    @FXML
    private TableColumn<Producto, String> nombreColumn;

    @FXML
    private TableColumn<Producto, DataFormat> fechaCaducidadColumn;

    @FXML
    private TableColumn<Producto, Double> precioColumn;

    @FXML
    private TableColumn<Producto, Integer> stockColumn;

    private ControlAgregarProducto controlAgregarProducto;
    private Controlinventario control;
    private boolean initialized = false;

    public VentanaInventario(ControlModificarProducto controlModificarProducto) {
        // Don't initialize JavaFX components in constructor
        this.controlModificarProducto = controlModificarProducto;
    }

    /**
     * Initialize UI components on the JavaFX application thread
     */
    private void initializeUI() {
        if (initialized) {
            return;
        }

        // Create UI only if we're on JavaFX thread
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::initializeUI);
            return;
        }

        try {
            stage = new Stage();
            stage.setTitle("Lista de productos");

            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-inventario-productos.fxml"));
            loader.setController(this);
            Scene scene = new Scene(loader.load(), 680, 400);
            stage.setScene(scene);


            // Configure columns after FXML is loaded
            idColumn.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
            nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            fechaCaducidadColumn.setCellValueFactory(new PropertyValueFactory<>("fechaCaducidad"));
            precioColumn.setCellValueFactory(new PropertyValueFactory<>("precio"));
            stockColumn.setCellValueFactory(new PropertyValueFactory<>("cantidadStock"));

            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Establece el controlador asociado a esta ventana
     *
     * @param control El controlador asociado
     */
    public void setControlinventario(Controlinventario control) {
        this.control = control;
    }

    /**
     * Muestra la ventana y carga los productos
     *
     * @param productos La lista de productos a mostrar
     */
    public void muestra(List<Producto> productos) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra(productos));
            return;
        }

        initializeUI();
        tableProducto.getItems().clear();
        ObservableList<Producto> data = FXCollections.observableArrayList(productos);
        tableProducto.setItems(data);

        stage.show();
    }

    // Enlaza con el fx:id del TextField
    @FXML
    private TextField campoIdProducto;

    // Método que se ejecuta al hacer clic en "Eliminar"
    @FXML
    private void handleEliminar(ActionEvent event) {
        // Captura el texto ingresado en el campo
        String textoId = campoIdProducto.getText();
        try {
            long id = Long.parseLong(textoId);
            // eliminar producto con ese ID de tipo long...
            control.eliminaProducto(id);
            campoIdProducto.clear();
        } catch (NumberFormatException e) {
            System.out.println("El ID ingresado no es un número válido.");
        }
    }

    @FXML
    private void handleModificar(){
        control.modificarProducto(tableProducto.getSelectionModel().getSelectedItem().getIdProducto(), tableProducto.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void handleAgregar() {
        if (control != null) {
            control.agregarProducto();
        }
    }

    // FXML Event Handlers

    @FXML
    private void handleCerrar() {
        stage.close();
    }


    public void muestraMensaje(String s) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Mensaje");
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();
    }

}
