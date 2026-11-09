package dao;

import conexion.Conexion;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.MensajeChat;

public class MensajeDAO {

    public boolean guardar(int idRemitente, int idDestinatario, String texto) {
        String sql = "INSERT INTO mensajes (id_remitente, id_destinatario, texto) VALUES (?, ?, ?)";
        try (PreparedStatement ps = Conexion.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setInt(1, idRemitente);
            ps.setInt(2, idDestinatario);
            ps.setString(3, texto);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Todos los mensajes entre dos usuarios, en orden
    public List<MensajeChat> listarConversacion(int idA, int idB) {
        List<MensajeChat> lista = new ArrayList<>();
        String sql = "SELECT m.id_remitente, u.nombre, m.texto, DATE_FORMAT(m.fecha, '%h:%i %p') AS hora "
                + "FROM mensajes m JOIN usuarios u ON u.id = m.id_remitente "
                + "WHERE (m.id_remitente = ? AND m.id_destinatario = ?) OR (m.id_remitente = ? AND m.id_destinatario = ?) "
                + "ORDER BY m.id";
        try (PreparedStatement ps = Conexion.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setInt(1, idA);
            ps.setInt(2, idB);
            ps.setInt(3, idB);
            ps.setInt(4, idA);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new MensajeChat(rs.getInt("id_remitente"), rs.getString("nombre"),
                        rs.getString("texto"), rs.getString("hora")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}