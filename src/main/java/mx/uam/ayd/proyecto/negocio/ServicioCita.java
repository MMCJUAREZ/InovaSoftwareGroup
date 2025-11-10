package mx.uam.ayd.proyecto.negocio;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import mx.uam.ayd.proyecto.datos.CitaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cita;
import mx.uam.ayd.proyecto.negocio.modelo.TipoCita;
import mx.uam.ayd.proyecto.negocio.modelo.Veterinario; // NUEVA IMPORTACIÓN
import mx.uam.ayd.proyecto.datos.VeterinarioRepository; // NUEVA IMPORTACIÓN

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Data

public class ServicioCita {

    private final CitaRepository citaRepository;
    private final ServicioCorreo servicioCorreo;
    private final VeterinarioRepository veterinarioRepository; // NUEVO REPOSITORIO INYECTADO

    // Horario laboral configurable

    private static final LocalTime HORA_INICIO = LocalTime.of(9, 0); // 9:00 AM
    private static final LocalTime HORA_FIN = LocalTime.of(18, 0);   // 6:00 PM
    private static final int DURACION_CITA_MINUTOS = 30; // Duración estándar de 30 minutos

    // Patrones de validación

    private static final String REGEX_CORREO = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    private static final String REGEX_TELEFONO = "^\\d{10}$";

    @Autowired

    public ServicioCita(CitaRepository citaRepository, ServicioCorreo servicioCorreo, VeterinarioRepository veterinarioRepository) {
        this.citaRepository = citaRepository;
        this.servicioCorreo = servicioCorreo;
        this.veterinarioRepository = veterinarioRepository;
    }

    /**
     * Validar los campos obligatorios y las reglas de negocio.
     */

    private void validarCamposYReglas(LocalDateTime fechaHora, TipoCita tipo, String nombre, String contacto, Veterinario veterinario) {

        if (fechaHora == null || tipo == null || nombre == null || nombre.trim().isEmpty() || contacto == null || contacto.trim().isEmpty() || veterinario == null) {
            throw new IllegalArgumentException("Todos los campos (Fecha/Hora, Tipo, Nombre, Contacto, Veterinario) son obligatorios.");
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
     * Agrega una nueva cita al sistema
     */

    public Cita agendarCita(LocalDateTime fechaHora, TipoCita tipo, String nombre, String contacto, boolean enviarCorreo,
                            Veterinario veterinario, String motivo, String notas) { // NUEVOS PARÁMETROS

        validarCamposYReglas(fechaHora, tipo, nombre, contacto, veterinario); // VALIDACIÓN CON VETERINARIO

        // Validacion

        LocalDateTime finCita = fechaHora.plusMinutes(DURACION_CITA_MINUTOS);

        // Uso de la nueva consulta que filtra por ID de Veterinario

        List<Cita> citasSolapadas = citaRepository.findCitasOverlap(
                fechaHora.minusMinutes(DURACION_CITA_MINUTOS - 1),
                finCita.minusMinutes(1),
                veterinario.getIdVeterinario() // Filtro por veterinario
        );

        if (!citasSolapadas.isEmpty()) {
            throw new IllegalArgumentException("El veterinario seleccionado ya tiene una cita agendada en esa fecha y hora.");
        }

        // Crear y guardar la cita con los nuevos campos

        Cita nuevaCita = new Cita();
        nuevaCita.setFechaHora(fechaHora);
        nuevaCita.setTipo(tipo);
        nuevaCita.setNombreSolicitante(nombre);
        nuevaCita.setContacto(contacto);
        nuevaCita.setAtendida(false);
        nuevaCita.setVeterinario(veterinario); // NUEVO
        nuevaCita.setMotivo(motivo != null ? motivo : ""); // NUEVO
        nuevaCita.setNotas(notas != null ? notas : ""); // NUEVO

        Cita citaGuardada = citaRepository.save(nuevaCita);

        // Envío de correo)
        if (enviarCorreo && Pattern.matches(REGEX_CORREO, contacto)) {
            String asunto = "Confirmación de Cita - Veterinaria UAM";
            String mensaje = String.format(
                    "Estimado(a) %s,\n\nSu cita para %s con %s ha sido agendada.\nDetalles:\n- Fecha y Hora: %s\n- ID: %d\n\nMotivo: %s",
                    nombre, tipo.toString(), veterinario.getNombreCompleto(), fechaHora.toString(), citaGuardada.getIdCita(), motivo
            );
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
     */

    public Cita modificarCita(Long idCita, LocalDateTime fechaHora, TipoCita tipo, String nombre, String contacto, Veterinario veterinario, String motivo, String notas) {
        if (idCita == null) {
            throw new IllegalArgumentException("El ID de la cita no puede ser nulo.");
        }

        Cita citaExistente = citaRepository.findById(idCita)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada."));

        if (citaExistente.isAtendida()) {
            throw new IllegalArgumentException("No se puede modificar una cita que ya fue atendida.");
        }

        // Realizar las validaciones de campos y reglas (con Veterinario)

        validarCamposYReglas(fechaHora, tipo, nombre, contacto, veterinario);

        // Validacion de conflicto CA2
        LocalDateTime finCita = fechaHora.plusMinutes(DURACION_CITA_MINUTOS);

        // Uso de la nueva consulta que filtra por ID de Veterinario

        List<Cita> citasSolapadas = citaRepository.findCitasOverlap(
                fechaHora.minusMinutes(DURACION_CITA_MINUTOS - 1),
                finCita.minusMinutes(1),
                veterinario.getIdVeterinario() // Filtrar por veterinario
        );

        // Permitimos el solapamiento solo si la cita encontrada es la misma que se está modificando

        for(Cita cita : citasSolapadas) {
            if (!cita.getIdCita().equals(idCita)) {
                throw new IllegalArgumentException("La nueva fecha, hora o veterinario se solapan con otra cita existente.");
            }
        }

        // Actualizar datos

        citaExistente.setFechaHora(fechaHora);
        citaExistente.setTipo(tipo);
        citaExistente.setNombreSolicitante(nombre);
        citaExistente.setContacto(contacto);
        citaExistente.setVeterinario(veterinario); // NUEVO
        citaExistente.setMotivo(motivo != null ? motivo : ""); // NUEVO
        citaExistente.setNotas(notas != null ? notas : ""); // NUEVO


        return citaRepository.save(citaExistente);
    }

    /**
     * Elimina una cita existente.
     */

    public void eliminarCita(Long idCita) {
        Cita cita = citaRepository.findById(idCita)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada."));

        if (cita.isAtendida()) {
            throw new IllegalArgumentException("No se puede eliminar una cita que ya fue atendida.");
        }

        citaRepository.delete(cita);
    }

    public List<Cita> recuperarCitas() {
        return citaRepository.findAllByOrderByFechaHoraAsc();
    }
}
