package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import modelo.Usuario;

// Clase PADRE de todas las ventanas: tamaño de celular, encabezado (logo + perfil) y barra de navegación
public abstract class VentanaBase extends JFrame {

    protected Usuario usuario;
    private String pestanaActiva;

    public VentanaBase(String titulo, Usuario usuario, String pestanaActiva) {
        super(titulo);
        this.usuario = usuario;
        this.pestanaActiva = pestanaActiva;
        getContentPane().setPreferredSize(new Dimension(380, 680));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
    }

    // Cada ventana hija arma aquí su contenido
    protected abstract JPanel crearContenido();

    // Cada hija llama a este método al final de su constructor
    protected void armarVentana(boolean conMenu) {
        setLayout(new BorderLayout());
        if (conMenu) {
            add(crearEncabezado(), BorderLayout.NORTH);
        }
        add(crearContenido(), BorderLayout.CENTER);
        if (conMenu) {
            add(crearBarra(), BorderLayout.SOUTH);
        }
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel crearEncabezado() {
        JPanel p = new JPanel(null);
        p.setBackground(Color.WHITE);
        p.setPreferredSize(new Dimension(380, 54));
        p.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Estilo.BORDE));

        LogoSafeUni logo = new LogoSafeUni();
        logo.setBounds(16, 9, 32, 34);
        p.add(logo);
        p.add(Estilo.etiqueta("SafeUni", new Font("SansSerif", Font.BOLD, 18), Estilo.MORADO, 56, 11, 150, 30));

        BotonRedondeado perfil = new BotonRedondeado("", new Color(228, 222, 240), Estilo.MORADO, null, 36);
        perfil.setIcono("perfil");
        perfil.setBounds(326, 8, 36, 36);
        perfil.addActionListener(e -> abrir("Perfil"));
        p.add(perfil);
        return p;
    }

    private JPanel crearBarra() {
        JPanel barra = new JPanel(new GridLayout(1, 5));
        barra.setBackground(Color.WHITE);
        barra.setPreferredSize(new Dimension(380, 52));
        barra.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Estilo.BORDE));

        String[] pestanas = {"Inicio", "Rutas", "Camina", "Alertas", "Perfil"};
        for (String nombre : pestanas) {
            JButton b = new JButton(nombre);
            b.setFont(Estilo.PEQUENA);
            b.setContentAreaFilled(false);
            b.setFocusPainted(false);
            if (nombre.equals(pestanaActiva)) {          // la pestaña activa se resalta en naranja
                b.setForeground(Estilo.NARANJA);
                b.setBorder(BorderFactory.createMatteBorder(3, 0, 0, 0, Estilo.NARANJA));
            } else {
                b.setForeground(Estilo.GRIS);
                b.setBorder(BorderFactory.createMatteBorder(3, 0, 0, 0, Color.WHITE));
            }
            b.addActionListener(e -> abrir(nombre));
            barra.add(b);
        }
        return barra;
    }

    // Abre la ventana que corresponde a cada pestaña
    protected void abrir(String destino) {
        VentanaBase nueva;
        switch (destino) {
            case "Rutas":
                nueva = new VentanaRutasSeguras(usuario);
                break;
            case "Camina":
                nueva = new VentanaCaminaConmigo(usuario);
                break;
            case "Alertas":
                nueva = new VentanaPrevencion(usuario);
                break;
            case "Perfil":
                nueva = new VentanaPerfil(usuario);
                break;
            default:
                nueva = new VentanaPrincipal(usuario);
        }
        irA(nueva);
    }

    // Muestra la nueva ventana y cierra la actual
    protected void irA(VentanaBase nueva) {
        nueva.setVisible(true);
        dispose();
    }

    protected void aviso(String texto) {
        JOptionPane.showMessageDialog(this, texto, "SafeUni", JOptionPane.INFORMATION_MESSAGE);
    }

    protected void error(String texto) {
        JOptionPane.showMessageDialog(this, texto, "SafeUni", JOptionPane.ERROR_MESSAGE);
    }
}