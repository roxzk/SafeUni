package dao;

import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import modelo.PuntoParada;
import modelo.Ruta;
import modelo.RutaRapida;
import modelo.RutaSegura;

public class RutaDAO {

    // Guarda el viaje y, uno por uno, los puntos de parada de los compañeros
    public boolean guardar(Ruta ruta, int idUsuario) {
        String sqlViaje = "INSERT INTO viajes (id_usuario, origen, destino, tipo_ruta) VALUES (?, ?, ?, ?)";
        String sqlParada = "INSERT INTO paradas (id_viaje, lugar, companero, orden) VALUES (?, ?, ?, ?)";
        try {
            Connection con = Conexion.getInstancia().getConexion();
            int idViaje;
            try (PreparedStatement ps = con.prepareStatement(sqlViaje, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, idUsuario);
                ps.setString(2, ruta.getOrigen());
                ps.setString(3, ruta.getDestino());
                ps.setString(4, ruta.getTipo());
                ps.executeUpdate();
                ResultSet claves = ps.getGeneratedKeys();
                claves.next();
                idViaje = claves.getInt(1);
            }
            int orden = 1;
            for (PuntoParada p : ruta.getParadas()) {
                try (PreparedStatement ps = con.prepareStatement(sqlParada)) {
                    ps.setInt(1, idViaje);
                    ps.setString(2, p.getLugar());
                    ps.setString(3, p.getCompanero());
                    ps.setInt(4, orden);
                    ps.executeUpdate();
                }
                orden++;
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Historial de viajes del usuario (el más reciente primero)
    public List<Ruta> listarPorUsuario(int idUsuario) {
        List<Ruta> lista = new ArrayList<>();
        String sql = "SELECT id, origen, destino, tipo_ruta, DATE_FORMAT(fecha, '%d/%m/%Y %h:%i %p') AS fecha "
                + "FROM viajes WHERE id_usuario = ? ORDER BY id DESC";
        try {
            Connection con = Conexion.getInstancia().getConexion();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idUsuario);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Ruta r = crearRuta(rs.getString("tipo_ruta"), rs.getString("origen"), rs.getString("destino"));
                    r.setFecha(rs.getString("fecha"));
                    cargarParadas(con, rs.getInt("id"), r);
                    lista.add(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Último viaje de otros compañeros que están visibles (para "Camina conmigo")
    public List<Ruta> listarSugeridas(int idUsuario) {
        List<Ruta> lista = new ArrayList<>();
        String sql = "SELECT u.id AS id_usuario, u.nombre, v.origen, v.destino, v.tipo_ruta, "
                + "DATE_FORMAT(v.fecha, '%h:%i %p') AS hora "
                + "FROM usuarios u JOIN viajes v ON v.id_usuario = u.id "
                + "WHERE u.visible = 1 AND u.id <> ? "
                + "AND v.id = (SELECT MAX(v2.id) FROM viajes v2 WHERE v2.id_usuario = u.id) "
                + "ORDER BY v.id DESC LIMIT 3";
        try (PreparedStatement ps = Conexion.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Ruta r = crearRuta(rs.getString("tipo_ruta"), rs.getString("origen"), rs.getString("destino"));
                r.setIdUsuario(rs.getInt("id_usuario"));
                r.setNombreUsuario(rs.getString("nombre"));
                r.setFecha(rs.getString("hora"));
                lista.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private void cargarParadas(Connection con, int idViaje, Ruta ruta) throws SQLException {
        String sql = "SELECT lugar, companero FROM paradas WHERE id_viaje = ? ORDER BY orden";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idViaje);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ruta.agregarParada(new PuntoParada(rs.getString("lugar"), rs.getString("companero")));
            }
        }
    }

    // Crea el objeto de la clase hija según el tipo guardado
    private Ruta crearRuta(String tipo, String origen, String destino) {
        if (tipo.equals("SEGURA")) {
            return new RutaSegura(origen, destino);
        }
        return new RutaRapida(origen, destino);
    }
}