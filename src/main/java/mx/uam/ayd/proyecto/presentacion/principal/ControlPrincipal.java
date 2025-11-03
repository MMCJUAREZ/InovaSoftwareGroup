package mx.uam.ayd.proyecto.presentacion.principal;

import jakarta.annotation.PostConstruct;

import mx.uam.ayd.proyecto.presentacion.Inventario.Controlinventario;
import mx.uam.ayd.proyecto.presentacion.agregarCartilla.ControlAgregarCartilla;
import mx.uam.ayd.proyecto.presentacion.citas.ControlCitas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.presentacion.configurarUmbrales.ControlConfiguracionUmbrales;
import mx.uam.ayd.proyecto.presentacion.alertas.ControlAlerta;
import mx.uam.ayd.proyecto.presentacion.alertas.VentanaAlerta;
import mx.uam.ayd.proyecto.presentacion.generarReporte.ControlGenerarReporte;
import mx.uam.ayd.proyecto.presentacion.registroVentas.ControlRegistroVentas;
import mx.uam.ayd.proyecto.presentacion.agregarCliente.ControlGestionarClientes;

/**
 * Esta clase lleva el flujo de control de la ventana principal
 *
 * @author humbertocervantes
 *
 */
@Component
public class ControlPrincipal {

	private final ControlConfiguracionUmbrales controlConfiguracionUmbrales;
	private final ControlAlerta controlAlerta;
	private final VentanaAlerta ventanaAlerta;
	private final Controlinventario controlinventario;
	private final ControlGenerarReporte controlGenerarReporte;
	private final ControlRegistroVentas controlRegistroVentas;
	private final ControlGestionarClientes controlGestionarClientes;
    private final ControlAgregarCartilla controlAgregarCartilla;
	private final ControlCitas controlCitas;

	private final VentanaPrincipal ventana;

	@Autowired
	public ControlPrincipal(
            ControlConfiguracionUmbrales controlConfiguracionUmbrales,
            ControlAlerta controlAlerta,
            VentanaAlerta ventanaAlerta,
            Controlinventario controlinventario,
            ControlGenerarReporte controlGenerarReporte,
            ControlRegistroVentas controlRegistroVentas,
            ControlGestionarClientes controlGestionarClientes, ControlAgregarCartilla controlAgregarCartilla,ControlCitas controlCitas,VentanaPrincipal ventana
            )  {
		this.controlConfiguracionUmbrales = controlConfiguracionUmbrales;
		this.controlAlerta = controlAlerta;
		this.ventanaAlerta = ventanaAlerta;
		this.controlinventario = controlinventario;
		this.controlGenerarReporte = controlGenerarReporte;
		this.controlRegistroVentas = controlRegistroVentas;
		this.controlGestionarClientes = controlGestionarClientes;
        this.controlAgregarCartilla = controlAgregarCartilla;
        this.ventana = ventana;
		this.controlCitas = controlCitas;

	}

	/**
	 * Método que se ejecuta después de la construcción del bean
	 * y realiza la conexión bidireccional entre el control principal y la ventana principal
	 */
	@PostConstruct
	public void init() {
		ventana.setControlPrincipal(this);
	}

	/**
	 * Inicia el flujo de control de la ventana principal
	 *
	 */
	public void inicia() {
		ventana.muestra();
	}

	/*
        Metodo que arranca la historia de usuario "configurar umbrales"
     */
	public void configurarUmbrales() {
		controlConfiguracionUmbrales.inicia();
	}

	/*
	 * Metodo que arranca la historia de usuario "Listar prodcutos "
	 *
	 * */
	public void Inventario() {
		controlinventario.inicia();
	}

	/**
	 * Método que arranca la historia de usuario "listar usuarios"
	 *
	 */
	public void registrarVenta() {
		controlRegistroVentas.inicia();
	}

	/**
	 * Método que arranca la historia de usuario "listar grupos"
	 *
	 */
	public void generarReporte() {
		controlGenerarReporte.inicia();
	}

	/**
	 * Método para mostrar ventana de alertas
	 */
	public void mostrarAlertas() {
		controlAlerta.inicia();
	}

	public void gestionarClientes() {
        controlGestionarClientes.inicia();
    }

    public void agregarCartilla() {
        controlAgregarCartilla.inicia();
    }

	/**
	 * Metodo que arranca la historia de usuario "listar citas"
	 */

	public void gestionarCitas() { controlCitas.inicia(); }
}
