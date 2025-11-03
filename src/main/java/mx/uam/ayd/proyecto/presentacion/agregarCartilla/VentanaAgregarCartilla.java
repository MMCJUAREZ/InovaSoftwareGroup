package mx.uam.ayd.proyecto.presentacion.agregarCartilla;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.modelo.Cartilla;
import mx.uam.ayd.proyecto.negocio.modelo.VacunaEnum;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Component
public class VentanaAgregarCartilla {

    private Stage stage;
    private ControlAgregarCartilla control;
    private ObservableList<Cartilla> cartillasData = FXCollections.observableArrayList();

    @FXML
    private TableView<Cartilla> tablaVacunas;
    @FXML
    private TableColumn<Cartilla, String> colVacuna;
    @FXML
    private TableColumn<Cartilla, LocalDate> colFechaAplicacion;
    @FXML
    private TableColumn<Cartilla, LocalDate> colProximaDosis;
    @FXML
    private TableColumn<Cartilla, String> colVeterinario;
    @FXML
    private TableColumn<Cartilla, Long> colLote;
    @FXML
    private TableColumn<Cartilla, String> colObservaciones;

    @FXML
    private TextField txtMascotaId;
    @FXML
    private ComboBox<VacunaEnum> cmbVacuna;
    @FXML
    private DatePicker dpFechaAplicacion;
    @FXML
    private TextField txtVeterinario;
    @FXML
    private TextField txtLote;
    @FXML
    private TextField txtObservaciones;

    /**
     * Muestra la ventana principal de gestión de cartillas
     */
    public void muestra(ControlAgregarCartilla control) {
        this.control = control;

        if (stage == null) {
            try {
                stage = new Stage();
                stage.setTitle("Agregar Cartilla de Vacunación");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-agregar-cartilla.fxml"));
                loader.setController(this);
                Scene scene = new Scene(loader.load(), 800, 600);
                stage.setScene(scene);

                configurarTabla();
                inicializarControles();

            } catch (IOException e) {
                e.printStackTrace();
                muestraAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cargar la interfaz: " + e.getMessage());
            }
        }

        stage.show();
    }

    /**
     * Configura las columnas de la tabla
     */
    private void configurarTabla() {
        colVacuna.setCellValueFactory(cellData -> {
            VacunaEnum vacuna = cellData.getValue().getVacuna();
            return new javafx.beans.property.SimpleStringProperty(vacuna != null ? vacuna.name() : "");
        });
        colFechaAplicacion.setCellValueFactory(new PropertyValueFactory<>("fechaAplicacion"));
        colProximaDosis.setCellValueFactory(new PropertyValueFactory<>("proximaDosis"));
        colVeterinario.setCellValueFactory(new PropertyValueFactory<>("veterinario"));
        colLote.setCellValueFactory(new PropertyValueFactory<>("lote"));
        colObservaciones.setCellValueFactory(new PropertyValueFactory<>("observaciones"));

        tablaVacunas.setItems(cartillasData);
    }

    /**
     * Inicializa los controles de la ventana
     */
    private void inicializarControles() {
        if (control != null) {
            // Obtener todas las vacunas disponibles del control
            List<VacunaEnum> vacunas = control.obtenerTodasLasVacunas();
            if (vacunas != null) {
                cmbVacuna.setItems(FXCollections.observableArrayList(vacunas));
            }
        }
        dpFechaAplicacion.setValue(LocalDate.now());
    }

    /**
     * Actualiza la tabla con la lista de cartillas
     */
    public void actualizaTabla(List<Cartilla> cartillas) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.actualizaTabla(cartillas));
            return;
        }

        cartillasData.clear();
        if (cartillas != null) {
            cartillasData.addAll(cartillas);
        }
    }

    @FXML
    private void handleCargarCartilla() {
        try {
            Long mascotaId = Long.parseLong(txtMascotaId.getText().trim());
            control.solicitaCargarCartilla(mascotaId);
        } catch (NumberFormatException e) {
            muestraAlerta(Alert.AlertType.ERROR, "Error de formato", "Por favor ingrese un ID de mascota válido.");
        }
    }

    @FXML
    private void handleRegistrarVacuna() {
        // Validaciones básicas
        if (cmbVacuna.getValue() == null) {
            muestraAlerta(Alert.AlertType.WARNING, "Vacuna no seleccionada", "Por favor, seleccione una vacuna.");
            return;
        }

        if (dpFechaAplicacion.getValue() == null) {
            muestraAlerta(Alert.AlertType.WARNING, "Fecha no seleccionada", "Por favor, seleccione la fecha de aplicación.");
            return;
        }

        if (txtVeterinario.getText().trim().isEmpty()) {
            muestraAlerta(Alert.AlertType.WARNING, "Veterinario requerido", "Por favor, ingrese el nombre del veterinario.");
            return;
        }

        if (txtMascotaId.getText().trim().isEmpty()) {
            muestraAlerta(Alert.AlertType.WARNING, "ID de mascota requerido", "Por favor, ingrese el ID de la mascota.");
            return;
        }

        try {
            Long mascotaId = Long.parseLong(txtMascotaId.getText().trim());
            Long lote = null;

            if (!txtLote.getText().trim().isEmpty()) {
                lote = Long.parseLong(txtLote.getText().trim());
            }

            control.solicitaRegistrarVacuna(
                    cmbVacuna.getValue(),
                    dpFechaAplicacion.getValue(),
                    txtVeterinario.getText().trim(),
                    lote,
                    txtObservaciones.getText().trim(),
                    mascotaId
            );

        } catch (NumberFormatException e) {
            muestraAlerta(Alert.AlertType.ERROR, "Error de formato", "El lote debe ser un número válido.");
        }
    }

    @FXML
    private void handleLimpiarFormulario() {
        limpiarFormulario();
    }

    @FXML
    private void handleCerrar() {
        if (stage != null) {
            stage.close();
        }
    }

    /**
     * Establece el control de la ventana
     */
    public void setControl(ControlAgregarCartilla control) {
        this.control = control;
        // Si la ventana ya fue inicializada, actualizar los controles
        if (stage != null) {
            inicializarControles();
        }
    }

    /**
     * Muestra una alerta al usuario
     */
    public void muestraAlerta(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Muestra una confirmación al usuario
     */
    public boolean muestraConfirmacion(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }

    /**
     * Limpia el formulario después de un registro exitoso
     */
    public void limpiarFormulario() {
        Platform.runLater(() -> {
            cmbVacuna.setValue(null);
            dpFechaAplicacion.setValue(LocalDate.now());
            txtVeterinario.clear();
            txtLote.clear();
            txtObservaciones.clear();
        });
    }

    /**
     * Obtiene el stage de la ventana
     */
    public Stage getStage() {
        return stage;
    }
}