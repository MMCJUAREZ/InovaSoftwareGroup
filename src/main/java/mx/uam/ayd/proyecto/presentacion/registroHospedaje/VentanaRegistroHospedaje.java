package mx.uam.ayd.proyecto.presentacion.registroHospedaje;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.Mascota;
import org.springframework.stereotype.Component;
import mx.uam.ayd.proyecto.negocio.ServicioMascota;
import mx.uam.ayd.proyecto.negocio.ServicioCliente;

import java.time.LocalDate;
import java.util.List;

@Component
public class VentanaRegistroHospedaje {

    @FXML private ComboBox<Cliente> cmbCliente;
    @FXML private ComboBox<Mascota> cmbMascota;
    @FXML private DatePicker dateInicio;
    @FXML private DatePicker dateFin;
    @FXML private Button btnRegistrarHospedaje;

    private ServicioMascota servicioMascota;
    private ServicioCliente servicioCliente;
    
    private ControlRegistroHospedaje controlRegistroHospedaje;

    public void setServicioMascota(ServicioMascota servicioMascota) {
        this.servicioMascota = servicioMascota;
    }

    public void setServicioCliente(ServicioCliente servicioCliente) {
        this.servicioCliente = servicioCliente;
    }

    public void setControlRegistroHospedaje(ControlRegistroHospedaje controlRegistroHospedaje){
        this.controlRegistroHospedaje = controlRegistroHospedaje;
    }

    /**
     * Inicia los datos de la ventana
     */
    public void iniciaDatos() {
        configurarComboBoxClientes();
        
        List<Cliente> clientes = servicioCliente.recuperarCliente();
        cmbCliente.getItems().addAll(clientes);

        cmbMascota.setDisable(true);
        btnRegistrarHospedaje.setDisable(true);
    }

    /**
     * Se llama cuando el usuario selecciona un Cliente
     */
    @FXML
    void handleClienteSeleccionado() {
        Cliente clienteSeleccionado = cmbCliente.getValue();
        if (clienteSeleccionado == null) {
            cmbMascota.setDisable(true);
            btnRegistrarHospedaje.setDisable(true);
            return;
        }

        // Limpiar y cargar las mascotas del cliente
        cmbMascota.getItems().clear();
        configurarComboBoxMascotas();
        
        List<Mascota> mascotas = servicioMascota.recuperaMascotas(clienteSeleccionado);
        cmbMascota.getItems().addAll(mascotas);
        
        cmbMascota.setDisable(false);
        btnRegistrarHospedaje.setDisable(true);
    }

    /**
     * Se llama cuando el usuario selecciona una Mascota
     */
    @FXML
    void handleMascotaSeleccionada() {
        // Habilitar el botón de registro solo si hay una mascota seleccionada
        if (cmbMascota.getValue() != null) {
            btnRegistrarHospedaje.setDisable(false);
        } else {
            btnRegistrarHospedaje.setDisable(true);
        }
    }

    /**
     * Se llama al presionar el botón "Registrar Hospedaje"
     */
    @FXML
    void registrarHospedaje() {
        try {
            Cliente cliente = cmbCliente.getValue();
            Mascota mascota = cmbMascota.getValue();
            LocalDate fechaInicio = dateInicio.getValue();
            LocalDate fechaFin = dateFin.getValue();

            if (cliente == null || mascota == null || fechaInicio == null || fechaFin == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Campos incompletos", "Por favor, selecciona cliente, mascota y ambas fechas.");
                return;
            }

            if (fechaFin.isBefore(fechaInicio)) {
                mostrarAlerta(Alert.AlertType.WARNING, "Fechas incorrectas", "La fecha de fin no puede ser anterior a la fecha de inicio.");
                return;
            }

            controlRegistroHospedaje.registraHospedaje(cliente, mascota, fechaInicio, fechaFin);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Hospedaje para " + mascota.getNombre() + " registrado exitosamente.");
            limpiarCampos();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error al registrar", "Ocurrió un error: " + e.getMessage());
        }
    }


    private void configurarComboBoxClientes() {
        cmbCliente.setConverter(new StringConverter<Cliente>() {
            @Override
            public String toString(Cliente cliente) {
                return (cliente == null) ? "Selecciona un cliente..." : cliente.getNombreCompleto();
            }
            @Override
            public Cliente fromString(String string) { return null; }
        });
        cmbCliente.setCellFactory(param -> new ListCell<Cliente>() {
            @Override
            protected void updateItem(Cliente cliente, boolean empty) {
                super.updateItem(cliente, empty);
                setText(empty ? null : cliente.getNombreCompleto());
            }
        });
    }
    
    private void configurarComboBoxMascotas() {
        cmbMascota.setConverter(new StringConverter<Mascota>() {
            @Override
            public String toString(Mascota mascota) {
                return (mascota == null) ? "Selecciona una mascota..." : mascota.getNombre();
            }
            @Override
            public Mascota fromString(String string) { return null; }
        });
        cmbMascota.setCellFactory(param -> new ListCell<Mascota>() {
            @Override
            protected void updateItem(Mascota mascota, boolean empty) {
                super.updateItem(mascota, empty);
                setText(empty ? null : mascota.getNombre());
            }
        });
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void limpiarCampos() {
        cmbCliente.setValue(null);
        cmbMascota.getItems().clear();
        cmbMascota.setDisable(true);
        dateInicio.setValue(null);
        dateFin.setValue(null);
        btnRegistrarHospedaje.setDisable(true);
    }
}