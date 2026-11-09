package modelo;

// Clase PADRE de las alertas (de zona y de SOS)
public class Alerta {

    private int id;
    private String mensaje;
    private String nivel;     // BAJO, MEDIO o ALTO
    private String fecha;

    public Alerta(int id, String mensaje, String nivel, String fecha) {
        this.id = id;
        this.mensaje = mensaje;
        this.nivel = nivel;
        this.fecha = fecha;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getMensaje() { return mensaje; }
    public String getNivel() { return nivel; }
    public String getFecha() { return fecha; }
}