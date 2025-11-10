package mx.uam.ayd.proyecto.negocio;

import mx.uam.ayd.proyecto.datos.VeterinarioRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Veterinario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import jakarta.annotation.PostConstruct;

@Service

/*
 * Servicio encargado de la lógica de negocio para los Veterinarios.
 */

public class ServicioVeterinario {

    private final VeterinarioRepository veterinarioRepository;

    @Autowired
    public ServicioVeterinario(VeterinarioRepository veterinarioRepository) {
        this.veterinarioRepository = veterinarioRepository;
    }

    /**
     * Inicializa la base de datos con algunos veterinarios de prueba.
     * Esto asegura que siempre haya datos para usar en el ComboBox de citas.
     */

    @PostConstruct
    private void inicializaVeterinarios() {
        if (veterinarioRepository.count() == 0) {
            Veterinario v1 = new Veterinario();
            v1.setNombreCompleto("Dr. Juan Perez (Cirugía)");
            v1.setCedulaProfesional("VET12345");
            veterinarioRepository.save(v1);

            Veterinario v2 = new Veterinario();
            v2.setNombreCompleto("Dra. Ana Lopez (General)");
            v2.setCedulaProfesional("VET67890");
            veterinarioRepository.save(v2);
        }
    }

    /**
     * Recupera todos los veterinarios disponibles.
     */

    public List<Veterinario> recuperarVeterinarios() {
        List<Veterinario> veterinarios = new ArrayList<>();

        // Utiliza un bucle for-each para copiar los elementos de Iterable a List

        for(Veterinario veterinario : veterinarioRepository.findAll()) {
            veterinarios.add(veterinario);
        }

        return veterinarios;
    }
}
