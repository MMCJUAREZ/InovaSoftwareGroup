package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import java.time.LocalDateTime;

/**
 * Entidad de negocio Cita, para registrar y gestionar agendamientos.
 */

@Entity
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

    // Getters y Setters

    public Long getIdCita() {

        return idCita;

    }

    public void setIdCita(Long idCita) {

        this.idCita = idCita;

    }

    public LocalDateTime getFechaHora() {

        return fechaHora;

    }

    public void setFechaHora(LocalDateTime fechaHora) {

        this.fechaHora = fechaHora;

    }

    public TipoCita getTipo() {

        return tipo;

    }

    public void setTipo(TipoCita tipo) {

        this.tipo = tipo;

    }

    public String getNombreSolicitante() {

        return nombreSolicitante;

    }

    public void setNombreSolicitante(String nombreSolicitante) {

        this.nombreSolicitante = nombreSolicitante;

    }

    public String getContacto() {

        return contacto;

    }

    public void setContacto(String contacto) {

        this.contacto = contacto;

    }

    public boolean isAtendida() {

        return atendida;

    }

    public void setAtendida(boolean atendida) {

        this.atendida = atendida;

    }

    @Override
    public String toString() {

        return "Cita [idCita=" + idCita + ", fechaHora=" + fechaHora + ", tipo=" + tipo + ", nombreSolicitante=" + nombreSolicitante + "]";

    }
}
