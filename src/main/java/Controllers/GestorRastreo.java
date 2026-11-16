
package Controllers;

import DAOs.EnvioDAO;
import Entities.Envio;
import Enumerations.EstadoEnvio;

public class GestorRastreo {

    private final EnvioDAO envioDAO;
    private final InterfazConexionGPS gps;

    public GestorRastreo() {
        this.envioDAO = new EnvioDAO();
        this.gps = new InterfazConexionGPS();
    }

    public Envio buscarPaquete(String numeroGuia) {
        if (numeroGuia == null || numeroGuia.isBlank()) {
            return null;
        }

        return envioDAO.buscarPorNumeroGuia(numeroGuia.trim());
    }

    public EstadoEnvio revisarEstado(String numeroGuia) {
        Envio envio = buscarPaquete(numeroGuia);

        return envio != null ? envio.getEstado() : null;
    }

    public String consultarRastreo(String numeroGuia) {
        Envio envio = buscarPaquete(numeroGuia);

        if (envio == null) {
            return "No se encontró la guía ingresada.";
        }

        String detalle = "Número de guía: "
                + envio.getNumeroGuia()
                + "\nDestino: " + envio.getDestino()
                + "\nEstado: " + envio.getEstado();

        if (envio.getEstado() == EstadoEnvio.ENTREGADO) {
            detalle += "\nFecha de entrega: "
                    + envio.getFechaRetiro();
        } else {
            detalle += "\n" + gps.obtenerUbicacion(envio);
        }

        return detalle;
    }
}
