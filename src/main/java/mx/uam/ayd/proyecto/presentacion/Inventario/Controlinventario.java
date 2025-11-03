package mx.uam.ayd.proyecto.presentacion.Inventario;

import jakarta.annotation.PostConstruct;
import mx.uam.ayd.proyecto.negocio.ServicioProducto;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import mx.uam.ayd.proyecto.negocio.modelo.TipoProducto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import mx.uam.ayd.proyecto.presentacion.agregarProducto.ControlAgregarProducto;
import mx.uam.ayd.proyecto.presentacion.modificarProducto.ControlModificarProducto;

/**
 * Controlador encargado de gestionar la vista y la lógica asociada al inventario de productos.
 *
 * Este controlador actúa como intermediario entre la capa de presentación
 * y la capa de negocio.
 * Permite realizar operaciones como mostrar el inventario, agregar, modificar y eliminar productos.
 */
@Component
public class Controlinventario {

    /** Logger para el registro de eventos e información de depuración. */
    private static final Logger log = LoggerFactory.getLogger(Controlinventario.class);

    /** Servicio que contiene la lógica de negocio relacionada con los productos. */
    private final ServicioProducto servicioProducto;

    /** Ventana asociada al módulo de inventario. */
    private final VentanaInventario ventana;

    /** Controlador para el flujo de agregar productos. */
    private final ControlAgregarProducto controlAgregarProducto;

    /** Controlador para el flujo de modificar productos. */
    private final ControlModificarProducto controlModificarProducto;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param servicioProducto Servicio de negocio para la gestión de productos.
     * @param ventana Vista principal del inventario.
     * @param controlAgregarProducto Controlador encargado de agregar nuevos productos.
     * @param controlModificarProducto Controlador encargado de modificar productos existentes.
     */
    @Autowired
    public Controlinventario(ServicioProducto servicioProducto,
                             VentanaInventario ventana,
                             ControlAgregarProducto controlAgregarProducto,
                             ControlModificarProducto controlModificarProducto) {
        this.servicioProducto = servicioProducto;
        this.ventana = ventana;
        this.controlAgregarProducto = controlAgregarProducto;
        this.controlModificarProducto = controlModificarProducto;
    }

    /**
     * Método que se ejecuta automáticamente después de la construcción del bean.
     * Establece la comunicación entre este controlador y la ventana del inventario.
     */
    @PostConstruct
    public void init() {
        ventana.setControlinventario(this);
    }

    /**
     * Inicia el caso de uso del inventario.
     * <Carga y muestra los productos de tipo {@link TipoProducto#Comida} por defecto.
     */
    public void inicia() {
        List<Producto> productos = servicioProducto.buscarPorTipo(TipoProducto.Comida);

        // Registra en logs la información de los productos cargados
        for (Producto producto : productos) {
            log.info("Producto cargado: " + producto);
        }

        ventana.muestra(productos);
    }

    /**
     * Filtra los productos según su tipo.
     *
     * @param tipoProducto Tipo de producto a filtrar.
     * @return Lista de productos del tipo especificado.
     */
    public List<Producto> filtroTipoProducto(TipoProducto tipoProducto) {
        return servicioProducto.buscarPorTipo(tipoProducto);
    }

    /**
     * Elimina un producto del inventario.
     * Si la operación es exitosa, actualiza la vista y muestra un mensaje de confirmación.
     * En caso de error, muestra el mensaje correspondiente.
     *
     * @param producto Producto que se desea eliminar.
     */
    public void eliminaProducto(Producto producto) {
        try {
            servicioProducto.eliminarProducto(producto.getIdProducto());
            actualizarVista(producto.getTipoProducto());
            ventana.muestraMensaje("Producto eliminado correctamente.");
        } catch (IllegalArgumentException e) {
            ventana.muestraMensaje("Error al eliminar: " + e.getMessage());
        }
    }

    /**
     * Inicia el flujo para agregar un nuevo producto.
     * <p>Abre la ventana de agregar producto y establece la referencia al inventario actual.</p>
     */
    public void agregarProducto() {
        controlAgregarProducto.inicia(this);
    }

    /**
     * Inicia el flujo para modificar un producto existente.
     *
     * @param producto Producto que se desea modificar.
     */
    public void modificarProducto(Producto producto) {
        controlModificarProducto.inicia(producto, this);
    }

    /**
     * Actualiza la vista del inventario para reflejar los cambios recientes.
     *
     * @param tipoProducto Tipo de producto que se debe recargar en la vista.
     */
    public void actualizarVista(TipoProducto tipoProducto) {
        ventana.muestra(servicioProducto.buscarPorTipo(tipoProducto));
    }
}
