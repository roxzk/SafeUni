package modelo;

// Clase PADRE (abstracta) de los tipos de usuario. Hereda de Persona.
public abstract class Usuario extends Persona {

    private String correo;
    private String documento;    // cédula cifrada (SHA-256)
    private String contrasena;   // contraseña cifrada (SHA-256)
    private boolean visible;     // "Compartir mi visibilidad"

    public Usuario(int id, String nombre, String correo, String telefono,
                   String documento, String contrasena, boolean visible) {
        super(id, nombre, telefono);
        this.correo = correo;
        this.documento = documento;
        this.contrasena = contrasena;
        this.visible = visible;
    }

    // Cada clase hija define su rol y sus permisos
    public abstract String getRol();
    public abstract String getPermisos();

    public String getCorreo() { return correo; }
    public String getDocumento() { return documento; }
    public String getContrasena() { return contrasena; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
}