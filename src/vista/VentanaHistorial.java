package vista;

import dao.RutaDAO;
import java.awt.Component;
import javax.swing.*;
import javax.swing.border.LineBorder;
import modelo.*;

// Historial de viajes (con las paradas de cada compañero)
public class VentanaHistorial extends VentanaBase {

    private RutaDAO rutaDAO = new RutaDAO();

    public VentanaHistorial(Usuario usuario) {
        super("SafeUni - Historial de viajes", usuario, "Inicio");
        armarVentana(true);
    }

    @Override
    protected JPanel crearContenido() {
        JPanel p = new JPanel(null);
        p.setBackground(Estilo.FONDO);
        p.add(Estilo.etiqueta("Historial de viajes", Estilo.TITULO, Estilo.MORADO, 16, 8, 348, 36));

        DefaultListModel<String> modelo = new DefaultListModel<>();
        for (Ruta r : rutaDAO.listarPorUsuario(usuario.getId())) {
            String tipo = r.getTipo().equals("SEGURA") ? "segura" : "rápida";
            String texto = "<html><b>" + r.getOrigen() + " → " + r.getDestino() + "</b><br>"
                    + r.getFecha() + "  ·  Ruta " + tipo + "  ·  " + r.calcularMinutos() + " min";
            for (PuntoParada parada : r.getParadas()) {
                texto += "<br>Parada: " + parada;
            }
            modelo.addElement(texto + "</html>");
        }

        if (modelo.isEmpty()) {
            p.add(Estilo.etiqueta("Aún no tienes viajes guardados.", Estilo.NORMAL, Estilo.GRIS, 16, 56, 348, 24));
        }
        JList<String> lista = new JList<>(modelo);
        lista.setFont(Estilo.PEQUENA);
        lista.setCellRenderer(new DefaultListCellRenderer() {      // deja un espacio alrededor de cada viaje
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object valor, int i, boolean sel, boolean foco) {
                JLabel etiqueta = (JLabel) super.getListCellRendererComponent(l, valor, i, sel, foco);
                etiqueta.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
                return etiqueta;
            }
        });
        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(new LineBorder(Estilo.BORDE, 1, true));
        scroll.setBounds(16, 90, 348, 400);
        p.add(scroll);

        BotonRedondeado btnVolver = new BotonRedondeado("Volver a Mi seguridad");
        btnVolver.setBounds(16, 500, 348, 46);
        btnVolver.addActionListener(e -> abrir("Inicio"));
        p.add(btnVolver);
        return p;
    }
}