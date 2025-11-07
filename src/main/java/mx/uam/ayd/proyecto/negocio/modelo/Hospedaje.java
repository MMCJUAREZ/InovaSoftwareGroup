package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;

/**
 * @file Hospedaje.java
 * @brief Representa la entidad de negocio que modela el hospedaje de una mascota.
 *
 * Esta clase forma parte del modelo de negocio y se encuentra mapeada a una tabla
 * en la base de datos mediante anotaciones de JPA.
 * Cada objeto {@link Hospedaje} representa una estancia de una mascota registrada
 * por un cliente dentro del sistema.
 *
 * <p>Incluye información sobre las fechas de entrada y salida, las observaciones
 * generales del hospedaje, y las relaciones con las entidades {@link Cliente} y {@link Mascota}.</p>
 *
 * @author Mitzi
 * @date 2025-11-01
 */


@Entity
public class Hospedaje {

    /**
     * Identificador único del hospedaje.
     * Se genera automáticamente en la base de datos.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHospedaje;

    /** Fecha en la que la mascota ingresa al hospedaje.*/
    private LocalDate fechaEntrada;

    /** Fecha en la que la mascota sale del hospedaje. */
    private LocalDate fechaSalida;

    /** Observaciones adicionales sobre el hospedaje (alimentación, comportamiento, etc.). */
    private String observaciones;

    /**
     * Relación muchos-a-uno con {@link Cliente}.
     * Indica el dueño de la mascota hospedada.
     */
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "idCliente")
    private Cliente cliente; // Dueño


    /**
     * Relación uno-a-uno con {@link Mascota}.
     * Se configura con cascada para que los cambios se propaguen automáticamente.
     */
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "idMascota")
    private Mascota mascota;

    // Getters y Setters

    /**
     * @return el identificador único del hospedaje.
     */
    public Long getIdHospedaje() { return idHospedaje; }

    /**
     * @param idHospedaje identificador único a asignar.
     */
    public void setIdHospedaje(Long idHospedaje) { this.idHospedaje = idHospedaje; }

    /**
     * @return la fecha de entrada del hospedaje.
     */
    public LocalDate getFechaEntrada() { return fechaEntrada; }

    /**
     * @param fechaEntrada la fecha en la que la mascota ingresa al hospedaje.
     */
    public void setFechaEntrada(LocalDate fechaEntrada) { this.fechaEntrada = fechaEntrada; }

    /**
     * @return la fecha de salida del hospedaje.
     */
    public LocalDate getFechaSalida() { return fechaSalida; }

    /**
     * @param fechaSalida la fecha en la que la mascota sale del hospedaje.
     */
    public void setFechaSalida(LocalDate fechaSalida) { this.fechaSalida = fechaSalida; }


    public String getObservaciones() { return observaciones; }

    /**
     * @param observaciones comentarios o notas adicionales sobre el hospedaje.
     */

    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }


    /**
     * @return el cliente (dueño de la mascota) asociado al hospedaje.
     */
    public Cliente getCliente() { return cliente; }

    /**
     * @param cliente objeto {@link Cliente} asociado al hospedaje.
     */
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    /**
     * @return la mascota hospedada.
     */
    public Mascota getMascota() { return mascota; }

    /**
     * @param mascota objeto {@link Mascota} hospedado.
     */
    public void setMascota(Mascota mascota) { this.mascota = mascota; }

}
