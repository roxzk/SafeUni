package safeuni;

import conexion.Conexion;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import vista.VentanaLogin;

public class SafeUni {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // si falla, se usa el estilo por defecto de Java
        }

        SwingUtilities.invokeLater(() -> {
            if (!Conexion.getInstancia().probar()) {
                JOptionPane.showMessageDialog(null,
                        "No se pudo conectar con MySQL.\nRevisa el usuario y la clave en Conexion.java\ny que ya hayas ejecutado el script safeuni.sql.",
                        "Error de conexión", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            new VentanaLogin().setVisible(true);
        });
    }
}