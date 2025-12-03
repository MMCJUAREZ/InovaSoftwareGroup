package mx.uam.ayd.proyecto.presentacion.citas;

import mx.uam.ayd.proyecto.negocio.ServicioCita;
import mx.uam.ayd.proyecto.negocio.ServicioVeterinario;
import mx.uam.ayd.proyecto.negocio.modelo.Cita;
import mx.uam.ayd.proyecto.negocio.modelo.TipoCita;
import mx.uam.ayd.proyecto.negocio.modelo.Veterinario;
import mx.uam.ayd.proyecto.util.UtilPDF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador principal para la gestión de citas.
 * Coordina la lógica de negocio (ServicioCita) con la interfaz de usuario (VentanaCitas).
 * @author InovaSoftwareGroup
 */
@Component
public class ControlCitas {

    private final ServicioCita servicioCita;
    private final VentanaCitas ventana;
    private final ServicioVeterinario servicioVeterinario;

    private final UtilPDF utilPDF = new UtilPDF();

    @Autowired
    public ControlCitas(ServicioCita servicioCita, VentanaCitas ventana, ServicioVeterinario servicioVeterinario) {
        this.servicioCita = servicioCita;
        this.ventana = ventana;
        this.servicioVeterinario = servicioVeterinario;
        this.ventana.setControl(this);
    }

    /**
     * Inicia el caso de uso mostrando la ventana de gestión de citas.
     */
    public void inicia() {
        List<Cita> citas = servicioCita.recuperarCitas();
        List<Veterinario> veterinarios = servicioVeterinario.recuperarVeterinarios();
        ventana.muestra(citas, veterinarios);
    }

    /**
     * Recupera y actualiza la lista de citas en la ventana.
     */
    public void actualizarListaCitas() {
        List<Cita> citas = servicioCita.recuperarCitas();
        ventana.actualizarTabla(citas);
    }

    /**
     * Recupera la lista de veterinarios disponibles.
     * @return Lista de veterinarios.
     */
    public List<Veterinario> recuperarVeterinarios() {
        return servicioVeterinario.recuperarVeterinarios();
    }

    /**
     * Genera el comprobante PDF para una cita específica.
     * (HU-03)
     * * @param cita La cita de la cual se generará el comprobante.
     */
    public void generarComprobante(Cita cita) {
        try {
            utilPDF.generarComprobanteCita(cita);
        } catch (Exception ex) {
            ventana.muestraAlerta("Error al generar PDF", ex.getMessage(), "ERROR");
        }
    }

    /**
     * Agendar una nueva cita.
     * * @param fechaHora Fecha y hora de la cita.
     * @param tipo Tipo de servicio.
     * @param nombre Nombre del solicitante.
     * @param contacto Datos de contacto.
     * @param enviarCorreo Bandera para enviar correo de confirmación.
     * @param veterinario Veterinario asignado.
     * @param motivo Motivo de la cita.
     * @param notas Notas adicionales.
     */
    public void agendarCita(LocalDateTime fechaHora, TipoCita tipo, String nombre, String contacto, boolean enviarCorreo,
                            Veterinario veterinario, String motivo, String notas) {
        try {
            servicioCita.agendarCita(fechaHora, tipo, nombre, contacto, enviarCorreo, veterinario, motivo, notas);
            ventana.muestraAlerta("Éxito", "Cita agendada correctamente.", "INFORMATION");
            actualizarListaCitas();
        } catch (IllegalArgumentException ex) {
            ventana.muestraAlerta("Error de Validación", ex.getMessage(), "ERROR");
        }
    }

    /**
     * Modificar una cita existente.
     * * @param idCita ID de la cita.
     * @param fechaHora Nueva fecha y hora.
     * @param tipo Nuevo tipo de servicio.
     * @param nombre Nuevo nombre del solicitante.
     * @param contacto Nuevo contacto.
     * @param veterinario Nuevo veterinario.
     * @param motivo Nuevo motivo.
     * @param notas Nuevas notas.
     */
    public void modificarCita(Long idCita, LocalDateTime fechaHora, TipoCita tipo, String nombre, String contacto,
                              Veterinario veterinario, String motivo, String notas) {
        try {
            servicioCita.modificarCita(idCita, fechaHora, tipo, nombre, contacto, veterinario, motivo, notas);
            ventana.muestraAlerta("Éxito", "Cita modificada correctamente.", "INFORMATION");
            actualizarListaCitas();
        } catch (IllegalArgumentException ex) {
            ventana.muestraAlerta("Error de Modificación", ex.getMessage(), "ERROR");
        }
    }

    /**
     * Eliminar una cita.
     * * @param idCita ID de la cita a eliminar.
     */
    public void eliminarCita(Long idCita) {
        try {
            servicioCita.eliminarCita(idCita);
            ventana.muestraAlerta("Éxito", "Cita eliminada correctamente.", "INFORMATION");
            actualizarListaCitas();
        } catch (IllegalArgumentException ex) {
            ventana.muestraAlerta("Error al Eliminar", ex.getMessage(), "ERROR");
        }
    }
}