
package modelo;

public class PersonaSospechosa extends Incidente {

    public PersonaSospechosa(int idUsuario, int idZona, String descripcion, String foto, double latitud, double longitud) {
        super(idUsuario, idZona, descripcion, foto, latitud, longitud);
    }

    @Override
    public String getCategoria() { return "Persona sospechosa"; }
}