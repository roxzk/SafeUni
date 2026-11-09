package vista;

import dao.MensajeDAO;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.*;
import modelo.MensajeChat;
import modelo.Usuario;

// Chat sencillo entre dos compañeros (no muestra el teléfono de nadie)
public class DialogoChat extends JDialog {

    private Usuario usuario;
    private int idOtro;
    private MensajeDAO mensajeDAO = new MensajeDAO();
    private JTextArea areaChat;
    private JTextField txtMensaje;

    public DialogoChat(JFrame padre, Usuario usuario, int idOtro, String nombreOtro) {
        super(padre, "Chat con " + nombreOtro, true);
        this.usuario = usuario;
        this.idOtro = idOtro;

        JPanel p = new JPanel(null);
        p.setBackground(Estilo.FONDO);
        p.setPreferredSize(new Dimension(340, 380));

        areaChat = new JTextArea();
        areaChat.setEditable(false);
        areaChat.setLineWrap(true);
        areaChat.setWrapStyleWord(true);
        areaChat.setFont(Estilo.NORMAL);
        JScrollPane scroll = new JScrollPane(areaChat);
        scroll.setBounds(10, 10, 320, 306);
        p.add(scroll);

        txtMensaje = new JTextField();
        Estilo.campo(txtMensaje, 10, 328, 232, 38);
        p.add(txtMensaje);

        BotonRedondeado btnEnviar = new BotonRedondeado("Enviar", Estilo.NARANJA, Color.WHITE, null, 30);
        btnEnviar.setBounds(250, 328, 80, 38);
        p.add(btnEnviar);

        btnEnviar.addActionListener(e -> enviar());
        txtMensaje.addActionListener(e -> enviar());

        setContentPane(p);
        setResizable(false);
        pack();
        setLocationRelativeTo(padre);
        cargarMensajes();
    }

    private void cargarMensajes() {
        StringBuilder sb = new StringBuilder();
        for (MensajeChat m : mensajeDAO.listarConversacion(usuario.getId(), idOtro)) {
            String quien = m.getIdRemitente() == usuario.getId() ? "Tú" : m.getRemitente();
            sb.append(quien).append(" (").append(m.getHora()).append("): ").append(m.getTexto()).append("\n");
        }
        areaChat.setText(sb.toString());
    }

    private void enviar() {
        String texto = txtMensaje.getText().trim();
        if (texto.isEmpty()) {
            return;
        }
        mensajeDAO.guardar(usuario.getId(), idOtro, texto);
        txtMensaje.setText("");
        cargarMensajes();
    }
}