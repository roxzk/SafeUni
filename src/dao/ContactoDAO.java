package dao;

import conexion.Conexion;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.ContactoConfianza;

public class ContactoDAO {

    public List<ContactoConfianza> listar(int idUsuario) {
        List<ContactoConfianza> lista = new ArrayList<>();
        String sql = "SELECT * FROM contactos_confianza WHERE id_usuario = ? ORDER BY id";
        try (PreparedStatement ps = Conexion.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new ContactoConfianza(rs.getInt("id"), rs.getInt("id_usuario"), rs.getString("nombre"),
                        rs.getString("telefono"), rs.getString("parentesco"), rs.getString("medio")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean guardar(ContactoConfianza c) {
        String sql = "INSERT INTO contactos_confianza (id_usuario, nombre, telefono, parentesco, medio) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = Conexion.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setInt(1, c.getIdUsuario());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getParentesco());
            ps.setString(5, c.getMedio());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM contactos_confianza WHERE id = ?";
        try (PreparedStatement ps = Conexion.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}