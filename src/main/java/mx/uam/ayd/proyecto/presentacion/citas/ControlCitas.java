package mx.uam.ayd.proyecto.presentacion.citas;

import mx.uam.ayd.proyecto.negocio.ServicioCita;
import mx.uam.ayd.proyecto.negocio.modelo.Cita;
import mx.uam.ayd.proyecto.negocio.modelo.TipoCita;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador principal para la gestión de citas.
 * Coordina la lógica de negocio (ServicioCita) con la interfaz de usuario (VentanaCitas).
 */

@Component
public class ControlCitas {

    private final ServicioCita servicioCita;
    private final VentanaCitas ventana;

    @Autowired
    public ControlCitas(ServicioCita servicioCita, VentanaCitas ventana) {

        this.servicioCita = servicioCita;
        this.ventana = ventana;
        this.ventana.setControl(this);

    }

    /**
     * Inicia el caso de uso mostrando la ventana de gestión de citas.
     */

    public void inicia() {

        List<Cita> citas = servicioCita.recuperarCitas();
        ventana.muestra(citas);

    }

    /**
     * Recupera y actualiza la lista de citas en la ventana.
     */

    public void actualizarListaCitas() {

        List<Cita> citas = servicioCita.recuperarCitas();
        ventana.actualizarTabla(citas);

    }

    /**
     * Agendar una nueva cita.
     */

    public void agendarCita(LocalDateTime fechaHora, TipoCita tipo, String nombre, String contacto, boolean enviarCorreo) {

        try {
            servicioCita.agendarCita(fechaHora, tipo, nombre, contacto, enviarCorreo);
            ventana.muestraAlerta("Éxito", "Cita agendada correctamente.", "INFORMATION");
            actualizarListaCitas();

        } catch (IllegalArgumentException ex) {
            ventana.muestraAlerta("Error de Validación", ex.getMessage(), "ERROR");
        }

    }

    /**
     * Modificar una cita existente.
     */

    public void modificarCita(Long idCita, LocalDateTime fechaHora, TipoCita tipo, String nombre, String contacto) {

        try {
            servicioCita.modificarCita(idCita, fechaHora, tipo, nombre, contacto);
            ventana.muestraAlerta("Éxito", "Cita modificada correctamente.", "INFORMATION");
            actualizarListaCitas();

        } catch (IllegalArgumentException ex) {
            ventana.muestraAlerta("Error de Modificación", ex.getMessage(), "ERROR");
        }

    }

    /**
     * Eliminar una cita.
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
