package mx.uam.ayd.proyecto.negocio;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.uam.ayd.proyecto.datos.AlertaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Alerta;
import mx.uam.ayd.proyecto.negocio.modelo.Umbral;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

@Service
public class ServicioAlerta {

    private final AlertaRepository alertaRepository;

    @Autowired
    public ServicioAlerta(AlertaRepository alertaRepository) {
        this.alertaRepository = alertaRepository;
    }

    public Alerta crearAlerta(Umbral umbral, String correo, String mensajePersonalizado, LocalDateTime fecha) {
        if (umbral.getAlerta() != null) {
            throw new IllegalArgumentException("Este umbral ya tiene una alerta asociada");
        }

        Alerta alerta = new Alerta();
        alerta.setMensajePersonalizado(mensajePersonalizado);
        alerta.setEnviadoPorCorreo(correo != null && !correo.isEmpty());
        alerta.setFechaHoraEnvio(fecha);
        alerta.setUmbral(umbral);

        umbral.setAlerta(alerta);

        return alertaRepository.save(alerta);
    }

    public Alerta editarAlerta(long idAlerta, String correo, String mensajePersonalizado, LocalDateTime fecha) {
        Alerta alerta = alertaRepository.findById(idAlerta)
                .orElseThrow(() -> new IllegalArgumentException("No existe la alerta especificada"));

        alerta.setMensajePersonalizado(mensajePersonalizado);
        alerta.setEnviadoPorCorreo(correo != null && !correo.isEmpty());
        alerta.setFechaHoraEnvio(fecha);

        return alertaRepository.save(alerta);
    }

    public Alerta crearAlertaSiNecesaria(Producto producto, Umbral umbral) {
        if (producto.getCantidadStock() < umbral.getValorMinimo()) {
            if (umbral.getAlerta() == null) {
                String mensaje = "El producto '" + producto.getNombre() + "' está por debajo del mínimo.";
                return crearAlerta(umbral, null, mensaje, LocalDateTime.now());
            } else {
                return umbral.getAlerta();
            }
        }
        return null;
    }
}
