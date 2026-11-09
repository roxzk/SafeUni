package dao;

import conexion.Conexion;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Zona;

public class ZonaDAO {

    // Cada zona trae cuántos incidentes VALIDADOS tuvo en los últimos 30 días
    public List<Zona> listar() {
        List<Zona> lista = new ArrayList<>();
        String sql = "SELECT z.*, (SELECT COUNT(*) FROM incidentes i WHERE i.id_zona = z.id "
                + "AND i.estado = 'VALIDADO' AND i.fecha >= NOW() - INTERVAL 30 DAY) AS total "
                + "FROM zonas z ORDER BY z.id";
        try (PreparedStatement ps = Conexion.getInstancia().getConexion().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Zona(rs.getInt("id"), rs.getString("nombre"), rs.getString("localidad"),
                        rs.getString("upz"), rs.getDouble("latitud"), rs.getDouble("longitud"), rs.getInt("total")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}