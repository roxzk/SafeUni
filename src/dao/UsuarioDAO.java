package dao;

import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import modelo.*;
import util.Seguridad;

public class UsuarioDAO {

    // Guarda un usuario nuevo y devuelve su id (o -1 si falló)
    public int registrar(Usuario u) {
        String sql = "INSERT INTO usuarios (nombre, correo, telefono, documento, rol, contrasena) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = Conexion.getInstancia().getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getCorreo());
            ps.setString(3, u.getTelefono());
            ps.setString(4, u.getDocumento());
            ps.setString(5, u.getRol());          // cada clase hija devuelve su propio rol
            ps.setString(6, u.getContrasena());
            ps.executeUpdate();
            ResultSet claves = ps.getGeneratedKeys();
            if (claves.next()) {
                return claves.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean existeCorreo(String correo) {
        return existe("correo", correo);
    }

    public boolean existeDocumento(String documentoCifrado) {
        return existe("documento", documentoCifrado);
    }

    private boolean existe(String columna, String valor) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE " + columna + " = ?";
        try (PreparedStatement ps = Conexion.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setString(1, valor);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Devuelve el usuario si los datos son correctos y null si no.
    // Lanza IllegalStateException si la cuenta está bloqueada (5 intentos fallidos).
    public Usuario iniciarSesion(String correo, String clave) {
        String sql = "SELECT *, (bloqueado_hasta IS NOT NULL AND bloqueado_hasta > NOW()) AS bloqueado "
                + "FROM usuarios WHERE correo = ?";
        try {
            Connection con = Conexion.getInstancia().getConexion();
            int id;
            int intentos;
            boolean bloqueado;
            boolean claveOk;
            Usuario usuario;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, correo);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    return null;
                }
                id = rs.getInt("id");
                intentos = rs.getInt("intentos_fallidos");
                bloqueado = rs.getBoolean("bloqueado");
                claveOk = rs.getString("contrasena").equals(Seguridad.cifrar(clave));
                usuario = crearUsuario(rs);
            }
            if (bloqueado) {
                throw new IllegalStateException("Cuenta bloqueada por 5 intentos fallidos. Intenta de nuevo en 15 minutos.");
            }
            if (claveOk) {
                actualizar(con, "UPDATE usuarios SET intentos_fallidos = 0 WHERE id = ?", id);
                return usuario;
            }
            if (intentos + 1 >= 5) {
                actualizar(con, "UPDATE usuarios SET intentos_fallidos = 0, bloqueado_hasta = DATE_ADD(NOW(), INTERVAL 15 MINUTE) WHERE id = ?", id);
            } else {
                actualizar(con, "UPDATE usuarios SET intentos_fallidos = intentos_fallidos + 1 WHERE id = ?", id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void actualizar(Connection con, String sql, int id) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public boolean cambiarVisibilidad(int idUsuario, boolean visible) {
        String sql = "UPDATE usuarios SET visible = ? WHERE id = ?";
        try (PreparedStatement ps = Conexion.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setBoolean(1, visible);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Compañeros que activaron "Compartir mi visibilidad"
    public List<Usuario> listarVisibles(int idActual) {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE visible = 1 AND id <> ? ORDER BY nombre";
        try (PreparedStatement ps = Conexion.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setInt(1, idActual);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(crearUsuario(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Según el rol guardado en la BD se crea el objeto de la clase hija que corresponde
    private Usuario crearUsuario(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nombre = rs.getString("nombre");
        String correo = rs.getString("correo");
        String telefono = rs.getString("telefono");
        String documento = rs.getString("documento");
        String contrasena = rs.getString("contrasena");
        boolean visible = rs.getBoolean("visible");

        switch (rs.getString("rol")) {
            case "DOCENTE":
                return new Docente(id, nombre, correo, telefono, documento, contrasena, visible);
            case "ADMINISTRATIVO":
                return new PersonalAdministrativo(id, nombre, correo, telefono, documento, contrasena, visible);
            case "ADMINISTRADOR":
                return new Administrador(id, nombre, correo, telefono, documento, contrasena, visible);
            default:
                return new Estudiante(id, nombre, correo, telefono, documento, contrasena, visible);
        }
    }
}