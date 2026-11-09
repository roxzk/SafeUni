
package modelo;

public class Administrador extends Usuario {

    public Administrador(int id, String nombre, String correo, String telefono,
                         String documento, String contrasena, boolean visible) {
        super(id, nombre, correo, telefono, documento, contrasena, visible);
    }

    @Override
    public String getRol() { return "ADMINISTRADOR"; }

    @Override
    public String getPermisos() {
        return "Todo lo anterior, más validar reportes y gestionar zonas y alertas (panel web).";
    }
}