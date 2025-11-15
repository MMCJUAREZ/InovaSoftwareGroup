package mx.uam.ayd.proyecto.presentacion.generarReceta;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import mx.uam.ayd.proyecto.negocio.modelo.UnidadProducto;
import mx.uam.ayd.proyecto.negocio.modelo.UsoVeterinario;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @class VentanaGenerarReceta
 * @brief Ventana JavaFX encargada de gestionar la interfaz para generar recetas veterinarias.
 *
 * Esta clase controla la carga de la UI, el llenado de campos,
 * la administración de la tabla de medicamentos y la interacción
 * con el controlador lógico {@link ControlGenerarReceta}.
 */
@Component
public class VentanaGenerarReceta {

    /** Ventana principal (Stage) de la interfaz. */
    private Stage stage;

    /** Controlador lógico encargado de gestionar la receta. */
    private ControlGenerarReceta control;

    @FXML private ComboBox<UsoVeterinario> cmbUsoVeterinario;
    @FXML private ComboBox<String> cmbPresentacion;
    @FXML private ComboBox<Producto> cmbMedicamento;

    @FXML private TextField txtDosis;
    @FXML private TextField txtCada;
    @FXML private TextField txtHasta;

    @FXML private TextArea txtAreaNota;

    @FXML private TableView<DatosReceta> tblReceta;

    @FXML private TableColumn<DatosReceta, String> clmMedicamento;
    @FXML private TableColumn<DatosReceta, String> clmDosis;
    @FXML private TableColumn<DatosReceta, String> clmCada;
    @FXML private TableColumn<DatosReceta, String> clmHasta;
    @FXML private TableColumn<DatosReceta, String> clmNota;

    /** Lista observable que posee la informacion de la tabla de la receta. */
    private ObservableList<DatosReceta> datosReceta = FXCollections.observableArrayList();

    /** Permite evitar inicializaciones dobles de la ventana. */
    private boolean initialized = false;

    /** Constructor vacío requerido por Spring. */
    public VentanaGenerarReceta() {
    }

    /**
     * @brief Inicializa la UI desde el archivo FXML.
     *
     * Carga la ventana, asigna propiedades, llena tablas y ComboBox.
     * Este método solo se ejecuta una vez y garantiza ejecución en el hilo JavaFX.
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
            stage.setTitle("Generar Receta");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-generar-receta.fxml"));
            loader.setController(this);

            Scene scene = new Scene(loader.load(), 700, 450);
            stage.setScene(scene);

            // Configurar tabla
            tblReceta.setItems(datosReceta);
            clmMedicamento.setCellValueFactory(new PropertyValueFactory<>("Producto"));
            clmDosis.setCellValueFactory(new PropertyValueFactory<>("Dosis"));
            clmCada.setCellValueFactory(new PropertyValueFactory<>("Cada"));
            clmHasta.setCellValueFactory(new PropertyValueFactory<>("Hasta"));
            clmNota.setCellValueFactory(new PropertyValueFactory<>("Nota"));

            // Llenar ComboBox de uso veterinario
            cmbUsoVeterinario.setItems(FXCollections.observableArrayList(UsoVeterinario.values()));

            // Llenar ComboBox de presentación filtrando Productos que sean medicamentos
            ObservableList<String> presentaciones = FXCollections.observableArrayList(UnidadProducto.values())
                    .stream()
                    .filter(unidad -> "Medicamento".equals(unidad.getTipoProducto()))
                    .map(UnidadProducto::name)
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));

            presentaciones.add("Sin filtro");
            cmbPresentacion.setItems(presentaciones);

            initialized = true;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @brief Asigna el controlador lógico para manejar la receta.
     * @param control Instancia de ControlGenerarReceta.
     */
    public void setControlGenerarReceta(ControlGenerarReceta control) {
        this.control = control;
    }

    /**
     * @brief Muestra la ventana con la lista de medicamentos disponibles.
     *
     * @param medicamentos Lista de medicamentos filtrados por el controlador.
     */
    public void muestra(List<Producto> medicamentos) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra(medicamentos));
            return;
        }

        initializeUI();

        cmbUsoVeterinario.getSelectionModel().selectFirst();
        cmbPresentacion.getSelectionModel().selectLast();

        cmbMedicamento.setItems(FXCollections.observableArrayList(medicamentos));
        stage.show();
    }

    /**
     * @brief Muestra un mensaje en un cuadro de diálogo informativo.
     * @param mensaje Texto del mensaje.
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
     * @brief Controla la visibilidad de la ventana.
     * @param visible true para mostrar, false para ocultar.
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
     * @brief Filtra medicamentos según el uso veterinario seleccionado.
     */
    @FXML
    private void onActionUsoVeterinario() {
        String uso = cmbUsoVeterinario.getValue().toString();
        String presentacion = cmbPresentacion.getValue();

        List<Producto> meds = control.filtrarMedicamentos(uso, presentacion);
        cmbMedicamento.setItems(FXCollections.observableArrayList(meds));
    }

    /**
     * @brief Filtra medicamentos según la presentación seleccionada.
     */
    @FXML
    private void onActionPresentacion() {
        String uso = cmbUsoVeterinario.getValue().toString();
        String presentacion = cmbPresentacion.getValue();

        List<Producto> meds = control.filtrarMedicamentos(uso, presentacion);
        cmbMedicamento.setItems(FXCollections.observableArrayList(meds));
    }

    /**
     * @brief Agrega un medicamento a la tabla de la receta.
     *
     * Valida campos obligatorios antes de agregarlo.
     */
    @FXML
    private void handleAgregar() {
        if (cmbMedicamento.getValue() == null ||
                txtCada.getText().isEmpty() ||
                txtDosis.getText().isEmpty() ||
                txtHasta.getText().isEmpty()) {

            muestraDialogoConMensaje("Llene los campos obligatorios");
            return;
        }

        datosReceta.add(new DatosReceta(
                cmbMedicamento.getValue(),
                txtDosis.getText(),
                txtCada.getText(),
                txtHasta.getText(),
                txtAreaNota.getText()
        ));

        tblReceta.setItems(datosReceta);
    }

    /**
     * @brief Genera la receta en PDF usando el controlador.
     */
    @FXML
    private void handleGenerar() {
        if (datosReceta.isEmpty()) {
            control.termina("Receta cancelada");
            return;
        }

        control.generarReceta(datosReceta);
    }

    /**
     * @brief Eliminar un medicamento seleccionado (pendiente de implementar).
     */
    @FXML
    private void handleEliminar() {
        // Pendiente
    }

    /**
     * @brief Cancela la operación y cierra la ventana.
     */
    @FXML
    private void handleCancelar() {
        control.termina("Receta cancelada");
    }
}
