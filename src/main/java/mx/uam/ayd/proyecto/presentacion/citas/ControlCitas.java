package mx.uam.ayd.proyecto.presentacion.citas;

import mx.uam.ayd.proyecto.negocio.ServicioCita;
import mx.uam.ayd.proyecto.negocio.ServicioVeterinario;
import mx.uam.ayd.proyecto.negocio.modelo.Cita;
import mx.uam.ayd.proyecto.negocio.modelo.TipoCita;
import mx.uam.ayd.proyecto.negocio.modelo.Veterinario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ControlCitas {

    private final ServicioCita servicioCita;
    private final VentanaCitas ventana;
    private final ServicioVeterinario servicioVeterinario;

    @Autowired
    public ControlCitas(ServicioCita servicioCita, ServicioCita servicioCita1, VentanaCitas ventana, ServicioVeterinario servicioVeterinario) {
        this.servicioCita = servicioCita1;
        this.ventana = ventana;
        this.servicioVeterinario = servicioVeterinario;
        this.ventana.setControl(this);
    }

    /**
     * Inicia el caso de uso mostrando la ventana de gestión de citas.
     */

    public void inicia() {
        List<Cita> citas = servicioCita.recuperarCitas();
        List<Veterinario> veterinarios = servicioVeterinario.recuperarVeterinarios(); // Obtener veterinarios
        ventana.muestra(citas, veterinarios); // Pasar veterinarios al control vista
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
     */

    public List<Veterinario> recuperarVeterinarios() {
        return servicioVeterinario.recuperarVeterinarios();
    }

    /**
     * Agendar una nueva cita.
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
