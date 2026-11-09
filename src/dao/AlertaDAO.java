package dao;

import conexion.Conexion;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import modelo.AlertaSOS;
import modelo.AlertaZona;

public class AlertaDAO {

    // Alertas en tiempo real para la pantalla de prevención
    public List<AlertaZona> listarRecientes(int limite) {
        List<AlertaZona> lista = new ArrayList<>();
        String sql = "SELECT a.id, a.mensaje, a.nivel, DATE_FORMAT(a.fecha, '%d/%m/%Y %h:%i %p') AS fecha, z.nombre AS zona "
                + "FROM alertas a JOIN zonas z ON z.id = a.id_zona ORDER BY a.id DESC LIMIT ?";
        try (PreparedStatement ps = Conexion.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setInt(1, limite);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new AlertaZona(rs.getInt("id"), rs.getString("mensaje"), rs.getString("nivel"),
                        rs.getString("fecha"), rs.getString("zona")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Guarda la alerta SOS y le asigna el id que generó MySQL
    public boolean guardarSOS(AlertaSOS a) {
        String sql = "INSERT INTO alertas_sos (id_usuario, contacto, medio, mensaje, latitud, longitud, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = Conexion.getInstancia().getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, a.getIdUsuario());
            ps.setString(2, a.getContacto().getNombre());
            ps.setString(3, a.getContacto().getMedio());
            ps.setString(4, a.getMensaje());
            ps.setDouble(5, a.getLatitud());
            ps.setDouble(6, a.getLongitud());
            ps.setString(7, a.getEstado());
            ps.executeUpdate();
            ResultSet claves = ps.getGeneratedKeys();
            if (claves.next()) {
                a.setId(claves.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean cancelarSOS(AlertaSOS a) {
        String sql = "UPDATE alertas_sos SET estado = 'CANCELADA' WHERE id = ?";
        try (PreparedStatement ps = Conexion.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setInt(1, a.getId());
            a.setEstado("CANCELADA");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}