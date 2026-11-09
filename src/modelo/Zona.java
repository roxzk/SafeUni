package modelo;

public class Zona {

    private int id;
    private String nombre;
    private String localidad;
    private String upz;
    private double latitud;
    private double longitud;
    private int totalIncidentes;   // incidentes validados en los últimos 30 días

    public Zona(int id, String nombre, String localidad, String upz,
                double latitud, double longitud, int totalIncidentes) {
        this.id = id;
        this.nombre = nombre;
        this.localidad = localidad;
        this.upz = upz;
        this.latitud = latitud;
        this.longitud = longitud;
        this.totalIncidentes = totalIncidentes;
    }

    // Regla del SRS: Bajo 0 a 2, Medio 3 a 5, Alto 6 o más
    public String getNivel() {
        if (totalIncidentes <= 2) {
            return "Bajo";
        } else if (totalIncidentes <= 5) {
            return "Medio";
        }
        return "Alto";
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getLocalidad() { return localidad; }
    public String getUpz() { return upz; }
    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }
    public int getTotalIncidentes() { return totalIncidentes; }

    @Override
    public String toString() { return nombre; }
}