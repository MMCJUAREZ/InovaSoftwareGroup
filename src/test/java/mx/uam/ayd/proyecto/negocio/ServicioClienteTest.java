package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.uam.ayd.proyecto.datos.ClienteRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.TipoMembresia;

@ExtendWith(MockitoExtension.class)
class ServicioClienteTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ServicioCliente servicioCliente;

    // Recuperacion de clientes
    @Test
    void testRecuperarCliente() {
        // Caso no hay clientes guardados
        when(clienteRepository.findAll()).thenReturn(new ArrayList<>());
        List<Cliente> clientes = servicioCliente.recuperarCliente();
        assertNotNull(clientes);
        assertEquals(0, clientes.size());

        // Caso hay clientes registrados
        Cliente c1 = new Cliente();
        c1.setNombreCompleto("Juan Pérez");
        Cliente c2 = new Cliente();
        c2.setNombreCompleto("María López");
        List<Cliente> lista = List.of(c1, c2);

        when(clienteRepository.findAll()).thenReturn(lista);
        clientes = servicioCliente.recuperarCliente();

        assertEquals(2, clientes.size());
        assertEquals("Juan Pérez", clientes.get(0).getNombreCompleto());
    }

    // Registro de cliente
    @Test
    void testRegistraCliente_Exitoso() {
        String nombre = "Carlos Sánchez";
        String telefono = "5512345678";
        String correo = "carlos@example.com";
        String direccion = "Av. Reforma 123";

        when(clienteRepository.findByTelefono(telefono)).thenReturn(null);
        when(clienteRepository.findByCorreoElectronico(correo)).thenReturn(null);

        Cliente clienteGuardado = new Cliente();
        clienteGuardado.setNombreCompleto(nombre);
        clienteGuardado.setTelefono(telefono);
        clienteGuardado.setCorreoElectronico(correo);
        clienteGuardado.setDireccion(direccion);
        clienteGuardado.setMontoAcumulado(500.0);

        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteGuardado);

        Cliente resultado = servicioCliente.registraCliente(nombre, telefono, correo, direccion);

        assertNotNull(resultado);
        assertEquals(nombre, resultado.getNombreCompleto());
        assertEquals(500.0, resultado.getMontoAcumulado());
    }

    @Test
    void testRegistraCliente_CamposInvalidos() {
        // Campos nulos o vacios
        assertThrows(IllegalArgumentException.class, () ->
            servicioCliente.registraCliente(null, "5512345678", "correo@dominio.com", "Direccion"));

        assertThrows(IllegalArgumentException.class, () ->
            servicioCliente.registraCliente("Juan", "", "correo@dominio.com", "Direccion"));

        // Nombre invalido
        assertThrows(IllegalArgumentException.class, () ->
            servicioCliente.registraCliente("Juan123", "5512345678", "correo@dominio.com", "Direccion"));

        // Teléfono invalido
        assertThrows(IllegalArgumentException.class, () ->
            servicioCliente.registraCliente("Juan Perez", "12345", "correo@dominio.com", "Direccion"));

        // Correo invalido
        assertThrows(IllegalArgumentException.class, () ->
            servicioCliente.registraCliente("Juan Perez", "5512345678", "correo-invalido", "Direccion"));
    }

    @Test
    void testRegistraCliente_Duplicados() {
        String nombre = "Ana Torres";
        String telefono = "5512345678";
        String correo = "ana@example.com";
        String direccion = "Av. Juárez 10";

        when(clienteRepository.findByTelefono(telefono)).thenReturn(new Cliente());
        assertThrows(IllegalArgumentException.class, () ->
            servicioCliente.registraCliente(nombre, telefono, correo, direccion));

        when(clienteRepository.findByTelefono(telefono)).thenReturn(null);
        when(clienteRepository.findByCorreoElectronico(correo)).thenReturn(new Cliente());
        assertThrows(IllegalArgumentException.class, () ->
            servicioCliente.registraCliente(nombre, telefono, correo, direccion));
    }

    // Eliminacion de cliente
    @Test
    void testEliminaCliente() {
        Long idCliente = 1L;

        // Caso cliente existe
        when(clienteRepository.existsById(idCliente)).thenReturn(true);
        servicioCliente.eliminaCliente(idCliente);
        verify(clienteRepository).deleteById(idCliente);

        // Caso cliente no existe
        when(clienteRepository.existsById(idCliente)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () ->
            servicioCliente.eliminaCliente(idCliente));
    }

    // Asignacion de membresia
    @Test
    void testAsignarMembresia_Exitoso() {
        Cliente cliente = new Cliente();
        cliente.setMontoAcumulado(600.0);

        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        servicioCliente.asignarMembresia(TipoMembresia.Platinum, cliente);

        assertNotNull(cliente.getMembresia());
        assertEquals(TipoMembresia.Platinum, cliente.getMembresia().getTipo());
        assertTrue(cliente.getMembresia().isEstado());
    }

    @Test
    void testAsignarMembresia_MontoInsuficiente() {
        Cliente cliente = new Cliente();
        cliente.setMontoAcumulado(300.0);

        assertThrows(IllegalArgumentException.class, () ->
            servicioCliente.asignarMembresia(TipoMembresia.Platinum, cliente));
    }
}
