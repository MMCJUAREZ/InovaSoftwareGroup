package mx.uam.ayd.proyecto.negocio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import mx.uam.ayd.proyecto.datos.MascotaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Mascota;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import java.util.List;


/**
 * @file ServicioMascota.java
 * @brief Proporciona la lógica de negocio para el registro y validación de mascotas.
 *
 * Esta clase forma parte de la capa de negocio y gestiona el registro de nuevas mascotas.
 * Aplica validaciones sobre los campos requeridos antes de persistir los datos
 * en la base de datos mediante {@link MascotaRepository}.
 *
 * <p>El servicio asegura que todos los campos obligatorios sean válidos antes de guardar.</p>
 *
 * @author Mitzi
 * @date 2025-11-01
 */
@Service
public class ServicioMascota {

    /** Repositorio que permite acceder a las operaciones CRUD sobre la entidad Mascota. */
    @Autowired
    private MascotaRepository mascotaRepository;


    /**
     * @brief Registra una nueva mascota con validaciones de negocio.
     *
     * Este método se encarga de crear una nueva instancia de {@link Mascota} validando
     * que los datos esenciales sean correctos y no estén vacíos.
     * Si las validaciones son exitosas, la mascota se guarda en la base de datos.
     *
     * @param nombre nombre de la mascota.
     * @param raza raza de la mascota.
     * @param especie especie de la mascota (por ejemplo: perro, gato, etc.).
     * @param edad edad de la mascota en años.
     * @param sexo sexo de la mascota.
     * @param vacunasVigentes indica si las vacunas están al día.
     *
     * @return la mascota registrada y persistida en la base de datos.
     *
     * @throws IllegalArgumentException si algún campo obligatorio falta o es inválido.
     */
    public Mascota registrarMascota (String nombre, String raza, String especie, int edad, String sexo, boolean vacunasVigentes) {
        //this.mascotaRepository = mascotaRepository;



    /**
     * Crea y valida una nueva mascota.
     * @throws IllegalArgumentException si algún campo obligatorio falta o es inválido.
     */
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la mascota es obligatorio.");
        }
        if (raza == null || raza.trim().isEmpty() || especie == null || especie.trim().isEmpty()) {
            throw new IllegalArgumentException("La raza y la especie son obligatorias.");
        }
        if (edad <= 0) {
            throw new IllegalArgumentException("La edad debe ser mayor a cero.");
        }
        if (sexo == null || sexo.trim().isEmpty()) {
            throw new IllegalArgumentException("El sexo de la mascota es obligatorio.");
        }

        // --- Creación del objeto Mascota ---
        Mascota mascota = new Mascota();
        mascota.setNombre(nombre);
        mascota.setRaza(raza);
        mascota.setEspecie(especie);
        mascota.setEdad(edad);
        mascota.setSexo(sexo);
        mascota.setVacunasVigentes(vacunasVigentes);

        // --- Persistencia ---
        return mascotaRepository.save(mascota); // Se guarda implícitamente a través del Hospedaje
    }

    /**
     * @brief Registra una nueva mascota ASOCIADA A UN CLIENTE.
     * (Usado por ControlGestionarMascotas)
     * Se realiza una sobrecarga de metodos
     */
    public Mascota registraMascota(Cliente cliente, String nombre, String raza, String especie, int edad, String sexo, boolean vacunasVigentes) {
        
        if (cliente == null) {
            throw new IllegalArgumentException("No se puede registrar una mascota sin un cliente");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la mascota es obligatorio.");
        }
        if (raza == null || raza.trim().isEmpty() || especie == null || especie.trim().isEmpty()) {
            throw new IllegalArgumentException("La raza y la especie son obligatorias.");
        }
        if (edad <= 0) {
            throw new IllegalArgumentException("La edad debe ser mayor a cero.");
        }
        if (sexo == null || sexo.trim().isEmpty()) {
            throw new IllegalArgumentException("El sexo de la mascota es obligatorio.");
        }

        Mascota mascota = new Mascota();
        mascota.setCliente(cliente);
        mascota.setNombre(nombre);
        mascota.setRaza(raza);
        mascota.setEspecie(especie);
        mascota.setEdad(edad);
        mascota.setSexo(sexo);
        mascota.setVacunasVigentes(vacunasVigentes);

        return mascotaRepository.save(mascota);
    }

    
    /**
     * @brief Recupera todas las mascotas de un cliente específico.
     */
    public List<Mascota> recuperaMascotas(Cliente cliente) {
        return mascotaRepository.findByCliente(cliente);
    }

    /**
     * @brief Elimina una mascota por su ID.
     */
    public void eliminaMascota(Long idMascota) {
        if (!mascotaRepository.existsById(idMascota)) {
            throw new IllegalArgumentException("La mascota con ID " + idMascota + " no existe");
        }
        mascotaRepository.deleteById(idMascota);
    }
}
