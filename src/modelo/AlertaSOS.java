package modelo;

// Alerta de emergencia: siempre es de nivel ALTO y va dirigida a un contacto de confianza
public class AlertaSOS extends Alerta {

    private int idUsuario;
    private ContactoConfianza contacto;
    private double latitud;
    private double longitud;
    private String estado;     // ENVIADA o CANCELADA

    public AlertaSOS(int idUsuario, String nombreUsuario, ContactoConfianza contacto,
                     double latitud, double longitud) {
        super(0, "ALERTA SOS: " + nombreUsuario + " necesita ayuda. Ubicación: https://www.google.com/maps?q="
                + latitud + "," + longitud, "ALTO", "");
        this.idUsuario = idUsuario;
        this.contacto = contacto;
        this.latitud = latitud;
        this.longitud = longitud;
        this.estado = "ENVIADA";
    }

    public int getIdUsuario() { return idUsuario; }
    public ContactoConfianza getContacto() { return contacto; }
    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}