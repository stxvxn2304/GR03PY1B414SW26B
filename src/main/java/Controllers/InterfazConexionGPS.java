
package Controllers;

import Entities.Envio;
import Enumerations.EstadoEnvio;

public class InterfazConexionGPS {

    public String obtenerUbicacion(Envio envio) {
        if (envio == null) {
            return "Ubicación no disponible.";
        }

        EstadoEnvio estado = envio.getEstado();

        if (estado == EstadoEnvio.REGISTRADO) {
            return "GPS simulado: pendiente de salida.";
        }

        if (estado == EstadoEnvio.RECIBIDO) {
            return "GPS simulado: encomienda recibida en "
                    + envio.getDestino();
        }

        if (estado == EstadoEnvio.CON_NOVEDAD) {
            return "GPS simulado: ubicación no confirmada "
                    + "debido a una novedad.";
        }

        if (estado == EstadoEnvio.ENTREGADO) {
            return "GPS simulado: entrega finalizada.";
        }

        if (envio.getSalida() == null
                || envio.getSalida().getCamion() == null) {
            return "GPS simulado: vehículo sin ubicación asignada.";
        }

        String placa = envio.getSalida().getCamion().getPlaca();

        return "GPS simulado: vehículo " + placa
                + " en ruta hacia " + envio.getDestino()
                + ". Posición exacta no disponible.";
    }
}
