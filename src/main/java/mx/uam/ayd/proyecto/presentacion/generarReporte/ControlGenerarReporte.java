package mx.uam.ayd.proyecto.presentacion.generarReporte;

import java.time.LocalDate;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import jakarta.annotation.PostConstruct;
import mx.uam.ayd.proyecto.negocio.modelo.TipoProducto;
import mx.uam.ayd.proyecto.presentacion.registroVentas.VentanaRegistroVentas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioVenta;
import mx.uam.ayd.proyecto.negocio.ServicioDetalleVenta;
import mx.uam.ayd.proyecto.negocio.ServicioProducto;


/**
 * Controlador principal para la gestión de la generación de reportes.
 *
 * Coordina la interacción entre la vista VentanaGenerarReporte y los servicios de negocio
 * para recuperar datos necesarios
 */
@Component
public class ControlGenerarReporte {

    private final ServicioVenta servicioVenta;
    private final ServicioDetalleVenta servicioDetalleVenta;
    private final ServicioProducto servicioProducto;
    private final VentanaGenerarReporte ventana;
    private final ControlReporteGenerado controlReporteGenerado;

    /**
     * Constructor con inyección de dependencias para servicios y componentes de la vista y reporte.
     *
     * @param servicioVenta Servicio para manejo de ventas.
     * @param servicioDetalleVenta Servicio para manejo de detalles de venta.
     * @param servicioProducto Servicio para manejo de productos.
     * @param ventana Vista encargada de la interfaz de generación de reportes.
     * @param controlReporteGenerado Controlador encargado de la generación y despliegue de reportes.
     */
    @Autowired
    public ControlGenerarReporte(
            ServicioVenta servicioVenta,
            ServicioDetalleVenta servicioDetalleVenta,
            ServicioProducto servicioProducto,
            VentanaGenerarReporte ventana,
            ControlReporteGenerado controlReporteGenerado) {
        this.servicioVenta = servicioVenta;
        this.servicioDetalleVenta = servicioDetalleVenta;
        this.servicioProducto = servicioProducto;
        this.ventana = ventana;
        this.controlReporteGenerado = controlReporteGenerado;
    }

    /**
     * Inicializa la ventana asignando este controlador como su manejador lógico.
     * Método llamado automáticamente después de crear el bean por Spring.
     */
    @PostConstruct
    public void init() {
        ventana.setControlGenerarReporte(this);
    }

    /**
     * Inicia el flujo mostrando la ventana para la selección de parámetros del reporte.
     */
    public void inicia() {
        ventana.muestra();
    }

    /**
     * Método que recibe los parámetros para generar un reporte y encarga la generación al controlador ControlReporteGenerado.
     *
     * @param desde Fecha de inicio para el reporte.
     * @param hasta Fecha de fin para el reporte.
     * @param tipoReporte Tipo de reporte seleccionado (por ejemplo, tabla o gráfica).
     * @param periodicidad Periodicidad del reporte (diaria, mensual, etc.).
     * @param tipoProducto Tipo de producto a incluir en el reporte.
     */
    public void reporteGenerado(LocalDate desde, LocalDate hasta, String tipoReporte, String periodicidad, TipoProducto tipoProducto){
        controlReporteGenerado.inicia(desde, hasta, tipoReporte, periodicidad, tipoProducto);
    }

    /**
     * Termina el proceso de generación de reporte mostrando un mensaje y ocultando la ventana.
     *
     */
    public void termina() {
        ventana.setVisible(false);
    }
}
