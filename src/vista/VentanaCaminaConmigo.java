package vista;

import dao.RutaDAO;
import dao.UsuarioDAO;
import java.awt.Color;
import java.awt.Font;
import java.util.List;
import javax.swing.*;
import modelo.*;

// Pantalla 3: Camina conmigo (rutas de otros compañeros, chat y "Compartir mi visibilidad")
public class VentanaCaminaConmigo extends VentanaBase {

    private RutaDAO rutaDAO = new RutaDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private JLabel lblModo;

    public VentanaCaminaConmigo(Usuario usuario) {
        super("SafeUni - Camina conmigo", usuario, "Camina");
        armarVentana(true);
    }

    @Override
    protected JPanel crearContenido() {
        JPanel p = new JPanel(null);
        p.setBackground(Estilo.FONDO);
        p.add(Estilo.etiqueta("Camina conmigo", Estilo.TITULO, Estilo.MORADO, 16, 8, 348, 36));
        p.add(Estilo.etiqueta("Rutas sugeridas", Estilo.SUBTITULO, Estilo.MORADO, 16, 50, 348, 20));

        List<Ruta> sugeridas = rutaDAO.listarSugeridas(usuario.getId());
        if (sugeridas.isEmpty()) {
            p.add(Estilo.etiqueta("Por ahora no hay compañeros visibles con rutas cercanas.",
                    Estilo.PEQUENA, Estilo.GRIS, 16, 78, 348, 20));
        }
        int y = 76;
        for (Ruta r : sugeridas) {
            p.add(crearTarjeta(r, y));
            y += 68;
        }

        p.add(Estilo.etiqueta("Usuarios cercanos", Estilo.SUBTITULO, Estilo.MORADO, 16, 290, 170, 22));
        JCheckBox chkVisible = new JCheckBox("Compartir mi visibilidad", usuario.isVisible());
        chkVisible.setFont(Estilo.PEQUENA);
        chkVisible.setForeground(Estilo.MORADO);
        chkVisible.setOpaque(false);
        chkVisible.setBounds(190, 290, 176, 22);
        chkVisible.addActionListener(e -> cambiarVisibilidad(chkVisible.isSelected()));
        p.add(chkVisible);

        lblModo = Estilo.etiqueta(textoModo(usuario.isVisible()), Estilo.PEQUENA, Estilo.GRIS, 16, 312, 348, 16);
        p.add(lblModo);

        PanelMapa mapa = new PanelMapa();
        mapa.setBounds(16, 334, 348, 190);
        mapa.setPinesCercanos(usuarioDAO.listarVisibles(usuario.getId()).size());
        mapa.setMostrarUsuario(true);
        mapa.setRuta(true, false, 0);
        p.add(mapa);
        return p;
    }

    // Tarjeta con la foto (inicial), el destino, la hora y el botón de chat
    private JPanel crearTarjeta(Ruta r, int y) {
        JPanel t = Estilo.tarjeta(16, y, 348, 62);
        String inicial = r.getNombreUsuario().isEmpty() ? "?" : r.getNombreUsuario().substring(0, 1);
        t.add(Estilo.avatar(inicial, 10, 9, 42));
        t.add(Estilo.etiqueta("Va hacia " + r.getDestino(), new Font("SansSerif", Font.BOLD, 12),
                Estilo.MORADO, 62, 8, 166, 22));
        t.add(Estilo.etiqueta(r.getNombreUsuario() + "  ·  " + r.getFecha(), Estilo.PEQUENA, Estilo.GRIS, 62, 32, 166, 18));

        BotonRedondeado chat = new BotonRedondeado("Conectar por chat", Estilo.NARANJA, Color.WHITE, null, 30);
        chat.setFont(new Font("SansSerif", Font.BOLD, 11));
        chat.setBounds(232, 16, 110, 30);
        chat.addActionListener(e -> new DialogoChat(this, usuario, r.getIdUsuario(), r.getNombreUsuario()).setVisible(true));
        t.add(chat);
        return t;
    }

    private void cambiarVisibilidad(boolean visible) {
        if (usuarioDAO.cambiarVisibilidad(usuario.getId(), visible)) {
            usuario.setVisible(visible);
            lblModo.setText(textoModo(visible));
        }
    }

    private String textoModo(boolean visible) {
        return visible ? "Tus compañeros cercanos pueden verte en el mapa" : "Modo invisible activado";
    }
}