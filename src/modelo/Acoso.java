
package modelo;

public class Acoso extends Incidente {

    public Acoso(int idUsuario, int idZona, String descripcion, String foto, double latitud, double longitud) {
        super(idUsuario, idZona, descripcion, foto, latitud, longitud);
    }

    @Override
    public String getCategoria() { return "Acoso"; }
}