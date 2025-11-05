package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

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


    // Getters y Setters (ocupamos Lombok para simplificar)

    /**
     * @return el identificador único de la mascota.
     */
    public Long getIdMascota() {return idMascota; }

    /**
     * @param idMascota identificador único a asignar.
     */
    public void setIdMascotas(Long idMascota) { this.idMascota = idMascota; }

    /**
     * @return el nombre de la mascota.
     */
    public String getNombre() {return nombre;}

    /**
     * @param nombre nombre de la mascota.
     */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /**
     * @return la raza de la mascota.
     */
    public String getRaza() { return raza; }

    /**
     * @param raza raza a la que pertenece la mascota.
     */
    public void setRaza(String raza) { this.raza = raza; }

    /**
     * @return la especie de la mascota.
     */
    public String getEspecie() { return especie; }

    /**
     * @param especie especie de la mascota (por ejemplo, perro o gato).
     */
    public void setEspecie(String especie) { this.especie = especie; }

    /**
     * @return la edad de la mascota.
     */
    public int getEdad() { return edad; }

    /**
     * @param edad edad de la mascota en años.
     */
    public void setEdad(int edad) { this.edad = edad; }

    /**
     * @return el sexo de la mascota.
     */
    public String getSexo() { return sexo; }

    /**
     * @param sexo sexo de la mascota (macho o hembra).
     */
    public void setSexo(String sexo) { this.sexo = sexo; }

    /**
     * @return true si las vacunas están vigentes, false en caso contrario.
     */
    public boolean isVacunasVigentes() { return vacunasVigentes; }

    /**
     * @param vacunasVigentes indica si la mascota tiene sus vacunas al día.
     */
    public void setVacunasVigentes(boolean vacunasVigentes) { this.vacunasVigentes = vacunasVigentes; }
}
