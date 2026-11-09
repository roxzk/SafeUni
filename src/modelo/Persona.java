package modelo;

// Clase PADRE: lo que tienen en común un usuario y un contacto de confianza
public class Persona {

    private int id;
    private String nombre;
    private String telefono;

    public Persona(int id, String nombre, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }
}