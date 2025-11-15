package mx.uam.ayd.proyecto.negocio;

import mx.uam.ayd.proyecto.negocio.modelo.TipoProducto;
import mx.uam.ayd.proyecto.negocio.modelo.UnidadProducto;
import mx.uam.ayd.proyecto.negocio.modelo.MarcaProducto;
import mx.uam.ayd.proyecto.negocio.modelo.UsoVeterinario;

import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @brief Servicio para la gestión de productos.
 *
 * Esta clase proporciona métodos para crear, modificar, recuperar y eliminar productos.
 * Realiza validaciones sobre los datos y utiliza el repositorio de productos
 * para interactuar con la base de datos.
 */
@Service
public class ServicioProducto {

    private static final Logger log = LoggerFactory.getLogger(ServicioProducto.class);
    private final ProductoRepository productoRepository;

    /**
     * @brief Constructor con inyección de dependencias.
     *
     * Inicializa el servicio con una instancia del repositorio de productos.
     *
     * @param productoRepository Repositorio que maneja las operaciones de persistencia de productos.
     */
    @Autowired
    public ServicioProducto(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    /**
     * @brief Agrega un nuevo producto a la base de datos.
     *
     * Antes de agregar el producto, se validan sus datos y se verifica que no exista
     * otro producto con el mismo nombre, tipo y marca.
     * También se comprueba que los valores como precio y fecha de caducidad sean válidos.
     *
     * @param nombre Nombre del producto (no nulo ni vacío).
     * @param tipoProducto Tipo del producto (no nulo).
     * @param marcaProducto Marca del producto (no nula).
     * @param precio Precio del producto (mayor que cero).
     * @param cantidad Cantidad en stock inicial.
     * @param unidadProducto Unidad de medida del producto (no nula).
     * @param fechaCaducidad Fecha de caducidad (opcional; si se proporciona, debe ser al menos una semana después de la fecha actual).
     * @param usoVeterinario Uso veterinario (obligatorio solo para medicamentos).
     * @return El producto guardado en la base de datos.
     *
     * @throws IllegalStateException Si ya existe un producto con el mismo nombre, tipo y marca.
     * @throws IllegalArgumentException Si alguno de los datos requeridos es inválido o nulo.
     */
    public Producto agregarProducto(String nombre, TipoProducto tipoProducto, MarcaProducto marcaProducto,
                                    double precio, int cantidad, UnidadProducto unidadProducto,
                                    LocalDate fechaCaducidad, UsoVeterinario usoVeterinario) {

        Optional<Producto> productoExistente =
                productoRepository.findByNombreAndTipoProductoAndMarcaProducto(nombre, tipoProducto, marcaProducto);

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
        if (tipoProducto.equals(TipoProducto.Medicamento) && usoVeterinario == null) {
            throw new IllegalArgumentException("El uso veterinario no puede ser nulo para medicamentos");
        }
        if ((tipoProducto.equals(TipoProducto.Comida) || tipoProducto.equals(TipoProducto.Medicamento)) && fechaCaducidad == null) {
            throw new IllegalArgumentException("La fecha de caducidad no puede ser nula en medicamentos o comida");
        }
        if (fechaCaducidad != null && fechaCaducidad.isBefore(LocalDate.now().plusWeeks(1))) {
            throw new IllegalArgumentException("La fecha no puede ser anterior a una semana a partir de hoy");
        }

        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setTipoProducto(tipoProducto);
        producto.setMarcaProducto(marcaProducto);
        producto.setPrecio(precio);
        producto.setCantidadStock(cantidad);
        producto.setUnidadProducto(unidadProducto);
        producto.setFechaCaducidad(fechaCaducidad);
        producto.setUsoVeterinario(usoVeterinario);

        return productoRepository.save(producto);
    }

    /**
     * @brief Modifica un producto existente.
     *
     * Actualiza los datos de un producto ya existente, realizando las mismas validaciones
     * que en la creación. Si el producto no es un medicamento, se elimina el uso veterinario.
     *
     * @param producto Producto a modificar.
     * @param nombre Nuevo nombre del producto.
     * @param tipoProducto Nuevo tipo de producto.
     * @param marcaProducto Nueva marca del producto.
     * @param precio Nuevo precio (mayor que cero).
     * @param cantidad Nueva cantidad en stock.
     * @param unidadProducto Nueva unidad de medida.
     * @param fechaCaducidad Nueva fecha de caducidad.
     * @param usoVeterinario Nuevo uso veterinario (si aplica).
     * @return El producto actualizado.
     *
     * @throws IllegalArgumentException Si alguno de los datos requeridos es inválido o nulo.
     */
    public Producto modificarProducto(Producto producto, String nombre, TipoProducto tipoProducto, MarcaProducto marcaProducto,
                                      double precio, int cantidad, UnidadProducto unidadProducto,
                                      LocalDate fechaCaducidad, UsoVeterinario usoVeterinario) {

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
        if (tipoProducto.equals(TipoProducto.Medicamento) && usoVeterinario == null) {
            throw new IllegalArgumentException("El uso veterinario no puede ser nulo para medicamentos");
        }
        if ((tipoProducto.equals(TipoProducto.Comida) || tipoProducto.equals(TipoProducto.Medicamento)) && fechaCaducidad == null) {
            throw new IllegalArgumentException("La fecha de caducidad no puede ser nula en medicamentos o comida");
        }
        if (fechaCaducidad != null && fechaCaducidad.isBefore(LocalDate.now().plusWeeks(1))) {
            throw new IllegalArgumentException("La fecha no puede ser anterior a una semana a partir de hoy");
        }

        if (!tipoProducto.equals(TipoProducto.Medicamento)) {
            producto.setUsoVeterinario(null);
        }

        producto.setNombre(nombre);
        producto.setTipoProducto(tipoProducto);
        producto.setMarcaProducto(marcaProducto);
        producto.setPrecio(precio);
        producto.setCantidadStock(cantidad);
        producto.setUnidadProducto(unidadProducto);
        producto.setFechaCaducidad(fechaCaducidad);
        producto.setUsoVeterinario(usoVeterinario);

        return productoRepository.save(producto);
    }

    /**
     * @brief Recupera todos los productos de la base de datos.
     *
     * @return Lista de productos (puede estar vacía si no existen registros).
     */
    public List<Producto> recuperaProductos() {
        List<Producto> productos = new ArrayList<>();
        for (Producto producto : productoRepository.findAll()) {
            productos.add(producto);
        }
        return productos;
    }

    /**
     * @brief Recupera todos los productos con stock disponible.
     *
     * @return Lista de productos con cantidad en stock mayor que cero.
     */
    public List<Producto> recuperaProductosConStock() {
        return productoRepository.findByCantidadStockGreaterThan(0);
    }

    /**
     * @brief Elimina un producto por su identificador.
     *
     * @param idProducto Identificador del producto a eliminar.
     */
    public void eliminarProducto(Long idProducto) {
        productoRepository.deleteById(idProducto);
    }

    /**
     * @brief Busca productos por tipo.
     *
     * @param tipoProducto Tipo de producto a buscar.
     * @return Lista de productos del tipo especificado.
     */
    public List<Producto> buscarPorTipo(TipoProducto tipoProducto) {
        return productoRepository.findByTipoProducto(tipoProducto);
    }
    /**
     * @brief Busca productos por tipo.
     *
     * @param usoVeterinario uso veterinario del producto a buscar.
     * @param unidadProducto  a buscar.
     * @return Lista de productos filtrados por su uso veterianrio y el tipo de unidad que manejan.
     */
    public List<Producto> buscarPorUsoVeterinarioAndUnidadProducto(UsoVeterinario usoVeterinario, UnidadProducto unidadProducto) {
        return productoRepository.findByUsoVeterinarioAndUnidadProducto(usoVeterinario, unidadProducto);
    }
    /**
     * @brief Busca productos por tipo.
     *
     * @param usoVeterinario uso veterinario del producto a buscar.
     * @return Lista de productos filtrados por su uso veterianrio.
     */
    public List<Producto> buscarPorUsoVeterinario(UsoVeterinario usoVeterinario) {
        return productoRepository.findByUsoVeterinario(usoVeterinario);
    }
}


