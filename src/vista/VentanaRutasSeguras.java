package vista;

import dao.RutaDAO;
import dao.UsuarioDAO;
import dao.ZonaDAO;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import modelo.*;

// Pantalla 4: Rutas seguras (con puntos de parada para compañeros o acompañantes)
public class VentanaRutasSeguras extends VentanaBase {

    private JTextField txtOrigen;
    private JTextField txtDestino;
    private JCheckBox chkRapida;
    private JLabel lblParadas;
    private JLabel lblTiempos;
    private PanelMapa mapa;
    private List<PuntoParada> paradas = new ArrayList<>();
    private Ruta rutaSegura;       // resultado de la última búsqueda
    private Ruta rutaRapida;
    private RutaDAO rutaDAO = new RutaDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private ZonaDAO zonaDAO = new ZonaDAO();

    public VentanaRutasSeguras(Usuario usuario) {
        super("SafeUni - Rutas seguras", usuario, "Rutas");
        armarVentana(true);
    }

    @Override
    protected JPanel crearContenido() {
        JPanel p = new JPanel(null);
        p.setBackground(Estilo.FONDO);
        p.add(Estilo.etiqueta("Rutas seguras", Estilo.TITULO, Estilo.MORADO, 16, 8, 348, 36));

        p.add(Estilo.etiqueta("Origen", Estilo.PEQUENA, Estilo.GRIS, 16, 46, 100, 16));
        txtOrigen = new JTextField("Sede Av. 68");
        Estilo.campo(txtOrigen, 16, 62, 348, 34);
        p.add(txtOrigen);

        p.add(Estilo.etiqueta("Destino", Estilo.PEQUENA, Estilo.GRIS, 16, 102, 100, 16));
        txtDestino = new JTextField();
        Estilo.campo(txtDestino, 16, 118, 246, 34);
        p.add(txtDestino);

        BotonRedondeado btnBuscar = new BotonRedondeado("Buscar", Estilo.MORADO, Color.WHITE, null, 16);
        btnBuscar.setBounds(270, 118, 94, 34);
        p.add(btnBuscar);

        chkRapida = new JCheckBox("Ruta alternativa más rápida");
        chkRapida.setFont(Estilo.PEQUENA);
        chkRapida.setOpaque(false);
        chkRapida.setBounds(16, 160, 260, 22);
        p.add(chkRapida);

        // Puntos de parada para un compañero o acompañante
        BotonRedondeado btnParada = new BotonRedondeado("+ Agregar parada", Color.WHITE, Estilo.MORADO, Estilo.MORADO, 30);
        btnParada.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnParada.setBounds(16, 188, 170, 32);
        p.add(btnParada);

        BotonRedondeado btnQuitar = new BotonRedondeado("Quitar última", Color.WHITE, Estilo.GRIS, Estilo.BORDE, 30);
        btnQuitar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnQuitar.setBounds(194, 188, 170, 32);
        p.add(btnQuitar);

        lblParadas = Estilo.etiqueta("Paradas: ninguna", Estilo.PEQUENA, Estilo.GRIS, 16, 226, 348, 34);
        lblParadas.setVerticalAlignment(SwingConstants.TOP);
        p.add(lblParadas);

        mapa = new PanelMapa();
        mapa.setBounds(16, 264, 348, 150);
        mapa.setZonas(zonaDAO.listar());
        JLabel etiquetaRuta = Estilo.etiqueta("Ruta segura recomendada", Estilo.PEQUENA, Estilo.NARANJA, 8, 8, 160, 22);
        etiquetaRuta.setOpaque(true);
        etiquetaRuta.setBackground(Color.WHITE);
        etiquetaRuta.setHorizontalAlignment(SwingConstants.CENTER);
        mapa.add(etiquetaRuta);
        p.add(mapa);

        lblTiempos = Estilo.etiqueta("Escribe un destino y presiona Buscar", Estilo.PEQUENA, Estilo.MORADO, 16, 420, 348, 20);
        p.add(lblTiempos);

        // Leyenda del mapa
        p.add(leyenda("Zonas seguras", Estilo.VERDE, 16));
        p.add(leyenda("Áreas de alto riesgo", Estilo.NARANJA, 120));
        p.add(leyenda("Puntos de alerta", Estilo.ROJO, 262));

        BotonRedondeado btnIniciar = new BotonRedondeado("Iniciar viaje");
        btnIniciar.setBounds(16, 472, 348, 46);
        p.add(btnIniciar);

        btnBuscar.addActionListener(e -> buscar());
        txtDestino.addActionListener(e -> buscar());
        chkRapida.addActionListener(e -> {
            if (rutaSegura != null) {
                mostrarRuta();
            }
        });
        btnParada.addActionListener(e -> agregarParada());
        btnQuitar.addActionListener(e -> quitarParada());
        btnIniciar.addActionListener(e -> iniciarViaje());
        return p;
    }

    private JLabel leyenda(String texto, Color color, int x) {
        String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        JLabel l = Estilo.etiqueta("<html><font color='" + hex + "'>●</font> " + texto + "</html>",
                new Font("SansSerif", Font.PLAIN, 11), Estilo.GRIS, x, 446, 140, 18);
        return l;
    }

    // Calcula las dos rutas (las clases hijas RutaSegura y RutaRapida calculan sus minutos)
    private void buscar() {
        String origen = txtOrigen.getText().trim();
        String destino = txtDestino.getText().trim();
        if (origen.isEmpty() || destino.isEmpty()) {
            aviso("Escribe el origen y el destino.");
            return;
        }
        rutaSegura = new RutaSegura(origen, destino);
        rutaRapida = new RutaRapida(origen, destino);
        for (PuntoParada parada : paradas) {
            rutaSegura.agregarParada(parada);
            rutaRapida.agregarParada(parada);
        }
        mostrarRuta();
    }

    private void mostrarRuta() {
        mapa.setRuta(true, chkRapida.isSelected(), paradas.size());
        int diferencia = rutaSegura.calcularMinutos() - rutaRapida.calcularMinutos();
        lblTiempos.setText("Ruta segura: " + rutaSegura.calcularMinutos() + " min   |   Más rápida: "
                + rutaRapida.calcularMinutos() + " min (+" + diferencia + " min)");
    }

    // Agrega un punto de parada donde se recoge o se espera a un compañero
    private void agregarParada() {
        if (paradas.size() >= 5) {
            aviso("Puedes agregar máximo 5 paradas.");
            return;
        }
        JTextField txtLugar = new JTextField();
        JComboBox<String> cmbCompanero = new JComboBox<>();
        cmbCompanero.setEditable(true);          // se puede elegir un compañero visible o escribir otro nombre
        for (Usuario u : usuarioDAO.listarVisibles(usuario.getId())) {
            cmbCompanero.addItem(u.getNombre());
        }
        Object[] campos = {"Lugar de la parada (donde lo recoges o lo esperas):", txtLugar,
            "Compañero o acompañante:", cmbCompanero};
        int r = JOptionPane.showConfirmDialog(this, campos, "Agregar punto de parada",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) {
            return;
        }
        Object elegido = cmbCompanero.getSelectedItem();
        String lugar = txtLugar.getText().trim();
        String companero = elegido == null ? "" : elegido.toString().trim();
        if (lugar.isEmpty() || companero.isEmpty()) {
            aviso("Escribe el lugar de la parada y el nombre del compañero.");
            return;
        }
        paradas.add(new PuntoParada(lugar, companero));
        actualizarParadas();
        if (rutaSegura != null) {
            buscar();                    // vuelve a calcular con la nueva parada
        }
    }

    private void quitarParada() {
        if (paradas.isEmpty()) {
            return;
        }
        paradas.remove(paradas.size() - 1);
        actualizarParadas();
        if (rutaSegura != null) {
            buscar();
        }
    }

    private void actualizarParadas() {
        if (paradas.isEmpty()) {
            lblParadas.setText("Paradas: ninguna");
            return;
        }
        StringBuilder sb = new StringBuilder("<html>Paradas: ");
        for (int i = 0; i < paradas.size(); i++) {
            sb.append(i + 1).append(". ").append(paradas.get(i)).append("   ");
        }
        lblParadas.setText(sb.append("</html>").toString());
    }

    // Guarda el viaje (y sus paradas) en el historial
    private void iniciarViaje() {
        if (rutaSegura == null) {
            aviso("Primero busca una ruta.");
            return;
        }
        Ruta elegida = chkRapida.isSelected() ? rutaRapida : rutaSegura;
        if (rutaDAO.guardar(elegida, usuario.getId())) {
            aviso("¡Buen viaje! Tu ruta quedó guardada en el historial.");
        } else {
            error("No se pudo guardar el viaje. Revisa la conexión.");
        }
    }
}