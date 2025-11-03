package mx.uam.ayd.proyecto.datos;

import mx.uam.ayd.proyecto.negocio.modelo.Cartilla;
import org.springframework.data.repository.CrudRepository;


import mx.uam.ayd.proyecto.negocio.modelo.Cartilla;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface CartillaRepository extends CrudRepository<Cartilla, Long> {

    /**
     * Busca cartillas por ID de mascota
     * @param mascotaId ID de la mascota
     * @return Lista de cartillas asociadas a la mascota
     */
    List<Cartilla> findByMascotaId(Long mascotaId);

    /**
     * Busca cartillas por tipo de vacuna
     * @param vacuna Tipo de vacuna
     * @return Lista de cartillas con esa vacuna
     */
    List<Cartilla> findByVacuna(String vacuna);

    /**
     * Busca cartillas por veterinario
     * @param veterinario Nombre del veterinario
     * @return Lista de cartillas aplicadas por ese veterinario
     */
    List<Cartilla> findByVeterinarioContainingIgnoreCase(String veterinario);

    /**
     * Busca cartillas por rango de fechas
     * @param fechaInicio Fecha inicial
     * @param fechaFin Fecha final
     * @return Lista de cartillas dentro del rango de fechas
     */
    List<Cartilla> findByFechaAplicacionBetween(java.time.LocalDate fechaInicio, java.time.LocalDate fechaFin);

    /**
     * Busca cartillas con próxima dosis próxima a vencer
     * @param fechaLimite Fecha límite para la próxima dosis
     * @return Lista de cartillas con próxima dosis antes de la fecha límite
     */
    List<Cartilla> findByProximaDosisBefore(java.time.LocalDate fechaLimite);
}