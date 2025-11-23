package mx.uam.ayd.proyecto.presentacion.generarReceta;

import java.util.List;

import jakarta.annotation.PostConstruct;

import mx.uam.ayd.proyecto.negocio.ServicioProducto;
import mx.uam.ayd.proyecto.negocio.ServicioCorreo;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import mx.uam.ayd.proyecto.negocio.modelo.UnidadProducto;
import mx.uam.ayd.proyecto.negocio.modelo.UsoVeterinario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.util.UtilPDF;

/**
 * @class ControlGenerarReceta
 * @brief Controlador principal para la gestión de la generación de recetas veterinarias.
 *
 * Coordina la interacción entre la vista {@link VentanaGenerarReceta} y los servicios
 * de negocio necesarios para obtener información y generar el documento PDF de la receta.
 */
@Component
public class ControlGenerarReceta {

    /** Servicio para manejar operaciones relacionadas con el envio de correos. */
    private final ServicioCorreo servicioCorreo;
    /** Servicio para manejar operaciones relacionadas con productos. */
    private final ServicioProducto servicioProducto;

    /** Vista encargada de mostrar la interfaz de generación de recetas. */
    private final VentanaGenerarReceta ventana;

    /** Utilidad para la generación de documentos PDF. */
    private final UtilPDF utilPDF = new UtilPDF();

    /**
     * @brief Constructor con inyección de dependencias.
     *
     * Inicializa el controlador con el servicio de productos y la ventana asociada.
     *
     * @param servicioProducto Servicio encargado de operaciones con productos.
     * @param ventana Vista para la selección y muestra de medicamentos.
     */
    @Autowired
    public ControlGenerarReceta(ServicioProducto servicioProducto,
                                ServicioCorreo servicioCorreo,
                                VentanaGenerarReceta ventana) {
        this.servicioProducto = servicioProducto;
        this.servicioCorreo = servicioCorreo;
        this.ventana = ventana;
    }

    /**
     * @brief Inicializa la vista asignándole este controlador.
     *
     * Método ejecutado automáticamente después de que el bean es creado por Spring.
     */
    @PostConstruct
    public void init() {
        ventana.setControlGenerarReceta(this);
    }

    /**
     * @brief Inicia el flujo mostrando la ventana con los medicamentos disponibles.
     *
     * Carga por defecto los medicamentos asociados al uso veterinario "Perro".
     */
    public void inicia() {
        List<Producto> medicamentos = servicioProducto.buscarPorUsoVeterinario(UsoVeterinario.Perro);
        ventana.muestra(medicamentos);
    }

    /**
     * @brief Filtra los medicamentos según uso veterinario y presentación.
     *
     * @param usoVeterinario Tipo de uso veterinario seleccionado.
     * @param presentacion Presentación del producto (unidad).
     * Si es "Sin filtro", no se filtra por presentación.
     *
     * @return Lista filtrada de productos.
     */
    public List<Producto> filtrarMedicamentos(String usoVeterinario, String presentacion) {
        if (presentacion.equals("Sin filtro")) {
            return servicioProducto.buscarPorUsoVeterinario(
                    UsoVeterinario.valueOf(usoVeterinario));
        } else {
            return servicioProducto.buscarPorUsoVeterinarioAndUnidadProducto(
                    UsoVeterinario.valueOf(usoVeterinario),
                    UnidadProducto.valueOf(presentacion));
        }
    }

    /**
     * @brief Genera el documento PDF de la receta.
     *
     * @param receta Lista de objetos DatosReceta que contienen la información a incluir.
     */
    public void generarReceta(List<DatosReceta> receta, String correo) {
        String ruta = utilPDF.crearReceta(receta);
        if(ruta != null && correo != null) {
            servicioCorreo.enviarCorreoConAdjunto(correo, ruta);
        }
    }

    /**
     * @brief Finaliza el proceso mostrando un mensaje y ocultando la ventana.
     *
     * @param mensaje Texto a mostrar en un diálogo modal.
     */
    public void termina(String mensaje) {
        ventana.muestraDialogoConMensaje(mensaje);
        ventana.setVisible(false);
    }
}
