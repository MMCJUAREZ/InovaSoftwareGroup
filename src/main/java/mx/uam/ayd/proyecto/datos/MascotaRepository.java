package mx.uam.ayd.proyecto.datos;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.Mascota;


/**
 * @file MascotaRepository.java
 * @brief Interfaz para la persistencia de la entidad {@link Mascota}.
 *
 * Esta interfaz forma parte de la capa de datos del sistema y extiende
 * {@link CrudRepository} para proporcionar las operaciones básicas CRUD
 * (crear, leer, actualizar y eliminar) sobre los objetos {@link Mascota}.
 *
 * <p>Spring Data JPA genera automáticamente la implementación de esta interfaz
 * en tiempo de ejecución, permitiendo la interacción directa con la base de datos
 * sin necesidad de escribir consultas SQL manualmente.</p>
 *
 * @see CrudRepository
 * @see Mascota
 *
 * @author Mitzi
 * @date 2025-11-01
 */

/**
 * Repositorio para Mascota
 */

public interface MascotaRepository extends CrudRepository<Mascota, Long> {

    // Por el momento no se definen métodos personalizados,
    // pero pueden agregarse en el futuro según los requerimientos del sistema.

    /**
     * Encuentra todas las mascotas asociadas a un cliente específico
     * @param cliente el cliente dueño de las mascotas
     * @return una lista de mascotas
     */
    public List<Mascota> findByCliente(Cliente cliente);
}
