package mx.uam.ayd.proyecto.datos;

import org.springframework.data.repository.CrudRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;

/**
 * Repositorio para Clientes
 */
public interface ClienteRepository extends CrudRepository<Cliente, Long>{
    /**
     * Encuentra un cliente a partir de su teléfono
     * * @param telefono
     * @return el cliente si existe, o null si no
     */
    public Cliente findByTelefono(String telefono);

    /**
     * Encuentra un cliente a partir de su correo electrónico
     * * @param correoElectronico
     * @return el cliente si existe, o null si no
     */

    public Cliente findByCorreoElectronico(String correoElectronico);
    
}
