package dao;

import conexion.Conexion;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// Registro de actividad (auditoría): inicios de sesión, SOS, reportes...
public class RegistroDAO {

    public void guardar(int idUsuario, String accion) {
        String sql = "INSERT INTO registro_actividad (id_usuario, accion) VALUES (?, ?)";
        try (PreparedStatement ps = Conexion.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setString(2, accion);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}