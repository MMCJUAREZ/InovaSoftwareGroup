package mx.uam.ayd.proyecto.presentacion.configurarUmbrales;
/**
 * @file VentanaConfiguracionUmbrales.java
 * @brief Ventana de configuración para gestionar los umbrales de stock de productos.
 *
 * Esta clase representa la interfaz gráfica que permite visualizar,
 * configurar y modificar los umbrales de stock para diferentes productos.
 * Utiliza JavaFX para la interfaz y se integra con el servicio de negocio
 * @ref ServicioUmbrales.
 */

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.ServicioUmbrales;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import mx.uam.ayd.proyecto.negocio.modelo.Umbral;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.List;

/**
 * @class VentanaConfiguracionUmbrales
 * @brief Ventana para la gestión visual de umbrales de productos.
 *
 * Esta clase controla la UI de configuración de umbrales, mostrando
 * los productos, su stock, umbral y estado, así como la opción
 * de editar la configuración.
 */
@Component
public class VentanaConfiguracionUmbrales {

    private Stage stage; ///< Ventana principal de la interfaz.
    private ControlConfiguracionUmbrales control; ///< Controlador lógico para esta ventana.
    private ServicioUmbrales servicioUmbrales; ///< Servicio de negocio para operaciones de umbrales.
    private boolean initialized = false; ///< Indica si la interfaz fue inicializada.

    // Componentes de la tabla
    @FXML private TableView<Producto> tablaProductos; ///< Tabla que muestra los productos.
    @FXML private TableColumn<Producto, String> columnaProducto; ///< Columna con el nombre del producto.
    @FXML private TableColumn<Producto, Integer> columnaStock; ///< Columna con el stock disponible.
    @FXML private TableColumn<Producto, Integer> columnaUmbral; ///< Columna con el umbral mínimo configurado.
    @FXML private TableColumn<Producto, String> columnaEstado; ///< Columna con el estado del producto según su umbral.
    @FXML private TableColumn<Producto, Void> columnaAccion; ///< Columna con el botón para editar umbrales.

    /**
     * @brief Constructor con inyección de dependencias.
     * @param servicioUmbrales Servicio de negocio para gestión de umbrales.
     */
    @Autowired
    public VentanaConfiguracionUmbrales(ServicioUmbrales servicioUmbrales) {
        this.servicioUmbrales = servicioUmbrales;
    }

    /**
     * @brief Inicializa la configuración de columnas de la tabla.
     */
    @FXML
    public void initialize() {
        configurarColumnas();
    }

    /**
     * @brief Inicializa y carga la interfaz gráfica si no ha sido creada.
     */
    private void initializeUI() {
        if (initialized) return;

        // Create UI only if we're on JavaFX thread
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::initializeUI);
            return;
        }

        //Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-menu-principal-umbrales.fxml"));
                loader.setController(this);
                stage = new Stage();
                stage.setTitle("Gestión de Umbrales");
                stage.setScene(new Scene(loader.load(), 850, 600));
                initialized = true;
            } catch (IOException e) {
                throw new RuntimeException("Error al inicializar la ventana", e);
            }
        //});
    }

    /**
     * @brief Configura las columnas de la tabla de productos.
     *
     * Establece los valores que mostrará cada columna, así como el formato de presentación.
     */
    private void configurarColumnas() {
        // Configuración de columnaProducto
        columnaProducto.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        // Configuración de columnaStock
        columnaStock.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getCantidadStock()).asObject());

        columnaStock.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer cantidad, boolean empty) {
                super.updateItem(cantidad, empty);
                setText(empty || cantidad == null ? null : cantidad + " unidades");
            }
        });

        // Configuración de columnaUmbral
        columnaUmbral.setCellValueFactory(cellData -> {
            Umbral umbral = servicioUmbrales.findById(cellData.getValue().getIdProducto());
            int valor = (umbral != null) ? umbral.getValorMinimo() : 0;
            return new SimpleIntegerProperty(valor).asObject();
        });

        columnaUmbral.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer valor, boolean empty) {
                super.updateItem(valor, empty);
                if (empty || valor == null) {
                    setText(null);
                } else {
                    int idx = getIndex();
                    if (idx >= 0 && idx < getTableView().getItems().size()) {
                        Producto producto = getTableView().getItems().get(idx);
                        Umbral umbral = servicioUmbrales.findById(producto.getIdProducto());
                        if (umbral == null) {
                            setText("No configurado");
                            return;
                        }
                    }
                    setText(valor + " unidades");
                }
            }
        });

        // Configuración de columnaEstado
        columnaEstado.setCellValueFactory(cellData -> {
            Producto producto = cellData.getValue();
            Umbral umbral = servicioUmbrales.findById(producto.getIdProducto());

            if (umbral == null) {
                return new SimpleStringProperty("No configurado");
            } else if (producto.getCantidadStock() < umbral.getValorMinimo()) {
                return new SimpleStringProperty("BAJO STOCK");
            } else {
                return new SimpleStringProperty("OK");
            }
        });

        columnaEstado.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);
                if (empty || estado == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(estado);
                    setStyle(estado.equals("BAJO STOCK") ?
                            "-fx-text-fill: red; -fx-font-weight: bold;" :
                            "-fx-text-fill: green;");
                }
            }
        });

        // Configuración de columnaAccion
        columnaAccion.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            {
                btnEditar.setOnAction(event -> {
                    Producto producto = getTableView().getItems().get(getIndex());
                    control.iniciarEdicionDeUmbral(producto.getIdProducto(), producto.getCantidadStock());
                });
                btnEditar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnEditar);
            }
        });
    }

    /**
     * @brief Asigna el controlador lógico de configuración de umbrales.
     * @param control Instancia de @ref ControlConfiguracionUmbrales.
     */
    public void setControlConfiguracionUmbrales(ControlConfiguracionUmbrales control) {
        this.control = control;
    }

    /**
     * @brief Muestra la ventana con la lista de productos.
     * @param productos Lista de productos a mostrar.
     */
    public void muestra(List<Producto> productos) {
        //Platform.runLater(() -> {
            //if (!initialized) {
          //      initializeUI();
            //}

        //});
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra(productos));
            return;
        }
        initializeUI();
        tablaProductos.getItems().clear();
        ObservableList<Producto> data = FXCollections.observableArrayList(productos);
        if (tablaProductos != null) {
            tablaProductos.setItems(data);
            stage.show();
        } else {
            System.err.println("Error: tablaProductos no fue inicializada");
        }
    }

    /**
     * @brief Actualiza en la tabla el umbral de un producto específico.
     * @param idProducto ID del producto.
     * @param nuevoMinimo Nuevo valor mínimo del umbral.
     */
    public void actualizarUmbralEnTabla(Long idProducto, int nuevoMinimo) {
        Platform.runLater(() -> {
            tablaProductos.getItems().stream()
                    .filter(p -> p.getIdProducto().equals(idProducto))
                    .findFirst()
                    .ifPresent(p -> {
                        if (p.getUmbral() == null) {
                            Umbral nuevoUmbral = new Umbral();
                            nuevoUmbral.setValorMinimo(nuevoMinimo);
                            nuevoUmbral.setProducto(p);
                            p.setUmbral(nuevoUmbral);
                        } else {
                            p.getUmbral().setValorMinimo(nuevoMinimo);
                        }
                        tablaProductos.refresh();
                    });
        });
    }

    /// Muestra un mensaje de éxito tras actualizar un umbral.
    public void mostrarMensajeExitoDeActualizacion() {
        mostrarAlerta(AlertType.INFORMATION, "Éxito", "Umbral actualizado correctamente");
    }

    /// Muestra un mensaje de éxito tras crear un nuevo umbral.
    public void mostrarMensajeDeUmbralCreado() {
        mostrarAlerta(AlertType.INFORMATION, "Éxito", "Nuevo umbral creado exitosamente");
    }

    /**
     * @brief Muestra un mensaje de error genérico.
     * @param mensaje Texto del error.
     */
    public void mostrarError(String mensaje) {
        mostrarAlerta(AlertType.ERROR, "Error", mensaje);
    }

    /**
     * @brief Muestra una alerta en pantalla.
     * @param tipo Tipo de alerta (INFORMATION, ERROR, etc.).
     * @param titulo Título de la alerta.
     * @param mensaje Contenido del mensaje.
     */
    private void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
        Platform.runLater(() -> {
            Alert alert = new Alert(tipo);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        });
    }

    /**
     * @brief Cambia la visibilidad de la ventana.
     * @param visible true para mostrar la ventana, false para ocultarla.
     */
    public void setVisible(boolean visible) {
        Platform.runLater(() -> {
            if (!initialized) initializeUI();
            if (visible) stage.show(); else stage.hide();
        });
    }
}

