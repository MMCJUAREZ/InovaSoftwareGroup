package mx.uam.ayd.proyecto.negocio;

import mx.uam.ayd.proyecto.datos.CartillaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cartilla;
import mx.uam.ayd.proyecto.negocio.modelo.VacunaEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ServicioCartilla {

    @Autowired
    private CartillaRepository repositorioCartilla;

    public List<Cartilla> obtenerCartillaPorMascota(Long mascotaId) {
        return repositorioCartilla.findByMascotaId(mascotaId);
    }

    public List<Cartilla> obtenerCartillaPorVacuna(VacunaEnum vacuna) {
        return repositorioCartilla.findByVacuna(vacuna.name());
    }

    public List<Cartilla> obtenerCartillaPorVeterinario(String veterinario) {
        return repositorioCartilla.findByVeterinarioContainingIgnoreCase(veterinario);
    }

    public List<Cartilla> obtenerCartillaPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return repositorioCartilla.findByFechaAplicacionBetween(fechaInicio, fechaFin);
    }

    public List<Cartilla> obtenerVacunasProximasAVencer(LocalDate fechaLimite) {
        return repositorioCartilla.findByProximaDosisBefore(fechaLimite);
    }

    public Cartilla registrarVacuna(VacunaEnum vacuna, LocalDate fechaAplicacion,
                                    String veterinario, Long lote, String observaciones, Long mascotaId) {
        Cartilla nuevaCartilla = new Cartilla(vacuna, fechaAplicacion, veterinario, lote, observaciones, mascotaId);
        return repositorioCartilla.save(nuevaCartilla);
    }

    public void eliminarRegistroVacuna(Long idVacuna) {
        repositorioCartilla.deleteById(idVacuna);
    }

    public Cartilla actualizarRegistroVacuna(Long idVacuna, VacunaEnum vacuna, LocalDate fechaAplicacion,
                                             String veterinario, Long lote, String observaciones) {
        return repositorioCartilla.findById(idVacuna)
                .map(cartilla -> {
                    cartilla.setVacuna(vacuna);
                    cartilla.setFechaAplicacion(fechaAplicacion);
                    cartilla.setVeterinario(veterinario);
                    cartilla.setLote(lote);
                    cartilla.setObservaciones(observaciones);
                    return repositorioCartilla.save(cartilla);
                })
                .orElseThrow(() -> new RuntimeException("Vacuna no encontrada con ID: " + idVacuna));
    }

    public List<VacunaEnum> obtenerTodasLasVacunas() {
        return List.of(VacunaEnum.values());
    }

    public List<Cartilla> obtenerTodasLasCartillas() {
        return (List<Cartilla>) repositorioCartilla.findAll();
    }
}