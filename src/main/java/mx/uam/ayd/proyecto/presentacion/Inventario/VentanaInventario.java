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
import javafx.scene.control.ComboBox;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DataFormat;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import mx.uam.ayd.proyecto.negocio.modelo.TipoProducto;
import mx.uam.ayd.proyecto.presentacion.agregarProducto.ControlAgregarProducto;
import mx.uam.ayd.proyecto.presentacion.modificarProducto.ControlModificarProducto;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Clase que representa la ventana de interfaz gráfica del módulo de inventario.
 *
 * Esta vista muestra los productos registrados en el sistema, permite filtrarlos por tipo,
 * y proporciona accesos a las operaciones de agregar, modificar y eliminar productos.
 * Su controlador asociado es {@link Controlinventario}.
 *
 * La clase utiliza JavaFX para la interfaz y está gestionada por el contenedor de Spring.
 */
@Component
public class VentanaInventario {

    /** Controlador para modificar productos existentes. */
    private final ControlModificarProducto controlModificarProducto;

    /** Lista observable utilizada por la tabla para mostrar los productos. */
    private ObservableList<Producto> productos = FXCollections.observableArrayList();

    /** Ventana principal (Stage) de esta vista. */
    private Stage stage;

    /** ComboBox para seleccionar el tipo de producto a visualizar. */
    @FXML
    private ComboBox<TipoProducto> cmbTipoProducto;

    /** Tabla que muestra la lista de productos. */
    @FXML
    private TableView<Producto> tableProducto;

    /** Columna que muestra el ID del producto. */
    @FXML
    private TableColumn<Producto, Long> idColumn;

    /** Columna que muestra el nombre del producto. */
    @FXML
    private TableColumn<Producto, String> nombreColumn;

    /** Columna que muestra la fecha de caducidad del producto. */
    @FXML
    private TableColumn<Producto, DataFormat> fechaCaducidadColumn;

    /** Columna que muestra el precio del producto. */
    @FXML
    private TableColumn<Producto, Double> precioColumn;

    /** Columna que muestra la cantidad en stock del producto. */
    @FXML
    private TableColumn<Producto, Integer> stockColumn;

    /** Columna que muestra el uso veterinario (solo visible en medicamentos). */
    @FXML
    private TableColumn<Producto, String> usoVeterinarioColumn;

    /** Controlador para agregar nuevos productos. */
    private ControlAgregarProducto controlAgregarProducto;

    /** Controlador principal del inventario. */
    private Controlinventario control;

    /** Indica si la interfaz ha sido inicializada. */
    private boolean initialized = false;

    /**
     * Constructor de la ventana del inventario.
     *
     * @param controlModificarProducto Controlador para modificar productos.
     */
    public VentanaInventario(ControlModificarProducto controlModificarProducto) {
        // Evita inicializar componentes JavaFX en el constructor
        this.controlModificarProducto = controlModificarProducto;
    }

    /**
     * Inicializa los componentes gráficos de la interfaz (FXML, tabla, columnas, etc.).
     * Este método se asegura de ejecutarse en el hilo de la aplicación JavaFX.
     */
    private void initializeUI() {
        if (initialized) {
            return;
        }

        // Asegura que la inicialización se ejecute en el hilo JavaFX
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::initializeUI);
            return;
        }

        try {
            stage = new Stage();
            stage.setTitle("Lista de productos");

            // Carga el archivo FXML asociado a la interfaz
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-inventario-productos.fxml"));
            loader.setController(this);
            Scene scene = new Scene(loader.load(), 680, 400);
            stage.setScene(scene);

            // Configuración de las columnas de la tabla
            idColumn.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
            nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            fechaCaducidadColumn.setCellValueFactory(new PropertyValueFactory<>("fechaCaducidad"));
            precioColumn.setCellValueFactory(new PropertyValueFactory<>("precio"));
            stockColumn.setCellValueFactory(new PropertyValueFactory<>("cantidadStock"));
            usoVeterinarioColumn.setCellValueFactory(new PropertyValueFactory<>("usoVeterinario"));
            usoVeterinarioColumn.setVisible(false);

            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Establece el controlador principal del inventario asociado a esta ventana.
     *
     * @param control Controlador de inventario.
     */
    public void setControlinventario(Controlinventario control) {
        this.control = control;
    }

    /**
     * Muestra la ventana y carga los productos proporcionados.
     *
     * @param productos Lista de productos a mostrar en la tabla.
     */
    public void muestra(List<Producto> productos) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra(productos));
            return;
        }

        this.productos = FXCollections.observableArrayList(productos);
        initializeUI();

        if (initialized) {
            cmbTipoProducto.setItems(FXCollections.observableArrayList(TipoProducto.values()));

            // Actualiza la tabla si el tipo seleccionado coincide
            if (!productos.isEmpty()) {
                if (cmbTipoProducto.getValue() == productos.get(productos.size() - 1).getTipoProducto()) {
                    tableProducto.getItems().clear();
                    tableProducto.setItems(FXCollections.observableArrayList(productos));
                }
            }
        }
        stage.show();
    }

    /** Campo de texto asociado al ID del producto (definido en FXML). */
    @FXML
    private TextField campoIdProducto;

    /**
     * Maneja el evento de eliminación de un producto.
     * Obtiene el producto seleccionado en la tabla y solicita su eliminación al controlador.
     *
     * @param event Evento de acción generado al presionar el botón "Eliminar".
     */
    @FXML
    private void handleEliminar(ActionEvent event) {
        try {
            control.eliminaProducto(tableProducto.getSelectionModel().getSelectedItem());
            campoIdProducto.clear();
        } catch (NumberFormatException e) {
            System.out.println("El ID ingresado no es un número válido.");
        }
    }

    /**
     * Maneja el evento cuando se selecciona un tipo de producto en el ComboBox.
     * Filtra la lista de productos según el tipo elegido y muestra u oculta la columna de uso veterinario.
     *
     * @param event Evento generado por la selección en el ComboBox.
     */
    @FXML
    private void onTipoSeleccionado(ActionEvent event) {
        TipoProducto tipoSeleccionado = cmbTipoProducto.getValue();
        tableProducto.getItems().clear();

        // Mostrar o ocultar columna según el tipo de producto
        if (tipoSeleccionado.equals(TipoProducto.Medicamento)) {
            usoVeterinarioColumn.setVisible(true);
        } else {
            usoVeterinarioColumn.setVisible(false);
        }

        productos = FXCollections.observableArrayList(control.filtroTipoProducto(tipoSeleccionado));
        tableProducto.setItems(productos);
    }

    /**
     * Maneja el evento de modificación de un producto.
     * Abre la ventana de modificación para el producto seleccionado.
     */
    @FXML
    private void handleModificar() {
        control.modificarProducto(tableProducto.getSelectionModel().getSelectedItem());
    }

    /**
     * Maneja el evento de agregar un nuevo producto.
     * Inicia el flujo de agregar producto desde el controlador principal.
     */
    @FXML
    private void handleAgregar() {
        if (control != null) {
            control.agregarProducto();
        }
    }

    /**
     * Cierra la ventana de inventario.
     */
    @FXML
    private void handleCerrar() {
        stage.close();
    }

    /**
     * Muestra un cuadro de diálogo con un mensaje informativo.
     *
     * @param s Mensaje a mostrar al usuario.
     */
    public void muestraMensaje(String s) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Mensaje");
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();
    }
}
