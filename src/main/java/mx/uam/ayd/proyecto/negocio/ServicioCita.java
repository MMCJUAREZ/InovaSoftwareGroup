package mx.uam.ayd.proyecto.negocio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import mx.uam.ayd.proyecto.datos.CitaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cita;
import mx.uam.ayd.proyecto.negocio.modelo.TipoCita;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service

/**
 * Servicio encargado de la lógica de negocio y validaciones para las Citas.
 */

public class ServicioCita {

    private final CitaRepository citaRepository;
    private final ServicioCorreo servicioCorreo; // Usamos el servicio de correo existente

    // Horario laboral configurable

    private static final LocalTime HORA_INICIO = LocalTime.of(9, 0); // 9:00 AM
    private static final LocalTime HORA_FIN = LocalTime.of(18, 0);   // 6:00 PM
    private static final int DURACION_CITA_MINUTOS = 30; // Duración estándar de 30 minutos

    // Patrones de validación

    private static final String REGEX_CORREO = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    private static final String REGEX_TELEFONO = "^\\d{10}$";

    @Autowired
    public ServicioCita(CitaRepository citaRepository, ServicioCorreo servicioCorreo) {
        this.citaRepository = citaRepository;
        this.servicioCorreo = servicioCorreo;

    }

    /**
     * Valida los campos obligatorios y las reglas de negocio antes de agendar o modificar una cita.
     * @param fechaHora Fecha y hora de la cita.
     * @param tipo Tipo de servicio.
     * @param nombre Nombre del solicitante.
     * @param contacto Correo o teléfono del solicitante.
     * @throws IllegalArgumentException si la validación falla.
     */

    private void validarCamposYReglas(LocalDateTime fechaHora, TipoCita tipo, String nombre, String contacto) {

        if (fechaHora == null || tipo == null || nombre == null || nombre.trim().isEmpty() || contacto == null || contacto.trim().isEmpty()) {
            throw new IllegalArgumentException("Todos los campos (Fecha/Hora, Tipo, Nombre, Contacto) son obligatorios.");
        }

        // 1. Validación: No permitir fechas pasadas

        if (fechaHora.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La cita no puede agendarse para una fecha y hora pasada.");
        }

        // 2. Validación de horario hábil

        LocalTime horaCita = fechaHora.toLocalTime();

        if (fechaHora.getDayOfWeek() == DayOfWeek.SUNDAY || horaCita.isBefore(HORA_INICIO) || horaCita.isAfter(HORA_FIN.minusMinutes(DURACION_CITA_MINUTOS))) {
            throw new IllegalArgumentException("La cita debe estar dentro del horario hábil (Lunes a Sábado, 9:00 AM - 6:00 PM) y tener al menos " + DURACION_CITA_MINUTOS + " minutos de duración.");
        }

        // 3. Validación de formato del contacto
        if (!Pattern.matches(REGEX_CORREO, contacto) && !Pattern.matches(REGEX_TELEFONO, contacto)) {
            throw new IllegalArgumentException("El contacto debe ser un correo electrónico válido o un número de teléfono de 10 dígitos.");
        }

    }

    /**
     * Agrega una nueva cita al sistema.
     * @param fechaHora Fecha y hora de la cita.
     * @param tipo Tipo de servicio.
     * @param nombre Nombre del solicitante.
     * @param contacto Correo o teléfono del solicitante.
     * @param enviarCorreo Indica si se debe enviar un correo de confirmación.
     * @return La cita guardada.
     * @throws IllegalArgumentException si hay errores de validación.
     */

    public Cita agendarCita(LocalDateTime fechaHora, TipoCita tipo, String nombre, String contacto, boolean enviarCorreo) {
        validarCamposYReglas(fechaHora, tipo, nombre, contacto);

        // 4. Validación de solapamiento

        LocalDateTime finCita = fechaHora.plusMinutes(DURACION_CITA_MINUTOS);
        List<Cita> citasSolapadas = citaRepository.findCitasOverlap(fechaHora.minusMinutes(DURACION_CITA_MINUTOS - 1), finCita.minusMinutes(1));

        if (!citasSolapadas.isEmpty()) {
            throw new IllegalArgumentException("La fecha y hora seleccionadas ya están ocupadas por otra cita.");
        }

        // Crear y guardar la cita

        Cita nuevaCita = new Cita();
        nuevaCita.setFechaHora(fechaHora);
        nuevaCita.setTipo(tipo);
        nuevaCita.setNombreSolicitante(nombre);
        nuevaCita.setContacto(contacto);
        nuevaCita.setAtendida(false);

        Cita citaGuardada = citaRepository.save(nuevaCita);

        // (Opcional) Envío de correo de confirmación

        if (enviarCorreo && Pattern.matches(REGEX_CORREO, contacto)) {

            String asunto = "Confirmación de Cita - Veterinaria UAM";
            String mensaje = String.format(
                    "Estimado(a) %s,\n\nSu cita para %s ha sido agendada con éxito.\nDetalles:\n- Fecha y Hora: %s\n- Tipo de Servicio: %s\n- ID de Cita: %d\n\nGracias por su preferencia.",
                    nombre, tipo.toString(), fechaHora.toString(), tipo.toString(), citaGuardada.getIdCita()
            );
            // El try-catch es para que la cita se guarde incluso si el correo falla
            try {
                servicioCorreo.enviarCorreo(contacto, asunto, mensaje);
            } catch (Exception e) {
                System.err.println("Error al enviar correo de confirmación: " + e.getMessage());
            }

        }

        return citaGuardada;

    }

    /**
     * Modifica una cita existente.
     * @param idCita ID de la cita a modificar.
     * @param fechaHora Nueva fecha y hora.
     * @param tipo Nuevo tipo de servicio.
     * @param nombre Nuevo nombre.
     * @param contacto Nuevo contacto.
     * @return La cita modificada.
     * @throws IllegalArgumentException si hay errores de validación o la cita no existe/ya está atendida.
     */

    public Cita modificarCita(Long idCita, LocalDateTime fechaHora, TipoCita tipo, String nombre, String contacto) {
        if (idCita == null) {
            throw new IllegalArgumentException("El ID de la cita no puede ser nulo.");
        }

        Cita citaExistente = citaRepository.findById(idCita)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada."));

        if (citaExistente.isAtendida()) {
            throw new IllegalArgumentException("No se puede modificar una cita que ya fue atendida.");
        }

        // Realizar las validaciones de campos y reglas (incluido horario)

        validarCamposYReglas(fechaHora, tipo, nombre, contacto);

        // 5. Validación de solapamiento al modificar (excluyendo la cita actual)

        LocalDateTime finCita = fechaHora.plusMinutes(DURACION_CITA_MINUTOS);
        List<Cita> citasSolapadas = citaRepository.findCitasOverlap(fechaHora.minusMinutes(DURACION_CITA_MINUTOS - 1), finCita.minusMinutes(1));

        // Permitimos el solapamiento si es la misma cita la que se encontró

        for(Cita cita : citasSolapadas) {
            if (!cita.getIdCita().equals(idCita)) {
                throw new IllegalArgumentException("La nueva fecha y hora se solapan con otra cita existente.");
            }
        }

        // Actualizar datos

        citaExistente.setFechaHora(fechaHora);
        citaExistente.setTipo(tipo);
        citaExistente.setNombreSolicitante(nombre);
        citaExistente.setContacto(contacto);

        return citaRepository.save(citaExistente);
    }

    /**
     * Elimina una cita por su ID si no ha sido atendida.
     * @param idCita ID de la cita a eliminar.
     * @throws IllegalArgumentException si la cita no existe o ya fue atendida.
     */

    public void eliminarCita(Long idCita) {
        Cita cita = citaRepository.findById(idCita)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada."));

        if (cita.isAtendida()) {
            throw new IllegalArgumentException("No se puede eliminar una cita que ya fue atendida.");
        }

        citaRepository.delete(cita);
    }

    /**
     * Recupera todas las citas agendadas, ordenadas por fecha y hora.
     * @return Lista de citas.
     */

    public List<Cita> recuperarCitas() {
        return citaRepository.findAllByOrderByFechaHoraAsc();
    }
}
