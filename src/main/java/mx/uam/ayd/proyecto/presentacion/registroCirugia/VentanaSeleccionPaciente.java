package mx.uam.ayd.proyecto.presentacion.registroCirugia;

import org.springframework.stereotype.Component;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import mx.uam.ayd.proyecto.negocio.ServicioCliente;
import mx.uam.ayd.proyecto.negocio.ServicioMascota;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.Mascota;

import java.io.IOException;

/**
 * @file VentanaSeleccionPaciente.java
 * @brief Controlador de la ventana para seleccionar cliente y mascota.
 *
 * Esta clase gestiona la interfaz grafica del primer paso del registro de cirugia.
 * Permite al usuario buscar un cliente mediante una lista desplegable y,
 * posteriormente, seleccionar una de las mascotas asociadas a dicho cliente.
 * Una vez seleccionada la mascota, permite avanzar al formulario de datos.
 *
 * @author InovaSoftwareGroup
 * @date 2025-11-20
 */
@Component
public class VentanaSeleccionPaciente {
    
    private Stage stage;
    private ControlRegistroCirugia control;
    private ServicioMascota servicioMascota;

    // Componentes de la interfaz grafica inyectados
    @FXML private ComboBox<Cliente> cmbCliente;
    @FXML private ComboBox<Mascota> cmbMascota;
    @FXML private Button btnContinuar;

    /**
     * @brief Inicializa y muestra la ventana de seleccion.
     *
     * Carga el archivo FXML si es la primera vez que se invoca.
     * Configura los componentes visuales (convertidores de texto) y carga
     * la lista completa de clientes para iniciar el flujo.
     *
     * @param control Controlador principal del flujo de registro de cirugia.
     * @param servicioCliente Servicio para recuperar la lista de clientes.
     * @param servicioMascota Servicio para recuperar las mascotas de un cliente.
     */
    public void muestra(ControlRegistroCirugia control, ServicioCliente servicioCliente, ServicioMascota servicioMascota) {
        this.control = control;
        this.servicioMascota = servicioMascota;

        if (stage == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-seleccion-paciente.fxml"));
                loader.setController(this);
                stage = new Stage();
                stage.setScene(new Scene(loader.load()));
                stage.setTitle("Seleccionar Paciente para Cirugía");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        limpiar();
        configurarCombos();
        
        // Carga inicial de todos los clientes en el ComboBox
        cmbCliente.getItems().setAll(servicioCliente.recuperarCliente());
        
        stage.show();
    }

    /**
     * @brief Restablece el estado inicial de los componentes de la ventana.
     *
     * Deselecciona cualquier valor previo, limpia la lista de mascotas
     * y deshabilita los botones dependientes.
     */
    private void limpiar() {
        cmbCliente.setValue(null);
        cmbMascota.getItems().clear();
        cmbMascota.setDisable(true);
        btnContinuar.setDisable(true);
    }

    /**
     * @brief Configura los convertidores de cadena para los ComboBox.
     *
     * Define como se deben mostrar los objetos Cliente y Mascota en las listas
     * desplegables (usando sus nombres en lugar de su representacion de objeto).
     */
    private void configurarCombos() {
        cmbCliente.setConverter(new StringConverter<>() {
            public String toString(Cliente c) { return c == null ? null : c.getNombreCompleto(); }
            public Cliente fromString(String s) { return null; }
        });
        cmbMascota.setConverter(new StringConverter<>() {
            public String toString(Mascota m) { return m == null ? null : m.getNombre(); }
            public Mascota fromString(String s) { return null; }
        });
    }

    /**
     * @brief Metodo invocado al seleccionar un cliente de la lista.
     *
     * Habilita el ComboBox de mascotas, limpia cualquier seleccion anterior
     * y carga las mascotas asociadas al cliente seleccionado mediante el servicio.
     */
    @FXML
    private void handleClienteSeleccionado() {
        Cliente cliente = cmbCliente.getValue();
        cmbMascota.getItems().clear();
        cmbMascota.setDisable(true);
        btnContinuar.setDisable(true);

        if (cliente != null) {
            cmbMascota.getItems().setAll(servicioMascota.recuperaMascotas(cliente));
            cmbMascota.setDisable(false);
        }
    }

    /**
     * @brief Metodo invocado al seleccionar una mascota de la lista.
     *
     * Habilita el boton "Continuar" solo si se ha seleccionado una mascota valida.
     */
    @FXML
    private void handleMascotaSeleccionada() {
        btnContinuar.setDisable(cmbMascota.getValue() == null);
    }

    /**
     * @brief Metodo invocado al presionar el boton Continuar.
     *
     * Notifica al controlador que se ha seleccionado una mascota valida
     * y solicita avanzar al siguiente paso (formulario de datos).
     */
    @FXML
    private void handleContinuar() {
        if (cmbMascota.getValue() != null) {
            control.mascotaSeleccionada(cmbMascota.getValue());
        }
    }
    
    /**
     * @brief Cierra la ventana actual.
     */
    public void cierra() { stage.close(); }
}