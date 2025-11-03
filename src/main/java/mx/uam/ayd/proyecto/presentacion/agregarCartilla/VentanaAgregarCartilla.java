package mx.uam.ayd.proyecto.presentacion.agregarCartilla;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import mx.uam.ayd.proyecto.negocio.modelo.Cartilla;
import mx.uam.ayd.proyecto.negocio.modelo.VacunaEnum;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class VentanaAgregarCartilla {

    private ControlAgregarCartilla control;

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

    private ObservableList<Cartilla> vacunasObservableList;

    public void setControl(ControlAgregarCartilla control) {
        this.control = control;
        inicializarControles();
    }

    @FXML
    public void initialize() {
        // La inicialización básica se hace aquí
        configurarTabla();
        vacunasObservableList = FXCollections.observableArrayList();
        tablaVacunas.setItems(vacunasObservableList);
        dpFechaAplicacion.setValue(LocalDate.now());
    }

    private void inicializarControles() {
        // La inicialización que depende del control se hace aquí
        if (control != null) {
            cmbVacuna.setItems(FXCollections.observableArrayList(control.obtenerTodasLasVacunas()));
        }
    }

    private void configurarTabla() {
        colVacuna.setCellValueFactory(cellData -> {
            VacunaEnum vacuna = cellData.getValue().getVacuna();
            return new javafx.beans.property.SimpleStringProperty(vacuna != null ? vacuna.name() : "");
        });
        colFechaAplicacion.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("fechaAplicacion"));
        colProximaDosis.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("proximaDosis"));
        colVeterinario.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("veterinario"));
        colLote.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("lote"));
        colObservaciones.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("observaciones"));
    }

    @FXML
    private void cargarCartilla() {
        try {
            Long mascotaId = Long.parseLong(txtMascotaId.getText().trim());
            List<Cartilla> cartillas = control.obtenerCartillaPorMascota(mascotaId);
            vacunasObservableList.setAll(cartillas);
            mostrarAlerta("Éxito", "Cartilla cargada para mascota ID: " + mascotaId, Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Por favor ingrese un ID de mascota válido", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void registrarVacuna() {
        try {
            // Validaciones
            if (cmbVacuna.getValue() == null) {
                mostrarAlerta("Error", "Seleccione una vacuna", Alert.AlertType.ERROR);
                return;
            }

            if (dpFechaAplicacion.getValue() == null) {
                mostrarAlerta("Error", "Seleccione la fecha de aplicación", Alert.AlertType.ERROR);
                return;
            }

            if (txtVeterinario.getText().trim().isEmpty()) {
                mostrarAlerta("Error", "Ingrese el nombre del veterinario", Alert.AlertType.ERROR);
                return;
            }

            if (txtMascotaId.getText().trim().isEmpty()) {
                mostrarAlerta("Error", "Ingrese el ID de la mascota", Alert.AlertType.ERROR);
                return;
            }

            Long lote = null;
            if (!txtLote.getText().trim().isEmpty()) {
                lote = Long.parseLong(txtLote.getText().trim());
            }

            Long mascotaId = Long.parseLong(txtMascotaId.getText().trim());

            // Registrar la vacuna a través del control
            Cartilla nuevaVacuna = control.registrarVacuna(
                    cmbVacuna.getValue(),
                    dpFechaAplicacion.getValue(),
                    txtVeterinario.getText().trim(),
                    lote,
                    txtObservaciones.getText().trim(),
                    mascotaId
            );

            // Actualizar tabla
            vacunasObservableList.add(nuevaVacuna);

            // Limpiar formulario
            limpiarFormulario();

            mostrarAlerta("Éxito", "Vacuna registrada correctamente", Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El lote debe ser un número válido", Alert.AlertType.ERROR);
        }
    }

    private void limpiarFormulario() {
        cmbVacuna.setValue(null);
        dpFechaAplicacion.setValue(LocalDate.now());
        txtVeterinario.clear();
        txtLote.clear();
        txtObservaciones.clear();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void muestra() {
        // Método para mostrar la ventana
        //tablaVacunas.refresh();
        initialize();
        inicializarControles();
        configurarTabla();

    }
}