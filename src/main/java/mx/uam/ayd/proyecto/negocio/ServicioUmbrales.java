package mx.uam.ayd.proyecto.negocio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import mx.uam.ayd.proyecto.datos.UmbralRepository;
import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import mx.uam.ayd.proyecto.negocio.modelo.Umbral;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServicioUmbrales {

    private static final Logger log = LoggerFactory.getLogger(ServicioUmbrales.class);

    private final UmbralRepository umbralRepository;
    private final ProductoRepository productoRepository;

    // Eliminamos las dependencias a componentes de presentación
    // private ControlConfiguracionUmbrales controlConfiguracionUmbrales;
    // private VentanaConfiguracionUmbrales ventanaConfiguracionUmbrales;

    @Autowired
    public ServicioUmbrales(UmbralRepository umbralRepository,
                            ProductoRepository productoRepository) {
        this.umbralRepository = umbralRepository;
        this.productoRepository = productoRepository;
    }

    /**
     * Recupera productos con stock > 0 (actualizado para coincidir con diagrama)
     */
    public List<Producto> recuperaConStockNoCero() {
        return productoRepository.findByCantidadStockGreaterThan(0);
    }

    /**
     * Busca un umbral por el ID del producto asociado
     */
    public Umbral findById(Long idProducto) {
        return umbralRepository.findByProductoIdProducto(idProducto);
    }

    /**
     * Guarda cambios en un umbral existente (renombrado para coincidir con diagrama)
     */
    public Umbral guardaCambios(Umbral umbral) {
        if(umbral == null) {
            throw new IllegalArgumentException("Umbral no puede ser nulo");
        }

        // Validar que el producto existe
        productoRepository.findById(umbral.getProducto().getIdProducto())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        return umbralRepository.save(umbral);
    }

    /**
     * Crea un nuevo umbral (renombrado para coincidir con diagrama)
     */
    public Umbral save(Umbral umbral) {
        if(umbral == null) {
            throw new IllegalArgumentException("Umbral no puede ser nulo");
        }

        // Validar que no exista ya un umbral para este producto
        if(umbralRepository.findByProductoIdProducto(umbral.getProducto().getIdProducto()) != null) {
            throw new IllegalArgumentException("El producto ya tiene umbral configurado");
        }

        return umbralRepository.save(umbral);
    }

    /**
     * Método unificado para manejar creación/actualización
     */
    public void manejarEdicionUmbral(Long idProducto, int minimo) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        Umbral umbral = umbralRepository.findByProductoIdProducto(idProducto);

        if(umbral == null) {
            umbral = new Umbral();
            umbral.setProducto(producto);
            umbral.setValorMinimo(minimo);
            this.save(umbral);
        } else {
            umbral.setValorMinimo(minimo);
            this.guardaCambios(umbral);
        }
    }
    public Optional<Producto> recuperarProductoPorId(Long idProducto) {
        return productoRepository.findById(idProducto);
    }
}