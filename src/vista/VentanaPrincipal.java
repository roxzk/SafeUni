package vista;

import dao.ContactoDAO;
import dao.RegistroDAO;
import dao.ZonaDAO;
import java.awt.Color;
import java.awt.Font;
import java.util.List;
import javax.swing.*;
import modelo.*;
import util.Seguridad;

// Pantalla 2: Mi seguridad (mapa, rutas recomendadas, accesos rápidos y botón SOS)
public class VentanaPrincipal extends VentanaBase {

    private ZonaDAO zonaDAO = new ZonaDAO();
    private ContactoDAO contactoDAO = new ContactoDAO();
    private RegistroDAO registroDAO = new RegistroDAO();

    public VentanaPrincipal(Usuario usuario) {
        super("SafeUni - Mi seguridad", usuario, "Inicio");
        armarVentana(true);
    }

    @Override
    protected JPanel crearContenido() {
        JPanel p = new JPanel(null);
        p.setBackground(Estilo.FONDO);
        p.add(Estilo.etiqueta("Mi seguridad", Estilo.TITULO, Estilo.MORADO, 16, 8, 348, 36));

        // Mapa con las zonas de riesgo, tu ubicación (pin morado) y la ruta recomendada
        List<Zona> zonas = zonaDAO.listar();
        PanelMapa mapa = new PanelMapa();
        mapa.setBounds(16, 50, 348, 260);
        mapa.setZonas(zonas);
        mapa.setMostrarUsuario(true);
        mapa.setRuta(true, false, 0);
        p.add(mapa);

        // Etiqueta con la zona de mayor riesgo
        Zona masRiesgo = null;
        for (Zona z : zonas) {
            if (masRiesgo == null || z.getTotalIncidentes() > masRiesgo.getTotalIncidentes()) {
                masRiesgo = z;
            }
        }
        if (masRiesgo != null) {
            JLabel etiquetaZona = Estilo.etiqueta("Zona: " + masRiesgo.getNombre() + " (" + masRiesgo.getNivel() + ")",
                    Estilo.PEQUENA, Estilo.MORADO, 98, 10, 240, 24);
            etiquetaZona.setOpaque(true);
            etiquetaZona.setBackground(Color.WHITE);
            etiquetaZona.setHorizontalAlignment(SwingConstants.CENTER);
            mapa.add(etiquetaZona);
        }

        // Tarjeta de rutas (los tiempos salen de las clases hijas RutaSegura y RutaRapida)
        RutaSegura segura = new RutaSegura("Sede", "Paradero");
        RutaRapida rapida = new RutaRapida("Sede", "Paradero");
        JPanel tarjeta = Estilo.tarjeta(16, 320, 348, 86);
        tarjeta.add(Estilo.etiqueta("Rutas seguras recomendadas", Estilo.SUBTITULO, Estilo.MORADO, 14, 8, 320, 22));
        tarjeta.add(Estilo.etiqueta("Segura: " + segura.calcularMinutos() + " min   |   Más rápida: "
                + rapida.calcularMinutos() + " min", Estilo.PEQUENA, Estilo.GRIS, 14, 32, 320, 18));
        JButton verRutas = Estilo.enlace("Planear una ruta  >", 6, 54, 150, 24);
        verRutas.addActionListener(e -> abrir("Rutas"));
        tarjeta.add(verRutas);
        p.add(tarjeta);

        // Tres accesos redondos
        agregarAcceso(p, "caminar", "Camina<br>conmigo", Estilo.MORADO, 24, () -> abrir("Camina"));
        agregarAcceso(p, "reporte", "Reportar<br>incidente", Estilo.NARANJA, 108, () -> irA(new VentanaReporte(usuario)));
        agregarAcceso(p, "historial", "Historial<br>de viajes", Estilo.MORADO, 192, () -> irA(new VentanaHistorial(usuario)));

        // Botón SOS
        BotonRedondeado sos = new BotonRedondeado("SOS", Estilo.NARANJA, Color.WHITE, Color.WHITE, 92);
        sos.setFont(new Font("SansSerif", Font.BOLD, 26));
        sos.setBounds(268, 416, 92, 92);
        sos.addActionListener(e -> activarSOS());
        p.add(sos);
        return p;
    }

    private void agregarAcceso(JPanel p, String icono, String texto, Color color, int x, Runnable accion) {
        BotonRedondeado b = new BotonRedondeado("", Color.WHITE, color, color, 62);
        b.setIcono(icono);
        b.setBounds(x, 420, 62, 62);
        b.addActionListener(e -> accion.run());
        p.add(b);

        JLabel l = Estilo.etiqueta("<html><center>" + texto + "</center></html>", Estilo.PEQUENA, Estilo.MORADO, x - 11, 486, 84, 34);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(l);
    }

    // Flujo del SOS: 1) tiene contactos, 2) confirmación, 3) verificación de ID, 4) cuenta regresiva con opción de cancelar
    private void activarSOS() {
        List<ContactoConfianza> contactos = contactoDAO.listar(usuario.getId());
        if (contactos.isEmpty()) {
            aviso("Primero agrega un familiar en tu Perfil para poder usar el SOS.");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Seguro que quieres activar la alerta SOS?\nSe avisará a Seguridad Universitaria y a tus contactos.",
                "Confirmar SOS", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        if (!verificarDocumento()) {
            return;
        }

        registroDAO.guardar(usuario.getId(), "SOS_ACTIVADO");
        new DialogoSOS(this, usuario, contactos).setVisible(true);
    }

    // Verificación de ID: el usuario escribe su documento y se compara con el que registró (3 intentos)
    private boolean verificarDocumento() {
        for (int intento = 1; intento <= 3; intento++) {
            JPasswordField campo = new JPasswordField();
            Object[] mensaje = {"Verifica tu identidad.\nEscribe tu número de documento (intento " + intento + " de 3):", campo};
            int r = JOptionPane.showConfirmDialog(this, mensaje, "Verificación de ID",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (r != JOptionPane.OK_OPTION) {
                return false;
            }
            String ingresado = new String(campo.getPassword()).trim();
            if (Seguridad.cifrar(ingresado).equals(usuario.getDocumento())) {
                return true;
            }
            error("El documento no coincide.");
        }
        registroDAO.guardar(usuario.getId(), "SOS_VERIFICACION_FALLIDA");
        error("No se pudo verificar tu identidad. El SOS no fue activado.");
        return false;
    }
}