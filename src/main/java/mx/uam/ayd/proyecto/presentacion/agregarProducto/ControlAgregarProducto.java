package mx.uam.ayd.proyecto.presentacion.agregarProducto;

import mx.uam.ayd.proyecto.negocio.modelo.TipoProducto;
import mx.uam.ayd.proyecto.negocio.modelo.UnidadProducto;
import mx.uam.ayd.proyecto.negocio.modelo.MarcaProducto;
import mx.uam.ayd.proyecto.negocio.modelo.UsoVeterinario;

import java.time.LocalDate;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioProducto;
import mx.uam.ayd.proyecto.presentacion.Inventario.Controlinventario;

/**
 * Controlador encargado de manejar la lógica de presentación
 * para la funcionalidad de agregar un nuevo producto al sistema.
 *
 * Actúa como intermediario entre la vista ({@link VentanaAgregarProducto})
 * y la capa de negocio ({@link ServicioProducto}).
 * Se encarga de recibir los datos de la interfaz gráfica,
 * validarlos y enviarlos al servicio correspondiente para su registro en la base de datos.

 * @see ServicioProducto
 * @see VentanaAgregarProducto
 * @see Controlinventario
 */
@Component
public class ControlAgregarProducto {

    /** Servicio encargado de la lógica de negocio relacionada con los productos. */
    private final ServicioProducto servicioProducto;

    /** Ventana asociada a la funcionalidad de agregar producto. */
    private final VentanaAgregarProducto ventana;

    /** Referencia al controlador principal del inventario, usada para actualizar la vista al finalizar. */
    private Controlinventario controlinventario = null;

    /**
     * Constructor con inyección de dependencias gestionada por Spring.
     *
     * @param servicioProducto Servicio de negocio que gestiona las operaciones sobre productos.
     * @param ventana Vista encargada de la interfaz gráfica para agregar productos.
     */
    @Autowired
    public ControlAgregarProducto(ServicioProducto servicioProducto,
                                  VentanaAgregarProducto ventana) {
        this.servicioProducto = servicioProducto;
        this.ventana = ventana;
    }

    /**
     * Método que se ejecuta automáticamente después de la inyección de dependencias.
     * Establece la relación entre el controlador y la vista,
     * permitiendo que la vista pueda invocar las operaciones del controlador.
     */
    @PostConstruct
    public void init() {
        ventana.setControlAgregarProducto(this);
    }

    /**
     * Inicia el flujo de agregar producto mostrando la ventana correspondiente.
     *
     * @param controlinventario Controlador del inventario que invoca esta funcionalidad.
     *                          Se usa posteriormente para actualizar la vista tras agregar el producto.
     */
    public void inicia(Controlinventario controlinventario) {
        this.controlinventario = controlinventario;
        ventana.muestra();
    }

    /**
     * Agrega un nuevo producto al sistema.
     * Los datos recibidos se envían al servicio de negocio {@link ServicioProducto}
     * para ser almacenados en la base de datos. Si la operación es exitosa,
     * se muestra un mensaje de confirmación; de lo contrario, se informa el error.
     *
     * @param nombre         Nombre del producto.
     * @param tipoProducto   Tipo de producto (por ejemplo, alimento, medicamento, etc.).
     * @param marcaProducto  Marca del producto.
     * @param precio         Precio unitario del producto.
     * @param cantidad       Cantidad disponible en inventario.
     * @param unidadProducto Unidad de medida (por ejemplo, pieza, litro, kilogramo, etc.).
     * @param fechaCaducidad Fecha de caducidad del producto (si aplica).
     * @param usoVeterinario Uso veterinario asociado al producto.
     */
    public void agregarProducto(String nombre,
                                TipoProducto tipoProducto,
                                MarcaProducto marcaProducto,
                                double precio,
                                int cantidad,
                                UnidadProducto unidadProducto,
                                LocalDate fechaCaducidad,
                                UsoVeterinario usoVeterinario) {
        try {
            // Llama al servicio para registrar el producto
            servicioProducto.agregarProducto(nombre, tipoProducto, marcaProducto,
                    precio, cantidad, unidadProducto, fechaCaducidad, usoVeterinario);

            // Notifica al usuario que la operación fue exitosa
            ventana.muestraDialogoConMensaje("Producto agregado exitosamente.");
        } catch (Exception ex) {
            // Muestra el mensaje de error en caso de excepción
            ventana.muestraDialogoConMensaje("Error al agregar producto: " + ex.getMessage());
        }

        // Finaliza el flujo cerrando la ventana y actualizando la vista
        termina(tipoProducto);
    }

    /**
     * Finaliza la operación de agregar producto.
     * Actualiza la vista principal del inventario para reflejar los cambios
     * y cierra la ventana actual.
     *
     * @param tipoProducto Tipo de producto agregado, usado para filtrar la actualización de la vista.
     */
    public void termina(TipoProducto tipoProducto) {
        controlinventario.actualizarVista(tipoProducto);
        ventana.setVisible(false);
    }

    /**
     * Cierra la ventana de agregar producto sin realizar ninguna acción adicional.
     */
    public void termina() {
        ventana.setVisible(false);
    }
}
