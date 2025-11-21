package mx.uam.ayd.proyecto.datos;

import org.springframework.data.repository.CrudRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cirugia;
import mx.uam.ayd.proyecto.negocio.modelo.Mascota;
import java.util.List;

/**
 * @file CirugiaRepository.java
 * @brief Interfaz de repositorio para la entidad Cirugia.
 *
 * Esta interfaz extiende CrudRepository de Spring Data, lo que permite
 * realizar operaciones basicas de persistencia (CRUD) sobre la tabla de cirugias
 * sin necesidad de implementar manualmente las consultas SQL.
 *
 * @author InovaSoftwareGroup
 * @date 2025-11-20
 */
public interface CirugiaRepository extends CrudRepository<Cirugia, Long> {

    /**
     * @brief Busca todas las cirugias asociadas a una mascota especifica.
     *
     * Este metodo sigue la convencion de nombres de Spring Data JPA (Query Method).
     * Genera automaticamente una consulta para filtrar los registros de cirugia
     * donde la llave foranea coincida con la mascota proporcionada.
     * Util para recuperar el historial clinico completo de un paciente.
     *
     * @param mascota El objeto Mascota del cual se requiere el historial.
     * @return Una lista conteniendo todas las cirugias registradas para esa mascota.
     */
    List<Cirugia> findByMascota(Mascota mascota);
}