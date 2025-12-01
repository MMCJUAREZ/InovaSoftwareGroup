package mx.uam.ayd.proyecto.negocio;

import java.util.List;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


import mx.uam.ayd.proyecto.datos.UmbralRepository;
import mx.uam.ayd.proyecto.negocio.modelo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uam.ayd.proyecto.datos.VentaRepository;
import mx.uam.ayd.proyecto.datos.ClienteRepository;
import mx.uam.ayd.proyecto.datos.DetalleVentaRepository;
import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.ReporteVentaDTO;

/**
 * Servicio para gestionar operaciones relacionadas con ventas,
 * incluyendo creación, almacenamiento, actualización de stock,
 * generación de reportes y exportación a PDF.
 */
@Service
public class ServicioVenta {

    private static final Logger log = LoggerFactory.getLogger(ServicioVenta.class);

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoRepository productoRepository;
    private final UmbralRepository umbralRepository;
    private final ServicioCorreo servicioCorreo;
    private final ClienteRepository clienteRepository;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param ventaRepository repositorio para operaciones de ventas
     * @param detalleVentaRepository repositorio para detalles de venta
     * @param productoRepository repositorio para productos
     */
    @Autowired
    public ServicioVenta(VentaRepository ventaRepository,
                         DetalleVentaRepository detalleVentaRepository,
                         ProductoRepository productoRepository, UmbralRepository umbralRepository, ServicioCorreo servicioCorreo,
                         ClienteRepository clienteRepository) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.productoRepository = productoRepository;
        this.umbralRepository = umbralRepository;
        this.servicioCorreo = servicioCorreo;
        this.clienteRepository = clienteRepository;
    }

    /**
     * Crea una nueva instancia de venta.
     *
     * @return venta nueva instancia de {@link Venta}
     */
    public Venta crearVenta() {
        log.info("Creando venta");
        return new Venta();
    }

    /**
     * Actualiza el stock de un producto restando la cantidad vendida.
     *
     * @param producto producto a actualizar
     * @param cantidadVendida cantidad vendida que se debe descontar
     * @throws IllegalArgumentException si el producto es nulo o cantidad es inválida
     * @throws IllegalStateException si la cantidad vendida es mayor al stock disponible
     */
    public void actualizarStock(Producto producto, int cantidadVendida){
        Umbral umbral = umbralRepository.findByProductoIdProducto(producto.getIdProducto());
        if (producto == null){
            throw new IllegalArgumentException("Producto no puede ser nulo");
        }
        if (cantidadVendida <= 0){
            throw new IllegalArgumentException("La cantidad vendida no puede ser menor a 0");
        }
        if (cantidadVendida > producto.getCantidadStock()){
            throw new IllegalStateException("La cantidad vendida no puede ser mayor al stock");
        }

        int nuevaCantidad = producto.getCantidadStock() - cantidadVendida;
        producto.setCantidadStock(nuevaCantidad);

        productoRepository.save(producto);

    }

    /**
     * Guarda una venta estableciendo su monto total y fecha actual.
     *
     * @param venta la venta a guardar
     * @param montoTotal monto total calculado para la venta
     * @throws IllegalArgumentException si la venta es nula o el monto es inválido
     */
    public void guardarVenta(Venta venta, double montoTotal, Cliente cliente){
        if (venta == null){
            throw new IllegalArgumentException("La venta no puede ser nulo");
        }
        if (montoTotal <= 0) {
            throw new IllegalArgumentException("El monto total no puede ser menor o igual a 0");
        }
        if (cliente != null){
            Double montoActual = cliente.getMontoAcumulado();
            Double total = montoActual + montoTotal;
            cliente.setMontoAcumulado(total);
            clienteRepository.save(cliente);
        }
        venta.setMontoTotal(montoTotal);
        venta.setFecha(LocalDate.now());
        ventaRepository.save(venta);
    }

    /**
     * Guarda una lista de detalles de venta asegurándose que no existan productos duplicados.
     *
     * @param detallesVenta lista con los detalles de la venta
     * @throws IllegalStateException si la lista está vacía o contiene productos duplicados
     */
    public void agregarDetallesVenta(List<DetalleVenta> detallesVenta) {
        if (detallesVenta.isEmpty()){
            throw new IllegalStateException("La lista no puede estar vacia");
        }
        Set<Producto> productos = new HashSet<>();
        for (DetalleVenta detalleVenta : detallesVenta){
            if (!productos.add(detalleVenta.getProducto())){
                throw new IllegalStateException("La lista de detalles contiene productos duplicados");
            }
        }
        detalleVentaRepository.saveAll(detallesVenta);
    }

    /**
     * Recupera un listado de reportes de ventas agrupados por periodicidad y filtrados por fechas y tipo de producto.
     *
     * @param desde fecha de inicio del rango para el reporte
     * @param hasta fecha final del rango para el reporte
     * @param tipoProducto tipo de producto para filtrar
     * @param periodicidad "Mensual" o cualquier otro valor para diario
     * @return lista de objetos {@link ReporteVentaDTO} con la información del reporte
     */
    public List<ReporteVentaDTO> recuperarVenta(LocalDate desde, LocalDate hasta, TipoProducto tipoProducto, String periodicidad) {
        if (periodicidad.equals("Mensual")){
            return ventaRepository.obtenerReporteVentasMensual(desde, hasta, tipoProducto);
        } else {
            return ventaRepository.obtenerReporteVentasDiario(desde, hasta, tipoProducto);
        }
    }
}