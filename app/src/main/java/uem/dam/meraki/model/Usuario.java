package uem.dam.meraki.model;

public class Usuario {

    private String uid;
    private String Nombre;
    private String Email;

    public Usuario() {
    }

    public Usuario(String uid, String nombre, String email) {
        this.uid = uid;
        Nombre = nombre;
        Email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
