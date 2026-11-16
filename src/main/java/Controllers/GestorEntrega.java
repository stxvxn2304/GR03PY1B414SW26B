
package Controllers;

import java.time.LocalDateTime;

import DAOs.EnvioDAO;
import Entities.Envio;
import Enumerations.EstadoEnvio;

public class GestorEntrega {

    private final EnvioDAO envioDAO;

    public GestorEntrega() {
        this.envioDAO = new EnvioDAO();
    }

    // Consulta si una encomienda puede entregarse.
    public Envio buscarPaquete(String numeroGuia, String documento) {
        if (numeroGuia == null || numeroGuia.isBlank()
                || documento == null || documento.isBlank()) {
            return null;
        }

        Envio envio = envioDAO.buscarPorNumeroGuia(numeroGuia);

        if (envio == null || envio.getDestinatario() == null) {
            return null;
        }

        String identificacion =
                envio.getDestinatario().getIdentificacion();

        if (identificacion == null ||
                !identificacion.trim().equals(documento.trim())) {
            return null;
        }

        // Solo se entregan encomiendas recibidas.
        if (envio.getEstado() != EstadoEnvio.RECIBIDO) {
            return null;
        }

        // Impide entregar dos veces.
        if (envio.getFechaRetiro() != null) {
            return null;
        }

        return envio;
    }

    // Confirma el retiro y actualiza el estado.
    public boolean confirmarEntrega(String numeroGuia,
                                     String documento) {

        Envio envio = buscarPaquete(numeroGuia, documento);

        if (envio == null) {
            return false;
        }

        envio.setFechaRetiro(LocalDateTime.now());
        envio.setEstado(EstadoEnvio.ENTREGADO);

        return envioDAO.actualizar(envio);
    }
}
