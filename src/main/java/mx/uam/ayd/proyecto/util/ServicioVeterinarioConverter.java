package mx.uam.ayd.proyecto.util;

import javafx.util.StringConverter;
import mx.uam.ayd.proyecto.negocio.modelo.Veterinario;
import java.util.List;
import java.util.Optional;

/**
 * Convertidor para mostrar Veterinarios en un ComboBox.
 * Permite que el ComboBox muestre el nombre (String) pero maneje el objeto (Veterinario).
 */

public class ServicioVeterinarioConverter extends StringConverter<Veterinario> {

    private final List<Veterinario> veterinarios;

    public ServicioVeterinarioConverter(List<Veterinario> veterinarios) {
        this.veterinarios = veterinarios;
    }

    @Override
    public String toString(Veterinario veterinario) {

        // Muestra el nombre en el ComboBox

        return (veterinario == null) ? "Seleccione un veterinario" : veterinario.getNombreCompleto();

    }

    @Override
    public Veterinario fromString(String string) {

        // Convierte el String (nombre) de vuelta al Objeto Veterinario

        Optional<Veterinario> vet = veterinarios.stream()
                .filter(v -> v.getNombreCompleto().equals(string))
                .findFirst();
        return vet.orElse(null);

    }
}