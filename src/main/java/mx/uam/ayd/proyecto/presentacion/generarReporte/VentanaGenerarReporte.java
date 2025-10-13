package mx.uam.ayd.proyecto.presentacion.generarReporte;

import javafx.scene.control.*;
import mx.uam.ayd.proyecto.negocio.modelo.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import mx.uam.ayd.proyecto.presentacion.registroVentas.ControlRegistroVentas;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Clase encargada de la ventana para generar reportes en la aplicación.
 *
 * Administra la interfaz gráfica para seleccionar parámetros de generación
 * de reportes, como tipo de producto, tipo de reporte, periodicidad y rango de fechas.
 * Se asegura de ejecutar las acciones relacionadas con la UI en el hilo de JavaFX.
 */
@Component
public class VentanaGenerarReporte {
    private Stage stage;
    private ControlGenerarReporte control;

    @FXML
    private ComboBox<TipoProducto> cmbTipoProducto;

    @FXML
    private ComboBox<String> cmbTipoReporte;

    @FXML
    private ComboBox<String> cmbPeriodicidad;

    @FXML
    private DatePicker dtpDesde;

    @FXML
    private DatePicker dtpHasta;

    private boolean initialized = false;

    public VentanaGenerarReporte() {
        // Don't initialize JavaFX components in constructor
    }

    /**
     * Inicializa la interfaz gráfica cargando el archivo FXML y configurando la ventana.
     *
     * Este método se asegura de ejecutarse únicamente una vez y en el hilo de JavaFX.
     * Si no se está en el hilo de JavaFX, programa la inicialización para ejecutarse en dicho hilo.
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
            stage.setTitle("Generar Reporte");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-generar-reporte.fxml"));
            loader.setController(this);
            Scene scene = new Scene(loader.load(), 700, 450);
            stage.setScene(scene);

            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Asigna el controlador lógico que maneja la generación de reportes.
     *
     * @param control instancia de ControlGenerarReporte
     */
    public void setControlGenerarReporte(ControlGenerarReporte control) {
        this.control = control;
    }

    /**
     * Muestra la ventana de generación de reportes.
     *
     * Inicializa la UI si no está inicializada, carga las opciones de los ComboBox,
     * y configura la habilitación de la periodicidad según el tipo de reporte seleccionado.
     * Se asegura que todo se ejecute en el hilo de JavaFX.
     */
    public void muestra() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::muestra);
            return;
        }

        initializeUI();

        // Carga opciones en los ComboBox
        cmbTipoProducto.setItems(FXCollections.observableArrayList(TipoProducto.values()));

        ObservableList<String> tipoReporte = FXCollections.observableArrayList("Tabla", "Grafica");
        cmbTipoReporte.setItems(tipoReporte);

        ObservableList<String> periodicidad = FXCollections.observableArrayList("Diario", "Mensual");
        cmbPeriodicidad.setItems(periodicidad);

        // Controla habilitación de periodicidad según tipo de reporte
        cmbTipoReporte.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if ("Grafica".equals(newVal)) {
                cmbPeriodicidad.setDisable(false);
            } else {
                cmbPeriodicidad.setDisable(true);
                cmbPeriodicidad.getSelectionModel().clearSelection();
            }
        });

        stage.show();
    }

    /**
     * Muestra un cuadro de diálogo informativo con un mensaje dado.
     * Se asegura de que se ejecute en el hilo de JavaFX.
     *
     * @param mensaje texto a mostrar al usuario
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
     * Inicializa la UI si es necesario.
     *
     * @param visible true para mostrar, false para ocultar la ventana
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
     * Manejador del evento cuando el usuario solicita generar un reporte.
     *
     * Valida que si el tipo de reporte es "Grafica", se haya seleccionado una periodicidad,
     * y llama al controlador para generar el reporte con los parámetros seleccionados.
     */
    @FXML
    private void handleGenerar() {
        if (Objects.equals(cmbTipoReporte.getValue(), "Grafica")) {
            if (cmbPeriodicidad.getValue() == null) {
                muestraDialogoConMensaje("Seleccione una periodicidad");
            } else {
                control.reporteGenerado(
                        dtpDesde.getValue(),
                        dtpHasta.getValue(),
                        cmbTipoReporte.getValue(),
                        cmbPeriodicidad.getValue(),
                        cmbTipoProducto.getValue());
            }
        } else {
            control.reporteGenerado(
                    dtpDesde.getValue(),
                    dtpHasta.getValue(),
                    cmbTipoReporte.getValue(),
                    "Diario", // periodicidad por defecto para "Tabla"
                    cmbTipoProducto.getValue());
        }
    }

    /**
     * Manejador del evento cuando el usuario cancela la generación de reporte.
     * Notifica al controlador y cierra la ventana.
     */
    @FXML
    private void handleCancelar() {
        control.termina();
    }
}