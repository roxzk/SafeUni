
package modelo;

public class PersonalAdministrativo extends Usuario {

    public PersonalAdministrativo(int id, String nombre, String correo, String telefono,
                                  String documento, String contrasena, boolean visible) {
        super(id, nombre, correo, telefono, documento, contrasena, visible);
    }

    @Override
    public String getRol() { return "ADMINISTRATIVO"; }

    @Override
    public String getPermisos() {
        return "Reportar incidentes, ver el mapa, planear rutas y usar el SOS.";
    }
}