package mx.uam.ayd.proyecto.negocio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import mx.uam.ayd.proyecto.datos.CirugiaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cirugia;
import mx.uam.ayd.proyecto.negocio.modelo.Mascota;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Proporciona la lógica de negocio para el registro de cirugías e historial clínico.
 *
 * <p>Esta clase se encarga de validar toda la información referente a un procedimiento
 * quirúrgico antes de ser persistido en la base de datos.
 * Verifica que los campos obligatorios no sean nulos, que las fechas sean válidas
 * (no pasadas) y que los textos no contengan caracteres ilegales.</p>
 *
 * @author InovaSoftwareGroup
 * @since 2025-11-20
 */
@Service
public class ServicioCirugia {

	/** Repositorio para realizar operaciones CRUD sobre la entidad Cirugia. */
	@Autowired
	private CirugiaRepository cirugiaRepository;

	/**
	 * Expresión regular para validar el texto.
	 * Solo permite letras, números, espacios, puntos y comas.
	 */
	private static final String TEXTO_VALIDO_REGEX = "^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ .,]+$";

	/**
	 * Registra una nueva cirugía aplicando validaciones de negocio.
	 *
	 * <p>Este método verifica que:
	 * <ul>
	 * <li>La mascota y la fecha no sean nulas.</li>
	 * <li>La fecha de la cirugía no sea anterior al día actual.</li>
	 * <li>Los campos de texto (tipo, consultas, tratamientos) no estén vacíos.</li>
	 * <li>Los campos de texto cumplan con la longitud y formato permitidos.</li>
	 * </ul></p>
	 *
	 * @param mascota La mascota a la que se le realiza la cirugía.
	 * @param fecha Fecha en la que se realiza el procedimiento.
	 * @param tipo Nombre o tipo de la cirugía.
	 * @param descripcion Descripción detallada del procedimiento.
	 * @param consultas Notas sobre las consultas pre o post operatorias.
	 * @param tratamientos Medicamentos o cuidados indicados.
	 * @param observaciones Notas generales adicionales (opcional).
	 * @return El objeto Cirugia guardado en la base de datos.
	 * @throws IllegalArgumentException Si algún dato es nulo, vacío, tiene una fecha inválida
	 * o contiene caracteres no permitidos.
	 */
	public Cirugia registrarCirugia(Mascota mascota, LocalDate fecha, String tipo, String descripcion,
									String consultas, String tratamientos, String observaciones) {

		// Validaciones de campos obligatorios (No nulos)
		if (mascota == null) throw new IllegalArgumentException("La mascota es obligatoria.");
		if (fecha == null) throw new IllegalArgumentException("La fecha es obligatoria.");
		if (tipo == null || tipo.trim().isEmpty()) throw new IllegalArgumentException("El tipo de cirugía es obligatorio.");
		if (consultas == null || consultas.trim().isEmpty()) throw new IllegalArgumentException("El campo consultas es obligatorio.");
		if (tratamientos == null || tratamientos.trim().isEmpty()) throw new IllegalArgumentException("El campo tratamientos es obligatorio.");

		// Validación de fecha (No pasado)
		if (fecha.isBefore(LocalDate.now())) {
			throw new IllegalArgumentException("La fecha no puede ser anterior al día de hoy.");
		}

		// Validación de formato y longitud de textos
		validarTexto(tipo, 100, "Tipo de cirugía");
		validarTexto(descripcion, 300, "Descripción");
		validarTexto(consultas, 300, "Consultas");
		validarTexto(tratamientos, 300, "Tratamientos");

		// Observaciones es opcional, solo se valida si contiene texto
		if (observaciones != null && !observaciones.isEmpty()) {
			validarTexto(observaciones, 500, "Observaciones");
		}

		// Creación y persistencia del objeto
		Cirugia cirugia = new Cirugia();
		cirugia.setMascota(mascota);
		cirugia.setFecha(fecha);
		cirugia.setTipoCirugia(tipo);
		cirugia.setDescripcion(descripcion);
		cirugia.setConsultas(consultas);
		cirugia.setTratamientos(tratamientos);
		cirugia.setObservacionesGenerales(observaciones);

		return cirugiaRepository.save(cirugia);
	}

	/**
	 * Método auxiliar para validar cadenas de texto.
	 *
	 * <p>Verifica dos condiciones:
	 * <ol>
	 * <li>Que la longitud del texto no exceda el máximo permitido.</li>
	 * <li>Que el contenido del texto coincida con la expresión regular (sin caracteres especiales raros).</li>
	 * </ol></p>
	 *
	 * @param texto El string a validar.
	 * @param maxLength La longitud máxima permitida.
	 * @param nombreCampo El nombre del campo para usar en el mensaje de error.
	 * @throws IllegalArgumentException Si el texto excede la longitud o contiene caracteres inválidos.
	 */
	private void validarTexto(String texto, int maxLength, String nombreCampo) {
		if (texto == null) return;

		if (texto.length() > maxLength) {
			throw new IllegalArgumentException(nombreCampo + " excede el máximo de " + maxLength + " caracteres.");
		}

		if (!Pattern.matches(TEXTO_VALIDO_REGEX, texto)) {
			throw new IllegalArgumentException(nombreCampo + " contiene caracteres inválidos (solo letras, números, puntos y comas).");
		}
	}
}