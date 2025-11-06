package mx.uam.ayd.proyecto.presentacion.registroHospedaje;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import mx.uam.ayd.proyecto.negocio.ServicioMascota;
import mx.uam.ayd.proyecto.negocio.modelo.Mascota;
import mx.uam.ayd.proyecto.negocio.ServicioCliente;
import mx.uam.ayd.proyecto.negocio.ServicioHospedaje;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import java.time.LocalDate;



/**
 * @file ControlRegistroHospedaje.java
 * @brief Controlador principal de la ventana de registro de hospedaje.
 *
 * Esta clase pertenece a la capa de presentación (MVC) y se encarga de iniciar
 * la interfaz gráfica correspondiente al registro de hospedajes.
 *
 * <p>Se integra con Spring Framework para la inyección de dependencias del
 * servicio {@link ServicioMascota}, y con JavaFX para el manejo de la vista
 * definida en el archivo FXML <code>ventana-registro-hospedaje.fxml</code>.</p>
 *
 * @author Mitzi
 * @date 2025-11-01
 */
@Component
public class ControlRegistroHospedaje {

    /** Servicio de negocio para gestionar las operaciones relacionadas con mascotas. */
    @Autowired
    private ServicioMascota servicioMascota;

    @Autowired
    private ServicioCliente servicioCliente;

    @Autowired
    private ServicioHospedaje servicioHospedaje;

    /**
     * @brief Inicia la ventana de registro de hospedaje.
     *
     * Carga la interfaz definida en el archivo FXML y establece las referencias
     * necesarias entre el controlador de la vista y los servicios de negocio.
     *
     * <p>También crea un nuevo {@link Stage} para mostrar la ventana de forma
     * independiente, manteniendo el patrón MVC entre controladores y vistas.</p>
     *
     * <p>El método utiliza el patrón de diseño <b>Dependency Injection</b> de Spring
     * para inyectar el servicio {@link ServicioMascota} en la ventana al momento
     * de la carga.</p>
     *
     * @post Muestra una ventana gráfica con la interfaz para registrar hospedajes.
     *
     * @exception Exception si ocurre un error durante la carga del archivo FXML o la creación de la ventana.
     */
    public void inicia() {
        try {
            // Cargar el archivo FXML de la ventana de registro de hospedaje
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/ventana-registro-hospedaje.fxml")
            );
            Parent root = loader.load();

            // Obtener el controlador de la ventana y pasarle el servicio
            VentanaRegistroHospedaje controller = loader.getController();
            controller.setControlRegistroHospedaje(this);
            controller.setServicioMascota(servicioMascota);
            controller.setServicioCliente(servicioCliente);

            controller.iniciaDatos();


            // Crear y configurar la nueva ventana modal
            Stage stage = new Stage();
            //stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Registrar Hospedaje");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registraHospedaje(Cliente cliente, Mascota mascota, LocalDate fechaInicio, LocalDate fechaFin) {
        servicioHospedaje.registrar(
            cliente, 
            mascota, 
            fechaInicio, 
            fechaFin, 
            null 
        );
    }
}
