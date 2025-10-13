package mx.uam.ayd.proyecto.presentacion.generarReporte;

import javafx.scene.Parent;
import javafx.scene.control.*;
import mx.uam.ayd.proyecto.negocio.modelo.*;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Clase encargada de la ventana para mostrar reportes generados.
 *
 * Muestra los datos de ventas ya procesados en forma de tabla o gráfica,
 * según la selección del usuario. Se asegura de operar sobre el hilo de JavaFX
 * para la manipulación de la interfaz gráfica.
 */
@Component
public class VentanaReporteGenerado {
    private Stage stage;
    private ControlReporteGenerado control;

    @FXML
    private BarChart<String, Number> barChartVentas;
    @FXML
    private TableView<ReporteVentaDTO> tblVentas;
    @FXML
    private TableColumn<ReporteVentaDTO, LocalDate> columnFecha;
    @FXML
    private TableColumn<ReporteVentaDTO, String> columnProducto;
    @FXML
    private TableColumn<ReporteVentaDTO, String> columnTipo;
    @FXML
    private TableColumn<ReporteVentaDTO, Long> columnVenta;
    @FXML
    private TableColumn<ReporteVentaDTO, Double> columnTotal;

    private ObservableList<ReporteVentaDTO> ventas = FXCollections.observableArrayList();;
    private boolean initialized = false;
    public VentanaReporteGenerado() {
        // Don't initialize JavaFX components in constructor
    }

    /**
     * Inicializa la interfaz gráfica cargando el archivo FXML y configurando
     * la ventana y las columnas de la tabla.
     *
     * Este método se ejecuta únicamente una vez y se asegura de hacerlo en el hilo JavaFX.
     */
    private void initializeUI() {
        if (initialized) {
            return; // Ya inicializado, no hacer nada
        }

        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::initializeUI);
            return;
        }

        try {
            stage = new Stage();
            stage.setTitle("Reporte generado");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-reporte-generado.fxml"));
            loader.setController(this);
            Parent root = loader.load();

            Scene scene = new Scene(root, 600, 400);
            stage.setScene(scene);

            // Configura las columnas de la tabla con las propiedades del DTO
            columnFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
            columnProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
            columnTipo.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTipoProducto().toString()));
            columnVenta.setCellValueFactory(new PropertyValueFactory<>("cantidadVendida"));
            columnTotal.setCellValueFactory(new PropertyValueFactory<>("totalVenta"));

            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Establece el controlador lógico que maneja las acciones del reporte generado.
     *
     * @param control instancia de ControlReporteGenerado
     */
    public void setControlReporteGenerado(ControlReporteGenerado control) {
        this.control = control;
    }

    /**
     * Muestra la ventana con el reporte de ventas recibido.
     *
     * Dependiendo del tipo de reporte, muestra los datos en una tabla o en un gráfico de barras.
     * Se asegura de ejecutar en el hilo JavaFX.
     *
     * @param ventas lista con los datos de ventas para mostrar
     * @param tipoReporte tipo de reporte ("Grafica" o cualquier otro valor para tabla)
     * @param periodicidad periodicidad del reporte ("Diario", "Mensual", etc.)
     */
    public void muestra(List<ReporteVentaDTO> ventas, String tipoReporte, String periodicidad) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra(ventas, tipoReporte, periodicidad));
            return;
        }

        initializeUI();

        barChartVentas.getData().clear();
        barChartVentas.setVisible(false);
        tblVentas.setVisible(false);
        this.ventas = FXCollections.observableArrayList(ventas);

        if ("Grafica".equalsIgnoreCase(tipoReporte)) {
            // Preparar y mostrar gráfica de barras
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Ventas");

            for (ReporteVentaDTO dto : ventas) {
                String label = dto.getNombreProducto() + "  " + dto.getFecha().toString();
                series.getData().add(new XYChart.Data<>(label, dto.getCantidadVendida()));
            }

            barChartVentas.getData().add(series);
            barChartVentas.setVisible(true);
        } else {
            // Mostrar tabla con los datos
            tblVentas.setItems(this.ventas);
            tblVentas.setVisible(true);
        }

        stage.show();
    }

    /**
     * Muestra un diálogo informativo con el mensaje proporcionado.
     * Se asegura de ejecutarse en el hilo JavaFX.
     *
     * @param mensaje texto que se mostrará en el diálogo.
     */
    public void muestraDialogoConMensaje(String mensaje) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestraDialogoConMensaje(mensaje));
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Controla la visibilidad de la ventana.
     * Inicializa la interfaz si es necesario.
     *
     * @param visible true para mostrar la ventana, false para ocultarla.
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
     * Manejador para el botón de descargar reporte.
     */
    @FXML
    private void handleDescargar() {
        control.descargarReporte(ventas);
    }

    /**
     * Manejador para el botón de regresar a la ventana anterior.
     */
    @FXML
    private void handleRegresar() {
        control.regresar();
    }
}