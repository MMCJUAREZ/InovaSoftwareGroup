package mx.uam.ayd.proyecto.datos;

import org.springframework.data.repository.CrudRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cirugia;
import mx.uam.ayd.proyecto.negocio.modelo.Mascota;
import java.util.List;

/**
 * Interfaz de repositorio para la entidad Cirugia.
 *
 * <p>Esta interfaz extiende CrudRepository de Spring Data, lo que permite
 * realizar operaciones básicas de persistencia (CRUD) sobre la tabla de cirugías
 * sin necesidad de implementar manualmente las consultas SQL.</p>
 *
 * @author InovaSoftwareGroup
 * @since 2025-11-20
 */
public interface CirugiaRepository extends CrudRepository<Cirugia, Long> {

	/**
	 * Busca todas las cirugías asociadas a una mascota específica.
	 *
	 * <p>Este método sigue la convención de nombres de Spring Data JPA (Query Method).
	 * Genera automáticamente una consulta para filtrar los registros de cirugía
	 * donde la llave foránea coincida con la mascota proporcionada.
	 * Útil para recuperar el historial clínico completo de un paciente.</p>
	 *
	 * @param mascota El objeto Mascota del cual se requiere el historial.
	 * @return Una lista conteniendo todas las cirugías registradas para esa mascota.
	 */
	List<Cirugia> findByMascota(Mascota mascota);
}