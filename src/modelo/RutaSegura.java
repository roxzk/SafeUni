package modelo;

public class RutaSegura extends Ruta {

    public RutaSegura(String origen, String destino) {
        super(origen, destino);
    }

    @Override
    public String getTipo() { return "SEGURA"; }

    // Es más larga porque evita las zonas de riesgo: 15 minutos base + 3 por cada parada
    @Override
    public int calcularMinutos() { return 15 + 3 * getParadas().size(); }
}