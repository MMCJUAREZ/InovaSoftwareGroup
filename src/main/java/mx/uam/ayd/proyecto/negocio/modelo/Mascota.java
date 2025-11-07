package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @file Mascota.java
 * @brief Representa la entidad de negocio que modela una mascota registrada en el sistema.
 *
 * Esta clase forma parte del modelo de negocio y se encuentra mapeada a una tabla
 * en la base de datos mediante anotaciones de JPA. Cada objeto {@link Mascota}
 * contiene los datos básicos de identificación y características de una mascota
 * asociada a un cliente.
 *
 * <p>Se utiliza en las funcionalidades relacionadas con el registro de hospedaje,
 * control de clientes y manejo de historiales veterinarios.</p>
 *
 * @author Mitzi
 * @date 2025-11-01
 */

@Entity
@Data
public class Mascota {

    /**
     * Identificador único de la mascota.
     * Se genera automáticamente en la base de datos.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMascota;

    //Nombre de la mascota
    private String nombre;

    //Raza a la que pertenece la mascota
    private String raza;

    //Especie de la mascota( por ejemplo, perro, gato, etc.)
    private String especie;

    //Edad de la mascota
    private int edad;

    //Sexo de l mascota
    private String sexo;

    //Indica si la mascota tiene las vacunas al dia.
    private boolean vacunasVigentes;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Cliente cliente;
}
