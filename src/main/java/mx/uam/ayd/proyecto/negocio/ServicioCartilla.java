package mx.uam.ayd.proyecto.negocio;

import mx.uam.ayd.proyecto.datos.CartillaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cartilla;
import mx.uam.ayd.proyecto.negocio.modelo.VacunaEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ServicioCartilla {

    @Autowired
    private CartillaRepository repositorioCartilla;
    // Constantes regex (agregar junto con las otras)
    private static final String REGEX_VETERINARIO = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{1,100}$";

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

        // Validamos campos obligatorios
        if (vacuna == null) {
            throw new IllegalArgumentException("El tipo de vacuna es obligatorio");
        }
        if (fechaAplicacion == null) {
            throw new IllegalArgumentException("La fecha de aplicación es obligatoria");
        }
        if (veterinario == null || veterinario.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del veterinario es obligatorio");
        }
        if (lote == null) {
            throw new IllegalArgumentException("El número de lote es obligatorio");
        }
        if (mascotaId == null) {
            throw new IllegalArgumentException("El ID de la mascota es obligatorio");
        }

        // Validamos formatos específicos
        if (!Pattern.matches(REGEX_VETERINARIO, veterinario)) {
            throw new IllegalArgumentException("El nombre del veterinario solo debe contener letras y espacios (max 100 caracteres)");
        }
        if (lote <= 0) {
            throw new IllegalArgumentException("El número de lote debe ser un valor positivo");
        }
        if (fechaAplicacion.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de aplicación no puede ser futura");
        }
        if (fechaAplicacion.isBefore(LocalDate.now().minusYears(50))) {
            throw new IllegalArgumentException("La fecha de aplicación no puede ser tan antigua");
        }

        // Validamos que no exista una vacuna duplicada (misma vacuna, misma mascota, misma fecha)
        if (repositorioCartilla.existsByVacunaAndMascotaIdAndFechaAplicacion(vacuna, mascotaId, fechaAplicacion)) {
            throw new IllegalArgumentException("Ya existe un registro de esta vacuna para la mascota en la fecha indicada");
        }

        // Validamos observaciones si se proporcionan
        if (observaciones != null && !observaciones.trim().isEmpty()) {
            if (observaciones.length() > 500) {
                throw new IllegalArgumentException("Las observaciones no pueden exceder los 500 caracteres");
            }
        }

        // Creamos la cartilla
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