package mx.uam.ayd.proyecto.negocio;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import mx.uam.ayd.proyecto.datos.ClienteRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;

@Service
public class ServicioCliente {
    
    @Autowired
    private ClienteRepository clienteRepository;

    private static final String REGEX_NOMBRE = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{1,100}$";
    private static final String REGEX_TELEFONO = "^\\d{10}$";
    private static final String REGEX_CORREO = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

    /**
     * Recupera todos los clientes
     * * @return una lista de clientes
     */
    public List<Cliente> recuperarCliente() {
        List<Cliente> clientes = new ArrayList<>();
        for (Cliente cliente : clienteRepository.findAll()) {
            clientes.add(cliente);
        }
        return clientes;
    }

    /**
     * Registra un nuevo cliente con validaciones
     * @param nombreCompleto
     * @param telefono
     * @param correoElectronico
     * @param direccion
     * @return El cliente guardado
     * @throws IllegalArgumentException si algún dato es inválido o ya existe
     */
    public Cliente registraCliente(String nombreCompleto, String telefono, String correoElectronico, String direccion) {
        
        // Validamos campos obligatorios a todos 
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty() ||
            telefono == null || telefono.trim().isEmpty() ||
            correoElectronico == null || correoElectronico.trim().isEmpty() ||
            direccion == null || direccion.trim().isEmpty()) {
            throw new IllegalArgumentException("Todos los campos son obligatorios");
        }
        
        // Validamos los formatos segun las reglas designadas
        if (!Pattern.matches(REGEX_NOMBRE, nombreCompleto)) {
            throw new IllegalArgumentException("El nombre solo debe contener letras y espacios (max 100 caracteres)");
        }
        if (!Pattern.matches(REGEX_TELEFONO, telefono)) {
            throw new IllegalArgumentException("El telefono debe contener exactamente 10 digitos numericos");
        }
        if (!Pattern.matches(REGEX_CORREO, correoElectronico)) {
            throw new IllegalArgumentException("El formato del correo electronico no es valido");
        }

        // Evitamos la existencia de duplicados
        if (clienteRepository.findByTelefono(telefono) != null) {
            throw new IllegalArgumentException("Ya existe un cliente registrado con ese numero de telefono");
        }
        if (clienteRepository.findByCorreoElectronico(correoElectronico) != null) {
            throw new IllegalArgumentException("Ya existe un cliente registrado con ese correo electronico");
        }

        // Creamos al cliente 
        Cliente cliente = new Cliente();
        cliente.setNombreCompleto(nombreCompleto);
        cliente.setTelefono(telefono);
        cliente.setCorreoElectronico(correoElectronico);
        cliente.setDireccion(direccion);

        return clienteRepository.save(cliente);
    }

    /**
     * Elimina un cliente por su ID
     * * @param idCliente el ID del cliente a eliminar
     * @throws IllegalArgumentException si el cliente no existe
     */
    public void eliminaCliente(Long idCliente) {
        if (!clienteRepository.existsById(idCliente)) {
            throw new IllegalArgumentException("El cliente con ID " + idCliente + " no existe");
        }
        clienteRepository.deleteById(idCliente);
    }
}
