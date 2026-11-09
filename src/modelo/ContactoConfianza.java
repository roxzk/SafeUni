package modelo;

// Clase HIJA de Persona: el familiar (o amigo) que recibe la alerta SOS
public class ContactoConfianza extends Persona {

    private int idUsuario;
    private String parentesco;
    private String medio;      // "LLAMADA" o "MENSAJE"

    public ContactoConfianza(int id, int idUsuario, String nombre, String telefono,
                             String parentesco, String medio) {
        super(id, nombre, telefono);
        this.idUsuario = idUsuario;
        this.parentesco = parentesco;
        this.medio = medio;
    }

    public int getIdUsuario() { return idUsuario; }
    public String getParentesco() { return parentesco; }
    public String getMedio() { return medio; }

    public boolean esLlamada() {
        return medio.equals("LLAMADA");
    }

    @Override
    public String toString() {
        return getNombre() + " (" + parentesco + ") - " + getTelefono() + " - " + (esLlamada() ? "Llamada" : "Mensaje");
    }
}