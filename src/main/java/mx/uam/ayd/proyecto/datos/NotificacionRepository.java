package mx.uam.ayd.proyecto.datos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Notificacion;

public interface NotificacionRepository extends CrudRepository<Notificacion, Long> {
}
