package mx.uam.ayd.proyecto.datos;

import mx.uam.ayd.proyecto.negocio.modelo.Veterinario;
import org.springframework.data.repository.CrudRepository;

/**
 * Repositorio para la entidad Veterinario
 */

public interface VeterinarioRepository extends CrudRepository<Veterinario, Long> {

}