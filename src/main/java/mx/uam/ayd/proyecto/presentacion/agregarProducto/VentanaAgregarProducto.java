package mx.uam.ayd.proyecto.presentacion.agregarProducto;

import mx.uam.ayd.proyecto.negocio.modelo.TipoProducto;
import mx.uam.ayd.proyecto.negocio.modelo.UnidadProducto;
import mx.uam.ayd.proyecto.negocio.modelo.MarcaProducto;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextFormatter;
import java.util.function.UnaryOperator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DateCell;
import javafx.util.Callback;
import java.time.LocalDate;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Clase encargada de manejar la ventana de agregar producto en la interfaz gráfica.
 * <
 * Contiene la lógica para inicializar la UI, mostrar la ventana, validar entradas
 * y comunicarse con el controlador
 * Se asegura de que todas las operaciones gráficas se realicen en el hilo de JavaFX.
 */

@Component
public class VentanaAgregarProducto{
    private Stage stage;
    private ControlAgregarProducto control;


    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtCantidad;

    @FXML
    private TextField txtPrecio;

    @FXML
    private ComboBox<TipoProducto> cmbTipo;

    @FXML
    private ComboBox<UnidadProducto> cmbUnidad;

    @FXML
    private ComboBox<MarcaProducto> cmbMarca;

    @FXML
    private DatePicker dtpFechaCaducidad;

    @FXML
    private void initialize() {
        // Deshabilitar fechas anteriores a partir de una semana
        dtpFechaCaducidad.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                if (item.isBefore(LocalDate.now().plusWeeks(1))) {
                    setDisable(true);
                    setStyle("-fx-background-color: #cccccc;");
                }
            }
        });
    }

    private boolean initialized = false;

    public VentanaAgregarProducto() {
        // Don't initialize JavaFX components in constructor
    }

    /**
     * Inicializa la interfaz de usuario cargando el FXML y configurando
     * validaciones en los campos de texto.
     *
     * Este método se asegura de ejecutarse en el hilo de JavaFX.
     * Si la UI ya fue inicializada previamente, no se vuelve a crear.
     */
    private void initializeUI() {
        // Evita inicializar dos veces
        if (initialized) {
            return;
        }

        // Verifica que el código se ejecute en el hilo de JavaFX
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::initializeUI);
            return;
        }

        try {
            // Crea la ventana
            stage = new Stage();
            stage.setTitle("Agregar producto");

            // Carga el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-agregar-producto.fxml"));
            loader.setController(this);
            Scene scene = new Scene(loader.load(), 700, 500);
            System.out.println("FXML cargado correctamente");
            stage.setScene(scene);

            // Validación: solo números enteros en txtCantidad
            txtCantidad.textProperty().addListener((obs, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    txtCantidad.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });

            // Validación: números decimales con un solo punto en txtPrecio
            UnaryOperator<TextFormatter.Change> filter = change -> {
                String newText = change.getControlNewText();
                if (newText.matches("\\d*\\.?\\d*")) { // vacío o número decimal válido
                    return change;
                }
                return null;
            };
            TextFormatter<String> textFormatter = new TextFormatter<>(filter);
            txtPrecio.setTextFormatter(textFormatter);

            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Asigna el controlador lógico ControlAgregarProducto para manejar las acciones de la ventana.
     *
     * @param control Controlador de agregar producto.
     */
    public void setControlAgregarProducto(ControlAgregarProducto control) {
        this.control = control;
    }

    /**
     * Muestra la ventana de agregar producto.
     *
     * Limpia todos los campos y carga las opciones en los ComboBox.
     * Se asegura de ejecutarse en el hilo de JavaFX.
     */
    public void muestra() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::muestra);
            return;
        }

        initializeUI();

        // Limpia campos
        txtNombre.setText("");
        txtCantidad.setText("");
        txtPrecio.setText("");

        // Carga opciones en los ComboBox
        cmbTipo.setItems(FXCollections.observableArrayList(TipoProducto.values()));
        cmbUnidad.setItems(FXCollections.observableArrayList(UnidadProducto.values()));
        cmbMarca.setItems(FXCollections.observableArrayList(MarcaProducto.values()));

        // Deja los ComboBox sin selección inicial
        cmbTipo.getSelectionModel().clearSelection();
        cmbUnidad.getSelectionModel().clearSelection();
        cmbMarca.getSelectionModel().clearSelection();

        stage.show();
    }

    /**
     * Muestra un cuadro de diálogo informativo con un mensaje.
     *
     * @param mensaje Texto a mostrar en el cuadro de diálogo.
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
     * Controla la visibilidad de la ventana.
     *
     * @param visible {@code true} para mostrar la ventana, {@code false} para ocultarla.
     */
    public void setVisible(boolean visible) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.setVisible(visible));
            return;
        }

        if (!initialized) {
            if (visible) {
                initializeUI();
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

    /**
     * Maneja la acción del botón "Agregar" llama al controlador para
     * guardar los productos en la Base de datos
     *
     * Valida que los campos no sean nulos y llama al controlador para agregar el producto.
     */
    @FXML
    private void handleAgregar() {
        if (cmbUnidad == null || cmbTipo == null || cmbMarca == null ||
                txtCantidad == null || txtNombre == null || txtPrecio == null) {
            muestraDialogoConMensaje("Llene los campos obligatorios");
        } else {
            control.agregarProducto(
                    txtNombre.getText(),
                    cmbTipo.getValue(),
                    cmbMarca.getValue(),
                    Double.parseDouble(txtPrecio.getText()),
                    Integer.parseInt(txtCantidad.getText()),
                    cmbUnidad.getValue(),
                    dtpFechaCaducidad.getValue()
            );
        }
    }

    /**
     * Maneja la acción del botón "Cancelar", cierra la ventana
     *
     */
    @FXML
    private void handleCancelar() {
        control.termina();
    }
}