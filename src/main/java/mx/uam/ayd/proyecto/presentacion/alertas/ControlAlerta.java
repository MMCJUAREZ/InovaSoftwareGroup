package mx.uam.ayd.proyecto.presentacion.alertas;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.negocio.ServicioAlerta;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import mx.uam.ayd.proyecto.negocio.modelo.Umbral;

@Component
public class ControlAlerta {

    @Autowired
    private ServicioAlerta servicioAlerta;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private VentanaAlerta ventanaAlerta;

    public void inicia() {
        ventanaAlerta.setControl(this);
        ventanaAlerta.muestra();
    }

    /**
     * Revisa el stock de productos en base a los umbrales y genera alertas cuando
     * es necesario.
     *
     * @return Lista de mensajes de alerta para mostrar
     */
    public List<String> revisarStock() {
        List<String> mensajesAlertas = new ArrayList<>();

        List<Producto> productos = new ArrayList<>();
        productoRepository.findAll().forEach(productos::add);

        for (Producto producto : productos) {
            System.out.println("DEBUG - Producto: " + producto.getNombre());
            System.out.println("DEBUG - Stock actual: " + producto.getCantidadStock());

            Umbral umbral = producto.getUmbral();
            System.out.println("DEBUG - Umbral: " + (umbral != null ? umbral.getValorMinimo() : "SIN UMBRAL"));

            if (umbral != null) {
                if (producto.getCantidadStock() < umbral.getValorMinimo()) {
                    System.out.println("DEBUG - ALERTA GENERADA para " + producto.getNombre());
                    servicioAlerta.crearAlertaSiNecesaria(producto, umbral);
                    String mensaje = "El producto '" + producto.getNombre() + "' está por debajo del mínimo. Stock actual: "
                            + producto.getCantidadStock() + ", mínimo permitido: " + umbral.getValorMinimo();
                    mensajesAlertas.add(mensaje);
                }
            }
        }


        if (mensajesAlertas.isEmpty()) {
            mensajesAlertas.add("No hay alertas. Todos los productos están con stock suficiente.");
        }

        return mensajesAlertas;
    }
}
