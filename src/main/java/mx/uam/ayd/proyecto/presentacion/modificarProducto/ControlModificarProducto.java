package mx.uam.ayd.proyecto.presentacion.modificarProducto;

import mx.uam.ayd.proyecto.negocio.modelo.TipoProducto;
import mx.uam.ayd.proyecto.negocio.modelo.UnidadProducto;
import mx.uam.ayd.proyecto.negocio.modelo.MarcaProducto;

import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioProducto;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import mx.uam.ayd.proyecto.presentacion.Inventario.Controlinventario;

@Component
public class ControlModificarProducto {
    private Long idProducto;
    private Producto producto;
    /** Servicio encargado de la lógica de negocio relacionada con los productos. */
    private final ServicioProducto servicioProducto;

    /** Ventana para la interfaz gráfica de agregar producto. */
    private final VentanaModificarProducto ventana;

    private Controlinventario controlinventario = null;

    /**
     * Constructor con inyección de dependencias.
     * Spring se encarga de proporcionar las instancias necesarias.
     *
     * @param servicioProducto Servicio de negocio para manejar productos.
     * @param ventana Vista de la ventana de agregar producto.
     */
    @Autowired
    public ControlModificarProducto(
            ServicioProducto servicioProducto,
            VentanaModificarProducto ventana) {
        this.servicioProducto = servicioProducto;
        this.ventana = ventana;
    }

    /**
     * Inicializa la relación entre este controlador y la vista.
     * Se ejecuta automáticamente después de que Spring ha inyectado las dependencias.
     */
    @PostConstruct
    public void init() {
        ventana.setControlModificarProducto(this);
    }

    /**
     * Inicia el flujo de agregar producto mostrando VentanaAgregarProducto.
     */
    public void inicia(Long idProducto, Producto producto, Controlinventario controlinventario) {
        this.idProducto = idProducto;
        this.producto = producto;
        this.controlinventario = controlinventario;
        ventana.muestra(idProducto, producto);
    }

    /**
     * Agrega un nuevo producto al sistema usando el servicio de negocio.
     * Si el producto se agrega exitosamente, se muestra un mensaje de confirmación.
     * Si ocurre algún error, se muestra el mensaje de error.
     *
     * @param nombre Nombre del producto.
     * @param tipoProducto Tipo de producto.
     * @param marcaProducto Marca del producto.
     * @param precio Precio del producto.
     * @param cantidad Cantidad en inventario.
     * @param unidadProducto Unidad de medida del producto.
     * @param fechaCaducidad Fecha de caducidad del producto.
     */
    public void modificarProducto(Producto producto, String nombre, TipoProducto tipoProducto, MarcaProducto marcaProducto,
                                double precio, int cantidad, UnidadProducto unidadProducto, LocalDate fechaCaducidad) {
        try {
            // Llama al servicio para registrar el producto en la base de datos
            servicioProducto.modificarProducto(producto, nombre, tipoProducto, marcaProducto, precio, cantidad, unidadProducto, fechaCaducidad);

            // Notifica al usuario que la operación fue exitosa
            ventana.muestraDialogoConMensaje("Producto modificado exitosamente.");
        } catch (Exception ex) {
            // Muestra el mensaje de error en caso de excepción
            ventana.muestraDialogoConMensaje("Error al modificar producto: " + ex.getMessage());
        }

        // Cierra la ventana después de intentar agregar el producto
        termina();
    }

    /**
     * Finaliza la operación de agregar producto y cierra la ventana.
     */
    public void termina() {
        controlinventario.actualizarVista();
        ventana.setVisible(false);
    }
}
