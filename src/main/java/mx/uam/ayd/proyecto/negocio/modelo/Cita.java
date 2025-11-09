package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import java.time.LocalDateTime;

// Importacion clave de Lombok

import lombok.Data;

/**
 * Entidad de negocio Cita, para registrar y gestionar las agendas de citas.
 */

@Entity
@Data // Genera automáticamente todos los getters, setters, equals, hashCode y toString.
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCita;

    private LocalDateTime fechaHora; // Para la validación de solapamiento

    @Enumerated(EnumType.STRING)
    private TipoCita tipo;

    private String nombreSolicitante;
    private String contacto; // Puede ser correo o teléfono
    private boolean atendida; // Para saber si puede modificarse/cancelarse

    // Campos de la HU-02

    @ManyToOne
    @JoinColumn(name = "veterinario_id")
    private Veterinario veterinario; // Relación con la nueva entidad

    private String motivo; // Campo de texto para el motivo de la cita
    private String notas;  // Campo de texto para notas/observaciones

    // Nota: No es necesario escribir los getters y setters aquí porque @Data los genera.

    @Override
    public String toString() {
        return "Cita [idCita=" + idCita + ", fechaHora=" + fechaHora + ", tipo=" + tipo + ", nombreSolicitante=" + nombreSolicitante + ", veterinario=" + (veterinario != null ? veterinario.getNombreCompleto() : "N/A") + "]";
    }
}
