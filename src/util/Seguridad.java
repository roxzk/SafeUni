
package util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Seguridad {

    public static final String DOMINIO = "@ucompensar.edu.co";

    // Convierte un texto a SHA-256 para no guardar la contraseña ni la cédula en texto plano
    public static String cifrar(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(texto.getBytes(StandardCharsets.UTF_8));
            return String.format("%064x", new BigInteger(1, hash));
        } catch (Exception e) {
            throw new RuntimeException("No se pudo cifrar el texto", e);
        }
    }

    // Debe terminar en el dominio institucional y tener un usuario válido antes de la @
    public static boolean correoInstitucional(String correo) {
        if (!correo.endsWith(DOMINIO)) {
            return false;
        }
        String usuario = correo.substring(0, correo.length() - DOMINIO.length());
        return usuario.matches("[A-Za-z0-9._-]+");
    }

    public static boolean documentoValido(String documento) {
        return documento.matches("\\d{6,12}");
    }

    public static boolean telefonoValido(String telefono) {
        return telefono.matches("\\d{10}");
    }
}