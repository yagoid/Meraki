package uem.dam.meraki.model;

public class Tienda {

    private String uid;
    private String Email;
    private String CIF;
    private String tienda;
    private Double Latitud;
    private Double Longitud;


    public Tienda() {
    }

    public Tienda(String uid, String email, String CIF, String tienda, Double latitud, Double longitud) {
        this.uid = uid;
        Email = email;
        this.CIF = CIF;
        this.tienda = tienda;
        Latitud = latitud;
        Longitud = longitud;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getCIF() {
        return CIF;
    }

    public void setCIF(String CIF) {
        this.CIF = CIF;
    }

    public String getTienda() {
        return tienda;
    }

    public void setTienda(String tienda) {
        this.tienda = tienda;
    }

    public Double getLatitud() {
        return Latitud;
    }

    public void setLatitud(Double latitud) {
        Latitud = latitud;
    }

    public Double getLongitud() {
        return Longitud;
    }

    public void setLongitud(Double longitud) {
        Longitud = longitud;
    }
}
