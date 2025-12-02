/* Copyright 2025
 * Proyecto Veterinaria - Sistema de Gestión de Hospedaje
 */
package mx.uam.ayd.proyecto.datos;

import mx.uam.ayd.proyecto.negocio.modelo.Hospedaje;
import mx.uam.ayd.proyecto.negocio.modelo.RegistroHospedaje;
import org.springframework.data.repository.CrudRepository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio encargado de gestionar los registros diarios asociados
 * a un hospedaje. Proporciona operaciones para listar y validar
 * registros existentes en un rango de tiempo.
 *
 * @author Mitzi
 */
public interface RegistroHospedajeRepository extends CrudRepository<RegistroHospedaje, Long> {

    /**
     * Obtiene todos los registros correspondientes a un hospedaje,
     * ordenados de forma descendente por fecha.
     *
     * @param hospedaje Objeto que representa el hospedaje consultado.
     * @return Lista de registros ordenados por fecha de registro en orden descendente.
     */
    List <RegistroHospedaje> findByHospedajeOrderByFechaRegistroDesc(Hospedaje hospedaje);

    /**
     * Verifica si existe un registro diario dentro de un rango de fecha/hora
     * para evitar duplicados en el mismo día.
     *
     * @param hospedaje Hospedaje asociado al registro.
     * @param desde     Fecha/hora inicial del rango.
     * @param hasta     Fecha/hora final del rango.
     * @return {@code true} si existe un registro dentro del rango, {@code false} en caso contrario.
     */
    boolean existsByHospedajeAndFechaRegistroBetween(
            Hospedaje hospedaje,
            LocalDateTime desde,
            LocalDateTime hasta
    );

}
