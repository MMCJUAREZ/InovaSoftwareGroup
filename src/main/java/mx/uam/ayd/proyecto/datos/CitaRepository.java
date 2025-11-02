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
     * Encuentra citas que se solapan con un rango de tiempo específico.
     * Esto permite validar si ya hay una cita agendada en un slot.
     * * @param inicio El inicio del periodo a verificar (hora de la nueva cita)
     * @param fin El fin del periodo a verificar (hora de la nueva cita y duración)
     * @return Una lista de citas existentes que se cruzan con el periodo dado.
     */

    @Query("SELECT c FROM Cita c WHERE c.fechaHora < :fin AND c.fechaHora > :inicio AND c.atendida = false")
    List<Cita> findCitasOverlap(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    // Metodo para recuperar todas las citas

    List<Cita> findAllByOrderByFechaHoraAsc();

}