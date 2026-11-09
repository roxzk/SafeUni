package vista;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import javax.swing.JComponent;

// Dibuja el logo: un escudo naranja con una "S" blanca
public class LogoSafeUni extends JComponent {

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        double w = getWidth();
        double h = getHeight();

        Path2D escudo = new Path2D.Double();
        escudo.moveTo(w * 0.5, 0);
        escudo.lineTo(w * 0.95, h * 0.15);
        escudo.lineTo(w * 0.95, h * 0.5);
        escudo.curveTo(w * 0.95, h * 0.8, w * 0.7, h * 0.93, w * 0.5, h);
        escudo.curveTo(w * 0.3, h * 0.93, w * 0.05, h * 0.8, w * 0.05, h * 0.5);
        escudo.lineTo(w * 0.05, h * 0.15);
        escudo.closePath();
        g2.setColor(Estilo.NARANJA);
        g2.fill(escudo);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, (int) (h * 0.55)));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString("S", (int) ((w - fm.stringWidth("S")) / 2), (int) (h * 0.68));
        g2.dispose();
    }
}