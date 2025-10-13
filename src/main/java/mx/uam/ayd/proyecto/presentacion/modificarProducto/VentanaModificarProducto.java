package mx.uam.ayd.proyecto.presentacion.modificarProducto;

import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import mx.uam.ayd.proyecto.negocio.modelo.TipoProducto;
import mx.uam.ayd.proyecto.negocio.modelo.UnidadProducto;
import mx.uam.ayd.proyecto.negocio.modelo.MarcaProducto;

import javafx.collections.FXCollections;
import javafx.scene.control.TextFormatter;
import java.util.function.UnaryOperator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DateCell;

import java.time.LocalDate;


import mx.uam.ayd.proyecto.presentacion.modificarProducto.ControlModificarProducto;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class VentanaModificarProducto {
    private Stage stage;
    private ControlModificarProducto control;
    private Long idProducto;
    private Producto producto;

    @FXML
    private Label lblIdProducto;

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

    public VentanaModificarProducto() {
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
            stage.setTitle("Modificar producto");

            // Carga el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-modificar-producto.fxml"));
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
     * Asigna el controlador lógico ControlModificarProducto para manejar las acciones de la ventana.
     *
     * @param control Controlador de agregar producto.
     */
    public void setControlModificarProducto(ControlModificarProducto control) {
        this.control = control;
    }

    /**
     * Muestra la ventana de agregar producto.
     *
     * Limpia todos los campos y carga las opciones en los ComboBox.
     * Se asegura de ejecutarse en el hilo de JavaFX.
     */
    public void muestra(Long idProducto, Producto producto) {
        this.idProducto = idProducto;
        this.producto = producto;
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra(idProducto, producto));
            return;
        }

        initializeUI();

        // NMOstramos la informacion actual del producto
        txtNombre.setText(producto.getNombre());
        txtCantidad.setText(Integer.toString(producto.getCantidadStock()));
        txtPrecio.setText(Double.toString(producto.getPrecio()));
        lblIdProducto.setText(idProducto.toString());

        // Carga opciones en los ComboBox
        cmbTipo.setItems(FXCollections.observableArrayList(TipoProducto.values()));
        cmbUnidad.setItems(FXCollections.observableArrayList(UnidadProducto.values()));
        cmbMarca.setItems(FXCollections.observableArrayList(MarcaProducto.values()));

        // Deja los ComboBox con la selección inicial del producto
        cmbTipo.getSelectionModel().select(producto.getTipoProducto());
        cmbUnidad.getSelectionModel().select(producto.getUnidadProducto());
        cmbMarca.getSelectionModel().select(producto.getMarcaProducto());

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
    private void handleModificar() {
        System.out.println("Nombre: "+producto.getNombre());
        control.modificarProducto(
                producto,
                txtNombre.getText(),
                cmbTipo.getValue(),
                cmbMarca.getValue(),
                Double.parseDouble(txtPrecio.getText()),
                Integer.parseInt(txtCantidad.getText()),
                cmbUnidad.getValue(),
                dtpFechaCaducidad.getValue()
        );
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
