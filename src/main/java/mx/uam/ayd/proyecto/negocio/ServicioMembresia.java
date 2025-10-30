package mx.uam.ayd.proyecto.negocio;

import org.springframework.stereotype.Service;

import mx.uam.ayd.proyecto.negocio.modelo.TipoMembresia;

@Service
public class ServicioMembresia {
    
    public TipoMembresia seleccionarMembresia(char c){

        if(c == 'S'){
            return TipoMembresia.Standard;
        }else{
            return TipoMembresia.Platinum;
        }
    }
}
