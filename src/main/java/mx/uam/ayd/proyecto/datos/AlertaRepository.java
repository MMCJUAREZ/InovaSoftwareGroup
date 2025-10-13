package mx.uam.ayd.proyecto.datos;

import org.springframework.data.repository.CrudRepository;

import mx.uam.ayd.proyecto.negocio.modelo.Alerta;

public interface AlertaRepository extends CrudRepository<Alerta, Long> {
    //Alerta findByIdProducto(Long idProducto);

} 
