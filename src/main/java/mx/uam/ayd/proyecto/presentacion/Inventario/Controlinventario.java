package mx.uam.ayd.proyecto.presentacion.Inventario;

import jakarta.annotation.PostConstruct;
import mx.uam.ayd.proyecto.negocio.ServicioProducto;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import mx.uam.ayd.proyecto.presentacion.agregarProducto.ControlAgregarProducto;
import mx.uam.ayd.proyecto.presentacion.modificarProducto.ControlModificarProducto;


@Component
public class Controlinventario {

    private static final Logger log = LoggerFactory.getLogger(Controlinventario.class);

    private final ServicioProducto servicioProducto;
    private final VentanaInventario ventana;
    private final ControlAgregarProducto controlAgregarProducto;
    private final ControlModificarProducto controlModificarProducto;

    @Autowired
    public Controlinventario(ServicioProducto servicioProducto, VentanaInventario ventana, ControlAgregarProducto controlAgregarProducto, ControlModificarProducto controlModificarProducto) {
        this.servicioProducto = servicioProducto;
        this.ventana = ventana;
        this.controlAgregarProducto = controlAgregarProducto;
        this.controlModificarProducto = controlModificarProducto;
    }
    /**
     * Método que se ejecuta después de la construcción del bean
     * y realiza la conexión bidireccional entre el control y la ventana
     */
    @PostConstruct
    public void init() {
        ventana.setControlinventario(this);
    }

    /**
     * Inicia el caso de uso
     */
    public void inicia() {
        List<Producto> productos = servicioProducto.recuperaProductos();

        for(Producto producto : productos) {
            log.info("producto " + producto);
        }

       ventana.muestra(productos);
    }

    public void eliminaProducto(Long idproducto){
        try {

            servicioProducto.eliminarProducto(idproducto);
            ventana.muestra(servicioProducto.recuperaProductos());
            ventana.muestraMensaje("Producto eliminado correctamente.");
        } catch (IllegalArgumentException e) {
            ventana.muestraMensaje("Error al eliminar: " + e.getMessage());
        }
    }


    public void agregarProducto() {
        controlAgregarProducto.inicia(this);
    }

    public void modificarProducto(Long idProducto, Producto producto) {
        controlModificarProducto.inicia(idProducto,producto, this);
        ventana.muestra(servicioProducto.recuperaProductos());
    }

    public void actualizarVista() {
        ventana.muestra(servicioProducto.recuperaProductos());
    }
}
