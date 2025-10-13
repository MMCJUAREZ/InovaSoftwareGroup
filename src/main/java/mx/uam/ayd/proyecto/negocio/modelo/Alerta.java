package mx.uam.ayd.proyecto.negocio.modelo;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;

import java.time.LocalDateTime;
/*
    Representa la alerta generada cuando un producto esta por debajo del umbral
 */
@Entity
public class Alerta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAlerta;
    private String mensajePersonalizado;
    private boolean enviadoPorCorreo;
    private String correo;
    private LocalDateTime fechaHoraEnvio;

    @OneToOne
    @JoinColumn(name ="idUmbral")
    private Umbral umbral;

    // ----- Getters y Setters -----

    public long getIdAlerta() {
        return idAlerta;
    }

    public void setIdAlerta(long idAlerta) {
        this.idAlerta = idAlerta;
    }

    public String getMensajePersonalizado() {
        return mensajePersonalizado;
    }

    public void setMensajePersonalizado(String mensajePersonalizado) {
        this.mensajePersonalizado = mensajePersonalizado;
    }

    public boolean isEnviadoPorCorreo() {
        return enviadoPorCorreo;
    }

    public void setEnviadoPorCorreo(boolean enviadaPorCorreo) {
        this.enviadoPorCorreo = enviadaPorCorreo;
    }

    public LocalDateTime getFechaHoraEnvio() {
        return fechaHoraEnvio;
    }

    public void setFechaHoraEnvio(LocalDateTime fechaHoraEnvio) {
        this.fechaHoraEnvio = fechaHoraEnvio;
    }

    public Umbral getUmbral() {
        return umbral;
    }

    public void setUmbral(Umbral umbral) {
        this.umbral = umbral;
    }

    public String getCorreo() {return correo;}
    public void setCorreo(String correo) {this.correo = correo;}

    // ----- Métodos utilitarios -----

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Alerta))
            return false;
        Alerta other = (Alerta) obj;
        return idAlerta == other.idAlerta;
    }

    @Override
    public int hashCode() {
        return (int) (31 * idAlerta);
    }

    @Override
    public String toString() {
        return "Alerta [idAlerta=" + idAlerta + ", mensajePersonalizado=" + mensajePersonalizado +
                ", enviadaPorCorreo=" + enviadoPorCorreo + ", fechaHoraEnvio=" + fechaHoraEnvio + "]";
    }
}

