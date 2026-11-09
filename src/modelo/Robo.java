
package modelo;

public class Robo extends Incidente {

    public Robo(int idUsuario, int idZona, String descripcion, String foto, double latitud, double longitud) {
        super(idUsuario, idZona, descripcion, foto, latitud, longitud);
    }

    @Override
    public String getCategoria() { return "Robo"; }
}