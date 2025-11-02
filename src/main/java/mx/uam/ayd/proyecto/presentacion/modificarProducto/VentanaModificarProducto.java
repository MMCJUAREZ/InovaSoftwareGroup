package mx.uam.ayd.proyecto.presentacion.modificarProducto;

import javafx.collections.FXCollections;
import javafx.scene.control.TextFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
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
import java.io.IOException;

import mx.uam.ayd.proyecto.negocio.modelo.*;
import org.springframework.stereotype.Component;

/**
 * Ventana de la interfaz gráfica encargada de modificar los datos de un producto existente.
 * Esta clase forma parte de la capa de presentación y utiliza JavaFX para construir la interfaz.
 * Se comunica directamente con {@link ControlModificarProducto}, que gestiona la lógica
 * y la interacción con la capa de negocio.
 * La ventana permite al usuario actualizar la información de un producto, incluyendo su
 * nombre, tipo, marca, cantidad, unidad, precio, fecha de caducidad y uso veterinario.
 */
@Component
public class VentanaModificarProducto {

    /** Ventana principal de la interfaz de modificación. */
    private Stage stage;

    /** Controlador lógico que maneja las operaciones del producto. */
    private ControlModificarProducto control;

    /** Producto actualmente seleccionado para modificación. */
    private Producto producto;

    /** Indica si la ventana ya ha sido inicializada. */
    private boolean initialized = false;

    // --- Componentes FXML ---
    @FXML private Label lblIdProducto;
    @FXML private TextField txtNombre;
    @FXML private TextField txtCantidad;
    @FXML private TextField txtPrecio;
    @FXML private ComboBox<TipoProducto> cmbTipo;
    @FXML private ComboBox<UnidadProducto> cmbUnidad;
    @FXML private ComboBox<MarcaProducto> cmbMarca;
    @FXML private ComboBox<UsoVeterinario> cmbUsoVeterinario;
    @FXML private DatePicker dtpFechaCaducidad;

    /**
     * Constructor por defecto.
     */
    public VentanaModificarProducto() {}

    /**
     * Inicializa ciertos comportamientos de la interfaz definidos en el FXML.
     * Este método se ejecuta automáticamente al cargar el archivo FXML.
     * Aquí se configuran restricciones de fecha para la fecha de caducidad.
     */
    @FXML
    private void initialize() {
        // Deshabilitar fechas anteriores a una semana desde hoy
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

    /**
     * Inicializa la interfaz gráfica cargando el archivo FXML,
     * creando la escena y configurando validaciones.
     * Este método solo se ejecuta una vez y se asegura de hacerlo
     * dentro del hilo principal de JavaFX.
     */
    private void initializeUI() {
        if (initialized) return; // Evita reinicialización

        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::initializeUI);
            return;
        }

        try {
            stage = new Stage();
            stage.setTitle("Modificar producto");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-modificar-producto.fxml"));
            loader.setController(this);
            Scene scene = new Scene(loader.load(), 700, 500);
            stage.setScene(scene);

            // Validación: solo números enteros en el campo cantidad
            txtCantidad.textProperty().addListener((obs, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    txtCantidad.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });

            // Validación: números decimales válidos en el campo precio
            UnaryOperator<TextFormatter.Change> filter = change -> {
                String newText = change.getControlNewText();
                return newText.matches("\\d*\\.?\\d*") ? change : null;
            };
            txtPrecio.setTextFormatter(new TextFormatter<>(filter));

            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Asigna el controlador lógico que gestionará las acciones de esta ventana.
     *
     * @param control Instancia de {@link ControlModificarProducto}.
     */
    public void setControlModificarProducto(ControlModificarProducto control) {
        this.control = control;
    }

    /**
     * Muestra la ventana con los datos actuales del producto a modificar.
     * Este método carga la información del producto, establece las selecciones
     * correspondientes en los ComboBox y aplica filtros según el tipo de producto.
     * @param producto Producto cuyos datos serán mostrados y editados.
     */
    public void muestra(Producto producto) {
        this.producto = producto;

        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra(producto));
            return;
        }

        initializeUI();

        // Cargar valores actuales
        txtNombre.setText(producto.getNombre());
        txtCantidad.setText(String.valueOf(producto.getCantidadStock()));
        txtPrecio.setText(String.valueOf(producto.getPrecio()));
        lblIdProducto.setText(String.valueOf(producto.getIdProducto()));

        // Configurar ComboBox según tipo de producto
        String filtroTipo = producto.getTipoProducto().toString();
        if (producto.getTipoProducto().equals("Medicamento")) {
            cmbUsoVeterinario.setVisible(true);
            cmbUsoVeterinario.getSelectionModel().select(producto.getUsoVeterinario());
        } else {
            cmbUsoVeterinario.setVisible(false);
            filtroTipo = "";
        }

        final String filtro = filtroTipo;

        ArrayList<UnidadProducto> unidades = Arrays.stream(UnidadProducto.values())
                .filter(u -> u.getTipoProducto().equals(filtro))
                .collect(Collectors.toCollection(ArrayList::new));

        ArrayList<MarcaProducto> marcas = Arrays.stream(MarcaProducto.values())
                .filter(m -> m.getTipoProducto().equals(producto.getTipoProducto().toString()))
                .collect(Collectors.toCollection(ArrayList::new));

        cmbTipo.setItems(FXCollections.observableArrayList(TipoProducto.values()));
        cmbUnidad.setItems(FXCollections.observableArrayList(unidades));
        cmbMarca.setItems(FXCollections.observableArrayList(marcas));
        cmbUsoVeterinario.setItems(FXCollections.observableArrayList(UsoVeterinario.values()));

        cmbTipo.getSelectionModel().select(producto.getTipoProducto());
        cmbUnidad.getSelectionModel().select(producto.getUnidadProducto());
        cmbMarca.getSelectionModel().select(producto.getMarcaProducto());

        stage.show();
    }

    /**
     * Muestra un cuadro de diálogo informativo con un mensaje.
     *
     * @param mensaje Texto que se mostrará en el cuadro de diálogo.
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

        if (!initialized && visible) {
            initializeUI();
        }

        if (visible) {
            stage.show();
        } else {
            stage.hide();
        }
    }

    /**
     * Maneja el evento cuando se selecciona un tipo de producto.
     * Este método actualiza las opciones disponibles en los ComboBox
     * de marca, unidad y uso veterinario, según el tipo elegido.
     */
    @FXML
    private void onTipoSeleccionado() {
        String tipoSeleccionado = cmbTipo.getValue().toString();

        cmbMarca.getItems().clear();
        cmbUnidad.getItems().clear();

        for (MarcaProducto marca : MarcaProducto.values()) {
            if (marca.getTipoProducto().equals(tipoSeleccionado)) {
                cmbMarca.getItems().add(marca);
            }
        }

        for (UnidadProducto unidad : UnidadProducto.values()) {
            if (tipoSeleccionado.equals("Medicamento")) {
                if (unidad.getTipoProducto().equals(tipoSeleccionado)) {
                    cmbUnidad.getItems().add(unidad);
                }
            } else if (unidad.getTipoProducto().equals("")) {
                cmbUnidad.getItems().add(unidad);
            }
        }

        cmbUsoVeterinario.setVisible(tipoSeleccionado.equals("Medicamento"));
        cmbMarca.setDisable(false);
        cmbUnidad.setDisable(false);

        if (!cmbMarca.getItems().isEmpty()) cmbMarca.getSelectionModel().selectFirst();
        if (!cmbUnidad.getItems().isEmpty()) cmbUnidad.getSelectionModel().selectFirst();
    }

    /**
     * Maneja la acción del botón “Modificar”.
     * Valida los datos ingresados y llama al controlador
     * {@link ControlModificarProducto#modificarProducto} para actualizar el producto.
     */
    @FXML
    private void handleModificar() {
        control.modificarProducto(
                producto,
                txtNombre.getText(),
                cmbTipo.getValue(),
                cmbMarca.getValue(),
                Double.parseDouble(txtPrecio.getText()),
                Integer.parseInt(txtCantidad.getText()),
                cmbUnidad.getValue(),
                dtpFechaCaducidad.getValue(),
                cmbUsoVeterinario.getValue()
        );
    }

    /**
     * Maneja la acción del botón “Cancelar”.
     */
    @FXML
    private void handleCancelar() {
        control.termina();
    }
}
