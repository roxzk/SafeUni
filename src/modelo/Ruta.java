package modelo;

import java.util.ArrayList;
import java.util.List;

// Clase PADRE (abstracta) de las rutas
public abstract class Ruta {

    private int idUsuario;
    private String nombreUsuario = "";
    private String origen;
    private String destino;
    private String fecha = "";
    private List<PuntoParada> paradas = new ArrayList<>();

    public Ruta(String origen, String destino) {
        this.origen = origen;
        this.destino = destino;
    }

    public abstract String getTipo();          // "SEGURA" o "RAPIDA"
    public abstract int calcularMinutos();     // tiempo simulado

    public void agregarParada(PuntoParada parada) { paradas.add(parada); }

    public String getOrigen() { return origen; }
    public String getDestino() { return destino; }
    public List<PuntoParada> getParadas() { return paradas; }
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}