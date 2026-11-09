package vista;

import dao.IncidenteDAO;
import dao.RegistroDAO;
import dao.ZonaDAO;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.util.List;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import modelo.*;

// Pantalla 5: Reporte de incidentes
public class VentanaReporte extends VentanaBase {

    private JTextArea txtDescripcion;
    private JRadioButton rbRobo;
    private JRadioButton rbAcoso;
    private JRadioButton rbSospechosa;
    private JRadioButton rbOtra;
    private JLabel lblFoto;
    private JCheckBox chkAuto;
    private JComboBox<Zona> cmbZona;
    private String rutaFoto = "";
    private IncidenteDAO incidenteDAO = new IncidenteDAO();
    private ZonaDAO zonaDAO = new ZonaDAO();
    private RegistroDAO registroDAO = new RegistroDAO();

    public VentanaReporte(Usuario usuario) {
        super("SafeUni - Reportar incidente", usuario, "Inicio");
        armarVentana(true);
    }

    @Override
    protected JPanel crearContenido() {
        JPanel p = new JPanel(null);
        p.setBackground(Estilo.FONDO);
        p.add(Estilo.etiqueta("Reportar incidente", Estilo.TITULO, Estilo.MORADO, 16, 8, 348, 36));

        p.add(Estilo.etiqueta("Describe el incidente...", Estilo.PEQUENA, Estilo.GRIS, 16, 50, 348, 18));
        txtDescripcion = new JTextArea();
        txtDescripcion.setFont(Estilo.NORMAL);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(txtDescripcion);
        scroll.setBorder(new LineBorder(Estilo.BORDE, 1, true));
        scroll.setBounds(16, 70, 348, 90);
        p.add(scroll);

        // Categorías
        p.add(Estilo.etiqueta("Categoría", Estilo.PEQUENA, Estilo.GRIS, 16, 168, 200, 18));
        rbRobo = new JRadioButton("Robo");
        rbAcoso = new JRadioButton("Acoso");
        rbSospechosa = new JRadioButton("Persona sospechosa");
        rbOtra = new JRadioButton("Otra");
        ButtonGroup grupo = new ButtonGroup();
        JRadioButton[] botones = {rbRobo, rbAcoso, rbSospechosa, rbOtra};
        int[][] posiciones = {{16, 190}, {190, 190}, {16, 218}, {190, 218}};
        for (int i = 0; i < botones.length; i++) {
            botones[i].setFont(Estilo.NORMAL);
            botones[i].setOpaque(false);
            botones[i].setBounds(posiciones[i][0], posiciones[i][1], 170, 26);
            grupo.add(botones[i]);
            p.add(botones[i]);
        }

        // Foto opcional
        BotonRedondeado btnFoto = new BotonRedondeado("Adjuntar foto (opcional)", Color.WHITE, Estilo.MORADO, Estilo.MORADO, 30);
        btnFoto.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnFoto.setBounds(16, 254, 190, 32);
        btnFoto.addActionListener(e -> elegirFoto());
        p.add(btnFoto);
        lblFoto = Estilo.etiqueta("Sin foto", Estilo.PEQUENA, Estilo.GRIS, 214, 254, 150, 32);
        p.add(lblFoto);

        // Ubicación automática (si está apagada, se elige la zona a mano)
        List<Zona> zonas = zonaDAO.listar();
        cmbZona = new JComboBox<>(zonas.toArray(new Zona[0]));
        cmbZona.setSelectedIndex(zonas.isEmpty() ? -1 : indiceZonaMasCercana(zonas));
        cmbZona.setEnabled(false);

        chkAuto = new JCheckBox("Ubicación automática", true);
        chkAuto.setFont(Estilo.NORMAL);
        chkAuto.setOpaque(false);
        chkAuto.setBounds(16, 296, 250, 26);
        chkAuto.addActionListener(e -> cmbZona.setEnabled(!chkAuto.isSelected()));
        p.add(chkAuto);

        p.add(Estilo.etiqueta("Zona del incidente", Estilo.PEQUENA, Estilo.GRIS, 16, 328, 200, 16));
        Estilo.campo(cmbZona, 16, 346, 348, 32);
        p.add(cmbZona);

        BotonRedondeado btnEnviar = new BotonRedondeado("Enviar reporte");
        btnEnviar.setBounds(16, 396, 348, 48);
        btnEnviar.addActionListener(e -> enviar());
        p.add(btnEnviar);
        return p;
    }

    // La zona más cercana a la ubicación simulada del usuario
    private int indiceZonaMasCercana(List<Zona> zonas) {
        int mejor = 0;
        double menor = Double.MAX_VALUE;
        for (int i = 0; i < zonas.size(); i++) {
            double dLat = zonas.get(i).getLatitud() - PanelMapa.LAT_USUARIO;
            double dLon = zonas.get(i).getLongitud() - PanelMapa.LON_USUARIO;
            double distancia = dLat * dLat + dLon * dLon;
            if (distancia < menor) {
                menor = distancia;
                mejor = i;
            }
        }
        return mejor;
    }

    private void elegirFoto() {
        JFileChooser selector = new JFileChooser();
        selector.setFileFilter(new FileNameExtensionFilter("Imágenes (jpg, png)", "jpg", "jpeg", "png"));
        if (selector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File archivo = selector.getSelectedFile();
            rutaFoto = archivo.getAbsolutePath();
            lblFoto.setText(archivo.getName());
        }
    }

    private String categoriaElegida() {
        if (rbRobo.isSelected()) {
            return "Robo";
        } else if (rbAcoso.isSelected()) {
            return "Acoso";
        } else if (rbSospechosa.isSelected()) {
            return "Persona sospechosa";
        } else if (rbOtra.isSelected()) {
            return "Otra";
        }
        return null;
    }

    private void enviar() {
        String descripcion = txtDescripcion.getText().trim();
        String categoria = categoriaElegida();
        if (descripcion.isEmpty() || categoria == null) {
            aviso("Describe el incidente y elige categoría");
            return;
        }
        Zona zona = (Zona) cmbZona.getSelectedItem();
        if (zona == null) {
            error("No hay zonas registradas en la base de datos.");
            return;
        }
        double latitud = chkAuto.isSelected() ? PanelMapa.LAT_USUARIO : zona.getLatitud();
        double longitud = chkAuto.isSelected() ? PanelMapa.LON_USUARIO : zona.getLongitud();

        // Se crea el objeto de la clase hija según la categoría
        Incidente incidente;
        switch (categoria) {
            case "Robo":
                incidente = new Robo(usuario.getId(), zona.getId(), descripcion, rutaFoto, latitud, longitud);
                break;
            case "Acoso":
                incidente = new Acoso(usuario.getId(), zona.getId(), descripcion, rutaFoto, latitud, longitud);
                break;
            case "Persona sospechosa":
                incidente = new PersonaSospechosa(usuario.getId(), zona.getId(), descripcion, rutaFoto, latitud, longitud);
                break;
            default:
                incidente = new Incidente(usuario.getId(), zona.getId(), descripcion, rutaFoto, latitud, longitud);
        }

        if (incidenteDAO.guardar(incidente)) {
            registroDAO.guardar(usuario.getId(), "REPORTE_ENVIADO");
            aviso("Reporte enviado");
            irA(new VentanaPrincipal(usuario));
        } else {
            error("No se pudo enviar el reporte. Revisa la conexión.");
        }
    }
}