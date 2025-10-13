package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.*;

/**
 * Representa un umbral de stock asociado a un producto en el sistema.
 * El umbral define un valor mínimo de inventario a partir del cual
 * se puede generar una alerta.
 *
 * Cada umbral está vinculado a un producto y puede tener una alerta asociada.
 * Se utiliza en la lógica de control de inventarios para evitar desabastecimientos.
 *
 * @author braulio sanchez
 */
@Entity
public class Umbral {

    /**
     * Identificador único del umbral (clave primaria).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUmbral;

    /**
     * Valor mínimo de stock permitido antes de activar una alerta.
     */
    private int valorMinimo;

    /**
     * Producto al que pertenece este umbral.
     */
    @OneToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    /**
     * Alerta asociada a este umbral (puede ser nula si no hay alerta activa).
     */
    @OneToOne
    @JoinColumn(name = "alerta_id")
    private Alerta alerta;

    /**
     * Obtiene el identificador único del umbral.
     * @return ID del umbral
     */
    public Long getIdUmbral() {
        return idUmbral;
    }

    /**
     * Asigna un identificador único al umbral.
     * @param idUmbral ID a asignar
     */
    public void setIdUmbral(Long idUmbral) {
        this.idUmbral = idUmbral;
    }

    /**
     * Obtiene el valor mínimo configurado para el umbral.
     * @return valor mínimo de stock
     */
    public int getValorMinimo() {
        return valorMinimo;
    }

    /**
     * Asigna un nuevo valor mínimo al umbral.
     * @param valorMinimo nuevo valor de stock mínimo
     */
    public void setValorMinimo(int valorMinimo) {
        this.valorMinimo = valorMinimo;
    }

    /**
     * Obtiene el producto asociado al umbral.
     * @return producto vinculado
     */
    public Producto getProducto() {
        return producto;
    }

    /**
     * Asocia un producto al umbral.
     * Además, sincroniza la relación bidireccional
     * @param producto producto a asociar
     */
    public void setProducto(Producto producto) {
        this.producto = producto;
        if (producto != null && producto.getUmbral() != this) {
            producto.setUmbral(this);
        }
    }

    /**
     * Obtiene la alerta asociada a este umbral.
     * @return alerta vinculada o null si no hay
     */
    public Alerta getAlerta() {
        return alerta;
    }

    /**
     * Asocia una alerta a este umbral.
     * @param alerta alerta a vincular
     */
    public void setAlerta(Alerta alerta) {
        this.alerta = alerta;
    }

    /**
     * Compara este umbral con otro por su identificador.
     * @param obj objeto a comparar
     * @return true si tienen el mismo ID, false en caso contrario
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Umbral other = (Umbral) obj;
        return idUmbral != null && idUmbral.equals(other.idUmbral);
    }

    /**
     * Calcula el código hash del umbral en base a su ID.
     * @return valor hash
     */
    @Override
    public int hashCode() {
        return idUmbral != null ? idUmbral.hashCode() : 0;
    }

    /**
     * Devuelve una representación en texto del umbral.
     * Incluye ID, valor mínimo, nombre del producto y alerta.
     * @return representación de texto
     */
    @Override
    public String toString() {
        return "Umbral [idUmbral=" + idUmbral + ", valorMinimo=" + valorMinimo +
                ", producto=" + (producto != null ? producto.getNombre() : "null") +
                ", alerta=" + (alerta != null ? alerta.getIdAlerta() : "null") + "]";
    }
}
