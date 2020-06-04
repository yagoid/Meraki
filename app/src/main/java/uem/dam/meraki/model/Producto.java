package uem.dam.meraki.model;

public class Producto {

    private String producto;
    private Double precio;

    public Producto() {
    }

    public Producto(String producto, Double precio) {
        this.producto = producto;
        this.precio = precio;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return producto + " - " + precio + "€";
    }
}
