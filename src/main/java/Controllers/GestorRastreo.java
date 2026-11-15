package Controllers;

import DAOs.EnvioDAO;
import Entities.Camion;
import Entities.Envio;
import Entities.Paquete;
import Entities.Salida;
import Enumerations.EstadoEnvio;

/**
 * Controlador según el Diagrama de Comunicación: Rastrear Envío
 * Interacciones:
 *  1. Ingresar Número Guía
 *  2. Buscar paquete -> GestorRastreo
 *  3. Revisar Estado y Movimientos -> Guia/Envio
 *  4. Entregar Estado en Tránsito
 *  5. Identificar Vehículo -> Salida / Hoja de ruta
 *  6. Entregar Vehículo -> Camión
 *  7. Solicitar ubicación vehículo -> InterfazConexionGPS
 *  8. Consultar posición actual -> Sistema GPS
 *  9. Proporcionar Posición -> InterfazConexionGPS
 *  9.1 Entregar Posición y hora -> GestorRastreo
 *  10. Informar Ubicación -> InterfazRastreoEnvio
 *  11. Mostrar Estado y ubicación
 */
public class GestorRastreo {
    private final EnvioDAO envioDAO;
    private final InterfazConexionGPS conexionGPS;

    public GestorRastreo() {
        this.envioDAO = new EnvioDAO();
        this.conexionGPS = new InterfazConexionGPS();
    }

    public static class ResultadoRastreo {
        private final Envio envio;
        private final EstadoEnvio estado;
        private final String vehiculo;
        private final InterfazConexionGPS.PosicionGPS posicionGPS;
        private final String mensaje;

        public ResultadoRastreo(Envio envio, EstadoEnvio estado, String vehiculo, InterfazConexionGPS.PosicionGPS posicionGPS, String mensaje) {
            this.envio = envio;
            this.estado = estado;
            this.vehiculo = vehiculo;
            this.posicionGPS = posicionGPS;
            this.mensaje = mensaje;
        }

        public Envio getEnvio() { return envio; }
        public EstadoEnvio getEstado() { return estado; }
        public String getVehiculo() { return vehiculo; }
        public InterfazConexionGPS.PosicionGPS getPosicionGPS() { return posicionGPS; }
        public String getMensaje() { return mensaje; }
    }

    public ResultadoRastreo rastrear(String numeroGuia) {
        if (numeroGuia == null || numeroGuia.isBlank()) {
            throw new IllegalArgumentException("Debe ingresar el número de guía a rastrear.");
        }

        // 2. Buscar paquete / envío
        Envio envio = envioDAO.buscarPorNumeroGuia(numeroGuia);
        if (envio == null) {
            return new ResultadoRastreo(null, null, null, null, "No se encontró ningún envío registrado con la guía: " + numeroGuia);
        }

        // 3. Revisar Estado y Movimientos
        EstadoEnvio estado = envio.getEstado();
        Salida salida = envio.getSalida();
        Camion camion = (salida != null) ? salida.getCamion() : null;
        String infoVehiculo = (camion != null) ? camion.getPlaca() + " (" + camion.getChofer() + ")" : "Sin asignar";

        InterfazConexionGPS.PosicionGPS posGPS = null;

        // 4, 5, 6, 7. Si está en tránsito o tiene camión asignado, consultar GPS
        if (camion != null) {
            posGPS = conexionGPS.consultarPosicionActual(camion.getPlaca());
        }

        StringBuilder sb = new StringBuilder();
        sb.append("DETALLES DEL RASTREO\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("• Guía: ").append(envio.getNumeroGuia()).append("\n");
        sb.append("• Estado actual: ").append(estado != null ? estado.toString() : "Registrado").append("\n");
        sb.append("• Destino: ").append(envio.getDestino()).append("\n");
        if (envio.getRemitente() != null) {
            sb.append("• Remitente: ").append(envio.getRemitente().getNombre()).append("\n");
        }
        if (envio.getDestinatario() != null) {
            sb.append("• Destinatario: ").append(envio.getDestinatario().getNombre()).append(" (CI: ").append(envio.getDestinatario().getIdentificacion()).append(")\n");
        }
        Paquete p = envio.getPaquete();
        if (p != null) {
            sb.append("• Paquete: ").append(p.getDescripcion()).append(" (").append(p.getPeso()).append(" kg)\n");
            if (p.getNovedad() != null) {
                sb.append("  ⚠️ Novedad reportada: ").append(p.getNovedad().toString()).append("\n");
            }
        }
        sb.append("• Vehículo asignado: ").append(infoVehiculo).append("\n");

        if (posGPS != null) {
            sb.append("• Ubicación satelital (GPS): ").append(posGPS.getUbicacion()).append("\n");
            sb.append("  Coordenadas: ").append(posGPS.getLatitud()).append(", ").append(posGPS.getLongitud()).append("\n");
        } else {
            sb.append("• Ubicación: En bodega central de despacho (Aún no embarcado)\n");
        }

        return new ResultadoRastreo(envio, estado, infoVehiculo, posGPS, sb.toString());
    }
}
