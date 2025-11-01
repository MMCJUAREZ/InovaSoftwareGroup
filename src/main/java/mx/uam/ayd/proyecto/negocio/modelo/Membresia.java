package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
public class Membresia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMembresia;

    @Column
    private TipoMembresia tipo;

    @Column (nullable = false)
    private double precio;

    @Column (nullable = false)
    private boolean estado; //true para vigente

    @OneToOne(mappedBy = "membresia")
    @EqualsAndHashCode.Exclude  //Agregue esto para evitar recursion #Ad
    @ToString.Exclude           //Agregue esto para evitar recursion #Ad
    private Cliente cliente;

}
