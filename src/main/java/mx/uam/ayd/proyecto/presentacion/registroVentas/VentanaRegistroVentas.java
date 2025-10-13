package mx.uam.ayd.proyecto.presentacion.registroVentas;

import mx.uam.ayd.proyecto.negocio.modelo.MarcaProducto;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.LongToDoubleFunction;

import mx.uam.ayd.proyecto.negocio.modelo.Venta;
import mx.uam.ayd.proyecto.negocio.modelo.DetalleVenta;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

/**
 * Controla la ventana para registrar ventas.
 *
 * Permite mostrar la ventana con productos disponibles, agregar productos a la venta,
 * finalizar o cancelar la venta.
 */
@Component
public class VentanaRegistroVentas{
    private Stage stage;
    private ControlRegistroVentas control;

    private Venta venta;

    @FXML
    private TextField txtCantidad;

    @FXML
    private ComboBox<Producto> cmbProductos;

    @FXML
    private TableView<DetalleVenta> tableVenta;

    private ObservableList<DetalleVenta> detallesVenta = FXCollections.observableArrayList();

    @FXML
    private TableColumn<DetalleVenta, Long> columnIdProducto;
    @FXML
    private TableColumn<DetalleVenta, String> columnNombreProducto;
    @FXML
    private TableColumn<DetalleVenta, String> columnMarca;
    @FXML
    private TableColumn<DetalleVenta, Double> columnPrecio;
    @FXML
    private TableColumn<DetalleVenta, Integer> columnCantidad;
    @FXML
    private TableColumn<DetalleVenta, Double> columnSubtotal;
    @FXML
    private Label lblTotal;

    private boolean initialized = false;

    public VentanaRegistroVentas() {
        // Don't initialize JavaFX components in constructor
    }

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
            stage.setTitle("Registro de ventas");

            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-registro-ventas.fxml"));
            loader.setController(this);
            Scene scene = new Scene(loader.load(), 500, 420);
            stage.setScene(scene);

            txtCantidad.textProperty().addListener((obs, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    txtCantidad.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });

            tableVenta.setItems(detallesVenta);

            columnIdProducto.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleLongProperty(
                            cellData.getValue().getProducto() != null ?
                                    cellData.getValue().getProducto().getIdProducto() :
                                    0L
                    ).asObject()
            );

            columnNombreProducto.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleStringProperty(cellData.getValue().getProducto().getNombre()));

            columnMarca.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleStringProperty(cellData.getValue().getProducto().getMarcaProducto().toString()));

            columnPrecio.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getProducto().getPrecio()).asObject());

            columnCantidad.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getCantidadVendida()).asObject());

            columnSubtotal.setCellValueFactory(cellData -> {
                DetalleVenta detalle = cellData.getValue(); //Mover a un servicio
                double subtotal = detalle.getCantidadVendida() * detalle.getProducto().getPrecio();
                return new javafx.beans.property.SimpleDoubleProperty(subtotal).asObject();
            });

            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Establece el controlador que maneja la lógica de ventas.
     *
     * @param control instancia de ControlRegistroVentas
     */
    public void setControlRegistroVentas(ControlRegistroVentas control) {
        this.control = control;
    }

    /**
     * Muestra la ventana para registrar ventas.
     *
     * Inicializa la interfaz si no está inicializada, limpia y carga la lista de productos
     * disponibles, establece la venta actual y muestra la ventana.
     * Se asegura que la operación se ejecute en el hilo de JavaFX.
     *
     * @param productos lista de productos disponibles para la venta
     * @param venta     instancia actual de la venta en curso
     */
    public void muestra(List<Producto> productos, Venta venta) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra(productos, venta));
            return;
        }

        initializeUI();

        // Limpia cantidad y lista previa
        txtCantidad.setText("");
        cmbProductos.getItems().clear();

        // Agrega productos al ComboBox
        for(Producto producto : productos) {
            cmbProductos.getItems().add(producto);
        }

        // Selecciona el primer producto si la lista no está vacía
        if(!cmbProductos.getItems().isEmpty()) {
            cmbProductos.setValue(cmbProductos.getItems().get(0));
        }

        this.venta = venta;

        stage.show();
    }

    /**
     * Muestra un diálogo informativo con el mensaje proporcionado.
     * Se asegura que la llamada se realice en el hilo de JavaFX.
     *
     * @param mensaje texto que se mostrará en el diálogo
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
     * Si la ventana no ha sido inicializada, la inicializa si se va a mostrar.
     * Ejecuta la operación en el hilo de JavaFX.
     *
     * @param visible true para mostrar la ventana, false para ocultarla
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
     * Evento asociado al botón "Agregar", agrega los productos a la tabla
     *
     * Valida que los campos necesarios no sean nulos y agrega un detalle de venta a la lista.
     * Actualiza la tabla con los detalles y calcula el total acumulado.
     */
    @FXML
    private void handleAgregar() {
        if(cmbProductos == null || txtCantidad == null) {
            muestraDialogoConMensaje("Llene todos los campos");
        } else {
            double montoTotal = 0;

            // Crea un detalle de venta y lo agrega a la lista
            detallesVenta.add(control.crearDetalleVenta(
                    cmbProductos.getValue(),
                    Integer.parseInt(txtCantidad.getText()),
                    detallesVenta));

            // Actualiza la tabla de venta con la lista actualizada
            tableVenta.setItems(detallesVenta);

            // Suma el subtotal de cada detalle para obtener el monto total
            for(DetalleVenta detalleVenta : detallesVenta){
                montoTotal += detalleVenta.getSubtotal();
            }

            // Limpia la cantidad para un nuevo ingreso
            txtCantidad.clear();

            // Actualiza la etiqueta con el total acumulado
            lblTotal.setText("Total: " + montoTotal);
        }
    }

    /**
     * Evento asociado al botón "Finalizar".
     *
     * Si no hay detalles de venta, cancela la venta.
     * Si hay detalles, guarda la venta, los detalles, actualiza stock,
     * genera el documento y termina el proceso mostrando mensaje de éxito.
     * Finalmente limpia la lista de detalles.
     */
    @FXML
    private void handleFinalizar() {
        if(detallesVenta == null) {
            String mensaje = "Venta cancelada";
            control.termina(mensaje);
            return;
        }

        control.guardarVenta(detallesVenta);
        control.guardarDetallesVenta(detallesVenta);
        control.actualizarStock(detallesVenta);
        control.crearDocumento(detallesVenta);

        String mensaje = "Se creó la venta exitosamente";
        control.termina(mensaje);

        detallesVenta.clear();
    }

    /**
     * Evento asociado al botón "Cancelar", se cancela la venta, no guarda nada en la Base de datos
     *
     * Limpia la lista de detalles y termina la operación indicando que la venta fue cancelada.
     */
    @FXML
    private void handleCancelar() {
        String mensaje = "Venta cancelada";
        detallesVenta.clear();
        control.termina(mensaje);
    }

    @FXML
    private void handleModificar() {
        if(cmbProductos == null || txtCantidad == null) {
            muestraDialogoConMensaje("Llene todos los campos, si selecciona 0 se eliminara el producto");
        } else {
            if(cmbProductos.getValue().getCantidadStock() < Integer.parseInt(txtCantidad.getText())) {
                muestraDialogoConMensaje("Stock no disponible");
            }else{
                double montoTotal = 0;
                int posicion = -1;
                int contador = 0;
                DetalleVenta detalleVentaAux;
                for(DetalleVenta detalleVenta : detallesVenta){
                    if(Objects.equals(detalleVenta.getProducto().getIdProducto(), cmbProductos.getValue().getIdProducto())) {
                        posicion = contador;
                        if(Integer.parseInt(txtCantidad.getText()) == 0){
                            detallesVenta.remove(detalleVenta);
                        }
                        detalleVentaAux = detalleVenta;
                        detalleVentaAux.setCantidadVendida(Integer.parseInt(txtCantidad.getText()));
                        detalleVenta.setSubtotal(Integer.parseInt(txtCantidad.getText()) * cmbProductos.getValue().getPrecio());
                        detallesVenta.set(posicion, detalleVentaAux);
                        muestraDialogoConMensaje("Modificando correctamente");
                        break;
                    }
                    contador++;
                }
                System.out.println(detallesVenta);
                // Actualiza la tabla de venta con la lista actualizada
                tableVenta.setItems(detallesVenta);
                // Suma el subtotal de cada detalle para obtener el monto total
                for(DetalleVenta detalleVenta : detallesVenta){
                    montoTotal += detalleVenta.getSubtotal();
                }
                // Limpia la cantidad para un nuevo ingreso
                txtCantidad.clear();

                lblTotal.setText("Total: " + montoTotal);
            }
        }
    }
}