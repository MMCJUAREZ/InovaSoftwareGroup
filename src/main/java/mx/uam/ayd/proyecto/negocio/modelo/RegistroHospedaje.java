/* Copyright 2025
 * Proyecto Veterinaria - Control de estancia
 */
package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad que representa un registro diario del hospedaje de una mascota.
 * Incluye información sobre alimentación, salud, comportamiento y observaciones.
 */
@Entity
public class RegistroHospedaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRegistro;

    /** Fecha y hora del registro(se asigna al crear).*/
    private LocalDateTime fechaRegistro;

    /** Descripcion de la alimentacion (texto libre).*/
    @Column(length = 1000)
    private String alimentacion;

    /**Estado de salud (notas).*/
    @Column(length = 1000)
    private String salud;

    /** Comportamineto y actividades.*/
    @Column(length = 1000)
    private String comportamiento;

    /** Observaciones adicionales*/
    @Column(length = 2000)
    private String observaciones;

    @ManyToOne
    @JoinColumn(name = "id_hospedaje")
    private Hospedaje hospedaje;

    //Getters y setters
    public Long getIdRegistro() {
        return idRegistro;
    }
    public void setIdRegistro(Long idRegistro) {
        this.idRegistro = idRegistro;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getAlimentacion() {
        return alimentacion;
    }
    public void setAlimentacion(String alimentacion) {
        this.alimentacion = alimentacion;
    }

    public String getSalud() {
        return salud;
    }
    public void setSalud(String salud) {
        this.salud = salud;
    }

    public String getComportamiento() {
        return comportamiento;
    }
    public void setComportamiento(String comportamiento) {
        this.comportamiento = comportamiento;
    }

    public String getObservaciones() {
        return observaciones;
    }
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Hospedaje getHospedaje() {
        return hospedaje;
    }
    public void setHospedaje(Hospedaje hospedaje) {
        this.hospedaje = hospedaje;
    }




}
