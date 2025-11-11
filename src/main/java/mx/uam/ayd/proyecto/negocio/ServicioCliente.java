package mx.uam.ayd.proyecto.negocio;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import mx.uam.ayd.proyecto.datos.ClienteRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.Membresia;
import mx.uam.ayd.proyecto.negocio.modelo.TipoMembresia;

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
        cliente.setMontoAcumulado(0.0);

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

    /**
     * Asigna membresia a un cliente
     * @param cliente a quien se le asigna la membresia
     * @param membresia que escoge el cliente
     * 
     * @throws IllegalArgumentExeption si el cliente no cumple con lo necesario
     */
    @Transactional
    public void asignarMembresia(TipoMembresia tipo, Cliente cliente) {
        if (cliente.getMontoAcumulado() < 500) {
            throw new IllegalArgumentException("El cliente no cumple con el monto mínimo para obtener una membresía.");
        }

        Membresia membresia = new Membresia();
        membresia.setTipo(tipo);
        membresia.setEstado(true); // activa
        membresia.setPrecio(tipo == TipoMembresia.Platinum ? 219.0 : 119.0);
        membresia.setCliente(cliente);

        cliente.setMembresia(membresia);
        clienteRepository.save(cliente); // Cascade.ALL asegura que se guarde también la membresía
    }
    /**
     * Permite buscar un cliente por su número de teléfono.
     * Es necesario para el flujo de registro de hospedaje (HU-01).
     * @param telefono El número de teléfono a buscar.
     * @return El cliente si existe, o null.
     */
    public Cliente findByTelefono(String telefono) {
        // La implementación se basa en que ClienteRepository tiene findByTelefono(String telefono)
        return clienteRepository.findByTelefono(telefono);
    }
}
