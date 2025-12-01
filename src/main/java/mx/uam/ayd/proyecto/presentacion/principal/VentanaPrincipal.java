package mx.uam.ayd.proyecto.presentacion.principal;

import mx.uam.ayd.proyecto.negocio.modelo.Hospedaje;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import org.springframework.stereotype.Component;
import mx.uam.ayd.proyecto.presentacion.RegistroDiarioHospedaje.VentanaRegistroDiarioHospedaje;
import org.springframework.beans.factory.annotation.Autowired;
import javafx.scene.control.Alert;

import java.awt.event.ActionEvent;
import java.io.IOException;

/**
 * Ventana principal usando JavaFX con FXML
 * 
 */
@Component
public class VentanaPrincipal {

	private Stage stage;
	private ControlPrincipal control;
	private boolean initialized = false;


    @Autowired
    private VentanaRegistroDiarioHospedaje ventanaRegistroDiarioHospedaje;

        /**
         * Constructor without UI initialization
         */
	public VentanaPrincipal() {
		// Don't initialize JavaFX components in constructor
	}
	
	/**
	 * Initialize UI components on the JavaFX application thread
	 */
	private void initializeUI() {
		if (initialized) {
			return;
		}
		
		// Create UI only if we're on JavaFX thread
		if (!Platform.isFxApplicationThread()) {
			Platform.runLater(this::initializeUI);
			return;
		}
		
		try {
			stage = new Stage();
			stage.setTitle("Mi Aplicación");
			
			// Load FXML
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-principal.fxml"));
			loader.setController(this);
			Scene scene = new Scene(loader.load(), 450, 300);
			stage.setScene(scene);
			
			initialized = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setControlPrincipal(ControlPrincipal control) {
		this.control = control;
	}
	/**
	 * Muestra la ventana y establece el control
	 * 
//	 * @param control El controlador asociado a esta ventana
	 */
	public void muestra() {
		//this.control = control;
		
		if (!Platform.isFxApplicationThread()) {
			Platform.runLater(() -> this.muestra());
			return;
		}
		
		initializeUI();
		stage.show();
	}
	
	// FXML Event Handlers
	@FXML
	private void handleConfigurarUmbrales(){
		if(control != null){
			control.configurarUmbrales();
		}
	}
	
	@FXML
	private void handleRegistrarVenta() {
		if (control != null) {
			control.registrarVenta();
		}
	}
	
	@FXML
	private void handleGenerarReporte() {
		if (control != null) {
			control.generarReporte();
		}
	}

	@FXML
	private void handleMostrarAlertas() {
		if (control != null) {
			control.mostrarAlertas();
		}
	}

    @FXML
    private void handleInventario() {
        if (control != null) {
            control.Inventario();
        }
    }

	@FXML
    private void handleGestionarClientes() {
        if (control != null) {
            control.gestionarClientes();
        }
    }

    @FXML
    private void handleCartilla(){
        if(control != null){
            control.agregarCartilla();
        }
    }

	@FXML
	private void handleGestionarCitas() {
		if (control != null) {
			control.gestionarCitas();
		}
	}

	@FXML
	private void handleRegistrarHospedaje() {
		System.out.println("handleRegistrarHospedaje() ejecutado (boton presionado.");
		if (control != null) {
			control.registrarHospedaje();
		}else{
			System.err.println("⚠️ ControlPrincipal es null. Revisa el PostConstruct o la inyección.");
		}
	}

	@FXML
	private void handleGenerarReceta() {
		if(control != null){
			control.generarReceta();
		}
	}

	@FXML
    private void handleRegistrarCirugia() {
        if (control != null) {
            control.registrarCirugia();
        }
    }
    @FXML
    private void handleRegistrarDiarioHospedaje() {
        control.registrarDiarioHospedaje();
    }

    /**
     * Muestra una alerta en pantalla con un mensaje.
     * @param mensaje Texto a mostrar en la ventana de alerta.
     */
   /* private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }*/
}
