package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    @EqualsAndHashCode.Exclude  
    @ToString.Exclude 
    private Cliente cliente;

}
