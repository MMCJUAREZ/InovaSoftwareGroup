package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "cartillas")
public class Cartilla {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VacunaEnum vacuna;

    @Column(name = "fecha_aplicacion", nullable = false)
    private LocalDate fechaAplicacion;

    @Column(name = "proxima_dosis")
    private LocalDate proximaDosis;

    @Column(nullable = false)
    private String veterinario;

    private Long lote;

    private String observaciones;

    @Column(name = "mascota_id", nullable = false)
    private Long mascotaId;

    // Constructor vacío requerido por JPA
    public Cartilla() {}

    public Cartilla(VacunaEnum vacuna, LocalDate fechaAplicacion, String veterinario,
                    Long lote, String observaciones, Long mascotaId) {
        this.vacuna = vacuna;
        this.fechaAplicacion = fechaAplicacion;
        this.veterinario = veterinario;
        this.lote = lote;
        this.observaciones = observaciones;
        this.mascotaId = mascotaId;

        // Calcular próxima dosis (simplificado - siempre 12 meses)
        if (fechaAplicacion != null) {
            this.proximaDosis = fechaAplicacion.plusMonths(12);
        }
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public VacunaEnum getVacuna() { return vacuna; }
    public void setVacuna(VacunaEnum vacuna) {
        this.vacuna = vacuna;
    }

    public LocalDate getFechaAplicacion() { return fechaAplicacion; }
    public void setFechaAplicacion(LocalDate fechaAplicacion) {
        this.fechaAplicacion = fechaAplicacion;
        if (fechaAplicacion != null) {
            this.proximaDosis = fechaAplicacion.plusMonths(12);
        }
    }

    public LocalDate getProximaDosis() { return proximaDosis; }
    public void setProximaDosis(LocalDate proximaDosis) { this.proximaDosis = proximaDosis; }

    public String getVeterinario() { return veterinario; }
    public void setVeterinario(String veterinario) { this.veterinario = veterinario; }

    public Long getLote() { return lote; }
    public void setLote(Long lote) { this.lote = lote; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Long getMascotaId() { return mascotaId; }
    public void setMascotaId(Long mascotaId) { this.mascotaId = mascotaId; }

    @Override
    public String toString() {
        return "Cartilla{" +
                "id=" + id +
                ", vacuna=" + vacuna +
                ", fechaAplicacion=" + fechaAplicacion +
                ", proximaDosis=" + proximaDosis +
                ", veterinario='" + veterinario + '\'' +
                ", lote=" + lote +
                ", mascotaId=" + mascotaId +
                '}';
    }
}