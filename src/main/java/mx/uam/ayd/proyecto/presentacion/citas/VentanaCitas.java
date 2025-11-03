package mx.uam.ayd.proyecto.presentacion.citas;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.scene.control.DatePicker;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import mx.uam.ayd.proyecto.negocio.modelo.Cita;
import mx.uam.ayd.proyecto.negocio.modelo.TipoCita;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
public class VentanaCitas {

    private Stage stage;
    private ControlCitas control;
    private ObservableList<Cita> citasData = FXCollections.observableArrayList();
    private boolean initialized = false;

    // Componentes FXML de la tabla

    @FXML private TableView<Cita> tableCitas;
    @FXML private TableColumn<Cita, Long> idColumn;
    @FXML private TableColumn<Cita, String> fechaHoraColumn;
    @FXML private TableColumn<Cita, TipoCita> tipoColumn;
    @FXML private TableColumn<Cita, String> nombreColumn;
    @FXML private TableColumn<Cita, String> contactoColumn;
    @FXML private TableColumn<Cita, String> estadoColumn; // Columna para mostrar si está atendida o no

    public VentanaCitas() {

        // Inicialización diferida

    }

    public void setControl(ControlCitas control) {

        this.control = control;

    }

    private void initializeUI() {

        if (initialized) return;

        if (!Platform.isFxApplicationThread()) {

            Platform.runLater(this::initializeUI);
            return;

        }

        try {

            stage = new Stage();
            stage.setTitle("Gestión de Citas");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-gestionar-citas.fxml"));
            loader.setController(this);
            Scene scene = new Scene(loader.load(), 800, 550);
            stage.setScene(scene);

            // Configurar columnas

            idColumn.setCellValueFactory(new PropertyValueFactory<>("idCita"));
            tipoColumn.setCellValueFactory(new PropertyValueFactory<>("tipo"));
            nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombreSolicitante"));
            contactoColumn.setCellValueFactory(new PropertyValueFactory<>("contacto"));

            // Formato de Fecha y Hora

            fechaHoraColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(
                            cellData.getValue().getFechaHora().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
                    )
            );

            // Columna de Estado (Pendiente/Atendida)

            estadoColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().isAtendida() ? "Atendida" : "Pendiente")
            );

            tableCitas.setItems(citasData);
            initialized = true;

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Muestra la ventana y carga la lista inicial de citas.
     * @param citas Lista de citas a mostrar.
     */

    public void muestra(List<Cita> citas) {

        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra(citas));
            return;

        }

        initializeUI();
        actualizarTabla(citas);
        stage.show();

    }

    /**
     * Actualiza la tabla con una nueva lista de citas.
     * @param citas Nueva lista de citas.
     */

    public void actualizarTabla(List<Cita> citas) {

        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.actualizarTabla(citas));
            return;

        }

        citasData.clear();
        citasData.addAll(citas);

    }

    // Handlers de la Vista Principal

    @FXML
    private void handleCrearCita() {
        mostrarFormulario(null);
    }

    @FXML
    private void handleModificarCita() {

        Cita citaSeleccionada = tableCitas.getSelectionModel().getSelectedItem();
        if (citaSeleccionada == null) {
            muestraAlerta("Advertencia", "Seleccione una cita para modificar.", "WARNING");
            return;

        }
        if (citaSeleccionada.isAtendida()) {

            muestraAlerta("Advertencia", "No se puede modificar una cita que ya fue atendida.", "WARNING");
            return;
        }

        mostrarFormulario(citaSeleccionada);

    }

    @FXML
    private void handleEliminarCita() {

        Cita citaSeleccionada = tableCitas.getSelectionModel().getSelectedItem();
        if (citaSeleccionada == null) {
            muestraAlerta("Advertencia", "Seleccione una cita para cancelar.", "WARNING");
            return;

        }

        if (mostrarConfirmacion("Confirmación", "¿Está seguro de que desea cancelar la cita seleccionada?")) {
            control.eliminarCita(citaSeleccionada.getIdCita());
        }
    }

    @FXML
    private void handleCerrar() {
        stage.close();
    }

    // Métodos de Interfaz y Lógica Auxiliar

    /**
     * Muestra un formulario modal para crear o modificar una cita.
     * @param cita Cita a modificar (null si es nueva).
     */

    private void mostrarFormulario(Cita cita) {
        Dialog<Cita> dialog = new Dialog<>();
        dialog.setTitle(cita == null ? "Agendar Nueva Cita" : "Modificar Cita ID: " + cita.getIdCita());
        dialog.initModality(Modality.APPLICATION_MODAL);

        // Controles del formulario

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        DatePicker datePicker = new DatePicker(cita != null ? cita.getFechaHora().toLocalDate() : LocalDate.now());
        TextField timeField = new TextField(cita != null ? cita.getFechaHora().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) : "09:00");
        ComboBox<TipoCita> tipoCombo = new ComboBox<>(FXCollections.observableArrayList(TipoCita.values()));
        TextField nombreField = new TextField(cita != null ? cita.getNombreSolicitante() : "");
        TextField contactoField = new TextField(cita != null ? cita.getContacto() : "");

        tipoCombo.getSelectionModel().select(cita != null ? cita.getTipo() : TipoCita.Consulta);

        grid.add(new Label("Fecha:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Hora (HH:mm):"), 0, 1);
        grid.add(timeField, 1, 1);
        grid.add(new Label("Tipo de Servicio:"), 0, 2);
        grid.add(tipoCombo, 1, 2);
        grid.add(new Label("Nombre:"), 0, 3);
        grid.add(nombreField, 1, 3);
        grid.add(new Label("Contacto (Correo/Teléfono):"), 0, 4);
        grid.add(contactoField, 1, 4);

        // Checkbox para envío de correo (solo para agendar)

        CheckBox enviarCorreoCheck = null;

        if (cita == null) {
            enviarCorreoCheck = new CheckBox("Enviar confirmación por correo (si el contacto es un email)");
            grid.add(enviarCorreoCheck, 1, 5);
        }

        dialog.getDialogPane().setContent(grid);

        // Botones de acción

        ButtonType botonGuardar = new ButtonType(cita == null ? "Agendar" : "Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(botonGuardar, ButtonType.CANCEL);

        final CheckBox finalEnviarCorreoCheck = enviarCorreoCheck;

        // Lógica al presionar "Guardar" o "Agendar"

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == botonGuardar) {
                try {
                    LocalDate fecha = datePicker.getValue();
                    LocalTime hora = LocalTime.parse(timeField.getText());
                    LocalDateTime fechaHora = LocalDateTime.of(fecha, hora);

                    TipoCita tipo = tipoCombo.getValue();
                    String nombre = nombreField.getText();
                    String contacto = contactoField.getText();
                    boolean enviar = (cita == null) ? finalEnviarCorreoCheck.isSelected() : false;

                    if (cita == null) {
                        // Crear
                        control.agendarCita(fechaHora, tipo, nombre, contacto, enviar);
                    } else {
                        // Modificar
                        control.modificarCita(cita.getIdCita(), fechaHora, tipo, nombre, contacto);
                    }
                    return null; // Operación manejada por el controlador

                } catch (Exception e) {
                    // Muestra el error de validación del servicio o formato de hora
                    muestraAlerta("Error", "Error al procesar la cita: " + e.getMessage(), "ERROR");
                    return null; // No cerrar el diálogo o simplemente dejar que se cierre si no hay más errores
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    /**
     * Muestra un cuadro de diálogo informativo o de error.
     * @param title Título del diálogo.
     * @param message Mensaje a mostrar.
     * @param type Tipo de alerta (ERROR, INFORMATION, etc.).
     */

    public void muestraAlerta(String title, String message, String type) {

        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestraAlerta(title, message, type));
            return;
        }

        AlertType alertType = AlertType.INFORMATION;
        if ("ERROR".equalsIgnoreCase(type)) {
            alertType = AlertType.ERROR;
        } else if ("WARNING".equalsIgnoreCase(type)) {
            alertType = AlertType.WARNING;
        }

        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Muestra una alerta de confirmación.
     * @param title Título.
     * @param message Mensaje.
     * @return true si el usuario confirma (OK), false si cancela.
     */

    public boolean mostrarConfirmacion(String title, String message) {

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();

    }

}