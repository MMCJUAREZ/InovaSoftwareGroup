package mx.uam.ayd.proyecto.negocio;

import mx.uam.ayd.proyecto.negocio.modelo.TipoProducto;
import mx.uam.ayd.proyecto.negocio.modelo.UnidadProducto;
import mx.uam.ayd.proyecto.negocio.modelo.MarcaProducto;

import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.DataBufferUShort;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Servicio para gestionar la creacion y recuperacion de productos.
 */
@Service
public class ServicioProducto {

    private static final Logger log = LoggerFactory.getLogger(ServicioProducto.class);
    private final ProductoRepository productoRepository;

    /**
     * Constructor con inyección de dependencias para el repositorio de productos.
     *
     * @param productoRepository repositorio para gestionar productos en la base de datos
     */
    @Autowired
    public ServicioProducto(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    /**
     * Agrega un nuevo producto a la base de datos luego de validar que no exista
     * y que sus datos sean correctos.
     *
     * @param nombre Nombre del producto (no nulo ni vacío)
     * @param tipoProducto Tipo del producto (no nulo)
     * @param marcaProducto Marca del producto (no nula)
     * @param precio Precio del producto (mayor a cero)
     * @param cantidad Cantidad en stock inicial
     * @param unidadProducto Unidad de medida del producto (no nula)
     * @param fechaCaducidad Fecha de caducidad (opcional, si existe debe ser al menos una semana después de la fecha actual)
     * @return Producto guardado en la base de datos
     * @throws IllegalStateException si el producto con el mismo nombre, tipo y marca ya existe
     * @throws IllegalArgumentException si algún dato obligatorio es inválido
     */
    public Producto agregarProducto(String nombre, TipoProducto tipoProducto, MarcaProducto marcaProducto,
                                    double precio, int cantidad, UnidadProducto unidadProducto, LocalDate fechaCaducidad) {
        // Buscar si ya existe el producto con esos datos
        Optional<Producto> productoExistente = productoRepository.findByNombreAndTipoProductoAndMarcaProducto(nombre, tipoProducto, marcaProducto);

        if (productoExistente.isPresent()) {
            throw new IllegalStateException("El producto ya existe en la base de datos.");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede ser nulo o vacío");
        }
        if (tipoProducto == null) {
            throw new IllegalArgumentException("El tipo de producto no puede ser nulo");
        }
        if (marcaProducto == null) {
            throw new IllegalArgumentException("La marca del producto no puede ser nula");
        }
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a cero");
        }

        if (unidadProducto == null) {
            throw new IllegalArgumentException("La unidad del producto no puede ser nula");
        }
        if (fechaCaducidad != null) {
            if(fechaCaducidad.isBefore(LocalDate.now().plusWeeks(1))) {
                throw new IllegalArgumentException("La fecha no puede ser anterior a partir de una semana");
            }
        }

        // Crear el producto
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setTipoProducto(tipoProducto);
        producto.setMarcaProducto(marcaProducto);
        producto.setPrecio(precio);
        producto.setCantidadStock(cantidad);
        producto.setUnidadProducto(unidadProducto);
        producto.setFechaCaducidad(fechaCaducidad);

        return productoRepository.save(producto);
    }

    public Producto modificarProducto(Producto producto, String nombre, TipoProducto tipoProducto, MarcaProducto marcaProducto,
                             double precio, int cantidad, UnidadProducto unidadProducto, LocalDate fechaCaducidad){
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede ser nulo o vacío");
        }
        if (tipoProducto == null) {
            throw new IllegalArgumentException("El tipo de producto no puede ser nulo");
        }
        if (marcaProducto == null) {
            throw new IllegalArgumentException("La marca del producto no puede ser nula");
        }
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a cero");
        }

        if (unidadProducto == null) {
            throw new IllegalArgumentException("La unidad del producto no puede ser nula");
        }
        if (fechaCaducidad != null) {
            if(fechaCaducidad.isBefore(LocalDate.now().plusWeeks(1))) {
                throw new IllegalArgumentException("La fecha no puede ser anterior a partir de una semana");
            }
        }
        producto.setNombre(nombre);
        producto.setTipoProducto(tipoProducto);
        producto.setMarcaProducto(marcaProducto);
        producto.setPrecio(precio);
        producto.setCantidadStock(cantidad);
        producto.setUnidadProducto(unidadProducto);
        producto.setFechaCaducidad(fechaCaducidad);

        return productoRepository.save(producto);
    }
    /**
     * Recupera todos los productos existentes en la base de datos.
     *
     * @return Lista de productos (puede estar vacía si no hay productos)
     */
    public List<Producto> recuperaProductos() {
        List<Producto> productos = new ArrayList<>();

        for (Producto producto : productoRepository.findAll()) {
            productos.add(producto);
        }

        return productos;
    }

    public List<Producto> recuperaProductosConStock() {
        List<Producto> productos = new ArrayList<>();

        productos = productoRepository.findByCantidadStockGreaterThan(0);

        return productos;
    }

    public void eliminarProducto(Long idProducto) {
        productoRepository.deleteById(idProducto);
    }
}




