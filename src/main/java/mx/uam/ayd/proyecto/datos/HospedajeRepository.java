package mx.uam.ayd.proyecto.datos;

import org.springframework.data.repository.CrudRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Hospedaje;

/**
 * @file HospedajeRepository.java
 * @brief Interfaz para la persistencia de la entidad {@link Hospedaje}.
 *
 * Esta interfaz extiende {@link CrudRepository} para proporcionar operaciones CRUD
 * (crear, leer, actualizar y eliminar) sobre la entidad {@link Hospedaje}.
 *
 * <p>Forma parte de la capa de datos del sistema, encargada de la comunicación
 * con la base de datos a través de Spring Data JPA.</p>
 *
 * @author Mitzi
 * @date 2025-11-01
 */

public interface HospedajeRepository extends CrudRepository<Hospedaje, Long> {

    /**
     * Busca un hospedaje en la base de datos a partir de su identificador.
     *
     * <p>Este método se implementa automáticamente por Spring Data JPA
     * a partir del nombre del método, siguiendo las convenciones de
     * nomenclatura de consulta.</p>
     *
     * @param idHospedaje Identificador único del hospedaje a buscar.
     * @return El objeto {@link Hospedaje} correspondiente, o {@code null} si no existe.
     *
     * @note Este método fue agregado para el cumplimiento del **Criterio de Aceptación 5**:
     * Editar o eliminar hospedajes existentes.
     */

    // Método para el Criterio de Aceptación 5: Editar/Eliminar
    Hospedaje findByIdHospedaje(Long idHospedaje);
}
