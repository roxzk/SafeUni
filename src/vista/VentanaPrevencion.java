// vista/VentanaPrevencion.java
package vista;

import dao.AlertaDAO;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.net.URI;
import java.util.List;
import javax.swing.*;
import modelo.*;
import util.ServicioEmergencia;

// Pantalla 6: Prevención y consejos
public class VentanaPrevencion extends VentanaBase {

    private AlertaDAO alertaDAO = new AlertaDAO();

    public VentanaPrevencion(Usuario usuario) {
        super("SafeUni - Prevención y consejos", usuario, "Alertas");
        armarVentana(true);
    }

    @Override
    protected JPanel crearContenido() {
        JPanel p = new JPanel(null);
        p.setBackground(Estilo.FONDO);
        p.add(Estilo.etiqueta("Prevención y consejos", Estilo.TITULO, Estilo.MORADO, 16, 8, 348, 36));

        // Alertas en tiempo real
        p.add(Estilo.etiqueta("Alertas en tiempo real", Estilo.SUBTITULO, Estilo.MORADO, 16, 50, 348, 20));
        List<AlertaZona> alertas = alertaDAO.listarRecientes(2);
        if (alertas.isEmpty()) {
            p.add(Estilo.etiqueta("No hay alertas por ahora.", Estilo.PEQUENA, Estilo.GRIS, 16, 76, 348, 20));
        }
        int y = 76;
        for (AlertaZona a : alertas) {
            p.add(crearAlerta(a, y));
            y += 62;
        }

        // Contenido educativo
        p.add(Estilo.etiqueta("Contenido educativo", Estilo.SUBTITULO, Estilo.MORADO, 16, 206, 348, 20));
        BotonRedondeado video = new BotonRedondeado("Video: Fundamentos de defensa personal",
                new Color(112, 64, 190), Color.WHITE, null, 16);
        video.setFont(new Font("SansSerif", Font.BOLD, 13));
        video.setBounds(16, 232, 348, 44);
        video.addActionListener(e -> abrirVideo());
        p.add(video);

        BotonRedondeado guia = new BotonRedondeado("Guía: Rutas seguras para caminar de noche",
                new Color(253, 232, 204), Estilo.MORADO, null, 16);
        guia.setFont(new Font("SansSerif", Font.BOLD, 13));
        guia.setBounds(16, 284, 348, 44);
        guia.addActionListener(e -> mostrarGuia());
        p.add(guia);

        // Contacto rápido
        p.add(Estilo.etiqueta("Contacto rápido", Estilo.SUBTITULO, Estilo.MORADO, 16, 346, 348, 20));
        BotonRedondeado btnSeguridad = new BotonRedondeado("Llamar a Seguridad Universitaria");
        btnSeguridad.setBounds(16, 372, 348, 46);
        btnSeguridad.addActionListener(e -> aviso("Llamando a Seguridad Universitaria (" + ServicioEmergencia.TEL_SEGURIDAD + ")..."));
        p.add(btnSeguridad);

        JButton btnPolicia = Estilo.enlace("Conectar con la Policía (Línea " + ServicioEmergencia.TEL_POLICIA + ")", 16, 426, 348, 26);
        btnPolicia.addActionListener(e -> aviso("Llamando a la Línea " + ServicioEmergencia.TEL_POLICIA + "..."));
        p.add(btnPolicia);
        return p;
    }

    // Tarjeta con barra naranja a la izquierda
    private JPanel crearAlerta(AlertaZona a, int y) {
        JPanel t = new JPanel(null);
        t.setBackground(new Color(253, 240, 224));
        t.setBounds(16, y, 348, 54);

        JPanel barra = new JPanel();
        barra.setBackground(Estilo.NARANJA);
        barra.setBounds(0, 0, 8, 54);
        t.add(barra);

        t.add(Estilo.etiqueta("<html><b>ALERTA:</b> " + a.getMensaje() + "</html>", Estilo.PEQUENA, Estilo.MORADO, 18, 4, 322, 46));
        return t;
    }

    private void abrirVideo() {
        String enlace = "https://www.youtube.com/results?search_query=defensa+personal+basica";
        try {
            Desktop.getDesktop().browse(new URI(enlace));
        } catch (Exception ex) {
            aviso("No se pudo abrir el navegador. Busca este enlace:\n" + enlace);
        }
    }

    private void mostrarGuia() {
        JOptionPane.showMessageDialog(this,
                "Consejos para caminar de noche:\n\n"
                + "1. Prefiere calles iluminadas y con buen flujo de personas.\n"
                + "2. Camina acompañado: usa la opción \"Camina conmigo\".\n"
                + "3. Evita mostrar el celular y usar audífonos a todo volumen.\n"
                + "4. Comparte tu ruta con un familiar antes de salir.\n"
                + "5. Si te sientes en peligro, usa el botón SOS o llama a la Línea 123.",
                "Guía de rutas seguras de noche", JOptionPane.INFORMATION_MESSAGE);
    }
}