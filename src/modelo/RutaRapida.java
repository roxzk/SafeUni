
package modelo;

public class RutaRapida extends Ruta {

    public RutaRapida(String origen, String destino) {
        super(origen, destino);
    }

    @Override
    public String getTipo() { return "RAPIDA"; }

    // 10 minutos base + 3 por cada parada
    @Override
    public int calcularMinutos() { return 10 + 3 * getParadas().size(); }
}