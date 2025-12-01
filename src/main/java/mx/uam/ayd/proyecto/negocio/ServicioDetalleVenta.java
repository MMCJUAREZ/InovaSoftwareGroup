package mx.uam.ayd.proyecto.negocio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uam.ayd.proyecto.datos.VentaRepository;
import mx.uam.ayd.proyecto.datos.DetalleVentaRepository;
import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.Venta;
import mx.uam.ayd.proyecto.negocio.modelo.DetalleVenta;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import mx.uam.ayd.proyecto.negocio.modelo.Membresia;
import mx.uam.ayd.proyecto.negocio.modelo.TipoMembresia;
/**
 * Servicio para gestionar la creacion de los detalle de venta.
 */
@Service
public class ServicioDetalleVenta {
    private static final Logger log = LoggerFactory.getLogger(ServicioDetalleVenta.class);

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoRepository productoRepository;
    private double subtotal;

    /**
     * Constructor con inyección de dependencias para los repositorios usados.
     *
     * @param ventaRepository repositorio para gestionar ventas
     * @param detalleVentaRepository repositorio para gestionar detalles de venta
     * @param productoRepository repositorio para gestionar productos
     */
    @Autowired
    public ServicioDetalleVenta(VentaRepository ventaRepository,
                                DetalleVentaRepository detalleVentaRepository,
                                ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.productoRepository = productoRepository;
    }

    /**
     * Crea un nuevo objeto DetalleVenta validando que el producto, cantidad y venta sean correctos,
     * y que no exista ya el producto en la lista de detalles.
     *
     * @param producto producto que se va a vender
     * @param cantidadVendida cantidad del producto que se vende
     * @param venta la venta a la que pertenece este detalle
     * @param detallesVenta lista actual de detalles de venta para evitar duplicados
     * @return un nuevo objeto DetalleVenta con los datos proporcionados y el subtotal calculado
     * @throws IllegalArgumentException si producto o venta son nulos, o cantidad es <= 0
     * @throws IllegalStateException si el producto ya está en la lista o la cantidad supera el stock disponible
     */
    public DetalleVenta newDetalleVenta(Producto producto, int cantidadVendida, Venta venta, List<DetalleVenta> detallesVenta, Cliente cliente){
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }
        for (DetalleVenta detalleVentaL : detallesVenta){
            if(detalleVentaL.getProducto() == producto){
                throw new IllegalStateException("El producto ya esta en la tabla");
            }
        }
        if (cantidadVendida <= 0) {
            throw new IllegalArgumentException("La cantidad no puede ser menor o igual a 0");
        }
        if (cantidadVendida > producto.getCantidadStock()){
            throw new IllegalStateException("La cantidad vendida no puede ser mayor al stock");
        }
        if (venta == null) {
            throw new IllegalArgumentException("La venta no puede ser nulo");
        }

        log.info("Agregando producto " + producto.getNombre());
        Membresia membresia = cliente.getMembresia();

        DetalleVenta detalleVenta = new DetalleVenta();
        detalleVenta.setVenta(venta);
        detalleVenta.setProducto(producto);
        double subtotal = producto.getPrecio() * cantidadVendida;
        detalleVenta.setCantidadVendida(cantidadVendida);
        if (cliente == null || membresia == null){
            detalleVenta.setSubtotal(subtotal);
        }else if (membresia.getTipoMembresia() == TipoMembresia.Standard){
            detalleVenta.setSubtotal(subtotal*0.9);
        }else {
            detalleVenta.setSubtotal(subtotal*0.85);
        }

        return detalleVenta;
    }
}
