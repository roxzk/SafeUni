package vista;

import dao.ContactoDAO;
import java.awt.Color;
import java.awt.Font;
import javax.swing.*;
import javax.swing.border.LineBorder;
import modelo.*;
import util.Seguridad;

// Perfil del usuario y sus contactos de confianza (máximo 5)
public class VentanaPerfil extends VentanaBase {

    private ContactoDAO contactoDAO = new ContactoDAO();
    private DefaultListModel<ContactoConfianza> modelo = new DefaultListModel<>();
    private JList<ContactoConfianza> lista;

    public VentanaPerfil(Usuario usuario) {
        super("SafeUni - Mi perfil", usuario, "Perfil");
        armarVentana(true);
    }

    @Override
    protected JPanel crearContenido() {
        JPanel p = new JPanel(null);
        p.setBackground(Estilo.FONDO);
        p.add(Estilo.etiqueta("Mi perfil", Estilo.TITULO, Estilo.MORADO, 16, 8, 348, 36));

        // Los datos del usuario: getRol() y getPermisos() cambian según la clase hija
        String rol = usuario.getRol().substring(0, 1) + usuario.getRol().substring(1).toLowerCase();
        JPanel tarjeta = Estilo.tarjeta(16, 50, 348, 130);
        tarjeta.add(Estilo.etiqueta(usuario.getNombre(), Estilo.SUBTITULO, Estilo.MORADO, 14, 8, 320, 22));
        tarjeta.add(Estilo.etiqueta(usuario.getCorreo(), Estilo.PEQUENA, Estilo.GRIS, 14, 32, 320, 18));
        tarjeta.add(Estilo.etiqueta("Tipo de usuario: " + rol, Estilo.PEQUENA, Estilo.GRIS, 14, 52, 320, 18));
        JLabel permisos = Estilo.etiqueta("<html>Permisos: " + usuario.getPermisos() + "</html>", Estilo.PEQUENA, Estilo.GRIS, 14, 74, 320, 46);
        permisos.setVerticalAlignment(SwingConstants.TOP);
        tarjeta.add(permisos);
        p.add(tarjeta);

        p.add(Estilo.etiqueta("Contactos de confianza (máximo 5)", Estilo.SUBTITULO, Estilo.MORADO, 16, 190, 348, 20));
        lista = new JList<>(modelo);
        lista.setFont(Estilo.PEQUENA);
        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(new LineBorder(Estilo.BORDE, 1, true));
        scroll.setBounds(16, 214, 348, 140);
        p.add(scroll);
        cargarContactos();

        BotonRedondeado btnAgregar = new BotonRedondeado("Agregar contacto", Estilo.NARANJA, Color.WHITE, null, 30);
        btnAgregar.setBounds(16, 362, 170, 38);
        btnAgregar.addActionListener(e -> agregarContacto());
        p.add(btnAgregar);

        BotonRedondeado btnEliminar = new BotonRedondeado("Eliminar", Color.WHITE, Estilo.MORADO, Estilo.MORADO, 30);
        btnEliminar.setBounds(194, 362, 170, 38);
        btnEliminar.addActionListener(e -> eliminarContacto());
        p.add(btnEliminar);

        JLabel nota = Estilo.etiqueta("<html>Cuando uses el SOS, SafeUni avisará a estos contactos por llamada o por mensaje.</html>",
                Estilo.PEQUENA, Estilo.GRIS, 16, 406, 348, 40);
        nota.setVerticalAlignment(SwingConstants.TOP);
        p.add(nota);

        BotonRedondeado btnSalir = new BotonRedondeado("Cerrar sesión", Color.WHITE, Estilo.MORADO, Estilo.MORADO, 40);
        btnSalir.setBounds(16, 458, 348, 44);
        btnSalir.addActionListener(e -> irA(new VentanaLogin()));
        p.add(btnSalir);
        return p;
    }

    private void cargarContactos() {
        modelo.clear();
        for (ContactoConfianza c : contactoDAO.listar(usuario.getId())) {
            modelo.addElement(c);
        }
    }

    private void agregarContacto() {
        if (modelo.size() >= 5) {
            aviso("Puedes tener máximo 5 contactos de confianza.");
            return;
        }
        JTextField txtNombre = new JTextField();
        JComboBox<String> cmbParentesco = new JComboBox<>(
                new String[]{"Mamá", "Papá", "Hermano/a", "Abuelo/a", "Tío/a", "Pareja", "Amigo/a", "Otro familiar"});
        JTextField txtTelefono = new JTextField();
        JComboBox<String> cmbMedio = new JComboBox<>(new String[]{"Mensaje", "Llamada"});
        Object[] campos = {"Nombre:", txtNombre, "Parentesco:", cmbParentesco,
            "Teléfono (10 dígitos):", txtTelefono, "¿Cómo avisar en un SOS?", cmbMedio};

        int r = JOptionPane.showConfirmDialog(this, campos, "Agregar contacto de confianza",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) {
            return;
        }
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        if (nombre.isEmpty() || !Seguridad.telefonoValido(telefono)) {
            error("Escribe el nombre y un teléfono de 10 dígitos.");
            return;
        }
        String medio = cmbMedio.getSelectedItem().equals("Llamada") ? "LLAMADA" : "MENSAJE";
        ContactoConfianza nuevo = new ContactoConfianza(0, usuario.getId(), nombre, telefono,
                (String) cmbParentesco.getSelectedItem(), medio);
        if (contactoDAO.guardar(nuevo)) {
            cargarContactos();
        } else {
            error("No se pudo guardar el contacto.");
        }
    }

    private void eliminarContacto() {
        ContactoConfianza elegido = lista.getSelectedValue();
        if (elegido == null) {
            aviso("Selecciona un contacto de la lista.");
            return;
        }
        String pregunta = "¿Eliminar a " + elegido.getNombre() + "?";
        if (modelo.size() == 1) {
            pregunta += "\nEs tu único contacto: sin contactos no podrás usar el SOS.";
        }
        int r = JOptionPane.showConfirmDialog(this, pregunta, "Eliminar contacto", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION && contactoDAO.eliminar(elegido.getId())) {
            cargarContactos();
        }
    }
}