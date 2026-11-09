
package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase Singleton: solo existe UNA conexión a MySQL para toda la aplicación.
 */
public class Conexion {

    
    private static final String URL = "jdbc:mysql://localhost:3306/safeuni?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Bogota";
    private static final String USUARIO = "root";
    private static final String CLAVE = "Sneyder.21phz";

    private static Conexion instancia;
    private Connection conexion;

    private Conexion() {
    }

    public static Conexion getInstancia() {
        if (instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }

    public Connection getConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(URL, USUARIO, CLAVE);
        }
        return conexion;
    }

    // Se usa al iniciar la app para avisar si MySQL no responde
    public boolean probar() {
        try {
            return getConexion() != null;
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
            return false;
        }
    }
}