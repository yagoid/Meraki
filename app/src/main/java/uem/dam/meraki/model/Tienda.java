package uem.dam.meraki.model;

public class Tienda {

    private String uid;
    private String email;
    private String nombre;
    private String tienda;
    private Double latitud;
    private Double longitud;

    private String telefono;
    private String direccion;


    public Tienda() {
    }

    public Tienda(String uid, String email, String nombre, String tienda, Double latitud, Double longitud) {
        this.uid = uid;
        this.email = email;
        this.nombre = nombre;
        this.tienda = tienda;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Tienda(String nombre, String tienda, String telefono, String direccion, String uid) {
        this.nombre = nombre;
        this.tienda = tienda;
        this.telefono = telefono;
        this.direccion = direccion;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTienda() {
        return tienda;
    }

    public void setTienda(String tienda) {
        this.tienda = tienda;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }


    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
