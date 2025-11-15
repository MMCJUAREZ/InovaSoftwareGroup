package mx.uam.ayd.proyecto.negocio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import mx.uam.ayd.proyecto.datos.HospedajeRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.Mascota;
import mx.uam.ayd.proyecto.negocio.modelo.Hospedaje;
import java.time.LocalDate;

/**
 * @file ServicioHospedaje.java
 * @brief Proporciona la lógica de negocio para gestionar los registros de hospedaje.
 *
 * Esta clase contiene los métodos que implementan las reglas de negocio relacionadas
 * con el registro, validación y eliminación de hospedajes.
 * Se encarga de validar las fechas y condiciones necesarias antes de guardar o eliminar
 * un registro, comunicándose con {@link HospedajeRepository}.
 *
 * <p>Forma parte de la capa de negocio (servicio) del sistema, implementando
 * los criterios de aceptación asociados al módulo de hospedaje.</p>
 *
 * @author Mitzi
 * @date 2025-11-01
 */

@Service
public class ServicioHospedaje {

    /** Repositorio para la persistencia de hospedajes. */
    private final HospedajeRepository hospedajeRepository;
    private final ServicioCorreo servicioCorreo;

    /**
     * Constructor que inyecta el repositorio de hospedaje.
     *
     * @param hospedajeRepository referencia al repositorio de hospedaje.
     */
    @Autowired
    public ServicioHospedaje(HospedajeRepository hospedajeRepository,ServicioCorreo servicioCorreo) {
        this.hospedajeRepository = hospedajeRepository;
        this.servicioCorreo = servicioCorreo;
    }

    /**
     * @brief Registra un nuevo hospedaje con validaciones de negocio.
     *
     * Este método valida las fechas de entrada y salida, asegurando que:
     * - Ambas fechas sean obligatorias.
     * - La fecha de entrada no sea anterior a la actual.
     * - La fecha de salida sea posterior a la de entrada.
     *
     * Si las validaciones son correctas, se crea un nuevo objeto {@link Hospedaje}
     * asociado al cliente y mascota, y se guarda en la base de datos.
     *
     * @param cliente el cliente dueño de la mascota.
     * @param mascota la mascota que será hospedada.
     * @param fechaEntrada la fecha en que inicia el hospedaje.
     * @param fechaSalida la fecha en que finaliza el hospedaje.
     * @param observaciones notas adicionales sobre el hospedaje.
     * @return el objeto {@link Hospedaje} registrado y persistido.
     *
     * @throws IllegalArgumentException si las fechas son nulas o inválidas.
     */
    public Hospedaje registrar(Cliente cliente, Mascota mascota, LocalDate fechaEntrada, LocalDate fechaSalida, String observaciones) {
        if (fechaEntrada == null || fechaSalida == null) {
            throw new IllegalArgumentException("Las fechas de entrada y salida son obligatorias.");
        }
        if (fechaEntrada.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de entrada no puede ser anterior a hoy.");
        }
        if (fechaSalida.isBefore(fechaEntrada) || fechaSalida.isEqual(fechaEntrada)) {
            throw new IllegalArgumentException("La fecha de salida debe ser posterior a la fecha de entrada.");
        }

        Hospedaje hospedaje = new Hospedaje();
        hospedaje.setCliente(cliente);
        hospedaje.setMascota(mascota);
        hospedaje.setFechaEntrada(fechaEntrada);
        hospedaje.setFechaSalida(fechaSalida);
        hospedaje.setObservaciones(observaciones);

        //Guardar en BD
        Hospedaje hospedajeGuardado = hospedajeRepository.save(hospedaje);

        //Enviar correo confirmando el hospedaje
         servicioCorreo.enviarCorreoConfirmacionHospedaje(
                 cliente,
                 mascota,
                 hospedajeGuardado
         );
         return hospedajeGuardado;



        //Enviar correo de confirmacion
        //enviarCorreoConfirmacion(cliente, mascota, hospedaje);
       // return hospedaje;
    }

   /* private void enviarCorreoConfirmacion(Cliente cliente, Mascota mascota, Hospedaje hospedaje) {
        try {
            String destinatario = cliente.getCorreoElectronico();
            String asunto = "confirmacion de hospedaje Kroketa";
            String mensaje = String.format(
                    "Hola %s,\n\nTu mascota %s ha sido registrada correctamente en el hospedaje del %s al %s.\n\nGracias por confiar en Kroketa ",
                    cliente.getNombreCompleto(),
                    mascota.getNombre(),
                    hospedaje.getFechaEntrada(),
                    hospedaje.getFechaSalida()
            );

            servicioCorreo.enviarCorreo(destinatario, asunto,mensaje);
        } catch (Exception e){
            System.err.println("Error al enviar correo de confirmacion: "+ e.getMessage());
        }
    }*/

    /**
     * @brief Elimina un hospedaje antes de que inicie.
     *
     * Implementa el Criterio de Aceptación 5: “Editar o eliminar antes del ingreso”.
     * Este método busca un hospedaje por su ID y lo elimina **solo si**
     * la fecha de entrada aún no ha llegado.
     *
     * @param idHospedaje identificador del hospedaje a eliminar.
     * @throws IllegalArgumentException si no existe el hospedaje con ese ID.
     * @throws IllegalStateException si la fecha de entrada ya pasó (la mascota ya ingresó).
     */
    public void eliminarHospedaje(Long idHospedaje) {
        Hospedaje hospedaje = hospedajeRepository.findById(idHospedaje)
                .orElseThrow(() -> new IllegalArgumentException("No existe el registro de hospedaje con ID: " + idHospedaje));

        if (hospedaje.getFechaEntrada().isAfter(LocalDate.now())) {
            hospedajeRepository.delete(hospedaje);
        } else {
            throw new IllegalStateException("No se puede eliminar el registro, la mascota ya ingresó (la fecha de entrada ya pasó).");
        }
    }
}
