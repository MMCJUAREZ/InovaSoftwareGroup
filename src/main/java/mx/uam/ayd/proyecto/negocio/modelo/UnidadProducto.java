package mx.uam.ayd.proyecto.negocio.modelo;

public enum UnidadProducto {
    Kg(""),
    Pieza(""),

    Tableta("Medicamento"),
    Inyeccion("Medicamento"),
    Bebible("Medicamento");

    private final String tipoProducto;

    UnidadProducto(String tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public String getTipoProducto() {
        return tipoProducto;
    }
}