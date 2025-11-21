package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

/**
 * @file Cirugia.java
 * @brief Representa la entidad de negocio que modela una cirugia realizada a una mascota.
 *
 * Esta clase forma parte del modelo de negocio y se encuentra mapeada a una tabla
 * en la base de datos mediante anotaciones de JPA.
 * Contiene los datos especificos del procedimiento quirurgico asi como informacion
 * relevante del historial clinico asociado (consultas, tratamientos).
 *
 * @author InovaSoftwareGroup
 * @date 2025-11-20
 */
@Entity
@Data
public class Cirugia {
    
    /**
     * Identificador unico de la cirugia.
     * Se genera automaticamente en la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCirugia;

    // Datos de la Cirugia

    /** Fecha en la que se realizo el procedimiento quirurgico. */
    @Column(nullable = false)
    private LocalDate fecha;

    /** * Nombre o tipo de la cirugia realizada. 
     * Campo obligatorio con una longitud maxima de 100 caracteres.
     */
    @Column(length = 100, nullable = false)
    private String tipoCirugia;

    /** * Descripcion detallada del procedimiento realizado. 
     * Longitud maxima de 300 caracteres.
     */
    @Column(length = 300)
    private String descripcion;

    // Datos del Historial Clinico asociados

    /** * Notas referentes a las consultas previas o asociadas a la cirugia.
     */
    @Column(length = 300)
    private String consultas; 

    /** * Medicamentos o tratamientos aplicados durante o despues de la cirugia.
     */
    @Column(length = 300)
    private String tratamientos;

    /** * Observaciones generales adicionales sobre la recuperacion o estado de la mascota.
     */
    private String observacionesGenerales;

    // Relacion con Mascota (Muchos a Uno)
    
    /**
     * Relacion muchos-a-uno con la entidad Mascota.
     * Indica a que mascota se le realizo la cirugia.
     * Se utiliza carga perezosa (LAZY) para optimizar el rendimiento.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idMascota", nullable = false)
    private Mascota mascota;
}