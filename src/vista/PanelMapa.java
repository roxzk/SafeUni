package vista;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import modelo.Zona;

// Mapa dibujado con Graphics2D (no usa Google Maps, así no necesita API key)
public class PanelMapa extends JPanel {

    // Ubicación simulada del usuario (en un celular sería el GPS)
    public static final double LAT_USUARIO = 4.6486;
    public static final double LON_USUARIO = -74.1050;
    private static final double ALCANCE = 0.006;   // grados que se ven desde el centro hacia cada lado

    private List<Zona> zonas = new ArrayList<>();
    private boolean mostrarUsuario = false;
    private int pinesCercanos = 0;
    private boolean mostrarRuta = false;
    private boolean mostrarAlternativa = false;
    private int paradas = 0;

    public PanelMapa() {
        setLayout(null);
        setBackground(new Color(234, 238, 234));
        setBorder(BorderFactory.createLineBorder(Estilo.BORDE));
    }

    public void setZonas(List<Zona> zonas) {
        this.zonas = zonas;
        repaint();
    }

    public void setMostrarUsuario(boolean mostrarUsuario) {
        this.mostrarUsuario = mostrarUsuario;
        repaint();
    }

    public void setPinesCercanos(int pinesCercanos) {
        this.pinesCercanos = pinesCercanos;
        repaint();
    }

    // Dibuja la ruta naranja con puntos morados (origen, paradas y destino)
    public void setRuta(boolean mostrarRuta, boolean mostrarAlternativa, int paradas) {
        this.mostrarRuta = mostrarRuta;
        this.mostrarAlternativa = mostrarAlternativa;
        this.paradas = paradas;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();

        dibujarCalles(g2, w, h);
        for (Zona z : zonas) {
            dibujarZona(g2, z, w, h);
        }
        if (mostrarRuta) {
            dibujarRuta(g2, w, h);
        }
        for (int i = 0; i < pinesCercanos; i++) {
            dibujarPin(g2, 50 + (i * 67) % (w - 100), 50 + (i * 43) % (h - 80));
        }
        if (mostrarUsuario) {
            dibujarPin(g2, w / 2, h / 2);
        }
        g2.dispose();
    }

    private void dibujarCalles(Graphics2D g2, int w, int h) {
        g2.setColor(new Color(207, 229, 207));                 // parque
        g2.fillRoundRect(w / 14, h / 12, w / 4, h / 4, 20, 20);
        g2.setColor(Color.WHITE);                              // calles
        g2.setStroke(new BasicStroke(9));
        g2.drawLine(0, h / 3, w, h / 2);
        g2.drawLine(0, h * 4 / 5, w, h * 3 / 5);
        g2.drawLine(w / 3, 0, w / 2, h);
        g2.drawLine(w * 3 / 4, 0, w * 4 / 5, h);
    }

    // Zona de riesgo: círculos concéntricos (más círculos = más riesgo). Las zonas de riesgo bajo van en verde.
    private void dibujarZona(Graphics2D g2, Zona z, int w, int h) {
        int x = (int) (w / 2 + (z.getLongitud() - LON_USUARIO) / ALCANCE * (w / 2));
        int y = (int) (h / 2 - (z.getLatitud() - LAT_USUARIO) / ALCANCE * (h / 2));
        String nivel = z.getNivel();
        int anillos = nivel.equals("Alto") ? 5 : (nivel.equals("Medio") ? 3 : 2);
        Color color = nivel.equals("Bajo") ? Estilo.VERDE : Estilo.NARANJA;
        for (int i = anillos; i >= 1; i--) {
            int r = i * 12;
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 55));
            g2.fillOval(x - r, y - r, r * 2, r * 2);
        }
        if (nivel.equals("Alto")) {                            // punto de alerta
            g2.setColor(Estilo.ROJO);
            g2.fillOval(x - 5, y - 5, 10, 10);
        }
    }

    private void dibujarRuta(Graphics2D g2, int w, int h) {
        int[] xs = {w / 8, w * 3 / 8, w / 2, w * 3 / 4, w * 7 / 8};
        int[] ys = {h * 7 / 8, h * 3 / 4, h / 2, h * 2 / 5, h / 6};

        if (mostrarAlternativa) {                              // ruta más rápida: línea punteada gris
            g2.setColor(Color.GRAY);
            g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[]{8, 8}, 0));
            g2.drawLine(xs[0], ys[0], xs[4], ys[4]);
        }
        g2.setColor(Estilo.NARANJA);                           // ruta segura: línea naranja
        g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawPolyline(xs, ys, 5);

        dibujarPunto(g2, xs[0], ys[0]);                        // origen y destino
        dibujarPunto(g2, xs[4], ys[4]);
        for (int i = 1; i <= paradas; i++) {                   // paradas repartidas a lo largo de la ruta
            double pos = i * 4.0 / (paradas + 1);
            int k = (int) pos;
            double f = pos - k;
            dibujarPunto(g2, (int) (xs[k] + f * (xs[k + 1] - xs[k])), (int) (ys[k] + f * (ys[k + 1] - ys[k])));
        }
    }

    private void dibujarPunto(Graphics2D g2, int x, int y) {
        g2.setColor(Estilo.MORADO);
        g2.fillOval(x - 7, y - 7, 14, 14);
        g2.setColor(Color.WHITE);
        g2.fillOval(x - 3, y - 3, 6, 6);
    }

    private void dibujarPin(Graphics2D g2, int x, int y) {
        g2.setColor(Estilo.MORADO);
        g2.fillOval(x - 9, y - 26, 18, 18);
        g2.fillPolygon(new int[]{x - 7, x + 7, x}, new int[]{y - 14, y - 14, y}, 3);
        g2.setColor(Color.WHITE);
        g2.fillOval(x - 3, y - 20, 6, 6);
    }
}