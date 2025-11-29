package mx.uam.ayd.proyecto.negocio;

import mx.uam.ayd.proyecto.negocio.modelo.Hospedaje;
import mx.uam.ayd.proyecto.negocio.modelo.RegistroHospedaje;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio encargado de gestionar los registros diarios asociados
 * a un hospedaje de mascota.
 *
 * Define las operaciones principales para registrar información diaria
 * y consultar el historial de registros de un hospedaje.
 *
 * @author Mitzi
 */
public interface ServicioRegistroHospedaje {

    /**
     * Registra una entrada diaria para un hospedaje.
     *
     * @param hospedaje      Hospedaje al que pertenece el registro.
     * @param alimentacion   Información sobre la alimentación.
     * @param salud          Estado de salud de la mascota.
     * @param comportamiento Comportamiento observado durante el día.
     * @param observaciones  Observaciones adicionales.
     * @param fechaRegistro  Fecha del registro; si es null, se usa la fecha/hora actual.
     * @return RegistroHospedaje guardado.
     * @throws IllegalArgumentException si el hospedaje es inválido,
     *                                  si la fecha está fuera del rango permitido
     *                                  o si ya existe un registro en esa fecha.
     */
    RegistroHospedaje registrar(Hospedaje hospedaje,
                                String alimentacion,
                                String salud,
                                String comportamiento,
                                String observaciones,
                                LocalDateTime fechaRegistro) throws IllegalArgumentException;

    /**
     * Obtiene todos los registros asociados a un hospedaje,
     * ordenados del más reciente al más antiguo.
     *
     * @param hospedaje Hospedaje cuya lista de registros se desea consultar.
     * @return Lista de registros ordenada.
     */
    List<RegistroHospedaje> listarPorHospedaje(Hospedaje hospedaje);



}
