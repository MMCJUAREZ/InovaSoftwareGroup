package mx.uam.ayd.proyecto.presentacion.agregarCartilla;

import javafx.scene.control.Alert;
import mx.uam.ayd.proyecto.negocio.ServicioCartilla;
import mx.uam.ayd.proyecto.negocio.modelo.Cartilla;
import mx.uam.ayd.proyecto.negocio.modelo.VacunaEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ControlAgregarCartilla {
    private final ServicioCartilla servicioCartilla;
    private final VentanaAgregarCartilla ventana;

  @Autowired
    public ControlAgregarCartilla(ServicioCartilla servicioCartilla,VentanaAgregarCartilla ventana) {
        this.servicioCartilla = servicioCartilla;
      this.ventana = ventana;

    }


    public void inicia() {
        
        ventana.muestra(this);
    }

    // Métodos de búsqueda delegados al servicio

    public List<Cartilla> obtenerCartillaPorMascota(Long mascotaId) {
        return servicioCartilla.obtenerCartillaPorMascota(mascotaId);
    }

    public List<Cartilla> obtenerCartillaPorVacuna(VacunaEnum vacuna) {
        return servicioCartilla.obtenerCartillaPorVacuna(vacuna);
    }

    public List<Cartilla> obtenerCartillaPorVeterinario(String veterinario) {
        return servicioCartilla.obtenerCartillaPorVeterinario(veterinario);
    }

    public List<Cartilla> obtenerCartillaPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return servicioCartilla.obtenerCartillaPorRangoFechas(fechaInicio, fechaFin);
    }

    public List<Cartilla> obtenerVacunasProximasAVencer(LocalDate fechaLimite) {
        return servicioCartilla.obtenerVacunasProximasAVencer(fechaLimite);
    }

    public Cartilla registrarVacuna(VacunaEnum vacuna, LocalDate fechaAplicacion,
                                    String veterinario, Long lote, String observaciones, Long mascotaId) {
        return servicioCartilla.registrarVacuna(vacuna, fechaAplicacion, veterinario, lote, observaciones, mascotaId);
    }

    public void eliminarRegistroVacuna(Long idVacuna) {
        servicioCartilla.eliminarRegistroVacuna(idVacuna);
    }

    public Cartilla actualizarRegistroVacuna(Long idVacuna, VacunaEnum vacuna, LocalDate fechaAplicacion,
                                             String veterinario, Long lote, String observaciones) {
        return servicioCartilla.actualizarRegistroVacuna(idVacuna, vacuna, fechaAplicacion, veterinario, lote, observaciones);
    }
    public void actualizaListaVacunas() {
        List<Cartilla> vacunas = servicioCartilla.obtenerTodasLasCartillas();
        ventana.actualizaTabla(vacunas);
    }
    public List<VacunaEnum> obtenerTodasLasVacunas() {
        return servicioCartilla.obtenerTodasLasVacunas();
    }

    public List<Cartilla> obtenerTodasLasCartillas() {
        return servicioCartilla.obtenerTodasLasCartillas();
    }

    public void solicitaCargarCartilla(Long mascotaId) {
        List<Cartilla> cartillas = servicioCartilla.obtenerCartillaPorMascota(mascotaId);

        if (cartillas == null || cartillas.isEmpty()) {
            ventana.muestraAlerta(Alert.AlertType.INFORMATION, "Sin resultados",
                    "No se encontraron registros de cartilla para la mascota con ID " + mascotaId);
        } else {
            ventana.actualizaTabla(cartillas);
        }
    }



    public void solicitaRegistrarVacuna(VacunaEnum tipoVacuna, LocalDate fechaAplicacion, String nombre, Long lote, String laboratorio, Long mascotaId) {
        try {
            servicioCartilla.registrarVacuna(tipoVacuna, fechaAplicacion, nombre, lote, laboratorio, mascotaId);

            // Si tiene éxito
            ventana.cierra(); // cerramos el formulario
            ventana.muestraAlerta(Alert.AlertType.INFORMATION, "Éxito", "Vacuna registrada exitosamente");
            actualizaListaVacunas(); // actualizamos la tabla de vacunas

        } catch (IllegalArgumentException ex) {
            throw ex;
        }
    }
}