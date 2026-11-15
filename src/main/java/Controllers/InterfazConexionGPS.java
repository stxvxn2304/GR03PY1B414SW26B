package Controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Adaptador / Interfaz de Conexión GPS según el diagrama de comunicación de Rastrear Envío.
 * Consulta la posición actual del vehículo al Sistema GPS.
 */
public class InterfazConexionGPS {

    public static class PosicionGPS {
        private final String ubicacion;
        private final double latitud;
        private final double longitud;
        private final LocalDateTime hora;

        public PosicionGPS(String ubicacion, double latitud, double longitud, LocalDateTime hora) {
            this.ubicacion = ubicacion;
            this.latitud = latitud;
            this.longitud = longitud;
            this.hora = hora;
        }

        public String getUbicacion() { return ubicacion; }
        public double getLatitud() { return latitud; }
        public double getLongitud() { return longitud; }
        public LocalDateTime getHora() { return hora; }

        @Override
        public String toString() {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return ubicacion + " [Lat: " + latitud + ", Lon: " + longitud + "] a las " + hora.format(fmt);
        }
    }

    private static final Map<String, PosicionGPS> simGPS = new HashMap<>();

    static {
        simGPS.put("ABC-1234", new PosicionGPS("Vía Panamericana Sur Km 84 - Sector Lasso", -0.7423, -78.6124, LocalDateTime.now()));
        simGPS.put("XYZ-9876", new PosicionGPS("Vía Alóag - Santo Domingo Km 42", -0.3842, -78.8911, LocalDateTime.now()));
        simGPS.put("PBA-5544", new PosicionGPS("Terminal Terrestre Quitumbe, Quito", -0.2982, -78.5492, LocalDateTime.now()));
    }

    public PosicionGPS consultarPosicionActual(String placaVehiculo) {
        if (placaVehiculo == null) {
            return new PosicionGPS("Ubicación satelital no disponible", 0.0, 0.0, LocalDateTime.now());
        }
        PosicionGPS pos = simGPS.get(placaVehiculo.trim().toUpperCase());
        if (pos == null) {
            return new PosicionGPS("En ruta principal hacia destino (" + placaVehiculo + ")", -0.2298, -78.5249, LocalDateTime.now());
        }
        return pos;
    }
}
