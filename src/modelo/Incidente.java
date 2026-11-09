package modelo;

// Clase PADRE: un incidente reportado. Si no es de un tipo especial, es "Otra".
public class Incidente {

    private int idUsuario;
    private int idZona;
    private String descripcion;
    private String foto;        // ruta de la foto (puede ir vacía)
    private double latitud;
    private double longitud;

    public Incidente(int idUsuario, int idZona, String descripcion, String foto,
                     double latitud, double longitud) {
        this.idUsuario = idUsuario;
        this.idZona = idZona;
        this.descripcion = descripcion;
        this.foto = foto;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public String getCategoria() { return "Otra"; }

    public int getIdUsuario() { return idUsuario; }
    public int getIdZona() { return idZona; }
    public String getDescripcion() { return descripcion; }
    public String getFoto() { return foto; }
    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }
}