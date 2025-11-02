package mx.uam.ayd.proyecto.negocio.modelo;

public enum MarcaProducto {
    // Medicamentos
    ZOETIS("Medicamento"),
    MSD("Medicamento"),
    ELANCO("Medicamento"),

    // Comida
    Royal_Canin("Comida"),
    HILLS("Comida"),
    Catchow("Comida"),

    // Estetica
    KONG("Estetica"),
    PETMATE("Estetica"),
    TRIXIE("Estetica"),

    // Limpieza
    TROPICLEAN("Limpieza"),
    EARRTHBATH("Limpieza"),
    FURMINATOR("Limpieza");

    private final String tipoProducto;

    MarcaProducto(String tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public String getTipoProducto() {
        return tipoProducto;
    }
}