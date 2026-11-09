package vista;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

// Colores, fuentes y pequeños métodos para que todas las pantallas se vean iguales
public class Estilo {

    public static final Color NARANJA = new Color(243, 130, 32);
    public static final Color MORADO = new Color(61, 31, 107);
    public static final Color FONDO = new Color(245, 245, 247);
    public static final Color GRIS = new Color(120, 120, 130);
    public static final Color BORDE = new Color(215, 215, 222);
    public static final Color VERDE = new Color(76, 175, 80);
    public static final Color ROJO = new Color(214, 40, 40);

    public static final Font TITULO = new Font("SansSerif", Font.BOLD, 26);
    public static final Font SUBTITULO = new Font("SansSerif", Font.BOLD, 15);
    public static final Font NORMAL = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font PEQUENA = new Font("SansSerif", Font.PLAIN, 12);

    // Crea una etiqueta ya ubicada en la pantalla (x, y, ancho, alto)
    public static JLabel etiqueta(String texto, Font fuente, Color color, int x, int y, int ancho, int alto) {
        JLabel l = new JLabel(texto);
        l.setFont(fuente);
        l.setForeground(color);
        l.setBounds(x, y, ancho, alto);
        return l;
    }

    // Le da posición, tamaño y borde redondeado a un campo de texto
    public static void campo(JComponent c, int x, int y, int ancho, int alto) {
        c.setBounds(x, y, ancho, alto);
        c.setFont(NORMAL);
        if (!(c instanceof JComboBox)) {
            c.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(BORDE, 1, true), new EmptyBorder(2, 10, 2, 10)));
        }
    }

    // Tarjeta blanca con borde suave
    public static JPanel tarjeta(int x, int y, int ancho, int alto) {
        JPanel p = new JPanel(null);
        p.setBackground(Color.WHITE);
        p.setBorder(new LineBorder(BORDE, 1, true));
        p.setBounds(x, y, ancho, alto);
        return p;
    }

    // Botón que parece un enlace (sin borde ni fondo)
    public static JButton enlace(String texto, int x, int y, int ancho, int alto) {
        JButton b = new JButton(texto);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setForeground(MORADO);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBounds(x, y, ancho, alto);
        return b;
    }

    // Círculo con la inicial del nombre
    public static JComponent avatar(String inicial, int x, int y, int diametro) {
        JComponent c = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(228, 222, 240));
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.setColor(MORADO);
                g2.setFont(new Font("SansSerif", Font.BOLD, getHeight() / 2));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(inicial, (getWidth() - fm.stringWidth(inicial)) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        c.setBounds(x, y, diametro, diametro);
        return c;
    }
}