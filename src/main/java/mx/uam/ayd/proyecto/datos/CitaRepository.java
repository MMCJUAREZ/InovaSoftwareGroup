package mx.uam.ayd.proyecto.datos;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import mx.uam.ayd.proyecto.negocio.modelo.Cita;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad Cita
 */

public interface CitaRepository extends CrudRepository<Cita, Long> {

    /**
     * Encuentra citas que se solapan con un rango de tiempo y para un veterinario específico.
     * @param inicio El inicio del periodo a verificar
     * @param fin El fin del periodo a verificar
     * @param idVeterinario El ID del veterinario para el que se busca el conflicto
     * @return Una lista de citas existentes que se cruzan con el periodo dado para ese veterinario.
     */

    @Query("SELECT c FROM Cita c WHERE c.fechaHora < :fin AND c.fechaHora > :inicio AND c.atendida = false AND c.veterinario.idVeterinario = :idVeterinario")
    List<Cita> findCitasOverlap(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin,
            @Param("idVeterinario") Long idVeterinario);

    // Metodo para recuperar todas las citas

    List<Cita> findAllByOrderByFechaHoraAsc();
}