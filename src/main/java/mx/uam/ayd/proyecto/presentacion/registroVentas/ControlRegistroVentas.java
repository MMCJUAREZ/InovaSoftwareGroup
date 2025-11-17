package mx.uam.ayd.proyecto.presentacion.registroVentas;

import java.util.List;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioVenta;
import mx.uam.ayd.proyecto.negocio.ServicioDetalleVenta;
import mx.uam.ayd.proyecto.negocio.ServicioProducto;
import mx.uam.ayd.proyecto.negocio.modelo.Venta;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.DetalleVenta;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import mx.uam.ayd.proyecto.util.UtilPDF;

/**
 * Controlador responsable del flujo y lógica para el registro de ventas.
 *
 * Gestiona la interacción entre la interfaz gráfica VentanaRegistroVentas
 * y los servicios de negocio relacionados con ventas, detalles de ventas y productos.
 */
@Component
public class ControlRegistroVentas {

    private final ServicioVenta servicioVenta;
    private final ServicioDetalleVenta servicioDetalleVenta;
    private final ServicioProducto servicioProducto;
    private final VentanaRegistroVentas ventana;
    private final UtilPDF utilPDF = new UtilPDF();
    

    /**
     * Venta actual en proceso.
     */
    private Venta venta;
    private Cliente cliente;
    /**
     * Constructor con inyección de dependencias.
     *
     * @param servicioVenta Servicio para manejo de ventas.
     * @param servicioDetalleVenta Servicio para manejo de detalles de venta.
     * @param servicioProducto Servicio para manejo de productos.
     * @param ventana Vista asociada a registro de ventas.
     */
    @Autowired
    public ControlRegistroVentas(
            ServicioVenta servicioVenta,
            ServicioDetalleVenta servicioDetalleVenta,
            ServicioProducto servicioProducto,
            VentanaRegistroVentas ventana) {
        this.servicioVenta = servicioVenta;
        this.servicioDetalleVenta = servicioDetalleVenta;
        this.servicioProducto = servicioProducto;
        this.ventana = ventana;
    }

    /**
     * Inicializa la ventana estableciendo este controlador como su manejador lógico.
     * Método llamado automáticamente por Spring después de la creación del bean.
     */
    @PostConstruct
    public void init() {
        ventana.setControlRegistroVentas(this);
    }

    /**
     * Inicia el proceso de registro de ventas.
     *
     * Recupera la lista de productos y crea una nueva venta.
     * Luego muestra la ventana con los datos inicializados.
     *
     * En caso de error al crear la venta, muestra mensaje al usuario.
     */
    public void inicia() {
        List<Producto> productos = servicioProducto.recuperaProductosConStock();
        if (productos.isEmpty()) {
            ventana.muestraDialogoConMensaje("No hay productos con stock disponible, por favor agregue manualmente productos primero");
        } else {
            try {
                this.venta = servicioVenta.crearVenta();
            } catch(Exception ex) {
                ventana.muestraDialogoConMensaje("Ocurrió un error al crear la venta");
            }

            ventana.muestra(productos, venta);
        }
    }

    public void inicia(Cliente cliente) {
        this.cliente = cliente;
        List<Producto> productos = servicioProducto.recuperaProductosConStock();
        if (productos.isEmpty()) {
            ventana.muestraDialogoConMensaje("No hay productos con stock disponible, por favor agregue manualmente productos primero");
        } else {
            try {
                this.venta = servicioVenta.crearVenta();
            } catch(Exception ex) {
                ventana.muestraDialogoConMensaje("Ocurrió un error al crear la venta");
            }

            ventana.muestra(productos, venta);
        }
    }

    /**
     * Crea un detalle de venta para un producto con cantidad dada.
     *
     * Llama al servicioDetalleVenta y muestra mensajes según el resultado.
     *
     * @param producto Producto a agregar.
     * @param cantidad Cantidad vendida.
     * @param detalleVentas Lista actual de detalles de venta para evitar duplicados.
     * @return El detalle de venta creado.
     * @throws Exception Si ocurre algún error al agregar el producto.
     */
    public DetalleVenta crearDetalleVenta(Producto producto, int cantidad, List<DetalleVenta> detalleVentas) {
        try {
            DetalleVenta detalleVenta = servicioDetalleVenta.newDetalleVenta(producto, cantidad, venta, detalleVentas);
            ventana.muestraDialogoConMensaje("Producto agregado exitosamente");
            return detalleVenta;
        } catch (Exception ex) {
            ventana.muestraDialogoConMensaje("Error al agregar el producto: " + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Actualiza el stock de los productos vendidos según los detalles de venta.
     *
     * @param detallesVenta Lista con los detalles de la venta para actualizar stock.
     */
    public void actualizarStock(List<DetalleVenta> detallesVenta) {
        for (DetalleVenta detalle : detallesVenta) {
            servicioVenta.actualizarStock(detalle.getProducto(), detalle.getCantidadVendida());
        }
    }

    /**
     * Guarda la venta con el monto total calculado a partir de los detalles.
     *
     * @param detallesVenta Lista de detalles de venta para calcular el total.
     */
    public void guardarVenta(List<DetalleVenta> detallesVenta) {
        double montoTotal = 0;
        for (DetalleVenta detalleVenta : detallesVenta) {
            montoTotal += detalleVenta.getSubtotal();
        }
        servicioVenta.guardarVenta(venta, montoTotal, cliente);
    }

    /**
     * Guarda los detalles asociados a la venta.
     *
     * @param detallesVenta Lista de detalles a guardar.
     */
    public void guardarDetallesVenta(List<DetalleVenta> detallesVenta) {
        servicioVenta.agregarDetallesVenta(detallesVenta);
    }

    /**
     * Finaliza el proceso de venta mostrando un mensaje y ocultando la ventana.
     *
     * @param mensaje Mensaje que se mostrará al usuario.
     */
    public void termina(String mensaje) {
        ventana.muestraDialogoConMensaje(mensaje);
        ventana.setVisible(false);
    }

    /**
     * Genera un documento asociado a la venta con sus detalles.
     *
     * @param detallesVenta Lista de detalles para incluir en el documento.
     */
    public void crearDocumento(List<DetalleVenta> detallesVenta) {
        //servicioVenta.crearDocumentoVenta(detallesVenta, venta);
        utilPDF.crearDocumentoVenta(detallesVenta, venta);
    }
    //Deberia agregar que dependiendo de si se vende por kilo pueda seleccionar si se vendo por gramos
    //o por kilo y deberia poder aceptar decimales si es por kilo
}
