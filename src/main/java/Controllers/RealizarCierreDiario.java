package Controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import DAOs.EnvioDAO;
import DAOs.GuiaRemisionDAO;
import DAOs.ReporteDiarioDAO;
import DAOs.SalidaDAO;
import Entities.Envio;
import Entities.GuiaRemision;
import Entities.ReporteDiario;
import Entities.Salida;
import Enumerations.EstadoEnvio;

/**
 * Controlador Para realizar el cierre Diario
 */
public class RealizarCierreDiario {
    private final ReporteDiarioDAO reporteDiarioDAO;
    private final EnvioDAO envioDAO;
    private final GuiaRemisionDAO guiaRemisionDAO;
    private final SalidaDAO salidaDAO;
    private static int secuenciaReporte = 0;

    public RealizarCierreDiario() {
        this.reporteDiarioDAO = new ReporteDiarioDAO();
        this.envioDAO = new EnvioDAO();
        this.guiaRemisionDAO = new GuiaRemisionDAO();
        this.salidaDAO = new SalidaDAO();
    }

    public ReporteDiario generarReporte(LocalDate fecha) {
        if (fecha == null) {
            fecha = LocalDate.now();
        }

        // Consultar Envíos registrados en la fecha
        List<Envio> enviosFecha = envioDAO.listarPorFechaRegistro(fecha);
        int totalEnvios = enviosFecha.size();

        // Si no hay envíos registrados exactamente en esa fecha, pero hay registrados en el sistema,
        // considerar los que coincidan con la fecha actual o listar los disponibles
        float totalCobrado = 0.0f;
        for (Envio e : enviosFecha) {
            if (e.getCotizacion() != null) {
                totalCobrado += e.getCotizacion().getValorEstimado();
            }
        }

        // Guías de remisión emitidas en esa fecha
        int totalGuias = 0;
        for (GuiaRemision gr : guiaRemisionDAO.listarTodos()) {
            if (gr.getFechaEmision() != null && gr.getFechaEmision().toLocalDate().isEqual(fecha)) {
                totalGuias++;
            }
        }

        // Viajes / Salidas despachadas en la fecha
        int totalViajes = 0;
        for (Salida sal : salidaDAO.listarTodos()) {
            GuiaRemision g = sal.getGuiaRemision();
            if (g != null && g.getFechaEmision() != null && g.getFechaEmision().toLocalDate().isEqual(fecha)) {
                totalViajes++;
            }
        }

        // Inventario activo (paquetes registrados o recibidos que no han sido entregados)
        int totalInventario = 0;
        for (Envio e : envioDAO.listarTodos()) {
            if (e.getEstado() != EstadoEnvio.ENTREGADO) {
                totalInventario++;
            }
        }

        int idReporte = ++secuenciaReporte;
        ReporteDiario reporte = new ReporteDiario(idReporte, fecha, totalEnvios, totalGuias, totalViajes, totalInventario, totalCobrado);
        reporteDiarioDAO.guardar(reporte);

        return reporte;
    }

    public String obtenerReporteFormateado(LocalDate fecha) {
        ReporteDiario r = generarReporte(fecha);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        StringBuilder sb = new StringBuilder();
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("          REPORTE DE CIERRE DIARIO               \n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("• Reporte ID:       #").append(r.getIdReporte()).append("\n");
        sb.append("• Fecha de balance: ").append(r.getFecha().format(fmt)).append("\n");
        sb.append("• Total de Envíos:  ").append(r.getTotalEnvios()).append(" registrados\n");
        sb.append("• Guías Remisión:   ").append(r.getTotalGuias()).append(" emitidas\n");
        sb.append("• Salidas / Viajes: ").append(r.getTotalViajes()).append(" despachados\n");
        sb.append("• Stock Inventario: ").append(r.getTotalInventario()).append(" encomiendas activas\n");
        sb.append("• Total Recaudado:  $").append(String.format("%.2f", r.getTotalCobrado())).append(" USD\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("Estado contable y operativo cerrado satisfactoriamente.");

        return sb.toString();
    }
}
