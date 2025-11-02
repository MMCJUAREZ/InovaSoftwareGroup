package mx.uam.ayd.proyecto.presentacion.agregarProducto;

import mx.uam.ayd.proyecto.negocio.modelo.TipoProducto;
import mx.uam.ayd.proyecto.negocio.modelo.UnidadProducto;
import mx.uam.ayd.proyecto.negocio.modelo.MarcaProducto;
import mx.uam.ayd.proyecto.negocio.modelo.UsoVeterinario;

import javafx.collections.FXCollections;
import javafx.scene.control.TextFormatter;
import java.util.function.UnaryOperator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DateCell;
import java.time.LocalDate;

import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Clase encargada de gestionar la ventana de la interfaz gráfica
 * para agregar un nuevo producto al sistema.
 * Controla los elementos visuales definidos en el archivo FXML,
 * valida la entrada del usuario, carga opciones dinámicas en los ComboBox
 * y se comunica con el controlador {@link ControlAgregarProducto}
 * para realizar la operación de agregado.
 * @see ControlAgregarProducto
 * @see mx.uam.ayd.proyecto.negocio.ServicioProducto
 */
@Component
public class VentanaAgregarProducto {

    /** Ventana principal de esta vista. */
    private Stage stage;

    /** Controlador asociado que maneja la lógica de negocio. */
    private ControlAgregarProducto control;

    /** Indica si la interfaz ya fue inicializada. */
    private boolean initialized = false;

    // ---------- Componentes FXML ----------

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
    public VentanaAgregarProducto() {
        // Constructor vacío por convención de Spring y JavaFX
    }

    /**
     * Inicializa componentes FXML después de cargar la vista.
     * Este método es llamado automáticamente por JavaFX al cargar el archivo FXML.
     * Configura la restricción de fechas del {@link DatePicker} para evitar seleccionar
     * fechas anteriores a una semana desde el día actual.
     */
    @FXML
    private void initialize() {
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
     * Inicializa la interfaz de usuario cargando el archivo FXML
     * y configurando las validaciones de los campos.
     * Si la interfaz ya ha sido inicializada previamente, no se vuelve a crear.
     * Se asegura de que este método siempre se ejecute en el hilo de JavaFX.
     */
    private void initializeUI() {
        if (initialized) {
            return;
        }

        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::initializeUI);
            return;
        }

        try {
            stage = new Stage();
            stage.setTitle("Agregar producto");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-agregar-producto.fxml"));
            loader.setController(this);
            Scene scene = new Scene(loader.load(), 700, 500);
            stage.setScene(scene);

            // Validación: solo números enteros en "Cantidad"
            txtCantidad.textProperty().addListener((obs, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    txtCantidad.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });

            // Validación: números decimales válidos en "Precio"
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
     * Asigna el controlador lógico para esta vista.
     *
     * @param control Instancia de {@link ControlAgregarProducto}.
     */
    public void setControlAgregarProducto(ControlAgregarProducto control) {
        this.control = control;
    }

    /**
     * Muestra la ventana para agregar un producto.
     * Limpia los campos, carga las opciones en los ComboBox
     * y se asegura de ejecutar la operación en el hilo de JavaFX.
     */
    public void muestra() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::muestra);
            return;
        }

        initializeUI();

        // Limpieza de campos
        txtNombre.clear();
        txtCantidad.clear();
        txtPrecio.clear();

        // Cargar opciones
        cmbTipo.setItems(FXCollections.observableArrayList(TipoProducto.values()));
        cmbUnidad.setItems(FXCollections.observableArrayList(UnidadProducto.values()));
        cmbUnidad.setDisable(true);
        cmbMarca.setItems(FXCollections.observableArrayList(MarcaProducto.values()));
        cmbMarca.setDisable(true);
        cmbUsoVeterinario.setItems(FXCollections.observableArrayList(UsoVeterinario.values()));

        cmbTipo.getSelectionModel().clearSelection();
        cmbUnidad.getSelectionModel().clearSelection();
        cmbMarca.getSelectionModel().clearSelection();

        stage.show();
    }

    /**
     * Muestra un cuadro de diálogo informativo.
     *
     * @param mensaje Mensaje que se desea mostrar.
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
     * Maneja la selección del tipo de producto.
     * Al seleccionar un tipo de producto, se actualizan las opciones
     * disponibles de marca, unidad y visibilidad del campo de uso veterinario.
     */
    @FXML
    private void onTipoSeleccionado() {
        String tipoSeleccionado = cmbTipo.getValue().toString();
        cmbMarca.getItems().clear();
        cmbUnidad.getItems().clear();

        // Filtrar marcas por tipo
        for (MarcaProducto marca : MarcaProducto.values()) {
            if (marca.getTipoProducto().equals(tipoSeleccionado)) {
                cmbMarca.getItems().add(marca);
            }
        }
        cmbMarca.setDisable(false);

        // Filtrar unidades según tipo
        for (UnidadProducto unidad : UnidadProducto.values()) {
            if (tipoSeleccionado.equals("Medicamento")) {
                if (unidad.getTipoProducto().equals(tipoSeleccionado)) {
                    cmbUnidad.getItems().add(unidad);
                }
            } else {
                if (unidad.getTipoProducto().equals("")) {
                    cmbUnidad.getItems().add(unidad);
                }
            }
        }
        cmbUnidad.setDisable(false);

        // Mostrar u ocultar uso veterinario
        cmbUsoVeterinario.setVisible(tipoSeleccionado.equals("Medicamento"));

        // Seleccionar primeras opciones por defecto
        if (!cmbMarca.getItems().isEmpty()) {
            cmbMarca.getSelectionModel().selectFirst();
        }
        if (!cmbUnidad.getItems().isEmpty()) {
            cmbUnidad.getSelectionModel().selectFirst();
        }
    }

    /**
     * Maneja la acción del botón "Agregar".
     * Valida que los campos requeridos estén completos y, si es así,
     * delega la operación al controlador {@link ControlAgregarProducto}.
     * </p>
     */
    @FXML
    private void handleAgregar() {
        String tipoSeleccionado = cmbTipo.getValue().toString();
        boolean valido = true;

        if (tipoSeleccionado.equals("Medicamento")) {
            if (dtpFechaCaducidad.getValue() == null || cmbUsoVeterinario.getValue() == null) {
                valido = false;
            }
        } else if (tipoSeleccionado.equals("Comida") && dtpFechaCaducidad == null) {
            valido = false;
        }

        if (cmbUnidad == null || cmbTipo == null || cmbMarca == null ||
                txtCantidad == null || txtNombre == null || txtPrecio == null || !valido) {
            muestraDialogoConMensaje("Llene los campos obligatorios");
        } else {
            control.agregarProducto(
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
    }

    /**
     * Maneja la acción del botón "Cancelar".
     * Cierra la ventana sin realizar cambios.
     */
    @FXML
    private void handleCancelar() {
        control.termina();
    }
}
