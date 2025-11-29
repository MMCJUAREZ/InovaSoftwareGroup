package mx.uam.ayd.proyecto.negocio;

import mx.uam.ayd.proyecto.datos.RegistroHospedajeRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Hospedaje;
import mx.uam.ayd.proyecto.negocio.modelo.RegistroHospedaje;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


/**
 * Implementación del servicio para gestionar los registros diarios
 * asociados a un hospedaje de mascota.
 *
 * Se encarga de validar fechas, evitar registros duplicados por día
 * y delegar operaciones al repositorio correspondiente.
 *
 * @author Mitzi
 */
@Service
public class ServicioImplRegistroHospedaje implements ServicioRegistroHospedaje{

    private final RegistroHospedajeRepository registroRepo;

    @Autowired
    public ServicioImplRegistroHospedaje(RegistroHospedajeRepository registroRepo) {
        this.registroRepo = registroRepo;
    }

    /**
     * Registra una entrada diaria para un hospedaje.
     *
     * Reglas de validación:
     * <ul>
     *   <li>El hospedaje debe ser válido.</li>
     *   <li>La fecha debe estar dentro del rango [fechaEntrada, fechaSalida] del hospedaje.</li>
     *   <li>No debe existir otro registro en la misma fecha (evita duplicados).</li>
     * </ul>
     *
     * @param hospedaje       Hospedaje al que pertenece el registro.
     * @param alimentacion    Información sobre alimentación.
     * @param salud           Estado de salud de la mascota.
     * @param comportamiento  Comportamiento observado.
     * @param observaciones   Observaciones adicionales.
     * @param fechaRegistro   Fecha explícita para registrar, puede ser null (usa LocalDateTime.now()).
     * @return RegistroHospedaje guardado.
     * @throws IllegalArgumentException si alguna validación falla.
     */
    @Override
    public RegistroHospedaje registrar(Hospedaje hospedaje,
                                       String alimentacion,
                                       String salud,
                                       String comportamiento,
                                       String observaciones,
                                       LocalDateTime fechaRegistro) throws IllegalArgumentException {

        if (hospedaje == null) {
            throw new IllegalArgumentException("Hospedaje inválido.");
        }

        // usar ahora si fechaRegistro es null
        LocalDateTime fecha = (fechaRegistro == null)
                ? LocalDateTime.now()
                : fechaRegistro;

       final LocalDate fechaSolo = fecha.toLocalDate();
       final LocalDate fechaEntrada = hospedaje.getFechaEntrada();
       final LocalDate fechaSalida = hospedaje.getFechaSalida();

        // Validaciones de rango (considerando solo fecha)
        if (fechaEntrada == null || fechaSalida == null) {
            throw new IllegalArgumentException("El hospedaje no tiene fechas válidas.");
        }

        //  Validacion del rango del hospedaje
        if (fechaSolo.isBefore(fechaEntrada) || fechaSolo.isAfter(fechaSalida)) {
            throw new IllegalArgumentException("La fecha del registro está fuera del periodo del hospedaje.");
        }

        // Rango diario para evitar duplicados
        final LocalDateTime desde = fechaSolo.atStartOfDay();
        final LocalDateTime hasta = fechaSolo.atTime(LocalTime.MAX);

        final boolean existeHoy = registroRepo.existsByHospedajeAndFechaRegistroBetween(hospedaje, desde, hasta);
        if (existeHoy) {
            throw new IllegalArgumentException("Ya existe un registro para este hospedaje en la misma fecha.");
        }

        // crear y guardar
        RegistroHospedaje r = new RegistroHospedaje();
        r.setHospedaje(hospedaje);
        r.setFechaRegistro(fecha);
        r.setAlimentacion(alimentacion);
        r.setSalud(salud);
        r.setComportamiento(comportamiento);
        r.setObservaciones(observaciones);

        return registroRepo.save(r);
    }


    /**
     * Obtiene los registros asociados a un hospedaje,
     * ordenados del más reciente al más antiguo.
     *
     * @param hospedaje Hospedaje cuyo historial se consultará.
     * @return Lista ordenada de registros.
     */
    @Override
    public List<RegistroHospedaje> listarPorHospedaje(Hospedaje hospedaje) {
        return registroRepo.findByHospedajeOrderByFechaRegistroDesc(hospedaje);
    }


}
