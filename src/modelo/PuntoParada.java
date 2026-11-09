package modelo;

// Punto de parada de una ruta, para recoger o esperar a un compañero o acompañante
public class PuntoParada {

    private String lugar;
    private String companero;

    public PuntoParada(String lugar, String companero) {
        this.lugar = lugar;
        this.companero = companero;
    }

    public String getLugar() { return lugar; }
    public String getCompanero() { return companero; }

    @Override
    public String toString() {
        return lugar + " (con " + companero + ")";
    }
}