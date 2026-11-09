package vista;

import dao.ContactoDAO;
import dao.UsuarioDAO;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.*;
import modelo.*;
import util.Seguridad;

// Interfaz de registro: datos del usuario + su primer familiar para el SOS
public class VentanaRegistro extends VentanaBase {

    private JTextField txtNombre;
    private JTextField txtDocumento;
    private JTextField txtCorreo;
    private JTextField txtTelefono;
    private JComboBox<String> cmbRol;
    private JPasswordField txtClave;
    private JPasswordField txtClave2;
    private JTextField txtNomFamiliar;
    private JComboBox<String> cmbParentesco;
    private JTextField txtTelFamiliar;
    private JRadioButton rbMensaje;
    private JRadioButton rbLlamada;
    private JCheckBox chkPolitica;
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private ContactoDAO contactoDAO = new ContactoDAO();

    public VentanaRegistro() {
        super("SafeUni - Crear cuenta", null, "");
        armarVentana(false);
    }

    @Override
    protected JPanel crearContenido() {
        JPanel form = new JPanel(null);
        form.setBackground(Estilo.FONDO);

        form.add(Estilo.etiqueta("Crear cuenta", Estilo.TITULO, Estilo.MORADO, 20, 20, 320, 36));
        form.add(Estilo.etiqueta("Regístrate con tu correo institucional", Estilo.PEQUENA, Estilo.GRIS, 20, 58, 320, 18));

        txtNombre = new JTextField();
        txtDocumento = new JTextField();
        txtCorreo = new JTextField();
        txtTelefono = new JTextField();
        cmbRol = new JComboBox<>(new String[]{"Estudiante", "Docente", "Personal administrativo"});
        txtClave = new JPasswordField();
        txtClave2 = new JPasswordField();

        int y = 90;
        y = agregar(form, "Nombre completo", txtNombre, y);
        y = agregar(form, "Número de documento (cédula)", txtDocumento, y);
        y = agregar(form, "Correo institucional", txtCorreo, y);
        y = agregar(form, "Teléfono celular (10 dígitos)", txtTelefono, y);
        y = agregar(form, "Tipo de usuario", cmbRol, y);
        y = agregar(form, "Contraseña (mínimo 8 caracteres)", txtClave, y);
        y = agregar(form, "Confirmar contraseña", txtClave2, y);

        // El familiar al que se conecta el botón SOS
        form.add(Estilo.etiqueta("Contacto de emergencia (familiar)", Estilo.SUBTITULO, Estilo.MORADO, 20, y + 4, 320, 22));
        form.add(Estilo.etiqueta("Este familiar recibirá tu alerta SOS.", Estilo.PEQUENA, Estilo.GRIS, 20, y + 26, 320, 18));
        y += 54;

        txtNomFamiliar = new JTextField();
        cmbParentesco = new JComboBox<>(new String[]{"Mamá", "Papá", "Hermano/a", "Abuelo/a", "Tío/a", "Pareja", "Otro familiar"});
        txtTelFamiliar = new JTextField();
        y = agregar(form, "Nombre del familiar", txtNomFamiliar, y);
        y = agregar(form, "Parentesco", cmbParentesco, y);
        y = agregar(form, "Teléfono del familiar (10 dígitos)", txtTelFamiliar, y);

        form.add(Estilo.etiqueta("¿Cómo quieres que SafeUni avise a tu familiar?", Estilo.PEQUENA, Estilo.GRIS, 20, y, 320, 18));
        rbMensaje = new JRadioButton("Mensaje", true);
        rbLlamada = new JRadioButton("Llamada");
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbMensaje);
        grupo.add(rbLlamada);
        rbMensaje.setOpaque(false);
        rbLlamada.setOpaque(false);
        rbMensaje.setBounds(20, y + 22, 150, 26);
        rbLlamada.setBounds(180, y + 22, 150, 26);
        form.add(rbMensaje);
        form.add(rbLlamada);
        y += 58;

        chkPolitica = new JCheckBox("<html>Acepto la política de privacidad y el tratamiento de mis datos, "
                + "incluida la ubicación (Ley 1581 de 2012).</html>");
        chkPolitica.setFont(Estilo.PEQUENA);
        chkPolitica.setOpaque(false);
        chkPolitica.setBounds(20, y, 320, 56);
        form.add(chkPolitica);
        y += 66;

        BotonRedondeado btnRegistrar = new BotonRedondeado("Registrarme");
        btnRegistrar.setBounds(20, y, 320, 46);
        btnRegistrar.addActionListener(e -> registrar());
        form.add(btnRegistrar);
        y += 56;

        JButton btnVolver = Estilo.enlace("Volver al inicio de sesión", 20, y, 320, 26);
        btnVolver.addActionListener(e -> irA(new VentanaLogin()));
        form.add(btnVolver);

        // El formulario es largo, por eso va dentro de un JScrollPane
        form.setPreferredSize(new Dimension(360, y + 40));
        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        JPanel envoltura = new JPanel(new BorderLayout());
        envoltura.add(scroll, BorderLayout.CENTER);
        return envoltura;
    }

    // Pone la etiqueta y el campo, y devuelve la siguiente posición vertical
    private int agregar(JPanel p, String titulo, JComponent campo, int y) {
        p.add(Estilo.etiqueta(titulo, Estilo.PEQUENA, Estilo.GRIS, 20, y, 320, 18));
        Estilo.campo(campo, 20, y + 20, 320, 36);
        p.add(campo);
        return y + 66;
    }

    private void registrar() {
        String nombre = txtNombre.getText().trim();
        String documento = txtDocumento.getText().trim();
        String correo = txtCorreo.getText().trim().toLowerCase();
        String telefono = txtTelefono.getText().trim();
        String clave = new String(txtClave.getPassword());
        String clave2 = new String(txtClave2.getPassword());
        String nomFamiliar = txtNomFamiliar.getText().trim();
        String telFamiliar = txtTelFamiliar.getText().trim();

        // Validaciones
        if (nombre.isEmpty() || documento.isEmpty() || correo.isEmpty() || telefono.isEmpty()
                || clave.isEmpty() || nomFamiliar.isEmpty() || telFamiliar.isEmpty()) {
            error("Completa todos los campos obligatorios.");
            return;
        }
        if (!Seguridad.documentoValido(documento)) {
            error("El documento debe tener solo números (entre 6 y 12 dígitos).");
            return;
        }
        if (!Seguridad.correoInstitucional(correo)) {
            error("Usa tu correo institucional (ejemplo: nombre" + Seguridad.DOMINIO + ").");
            return;
        }
        if (!Seguridad.telefonoValido(telefono) || !Seguridad.telefonoValido(telFamiliar)) {
            error("Los teléfonos deben tener 10 dígitos.");
            return;
        }
        if (clave.length() < 8) {
            error("La contraseña debe tener mínimo 8 caracteres.");
            return;
        }
        if (!clave.equals(clave2)) {
            error("Las contraseñas no coinciden.");
            return;
        }
        if (!chkPolitica.isSelected()) {
            error("Debes aceptar la política de privacidad para registrarte.");
            return;
        }

        String documentoCifrado = Seguridad.cifrar(documento);
        if (usuarioDAO.existeCorreo(correo)) {
            error("Ese correo ya está registrado.");
            return;
        }
        if (usuarioDAO.existeDocumento(documentoCifrado)) {
            error("Ese documento ya está registrado.");
            return;
        }

        // Se crea el objeto de la clase hija que corresponde al tipo de usuario
        String claveCifrada = Seguridad.cifrar(clave);
        String tipo = (String) cmbRol.getSelectedItem();
        Usuario nuevo;
        if (tipo.equals("Docente")) {
            nuevo = new Docente(0, nombre, correo, telefono, documentoCifrado, claveCifrada, false);
        } else if (tipo.equals("Personal administrativo")) {
            nuevo = new PersonalAdministrativo(0, nombre, correo, telefono, documentoCifrado, claveCifrada, false);
        } else {
            nuevo = new Estudiante(0, nombre, correo, telefono, documentoCifrado, claveCifrada, false);
        }

        int idNuevo = usuarioDAO.registrar(nuevo);
        if (idNuevo == -1) {
            error("No se pudo crear la cuenta. Revisa la conexión con la base de datos.");
            return;
        }

        String medio = rbLlamada.isSelected() ? "LLAMADA" : "MENSAJE";
        ContactoConfianza familiar = new ContactoConfianza(0, idNuevo, nomFamiliar, telFamiliar,
                (String) cmbParentesco.getSelectedItem(), medio);
        contactoDAO.guardar(familiar);

        aviso("¡Cuenta creada! Ya puedes iniciar sesión.");
        irA(new VentanaLogin());
    }
}