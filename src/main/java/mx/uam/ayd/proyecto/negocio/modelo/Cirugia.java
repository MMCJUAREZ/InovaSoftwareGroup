package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

/**
 * Representa la entidad de negocio que modela una cirugía realizada a una mascota.
 *
 * <p>Esta clase forma parte del modelo de negocio y se encuentra mapeada a una tabla
 * en la base de datos mediante anotaciones de JPA. Contiene los datos específicos
 * del procedimiento quirúrgico así como información relevante del historial clínico
 * asociado (consultas, tratamientos).</p>
 *
 * @author InovaSoftwareGroup
 * @since 2025-11-20
 */
@Entity
@Data
public class Cirugia {

	/**
	 * Identificador único de la cirugía.
	 * Se genera automáticamente en la base de datos.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idCirugia;

	// Datos de la Cirugía

	/** Fecha en la que se realizó el procedimiento quirúrgico. */
	@Column(nullable = false)
	private LocalDate fecha;

	/**
	 * Nombre o tipo de la cirugía realizada.
	 * Campo obligatorio con una longitud máxima de 100 caracteres.
	 */
	@Column(length = 100, nullable = false)
	private String tipoCirugia;

	/**
	 * Descripción detallada del procedimiento realizado.
	 * Longitud máxima de 300 caracteres.
	 */
	@Column(length = 300)
	private String descripcion;

	// Datos del Historial Clínico asociados

	/** Notas referentes a las consultas previas o asociadas a la cirugía. */
	@Column(length = 300)
	private String consultas;

	/** Medicamentos o tratamientos aplicados durante o después de la cirugía. */
	@Column(length = 300)
	private String tratamientos;

	/** Observaciones generales adicionales sobre la recuperación o estado de la mascota. */
	private String observacionesGenerales;

	// Relación con Mascota (Muchos a Uno)

	/**
	 * Relación muchos-a-uno con la entidad Mascota.
	 * Indica a qué mascota se le realizó la cirugía.
	 * Se utiliza carga perezosa (LAZY) para optimizar el rendimiento.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idMascota", nullable = false)
	private Mascota mascota;
}