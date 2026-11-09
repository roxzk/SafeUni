package modelo;

public class MensajeChat {

    private int idRemitente;
    private String remitente;
    private String texto;
    private String hora;

    public MensajeChat(int idRemitente, String remitente, String texto, String hora) {
        this.idRemitente = idRemitente;
        this.remitente = remitente;
        this.texto = texto;
        this.hora = hora;
    }

    public int getIdRemitente() { return idRemitente; }
    public String getRemitente() { return remitente; }
    public String getTexto() { return texto; }
    public String getHora() { return hora; }
}