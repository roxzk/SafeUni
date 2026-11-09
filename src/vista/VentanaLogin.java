package vista;

import dao.RegistroDAO;
import dao.UsuarioDAO;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import modelo.Usuario;
import util.Seguridad;

// Pantalla 1: inicio de sesión
public class VentanaLogin extends VentanaBase {

    private JTextField txtUsuario;
    private JPasswordField txtClave;
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private RegistroDAO registroDAO = new RegistroDAO();

    public VentanaLogin() {
        super("SafeUni - Iniciar sesión", null, "");
        armarVentana(false);
    }

    @Override
    protected JPanel crearContenido() {
        JPanel p = new JPanel(null);
        p.setBackground(Estilo.FONDO);

        LogoSafeUni logo = new LogoSafeUni();
        logo.setBounds(100, 40, 48, 52);
        p.add(logo);
        p.add(Estilo.etiqueta("SafeUni", new Font("SansSerif", Font.BOLD, 28), Estilo.MORADO, 158, 48, 150, 36));

        JLabel bienvenido = Estilo.etiqueta("Bienvenido.", new Font("SansSerif", Font.BOLD, 34), Estilo.MORADO, 0, 120, 380, 44);
        bienvenido.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(bienvenido);

        JLabel subtitulo = Estilo.etiqueta("Inicia sesión o regístrate", Estilo.NORMAL, Estilo.GRIS, 0, 166, 380, 24);
        subtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(subtitulo);

        p.add(Estilo.etiqueta("Usuario o correo institucional", Estilo.PEQUENA, Estilo.GRIS, 30, 212, 320, 18));
        txtUsuario = new JTextField();
        Estilo.campo(txtUsuario, 30, 232, 320, 40);
        p.add(txtUsuario);

        p.add(Estilo.etiqueta("Contraseña", Estilo.PEQUENA, Estilo.GRIS, 30, 282, 320, 18));
        txtClave = new JPasswordField();
        Estilo.campo(txtClave, 30, 302, 320, 40);
        p.add(txtClave);

        BotonRedondeado btnIniciar = new BotonRedondeado("Iniciar sesión");
        btnIniciar.setBounds(30, 364, 320, 46);
        p.add(btnIniciar);

        JButton btnRegistro = Estilo.enlace("¿No tienes una cuenta? Regístrate", 30, 420, 320, 26);
        p.add(btnRegistro);

        JSeparator linea1 = new JSeparator();
        linea1.setBounds(30, 478, 120, 2);
        p.add(linea1);
        JLabel entraCon = Estilo.etiqueta("o entra con", Estilo.PEQUENA, Estilo.GRIS, 0, 470, 380, 18);
        entraCon.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(entraCon);
        JSeparator linea2 = new JSeparator();
        linea2.setBounds(230, 478, 120, 2);
        p.add(linea2);

        BotonRedondeado btnInstitucional = new BotonRedondeado("Acceso institucional", Color.WHITE, Estilo.MORADO, Estilo.MORADO, 30);
        btnInstitucional.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnInstitucional.setBounds(30, 500, 155, 44);
        p.add(btnInstitucional);

        BotonRedondeado btnGoogle = new BotonRedondeado("Continuar con Google", Color.WHITE, Estilo.GRIS, Estilo.BORDE, 30);
        btnGoogle.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnGoogle.setBounds(195, 500, 155, 44);
        p.add(btnGoogle);

        btnIniciar.addActionListener(e -> iniciarSesion());
        txtClave.addActionListener(e -> iniciarSesion());
        btnRegistro.addActionListener(e -> irA(new VentanaRegistro()));
        btnInstitucional.addActionListener(e -> aviso("El acceso institucional se conectará con el sistema de cuentas de la universidad en una versión posterior."));
        btnGoogle.addActionListener(e -> aviso("El acceso con Google (OAuth 2.0) se agregará en una versión posterior."));
        return p;
    }

    private void iniciarSesion() {
        String entrada = txtUsuario.getText().trim().toLowerCase();
        String clave = new String(txtClave.getPassword());
        if (entrada.isEmpty() || clave.isEmpty()) {
            aviso("Completa usuario y contraseña");
            return;
        }
        // Si solo escribe el usuario (sin @), se le agrega el dominio institucional
        if (!entrada.contains("@")) {
            entrada = entrada + Seguridad.DOMINIO;
        }
        try {
            Usuario u = usuarioDAO.iniciarSesion(entrada, clave);
            if (u == null) {
                error("Usuario o contraseña incorrectos");
                return;
            }
            registroDAO.guardar(u.getId(), "INICIO_SESION");
            irA(new VentanaPrincipal(u));
        } catch (IllegalStateException ex) {      // cuenta bloqueada
            error(ex.getMessage());
        }
    }
}