package mx.uam.ayd.proyecto.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import mx.uam.ayd.proyecto.ProyectoApplication;
import mx.uam.ayd.proyecto.datos.ClienteRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.presentacion.principal.ControlPrincipal;

/**
 * Test de sistema para probar el flujo completo de registrar y eliminar cliente
 * apegado al estilo de AgregarUsuarioSystemTest.
 */

// Esta anotacion crea la aplicacion Spring completa como si se ejecutara normal
@SpringBootTest(classes = ProyectoApplication.class)
// Indica explicitamente cual es la clase principal para arrancar el contexto
@ActiveProfiles("test")
public class RegistrarClienteSystemTest extends ApplicationTest {

    @Autowired
    private ControlPrincipal controlPrincipal;

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public void start(Stage stage) throws Exception {
        // La inicializacion se hace en setUp
    }

    @BeforeEach
    public void setUp() throws Exception {
        // Limpiar la base de datos antes de cada test
        clienteRepository.deleteAll();

        // Inicializar la aplicacion
        controlPrincipal.inicia();
        
        // Esperar a que la ventana principal aparezca
        sleep(500);
    }

    @Test
    public void testRegistrarYEliminarCliente() {
        // Given: Datos del cliente a registrar
        String nombre = "Cliente Sistema";
        String telefono = "5512345678";
        String correo = "sistema@test.com";
        String direccion = "Av. Test 123";

        // Verificar que el cliente no existe previamente
        Cliente clienteExistente = clienteRepository.findByCorreoElectronico(correo);
        assertTrue(clienteExistente == null, "El cliente no debería existir antes del test");

        // Primero entramos en la seccion de clientes
        
        // Hacer clic en "Gestionar Clientes" en la ventana principal
        // Usamos el texto del boton como identificador
        Button btnGestionar = waitForNode(() -> {
            try {
                return lookup(button -> button instanceof Button && 
                    ((Button) button).getText().equals("Gestionar Clientes")).query();
            } catch (Exception e) {
                return null;
            }
        }, 10);
        assertNotNull(btnGestionar, "El botón Gestionar Clientes deberia estar visible");
        clickOn(btnGestionar);
        
        sleep(1000); // Esperar a que abra la ventana

        // Registrar nuevo cliente

        // Hacer clic en "Registrar Nuevo Cliente"
        Button btnRegistrarNuevo = waitForNode(() -> {
            try {
                return lookup(button -> button instanceof Button && 
                    ((Button) button).getText().equals("Registrar Nuevo Cliente")).query();
            } catch (Exception e) {
                return null;
            }
        }, 10);
        assertNotNull(btnRegistrarNuevo, "El botón Registrar Nuevo Cliente deberia estar visible");
        clickOn(btnRegistrarNuevo);

        sleep(1000); // Esperar a que abra el formulario modal

        // Llenar Nombre
        TextField txtNombre = waitForNode(() -> lookup("#textFieldNombre").query(), 10);
        assertNotNull(txtNombre, "El campo Nombre deberia estar visible");
        clickOn(txtNombre);
        write(nombre);

        // Llenar Telefono
        TextField txtTelefono = waitForNode(() -> lookup("#textFieldTelefono").query(), 10);
        clickOn(txtTelefono);
        write(telefono);

        // Llenar Correo
        TextField txtCorreo = waitForNode(() -> lookup("#textFieldCorreo").query(), 10);
        clickOn(txtCorreo);
        write(correo);

        // Llenar Direccion
        TextField txtDireccion = waitForNode(() -> lookup("#textFieldDireccion").query(), 10);
        clickOn(txtDireccion);
        write(direccion);

        // Hacer clic en "Guardar"
        Button btnGuardar = waitForNode(() -> {
            try {
                return lookup(button -> button instanceof Button && 
                    ((Button) button).getText().equals("Guardar")).query();
            } catch (Exception e) {
                return null;
            }
        }, 10);
        clickOn(btnGuardar);

        sleep(1000); // Esperar alerta de éxito

        // Cerrar la ventana de alerta directamente (equivalente al tache 'X')
        // Buscamos el contenedor principal de cualquier alerta (DialogPane)
        Node dialogPane = waitForNode(() -> lookup(".dialog-pane").query(), 10);
        assertNotNull(dialogPane, "Deberia aparecer la alerta de exito");

        // Usamos 'interact' para ejecutar la acción de cerrar en el hilo de JavaFX
        interact(() -> ((Stage) dialogPane.getScene().getWindow()).close());

        sleep(1000);

        // Verificacion en la base de datos
        Cliente clienteGuardado = clienteRepository.findByCorreoElectronico(correo);
        assertNotNull(clienteGuardado, "El cliente deberia haberse guardado en la BD");
        assertEquals(nombre, clienteGuardado.getNombreCompleto());

        // Paso de la eliminacion de cliente

        // Seleccionar al cliente en la tabla
        // En JavaFX tables, hacer clic en el texto de la celda selecciona la fila
        Node celdaNombre = waitForNode(() -> lookup(nombre).query(), 10);
        assertNotNull(celdaNombre, "El cliente deberia aparecer en la tabla");
        clickOn(celdaNombre);

        // Hacer clic en "Eliminar Cliente Seleccionado"
        Button btnEliminar = waitForNode(() -> {
            try {
                return lookup(button -> button instanceof Button && 
                    ((Button) button).getText().equals("Eliminar Cliente Seleccionado")).query();
            } catch (Exception e) {
                return null;
            }
        }, 10);
        clickOn(btnEliminar);

        sleep(500); // Esperar alerta de confirmacion

        // Confirmar eliminacion
        // Encontrar el boton de la alerta
        Button btnConfirmar = waitForNode(() -> lookup("OK").queryButton(), 10); 
        clickOn(btnConfirmar);

        sleep(500); // Esperar alerta de éxito de eliminacion

        // Cerrar alerta de exito de eliminacion
        Button btnOkEliminado = waitForNode(() -> lookup("OK").queryButton(), 10);
        clickOn(btnOkEliminado);
        sleep(500);

        // Verificacion en base de datos
        Cliente clienteBorrado = clienteRepository.findByCorreoElectronico(correo);
        assertTrue(clienteBorrado == null, "El cliente debería haber sido eliminado de la BD");
    }

    /**
     * Método helper para esperar a que un nodo este disponible en el scene graph
     */
    private <T extends Node> T waitForNode(java.util.function.Supplier<T> supplier, int maxAttempts) {
        for (int i = 0; i < maxAttempts; i++) {
            try {
                WaitForAsyncUtils.waitForFxEvents();
                T node = supplier.get();
                if (node != null && node.isVisible()) {
                    return node;
                }
            } catch (Exception e) {
                // Continuar intentando
            }
            sleep(200);
        }
        return null;
    }

    @Override
    public void stop() throws Exception {
        FxToolkit.hideStage();
        super.stop();
    }
}