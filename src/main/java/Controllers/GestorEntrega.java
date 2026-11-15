package Controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import DAOs.EnvioDAO;
import DAOs.PaqueteDAO;
import Entities.Cliente;
import Entities.Envio;
import Entities.Paquete;
import Enumerations.EstadoEnvio;

/**
 * Controlador según el Diagrama de Comunicación: Entregar Encomienda
 * Interacciones:
 *  1. Ingresar guía y documento
 *  2. Buscar paquete -> GestorEntrega
 *  3. Procesar solicitud de retiro -> Guia/Envio
 *  4. Entregar Información Guía
 *  5. Identificar Cliente -> Cliente
 *  6. Entregar Datos Cliente
 *  7. Descontar Paquete de Inventario -> Paquete (marcar ENTREGADO)
 *  7.1 Registrar Retiro en Guía -> Guia/Envio (fechaRetiro = now)
 *  8. Confirmar Entrega
 *  9. Confirmar Retiro
 */
public class GestorEntrega {
    private final EnvioDAO envioDAO;
    private final PaqueteDAO paqueteDAO;

    public GestorEntrega() {
        this.envioDAO = new EnvioDAO();
        this.paqueteDAO = new PaqueteDAO();
    }

    public static class ResultadoConsultaEntrega {
        private final boolean valida;
        private final Envio envio;
        private final String mensaje;

        public ResultadoConsultaEntrega(boolean valida, Envio envio, String mensaje) {
            this.valida = valida;
            this.envio = envio;
            this.mensaje = mensaje;
        }

        public boolean isValida() { return valida; }
        public Envio getEnvio() { return envio; }
        public String getMensaje() { return mensaje; }
    }

    public ResultadoConsultaEntrega buscarPaquete(String numeroGuia, String documento) {
        if (numeroGuia == null || numeroGuia.isBlank()) {
            return new ResultadoConsultaEntrega(false, null, "Debe ingresar el número de guía.");
        }
        if (documento == null || documento.isBlank()) {
            return new ResultadoConsultaEntrega(false, null, "Debe ingresar el documento de quien retira.");
        }

        // 2. Buscar paquete/envío
        Envio envio = envioDAO.buscarPorNumeroGuia(numeroGuia);
        if (envio == null) {
            return new ResultadoConsultaEntrega(false, null, "Guía no encontrada: " + numeroGuia);
        }

        // 3 y 4. Procesar solicitud y verificar estado
        if (envio.getEstado() == EstadoEnvio.ENTREGADO) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String fechaRet = (envio.getFechaRetiro() != null) ? envio.getFechaRetiro().format(fmt) : "previamente";
            return new ResultadoConsultaEntrega(false, envio, "Esta encomienda YA FUE ENTREGADA el " + fechaRet + ".");
        }

        // 5 y 6. Identificar cliente destinatario
        Cliente dest = envio.getDestinatario();
        if (dest == null) {
            return new ResultadoConsultaEntrega(false, envio, "No hay destinatario registrado en la guía.");
        }

        String docIngresado = documento.trim();
        String docDest = (dest.getIdentificacion() != null) ? dest.getIdentificacion().trim() : "";

        if (!docDest.equalsIgnoreCase(docIngresado)) {
            return new ResultadoConsultaEntrega(false, envio,
                "El documento ingresado (" + docIngresado + ") NO coincide con el destinatario autorizado (" + dest.getNombre() + " - CI: " + docDest + ").");
        }

        StringBuilder info = new StringBuilder();
        info.append("INFORMACIÓN DEL ENVÍO AUTORIZADO PARA RETIRO\n");
        info.append("• Guía: ").append(envio.getNumeroGuia()).append("\n");
        info.append("• Destinatario: ").append(dest.getNombre()).append(" (C.I. ").append(dest.getIdentificacion()).append(")\n");
        info.append("• Teléfono: ").append(dest.getTelefono()).append("\n");
        info.append("• Destino de entrega: ").append(envio.getDestino()).append("\n");
        Paquete p = envio.getPaquete();
        if (p != null) {
            info.append("• Paquete: ").append(p.getDescripcion()).append(" (").append(p.getPeso()).append(" kg)\n");
            if (p.getNovedad() != null) {
                info.append("  ⚠️ Advertencia de novedad: ").append(p.getNovedad()).append("\n");
            }
        }
        info.append("• Estado actual: ").append(envio.getEstado()).append("\n");
        info.append("Listo para confirmar la entrega y descontar del inventario.");

        return new ResultadoConsultaEntrega(true, envio, info.toString());
    }

    public boolean confirmarEntrega(String numeroGuia, String documento) {
        ResultadoConsultaEntrega consulta = buscarPaquete(numeroGuia, documento);
        if (!consulta.isValida()) {
            return false;
        }

        Envio envio = consulta.getEnvio();

        // 7. Descontar Paquete de Inventario
        Paquete paquete = envio.getPaquete();
        if (paquete != null) {
            paquete.setEstado(EstadoEnvio.ENTREGADO);
            paqueteDAO.actualizar(paquete);
        }

        // 7.1 Registrar Retiro en Guía
        envio.setFechaRetiro(LocalDateTime.now());
        envio.setEstado(EstadoEnvio.ENTREGADO);
        envioDAO.actualizar(envio);

        return true;
    }
}
