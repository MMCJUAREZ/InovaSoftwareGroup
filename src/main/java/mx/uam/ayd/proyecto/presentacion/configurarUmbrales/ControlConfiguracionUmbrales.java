package mx.uam.ayd.proyecto.presentacion.configurarUmbrales;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import mx.uam.ayd.proyecto.negocio.ServicioUmbrales;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import mx.uam.ayd.proyecto.negocio.modelo.Umbral;

import java.io.IOException;
import java.util.List;

/**
 * @class ControlConfiguracionUmbrales
 * @brief Controlador para la configuración y edición de umbrales de productos.
 *
 * Esta clase actúa como intermediaria entre la capa de presentación (ventanas) y la
 * capa de negocio (`ServicioUmbrales`), permitiendo al usuario visualizar, editar y
 * validar los valores mínimos (umbrales) de stock de los productos.
 *
 * @details
 * - Permite mostrar una ventana con la lista de productos y sus umbrales.
 * - Gestiona el flujo de edición de un umbral específico.
 * - Realiza validaciones antes de guardar los cambios.
 * - Interactúa con la clase `VentanaConfiguracionUmbrales` para mostrar mensajes al usuario.
 *
 * @see VentanaConfiguracionUmbrales
 * @see ServicioUmbrales
 */
@Component
public class ControlConfiguracionUmbrales {

    /** Servicio para la gestión de umbrales. */
    @Autowired
    private ServicioUmbrales servicioUmbrales;

    /** Ventana de configuración de umbrales asociada. */
    @Autowired
    private VentanaConfiguracionUmbrales ventana;

    /** Producto actualmente seleccionado para edición. */
    private Producto producto;

    /** Ventana modal utilizada para la edición del umbral. */
    private Stage stage;

    // ==== Componentes FXML ====
    @FXML private TextField editUmbral;       /**< Campo para mostrar el nombre del producto en edición. */
    @FXML private TextField stockActual;      /**< Campo para mostrar el stock actual del producto. */
    @FXML private TextField umbralActual;     /**< Campo para mostrar el umbral configurado actualmente. */
    @FXML private TextField nuevoUmbral;      /**< Campo para mostrar el nuevo umbral seleccionado. */
    @FXML private ComboBox<Integer> nuevoUmbralCombo; /**< ComboBox para elegir el nuevo valor de umbral. */

    /**
     * @brief Inicializa los componentes de la ventana de edición de umbrales.
     *
     * Configura el combo de selección de nuevo umbral (1 a 100), establece los campos no editables,
     * y añade listeners para actualizar el campo `nuevoUmbral` según la selección.
     */
    @FXML
    private void initialize() {
        for (int i = 1; i <= 100; i++) {
            nuevoUmbralCombo.getItems().add(i);
        }

        nuevoUmbralCombo.setEditable(true);
        stockActual.setEditable(false);
        umbralActual.setEditable(false);
        editUmbral.setEditable(false);

        nuevoUmbralCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                nuevoUmbral.setText("Nuevo umbral: " + newVal);
            } else {
                nuevoUmbral.clear();
            }
        });
    }

    /**
     * @brief Asigna el controlador a la ventana de configuración de umbrales.
     */
    @PostConstruct
    public void init() {
        ventana.setControlConfiguracionUmbrales(this);
    }

    /**
     * @brief Inicia el flujo de visualización de productos con stock no nulo.
     *
     * Recupera de la capa de negocio los productos con stock disponible y los muestra en la ventana.
     * Si no hay productos, se muestra un mensaje de error.
     */
    public void inicia() {
        List<Producto> productos = servicioUmbrales.recuperaConStockNoCero();
        if (productos.isEmpty()) {
            ventana.mostrarError("No hay productos con stock disponible, por favor agregue manualmente productos primero");
        } else {
            ventana.muestra(productos);
        }
    }

    /**
     * @brief Inicia el flujo de edición de un umbral específico.
     *
     * @param idProducto ID del producto a editar.
     * @param minimo Valor mínimo actual del umbral.
     *
     * Carga la vista de edición (`ventana-editar-umbral.fxml`), inicializa los campos
     * con los datos del producto y muestra la ventana en modo modal.
     */
    public void iniciarEdicionDeUmbral(Long idProducto, int minimo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-editar-umbral.fxml"));
            loader.setController(this);

            Parent root = loader.load();

            this.producto = servicioUmbrales.recuperarProductoPorId(idProducto)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

            cargarDatosProducto();

            stage = new Stage();
            stage.setTitle("Editar Umbral - " + producto.getNombre());
            stage.setScene(new Scene(root, 600, 400));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            throw new RuntimeException("Error al cargar la ventana de edición", e);
        }
    }

    /**
     * @brief Carga en la interfaz los datos del producto actualmente seleccionado.
     */
    private void cargarDatosProducto() {
        editUmbral.setText(producto.getNombre());
        stockActual.setText(String.valueOf(producto.getCantidadStock()));

        Umbral umbral = servicioUmbrales.findById(producto.getIdProducto());
        if (umbral != null) {
            umbralActual.setText(String.valueOf(umbral.getValorMinimo()));
            nuevoUmbralCombo.setValue(umbral.getValorMinimo());
        } else {
            umbralActual.setText("No configurado");
            nuevoUmbralCombo.setValue(1);
        }
    }

    /**
     * @brief Maneja el evento de guardar cambios en el umbral.
     *
     * Valida que el nuevo valor ingresado sea un número entero entre 1 y 100,
     * guarda los cambios en la capa de negocio y actualiza la tabla en la ventana principal.
     */
    @FXML
    private void handleGuardar() {
        try {
            String textoIngresado = nuevoUmbralCombo.getEditor().getText();

            if (textoIngresado == null || textoIngresado.trim().isEmpty()) {
                ventana.mostrarError("Debe seleccionar o ingresar un valor para el nuevo umbral");
                return;
            }

            Integer nuevoValor = obtenerValorDesdeCombo();

            if (nuevoValor == null) {
                ventana.mostrarError("Debe ingresar un valor numérico para el nuevo umbral");
                return;
            }

            if (nuevoValor < 1) {
                ventana.mostrarError("El umbral debe ser ≥ 1");
                return;
            }
            if (nuevoValor > 100) {
                ventana.mostrarError("El umbral no puede ser mayor a 100");
                return;
            }

            servicioUmbrales.manejarEdicionUmbral(producto.getIdProducto(), nuevoValor);
            ventana.mostrarMensajeExitoDeActualizacion();
            ventana.actualizarUmbralEnTabla(producto.getIdProducto(), nuevoValor);
            cerrarVentana();

        } catch (Exception e) {
            ventana.mostrarError(e.getMessage());
        }
    }

    /**
     * @brief Obtiene el valor numérico del ComboBox.
     * @return Entero con el valor seleccionado o escrito, o `null` si no es válido.
     */
    private Integer obtenerValorDesdeCombo() {
        try {
            String texto = nuevoUmbralCombo.getEditor().getText();
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * @brief Maneja la acción de cancelar la edición del umbral.
     */
    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }

    /**
     * @brief Cierra la ventana modal actual.
     */
    private void cerrarVentana() {
        ((Stage) nuevoUmbralCombo.getScene().getWindow()).close();
    }

    /**
     * @brief Finaliza el flujo y oculta la ventana principal de configuración de umbrales.
     */
    public void termina() {
        ventana.setVisible(false);
    }
}
