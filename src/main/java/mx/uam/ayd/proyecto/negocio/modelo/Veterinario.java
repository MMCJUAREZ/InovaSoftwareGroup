package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad de negocio Veterinario.
 */

@Entity
@Data
public class Veterinario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVeterinario;

    private String nombreCompleto;
    private String cedulaProfesional;

    // Relación bidireccional: Un veterinario tiene muchas citas

    @OneToMany(mappedBy = "veterinario")
    private List<Cita> citasAsignadas = new ArrayList<>();

    @Override
    public String toString() {
        return nombreCompleto;
    }
}