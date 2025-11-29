package mx.uam.ayd.proyecto.presentacion.generarReceta;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.Spinner;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javafx.util.Callback;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import mx.uam.ayd.proyecto.negocio.modelo.UnidadProducto;
import mx.uam.ayd.proyecto.negocio.modelo.UsoVeterinario;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
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

    @FXML private TextField txtCorreo;

    @FXML private TableColumn<DatosReceta, String> clmMedicamento;
    @FXML private TableColumn<DatosReceta, Integer> clmDosis;
    @FXML private TableColumn<DatosReceta, Integer> clmCada;
    @FXML private TableColumn<DatosReceta, Integer> clmHasta;
    @FXML private TableColumn<DatosReceta, String> clmNota;

    @FXML private Button btnGenerarReceta;

    @FXML private Pane panePrincipal;

    /** Lista observable que posee la informacion de la tabla de la receta. */
    private final ObservableList<DatosReceta> datosReceta = FXCollections.observableArrayList();

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

            Scene scene = new Scene(loader.load(), 645, 804);
            stage.setScene(scene);

            clmMedicamento.setCellValueFactory(new PropertyValueFactory<>("NombreProducto"));

            clmDosis.setCellValueFactory(new PropertyValueFactory<>("Dosis"));
            clmDosis.setCellFactory(crearSpinnerCellFactory("", 1, 1000, DatosReceta::setDosis));

            clmCada.setCellValueFactory(new PropertyValueFactory<>("Cada"));
            clmCada.setCellFactory(crearSpinnerCellFactory("hrs", 1, 1000, DatosReceta::setHasta));


            clmHasta.setCellValueFactory(new PropertyValueFactory<>("Hasta"));
            clmHasta.setCellFactory(crearSpinnerCellFactory("dosis", 1, 1000, DatosReceta::setHasta));

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

            soloNumeros(txtCada);
            soloNumeros(txtDosis);
            soloNumeros(txtHasta);
            validarEmail(txtCorreo);

            initialized = true;

        }catch (IOException e) {
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

        txtCorreo.setText("");
        txtDosis.setText("");
        txtCada.setText("");
        txtHasta.setText("");

        datosReceta.clear();
        tblReceta.setItems(datosReceta);
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
        if(!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.setVisible(visible));
            return;
        }
        if(!initialized && visible) {
            initializeUI();
        }
        if(visible) {
            stage.show();
        }else {
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
        List<Producto> medicamentos = control.filtrarMedicamentos(uso, presentacion);
        cmbMedicamento.setItems(FXCollections.observableArrayList(medicamentos));
        if(medicamentos.isEmpty()) {
            muestraDialogoConMensaje("No se encontraron productos con los filtros seleccionados");
            cmbMedicamento.setPromptText("No hay productos");
        }else {
            cmbMedicamento.getSelectionModel().selectFirst();
        }
    }

    /**
     * @brief Filtra medicamentos según la presentación seleccionada.
     */
    @FXML
    private void onActionPresentacion() {
        String uso = cmbUsoVeterinario.getValue().toString();
        String presentacion = cmbPresentacion.getValue();
        List<Producto> medicamentos = control.filtrarMedicamentos(uso, presentacion);
        cmbMedicamento.setItems(FXCollections.observableArrayList(medicamentos));
        if(medicamentos.isEmpty()) {
            muestraDialogoConMensaje("No se encontraron productos con los filtros seleccionados");
            cmbMedicamento.setPromptText("No hay productos");
        }else {
            cmbMedicamento.getSelectionModel().selectFirst();
        }
    }

    /**
     * @brief Agrega un medicamento a la tabla de la receta.
     *
     * Valida campos obligatorios antes de agregarlo.
     */
    @FXML
    private void handleAgregar() {
        ButtonType buttonYes = new ButtonType("Sí", ButtonBar.ButtonData.YES);
        ButtonType buttonNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        if(cmbMedicamento.getValue() == null || txtCada.getText().isEmpty() || txtDosis.getText().isEmpty() || txtHasta.getText().isEmpty()) {
            muestraDialogoConMensaje("Llene los campos obligatorios");
            return;
        }
        DatosReceta nuevaMedicacion = new DatosReceta(cmbMedicamento.getValue(), Integer.parseInt(txtDosis.getText()), Integer.parseInt(txtCada.getText()), Integer.parseInt(txtHasta.getText()), txtAreaNota.getText());
        if(datosReceta.isEmpty()) {
            datosReceta.add(nuevaMedicacion);
            tblReceta.setItems(datosReceta);
        }else {
            for(DatosReceta r : datosReceta) {
                if(r.getProducto().equals(nuevaMedicacion.getProducto())) {
                    muestraDialogoConMensaje("El medicamento ya esta en la receta");
                    return;
                }
                if(!r.getProducto().getUsoVeterinario().equals(nuevaMedicacion.getProducto().getUsoVeterinario())) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Alerta");
                    alert.setHeaderText("El uso veterinario es distinto a los demás medicamentos");
                    alert.setContentText("¿Desea continuar?");
                    alert.getButtonTypes().setAll(buttonYes, buttonNo);

                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.isPresent() && result.get() == buttonYes) {
                        datosReceta.add(nuevaMedicacion);
                        tblReceta.setItems(datosReceta);
                    }
                    return;
                }
            }
        }
    }

    /**
     * @brief Genera la receta en PDF usando el controlador.
     */
    @FXML
    private void handleGenerar() {
        if(datosReceta.isEmpty()) {
            muestraDialogoConMensaje("Agruga almenos un medicamento");
            return;
        }
        panePrincipal.setDisable(true);
        try {
            control.generarReceta(datosReceta, txtCorreo.getText());
        }catch(Exception e) {
            muestraDialogoConMensaje(e.getMessage());
        }
        panePrincipal.setDisable(false);
    }

    /**
     * @brief Eliminar un medicamento seleccionado (pendiente de implementar).
     */
    @FXML
    private void handleEliminar() {
        datosReceta.remove(tblReceta.getSelectionModel().getSelectedItem());
        tblReceta.setItems(datosReceta);
    }

    /**
     * @brief Cancela la operación y cierra la ventana.
     */
    @FXML
    private void handleCancelar() {
        control.termina("Receta cancelada");
    }

    /**
     * @brief Crea una fábrica de celdas para una columna de TableView que usa un Spinner.
     *
     * Esta fábrica permite insertar un Spinner dentro de la celda para editar valores numéricos
     * directamente desde la tabla. Cada cambio en el Spinner actualiza el objeto DatosReceta
     * correspondiente usando el setter proporcionado.
     *
     * @param unidad   Texto que se mostrará como unidad junto al Spinner.
     * @param min      Valor mínimo permitido en el Spinner.
     * @param max      Valor máximo permitido en el Spinner.
     * @param setter   Función que recibe (DatosReceta, nuevoValor) para actualizar la propiedad correspondiente.
     *
     * @return Callback que genera celdas personalizadas con Spinner para la columna.
     */
    private Callback<TableColumn<DatosReceta, Integer>, TableCell<DatosReceta, Integer>>
    crearSpinnerCellFactory(String unidad, int min, int max, BiConsumer<DatosReceta, Integer> setter) {
        return column -> new TableCell<DatosReceta, Integer>() {

            /** Spinner usado para editar el valor numérico dentro de la celda. */
            private final Spinner<Integer> spinner = new Spinner<>(min, max, min); {
                spinner.setEditable(true);
                spinner.setMaxWidth(60);
                spinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                    DatosReceta medicacion = getTableView().getItems().get(getIndex());
                    if (medicacion != null) setter.accept(medicacion, newVal);
                });
            }
            private final Label lbl = new Label(" " + unidad);
            private final HBox box = new HBox(5, spinner, lbl);

            /**
             * @brief Actualiza la celda cuando cambia su estado o el valor mostrado.
             *
             * @param value  Valor entero mostrado en el Spinner.
             * @param empty  Indica si la celda está vacía.
             */
            @Override
            protected void updateItem(Integer value, boolean empty) {
                super.updateItem(value, empty);
                if(empty || value == null) {
                    setGraphic(null);
                    return;
                }
                spinner.getValueFactory().setValue(value);
                setGraphic(box);
                setText(null);
            }
        };
    }

    /**
     * @brief Restringe un TextField para que solo acepte números.
     *
     * @param textField Campo de texto al que se aplicará la restricción.
     */
    public static void soloNumeros(TextField textField) {
        TextFormatter<Integer> formatter = new TextFormatter<>(
                change -> {
                    String newText = change.getControlNewText();
                    if(newText.matches("\\d*")) {
                        return change;
                    }
                    return null;
                }
        );
        textField.setTextFormatter(formatter);
    }

    /**
     * @brief Valida en tiempo real si un TextField contiene un correo electrónico válido.
     *
     * @param txt Campo de texto a validar.
     */
    public static void validarEmail(TextField txt) {
        txt.textProperty().addListener((obs, oldText, newText) -> {
            if(newText.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                txt.setStyle("-fx-border-color: green;");
            }else {
                txt.setStyle("-fx-border-color: red;");
            }
        });
    }

}
