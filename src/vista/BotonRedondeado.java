package vista;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;

// Botón con esquinas redondeadas. Con un radio grande se ve como píldora o como círculo.
public class BotonRedondeado extends JButton {

    private Color colorFondo;
    private Color colorBorde;      // null si no lleva borde
    private int radio;
    private String icono = "";

    public BotonRedondeado(String texto, Color colorFondo, Color colorTexto, Color colorBorde, int radio) {
        super(texto);
        this.colorFondo = colorFondo;
        this.colorBorde = colorBorde;
        this.radio = radio;
        setForeground(colorTexto);
        setFont(new Font("SansSerif", Font.BOLD, 14));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));    // márgenes pequeños para que quepa el texto
    }

    // Botón naranja de esquinas muy redondeadas (el principal de la app)
    public BotonRedondeado(String texto) {
        this(texto, Estilo.NARANJA, Color.WHITE, null, 44);
    }

    public void setIcono(String icono) {
        this.icono = icono;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getModel().isPressed() ? colorFondo.darker() : colorFondo);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radio, radio);
        if (colorBorde != null) {
            g2.setColor(colorBorde);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, radio, radio);
        }
        dibujarIcono(g2);
        g2.dispose();
        super.paintComponent(g);   // dibuja el texto
    }

    // Iconos sencillos dibujados con líneas (así no dependen de imágenes ni de fuentes)
    private void dibujarIcono(Graphics2D g2) {
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        g2.setColor(getForeground());
        g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        switch (icono) {
            case "caminar":
                g2.fillOval(cx - 5, cy - 16, 10, 10);
                g2.drawLine(cx, cy - 5, cx, cy + 6);
                g2.drawLine(cx - 8, cy - 2, cx + 8, cy - 2);
                g2.drawLine(cx, cy + 6, cx - 6, cy + 16);
                g2.drawLine(cx, cy + 6, cx + 6, cy + 16);
                break;
            case "reporte":
                g2.drawPolygon(new int[]{cx, cx + 15, cx - 15}, new int[]{cy - 15, cy + 12, cy + 12}, 3);
                g2.drawLine(cx, cy - 4, cx, cy + 3);
                g2.fillOval(cx - 2, cy + 6, 4, 4);
                break;
            case "historial":
                g2.drawOval(cx - 14, cy - 14, 28, 28);
                g2.drawLine(cx, cy, cx, cy - 8);
                g2.drawLine(cx, cy, cx + 6, cy + 4);
                break;
            case "perfil":
                g2.fillOval(cx - 5, cy - 12, 10, 10);
                g2.fillArc(cx - 10, cy + 1, 20, 20, 0, 180);
                break;
            default:
                break;
        }
    }
}