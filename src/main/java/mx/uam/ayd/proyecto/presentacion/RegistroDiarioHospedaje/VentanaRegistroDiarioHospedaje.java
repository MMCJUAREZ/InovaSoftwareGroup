package mx.uam.ayd.proyecto.presentacion.RegistroDiarioHospedaje;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.Parent;
import mx.uam.ayd.proyecto.negocio.ServicioRegistroHospedaje;
import mx.uam.ayd.proyecto.negocio.modelo.Hospedaje;
import mx.uam.ayd.proyecto.negocio.modelo.RegistroHospedaje;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Ventana encargada de mostrar y registrar la bitácora diaria de un hospedaje.
 * Permite visualizar registros previos y capturar uno nuevo.
 *
 * @author Mitzi
 */
@Component
public class VentanaRegistroDiarioHospedaje {


    @FXML private Label lblDueno;
    @FXML private Label lblMascota;
    @FXML private Label lblEntrada;
    @FXML private Label lblSalida;
    @FXML private Label lblHospedaje;
    @FXML private Label lblFecha;

    @FXML private TextArea taAlimentacion;
    @FXML private TextArea taSalud;
    @FXML private TextArea taComportamiento;
    @FXML private TextArea taObservaciones;
    @FXML private TableView<RegistroHospedaje> tableRegistros;

    @FXML private TableColumn<RegistroHospedaje, String> colFecha;
    @FXML private TableColumn<RegistroHospedaje, String> colAlimentacion;
    @FXML private TableColumn<RegistroHospedaje, String> colSalud;
    @FXML private TableColumn<RegistroHospedaje, String> colComportamiento;
    @FXML private TableColumn<RegistroHospedaje, String> colObservaciones;

    private Hospedaje hospedaje;
    private Stage stage;

    // Dependencias
    @Autowired
    private ServicioRegistroHospedaje registroService;

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Muestra la ventana en pantalla. Si no se está en el hilo de JavaFX,
     * reprograma la ejecución adecuadamente.
     */
    public void muestra() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::muestra);
            return;
        }

        initializeUI();
        stage.show();
    }

    /**
     * Inicializa la UI y el Stage si es la primera vez que se abre la ventana.
     */
    private void initializeUI() {
        if (stage != null) {
            return; // Ya inicializado
        }

        try {
            stage = new Stage();
            stage.setTitle("Registro Diario de Hospedaje");

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/ventana-registro-diario-hospedaje.fxml")
            );

            Parent root = loader.load();
            initialize();

            Scene scene = new Scene(root); // Usar el root aquí
            stage.setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return this.stage;
    }

    /**
     * Asigna el {@link Hospedaje} activo y prepara la UI.
     *
     * @param hospedaje Hospedaje seleccionado.
     */
    public void setHospedaje(Hospedaje hospedaje) {
        // Etiqueta superior con el nombre de la mascota y el ID
        lblHospedaje.setText(String.format(
                "%s (ID: %s)",
                hospedaje.getMascota().getNombre(),
                hospedaje.getIdHospedaje()
        ));

        // Mostrar datos del dueño, mascota y fechas del hospedaje
        if (hospedaje.getCliente() != null) {
            lblDueno.setText(hospedaje.getCliente().getNombreCompleto());
        } else {
            lblDueno.setText("(sin dueño)");
        }

        if (hospedaje.getMascota() != null) {
            lblMascota.setText(hospedaje.getMascota().getNombre());
        } else {
            lblMascota.setText("(sin mascota)");
        }

        if (hospedaje.getFechaEntrada() != null) {
            lblEntrada.setText(hospedaje.getFechaEntrada().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        } else {
            lblEntrada.setText("(sin fecha)");
        }

        if (hospedaje.getFechaSalida() != null) {
            lblSalida.setText(hospedaje.getFechaSalida().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        } else {
            lblSalida.setText("(sin fecha)");
        }

        // Fecha actual para el registro diario
        lblFecha.setText(java.time.LocalDateTime.now().format(fmt));

        // Cargar historial previo
        cargaRegistros();
    }

    /**
     * Inicializa las columnas de la tabla y la configuración visual.
     */
    @FXML
    public void initialize() {
        if (colFecha != null) {
            colFecha.setCellValueFactory(cell -> {
                var v = cell.getValue().getFechaRegistro();
                return new javafx.beans.property.SimpleStringProperty(
                        v == null ? "" : v.format(fmt)
                );
            });
        }

        if (colAlimentacion != null)
            colAlimentacion.setCellValueFactory(new PropertyValueFactory<>("alimentacion"));

        if (colSalud != null)
            colSalud.setCellValueFactory(new PropertyValueFactory<>("salud"));

        if (colComportamiento != null)
            colComportamiento.setCellValueFactory(new PropertyValueFactory<>("comportamiento"));

        if (colObservaciones != null)
            colObservaciones.setCellValueFactory(new PropertyValueFactory<>("observaciones"));
    }

    /**
     * Carga en la tabla todos los registros asociados al hospedaje.
     */
    private void cargaRegistros() {
        if (hospedaje == null) return;

        List<RegistroHospedaje> lista = registroService.listarPorHospedaje(hospedaje);
        tableRegistros.getItems().setAll(lista);
    }

    /**
     * Guarda un nuevo registro diario en la base de datos.
     */
    @FXML
    private void handleGuardarRegistro() {
        try {
            String alimentacion = taAlimentacion.getText();
            String salud = taSalud.getText();
            String comportamiento = taComportamiento.getText();
            String observaciones = taObservaciones.getText();

            registroService.registrar(
                    hospedaje,
                    alimentacion,
                    salud,
                    comportamiento,
                    observaciones,
                    null
            );

            mostrarAlerta(Alert.AlertType.INFORMATION,
                    "Registro guardado",
                    "Registro diario guardado correctamente."
            );

            limpiarCampos();
            cargaRegistros();

        } catch (Exception ex) {
            mostrarAlerta(Alert.AlertType.ERROR,
                    "Error al guardar",
                    ex.getMessage()
            );
        }
    }

    /**
     * Limpia los campos tras un guardado exitoso.
     */
    private void limpiarCampos() {
        taAlimentacion.clear();
        taSalud.clear();
        taComportamiento.clear();
        taObservaciones.clear();
        lblFecha.setText(java.time.LocalDateTime.now().format(fmt));
    }

    /**
     * Cierra la ventana.
     */
    @FXML
    private void handleCerrar() {
        if (stage != null) {
            stage.close();
        }
    }


    /**
     * Muestra una alerta modal al usuario.
     *
     * @param type Tipo de alerta.
     * @param title Título del cuadro de diálogo.
     * @param msg Mensaje a mostrar.
     */
    private void mostrarAlerta(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

}