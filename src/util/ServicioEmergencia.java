package util;

import modelo.ContactoConfianza;

/**
 * Simula el aviso a los familiares y a Seguridad Universitaria.
 * Una app de escritorio no puede marcar ni enviar SMS por sí sola; en un celular
 * real aquí se usaría la llamada del teléfono o un servicio de SMS.
 */
public class ServicioEmergencia {

    // Poner aquí el número real de Seguridad Universitaria
    public static final String TEL_SEGURIDAD = "601 000 0000";
    public static final String TEL_POLICIA = "123";

    public static String contactar(ContactoConfianza c) {
        if (c.esLlamada()) {
            return "Llamando a " + c.getNombre() + " (" + c.getTelefono() + ")...";
        }
        return "Mensaje enviado a " + c.getNombre() + " (" + c.getTelefono() + ")";
    }

    public static String avisarSeguridad() {
        return "Seguridad Universitaria notificada (" + TEL_SEGURIDAD + ")";
    }
}