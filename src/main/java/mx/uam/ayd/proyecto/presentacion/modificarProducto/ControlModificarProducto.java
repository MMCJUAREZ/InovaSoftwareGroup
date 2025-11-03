package mx.uam.ayd.proyecto.presentacion.modificarProducto;

import mx.uam.ayd.proyecto.negocio.modelo.TipoProducto;
import mx.uam.ayd.proyecto.negocio.modelo.UnidadProducto;
import mx.uam.ayd.proyecto.negocio.modelo.MarcaProducto;
import mx.uam.ayd.proyecto.negocio.modelo.UsoVeterinario;

import java.time.LocalDate;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioProducto;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import mx.uam.ayd.proyecto.presentacion.Inventario.Controlinventario;

/**
 * Controlador encargado de manejar la lógica de la vista {@link VentanaModificarProducto}.
 * <p>
 * Esta clase permite la modificación de los datos de un producto existente en el sistema.
 * Se comunica con el servicio de negocio {@link ServicioProducto} para realizar las operaciones
 * y con la interfaz {@link VentanaModificarProducto} para mostrar los resultados al usuario.
 * </p>
 *
 * <p>
 * Forma parte de la capa de presentación del proyecto, dentro del patrón MVC.
 * </p>
 *
 * @author
 */
@Component
public class ControlModificarProducto {

    /** Identificador del producto que se desea modificar. */
    private Long idProducto;

    /** Producto que será modificado. */
    private Producto producto;

    /** Servicio encargado de la lógica de negocio relacionada con los productos. */
    private final ServicioProducto servicioProducto;

    /** Ventana correspondiente a la interfaz gráfica de modificación de producto. */
    private final VentanaModificarProducto ventana;

    /** Controlador del inventario, utilizado para actualizar la vista al finalizar la modificación. */
    private Controlinventario controlinventario = null;

    /**
     * Constructor con inyección de dependencias.
     * Spring se encarga automáticamente de proporcionar las instancias de
     * {@link ServicioProducto} y {@link VentanaModificarProducto}.
     *
     * @param servicioProducto Servicio de negocio que administra las operaciones sobre productos.
     * @param ventana Ventana que muestra la interfaz para modificar productos.
     */
    @Autowired
    public ControlModificarProducto(ServicioProducto servicioProducto, VentanaModificarProducto ventana) {
        this.servicioProducto = servicioProducto;
        this.ventana = ventana;
    }

    /**
     * Inicializa la relación entre el controlador y la vista.
     */
    @PostConstruct
    public void init() {
        ventana.setControlModificarProducto(this);
    }

    /**
     * Inicia el flujo de modificación de producto mostrando la ventana correspondiente.
     *
     * @param producto Producto que se desea modificar.
     * @param controlinventario Controlador de inventario para actualizar la vista al finalizar.
     */
    public void inicia(Producto producto, Controlinventario controlinventario) {
        this.producto = producto;
        this.controlinventario = controlinventario;
        ventana.muestra(producto);
    }

    /**
     * Modifica los datos de un producto existente.
     * Este método invoca el servicio de negocio {@link ServicioProducto#modificarProducto}
     * para actualizar la información en la base de datos. Si la operación es exitosa, se
     * muestra un mensaje de confirmación; en caso contrario, se muestra el mensaje de error.
     *
     * @param producto Producto a modificar.
     * @param nombre Nuevo nombre del producto.
     * @param tipoProducto Tipo del producto.
     * @param marcaProducto Marca del producto.
     * @param precio Nuevo precio del producto.
     * @param cantidad Nueva cantidad disponible.
     * @param unidadProducto Unidad de medida del producto.
     * @param fechaCaducidad Nueva fecha de caducidad.
     * @param usoVeterinario Uso veterinario asociado al producto.
     */
    public void modificarProducto(Producto producto, String nombre, TipoProducto tipoProducto, MarcaProducto marcaProducto,
                                  double precio, int cantidad, UnidadProducto unidadProducto, LocalDate fechaCaducidad,
                                  UsoVeterinario usoVeterinario) {
        try {
            // Llama al servicio para registrar los cambios del producto
            servicioProducto.modificarProducto(producto, nombre, tipoProducto, marcaProducto,
                    precio, cantidad, unidadProducto, fechaCaducidad, usoVeterinario);

            // Notifica al usuario que la operación fue exitosa
            ventana.muestraDialogoConMensaje("Producto modificado exitosamente.");
        } catch (Exception ex) {
            // Muestra el mensaje de error en caso de excepción
            ventana.muestraDialogoConMensaje("Error al modificar producto: " + ex.getMessage());
        }

        // Cierra la ventana después de intentar modificar el producto
        termina(tipoProducto);
    }

    /**
     * Finaliza la operación de modificación, actualiza la vista del inventario
     * y cierra la ventana de modificación.
     *
     * @param tipoProducto Tipo del producto que fue modificado, utilizado para actualizar la vista.
     */
    public void termina(TipoProducto tipoProducto) {
        controlinventario.actualizarVista(tipoProducto);
        ventana.setVisible(false);
    }

    /**
     * Cierra la ventana de modificación sin actualizar la vista.
     */
    public void termina() {
        ventana.setVisible(false);
    }
}
