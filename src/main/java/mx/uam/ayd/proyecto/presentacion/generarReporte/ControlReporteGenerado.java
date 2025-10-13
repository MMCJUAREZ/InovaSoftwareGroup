package mx.uam.ayd.proyecto.presentacion.generarReporte;

import mx.uam.ayd.proyecto.negocio.modelo.TipoProducto;
import mx.uam.ayd.proyecto.negocio.modelo.ReporteVentaDTO;
import java.util.List;
import java.time.LocalDate;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import mx.uam.ayd.proyecto.negocio.ServicioVenta;
import mx.uam.ayd.proyecto.util.UtilPDF;

/**
 * Controlador encargado de gestionar la lógica para mostrar reportes generados de ventas.

 * Se encarga de solicitar los datos al servicio de ventas, validar y mostrar los resultados.
 */
@Component
public class ControlReporteGenerado {

    private final ServicioVenta servicioVenta;
    private final VentanaReporteGenerado ventana;
    private final UtilPDF utilPDF = new UtilPDF();

    /**
     * Constructor con inyección de dependencias para el servicio y la ventana.
     *
     * @param servicioVenta servicio para acceder a datos y operaciones de venta
     * @param ventana ventana encargada de mostrar los reportes generados
     */
    @Autowired
    public ControlReporteGenerado(
            ServicioVenta servicioVenta,
            VentanaReporteGenerado ventana) {
        this.servicioVenta = servicioVenta;
        this.ventana = ventana;
    }

    /**
     * Inicializa la ventana estableciendo este controlador como su manejador lógico.
     * Se ejecuta automáticamente después de la construcción del bean.
     */
    @PostConstruct
    public void init() {
        ventana.setControlReporteGenerado(this);
    }

    /**
     * Inicia el proceso de generación y despliegue de un reporte de ventas.

     * Recupera los datos filtrados según los parámetros y los muestra en la ventana.
     * Valida que el tipo de reporte sea válido y maneja errores mostrando mensajes al usuario.
     *
     * @param desde fecha inicial para el filtro del reporte
     * @param hasta fecha final para el filtro del reporte
     * @param tipoReporte tipo de reporte solicitado ("Grafica" o "Tabla")
     * @param periodicidad periodicidad de los datos ("Diario", "Mensual", etc.)
     * @param tipoProducto tipo de producto para filtrar el reporte
     */
    public void inicia(LocalDate desde, LocalDate hasta, String tipoReporte,
                       String periodicidad, TipoProducto tipoProducto) {
        try {
            List<ReporteVentaDTO> ventas = servicioVenta.recuperarVenta(
                    desde, hasta, tipoProducto, periodicidad);

            if (ventas.isEmpty()) {
                ventana.muestraDialogoConMensaje("No hay ventas con estos filtros");
            } else {
                if (!"Grafica".equals(tipoReporte) && !"Tabla".equals(tipoReporte)) {
                    ventana.muestraDialogoConMensaje("Tipo de reporte inválido: " + tipoReporte);
                    return;
                }
                ventana.muestra(ventas, tipoReporte, periodicidad);
            }
        } catch (Exception e) {
            ventana.muestraDialogoConMensaje("Error al generar reporte: " + e.getMessage());
        }
    }

    /**
     * Solicita al servicio descargar el reporte generado y muestra un mensaje informativo.
     * Maneja posibles errores durante la descarga.
     *
     * @param ventas lista con los datos del reporte que se desea descargar
     */
    public void descargarReporte(List<ReporteVentaDTO> ventas) {
        try {
            utilPDF.descargarReporte(ventas);
            ventana.muestraDialogoConMensaje("Reporte descargado exitosamente");
        } catch (Exception e) {
            ventana.muestraDialogoConMensaje("Error al descargar: " + e.getMessage());
        }
    }

    /**
     * Maneja la acción de regresar a la ventana anterior.
     */
    public void regresar() {
        ventana.setVisible(false);
    }
}