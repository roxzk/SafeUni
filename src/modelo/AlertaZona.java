package modelo;

// Alerta en tiempo real sobre una zona (por ejemplo: "Intento de robo cerca de la Biblioteca")
public class AlertaZona extends Alerta {

    private String zona;

    public AlertaZona(int id, String mensaje, String nivel, String fecha, String zona) {
        super(id, mensaje, nivel, fecha);
        this.zona = zona;
    }

    public String getZona() { return zona; }
}