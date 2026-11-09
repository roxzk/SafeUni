package dao;

import conexion.Conexion;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import modelo.Incidente;

public class IncidenteDAO {

    // El reporte queda "PENDIENTE" hasta que un administrador lo valide
    public boolean guardar(Incidente i) {
        String sql = "INSERT INTO incidentes (id_usuario, id_zona, categoria, descripcion, foto, latitud, longitud) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = Conexion.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setInt(1, i.getIdUsuario());
            ps.setInt(2, i.getIdZona());
            ps.setString(3, i.getCategoria());     // cada clase hija devuelve su categoría
            ps.setString(4, i.getDescripcion());
            ps.setString(5, i.getFoto());
            ps.setDouble(6, i.getLatitud());
            ps.setDouble(7, i.getLongitud());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}