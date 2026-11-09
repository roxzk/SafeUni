package vista;

import dao.AlertaDAO;
import dao.RegistroDAO;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import modelo.*;
import util.ServicioEmergencia;

// Ventana del SOS: cuenta regresiva de 5 segundos (se puede cancelar), envío del aviso
// y opción de cancelar también la alerta que ya se envió
public class DialogoSOS extends JDialog {

    private Usuario usuario;
    private List<ContactoConfianza> contactos;
    private List<AlertaSOS> enviadas = new ArrayList<>();
    private AlertaDAO alertaDAO = new AlertaDAO();
    private RegistroDAO registroDAO = new RegistroDAO();

    private int segundos = 5;
    private boolean enviada = false;
    private Timer reloj;

    private JLabel lblEstado;
    private JTextArea txtDetalle;
    private BotonRedondeado btnCancelar;
    private BotonRedondeado btnCerrar;

    public DialogoSOS(JFrame padre, Usuario usuario, List<ContactoConfianza> contactos) {
        super(padre, "Alerta SOS", true);
        this.usuario = usuario;
        this.contactos = contactos;

        JPanel p = new JPanel(null);
        p.setBackground(Estilo.FONDO);
        p.setPreferredSize(new Dimension(340, 340));

        JLabel titulo = Estilo.etiqueta("ALERTA SOS", Estilo.TITULO, Estilo.NARANJA, 0, 14, 340, 36);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(titulo);

        lblEstado = Estilo.etiqueta("Enviando alerta en " + segundos + " s...", Estilo.SUBTITULO, Estilo.MORADO, 0, 56, 340, 26);
        lblEstado.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(lblEstado);

        txtDetalle = new JTextArea("Si fue un error, presiona CANCELAR SOS antes de que termine la cuenta regresiva.");
        txtDetalle.setEditable(false);
        txtDetalle.setLineWrap(true);
        txtDetalle.setWrapStyleWord(true);
        txtDetalle.setFont(Estilo.PEQUENA);
        JScrollPane scroll = new JScrollPane(txtDetalle);
        scroll.setBounds(20, 94, 300, 120);
        p.add(scroll);

        btnCancelar = new BotonRedondeado("CANCELAR SOS", Estilo.MORADO, Color.WHITE, null, 40);
        btnCancelar.setBounds(20, 228, 300, 46);
        btnCancelar.addActionListener(e -> cancelar());
        p.add(btnCancelar);

        btnCerrar = new BotonRedondeado("Cerrar", Color.WHITE, Estilo.MORADO, Estilo.MORADO, 40);
        btnCerrar.setBounds(20, 284, 300, 40);
        btnCerrar.setVisible(false);
        btnCerrar.addActionListener(e -> dispose());
        p.add(btnCerrar);

        setContentPane(p);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);    // solo se cierra con los botones
        setResizable(false);
        pack();
        setLocationRelativeTo(padre);

        // Cuenta regresiva: cada segundo baja el número y en cero se envía la alerta
        reloj = new Timer(1000, e -> {
            segundos--;
            if (segundos > 0) {
                lblEstado.setText("Enviando alerta en " + segundos + " s...");
            } else {
                reloj.stop();
                enviarAlerta();
            }
        });
        reloj.start();
    }

    // Se guarda una alerta por cada contacto y se le avisa por llamada o mensaje (según lo que eligió)
    private void enviarAlerta() {
        StringBuilder detalle = new StringBuilder();
        for (ContactoConfianza c : contactos) {
            AlertaSOS alerta = new AlertaSOS(usuario.getId(), usuario.getNombre(), c,
                    PanelMapa.LAT_USUARIO, PanelMapa.LON_USUARIO);
            alertaDAO.guardarSOS(alerta);
            enviadas.add(alerta);
            detalle.append("• ").append(ServicioEmergencia.contactar(c)).append("\n");
        }
        detalle.append("• ").append(ServicioEmergencia.avisarSeguridad()).append("\n");
        registroDAO.guardar(usuario.getId(), "SOS_ENVIADO");

        enviada = true;
        lblEstado.setText("Alerta enviada. Ayuda en camino");
        txtDetalle.setText(detalle.toString());
        btnCancelar.setText("Cancelar alerta (ya estoy bien)");
        btnCerrar.setVisible(true);
    }

    // Cancela el SOS: antes de enviarlo (no se avisa a nadie) o después (se avisa que fue falsa alarma)
    private void cancelar() {
        if (!enviada) {
            reloj.stop();
            registroDAO.guardar(usuario.getId(), "SOS_CANCELADO_ANTES_DE_ENVIAR");
            JOptionPane.showMessageDialog(this, "Alerta SOS cancelada. No se envió ningún aviso.");
            dispose();
            return;
        }

        int r = JOptionPane.showConfirmDialog(this,
                "¿Cancelar la alerta que ya se envió?\nSe avisará a tus contactos que fue una falsa alarma.",
                "Cancelar SOS", JOptionPane.YES_NO_OPTION);
        if (r != JOptionPane.YES_OPTION) {
            return;
        }
        StringBuilder detalle = new StringBuilder();
        for (AlertaSOS a : enviadas) {
            alertaDAO.cancelarSOS(a);
            detalle.append("• Falsa alarma: se avisó a ").append(a.getContacto().getNombre()).append(" que ya estás bien.\n");
        }
        registroDAO.guardar(usuario.getId(), "SOS_CANCELADO_DESPUES_DE_ENVIAR");
        lblEstado.setText("Alerta cancelada");
        txtDetalle.setText(detalle.toString());
        btnCancelar.setVisible(false);
    }
}