
package modelo;

public class Docente extends Usuario {

    public Docente(int id, String nombre, String correo, String telefono,
                   String documento, String contrasena, boolean visible) {
        super(id, nombre, correo, telefono, documento, contrasena, visible);
    }

    @Override
    public String getRol() { return "DOCENTE"; }

    @Override
    public String getPermisos() {
        return "Reportar incidentes, ver el mapa, planear rutas y usar el SOS.";
    }
}