package Controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import DAOs.CamionDAO;
import DAOs.EnvioDAO;
import DAOs.GuiaRemisionDAO;
import DAOs.PaqueteDAO;
import Entities.Camion;
import Entities.Envio;
import Entities.GuiaRemision;
import Entities.Novedad;
import Entities.Paquete;
import Entities.Salida;
import Enumerations.EstadoEnvio;
import Enumerations.EstadoGuiaRemision;
import Enumerations.TipoNovedad;

/**
 * Controlador según el Diagrama de Comunicación: Recibir Encomienda
 * Interacciones: 1.1 Buscar Camión -> Obtener registro Camión, Obtener
 * registros Paquetes 2.1 Buscar Guía de Remisión -> Obtener registro Guía de
 * Remisión 3.1 Buscar Envío -> Obtener registro Paquete, Mostrar Datos Envío
 * 4.1 Registrar Novedad -> Adjuntar Novedad al registro Paquete 5.1 Registrar
 * Recepción Paquetes -> Actualizar Estado de Paquetes (RECIBIDO o CON_NOVEDAD)
 */
public class RecibirEncomienda {
    private final CamionDAO camionDAO;
    private final GuiaRemisionDAO guiaRemisionDAO;
    private final EnvioDAO envioDAO;
    private final PaqueteDAO paqueteDAO;

    public RecibirEncomienda() {
        this.camionDAO = new CamionDAO();
        this.guiaRemisionDAO = new GuiaRemisionDAO();
        this.envioDAO = new EnvioDAO();
        this.paqueteDAO = new PaqueteDAO();
    }

    public Camion buscarCamion(String placa) {
        if (placa == null || placa.isBlank())
            return null;
        return camionDAO.buscarPorPlaca(placa);
    }

    public GuiaRemision buscarGuiaRemision(String numeroGuiaRemision) {
        if (numeroGuiaRemision == null || numeroGuiaRemision.isBlank())
            return null;
        return guiaRemisionDAO.buscarPorNumero(numeroGuiaRemision);
    }

    public Envio buscarEnvio(String numeroGuiaEnvio) {
        if (numeroGuiaEnvio == null || numeroGuiaEnvio.isBlank())
            return null;
        return envioDAO.buscarPorNumeroGuia(numeroGuiaEnvio);
    }

    // Valida y se explica el motivo del rechazo
    public ResultadoRecepcion validarNovedadRecepcion(String placa, String numeroGuiaRemision, String numeroGuiaEnvio,
            Integer idPaquete, String tipoStr, String descripcion) {

        ResultadoRecepcion recepcion = validarRecepcion(placa, numeroGuiaRemision, numeroGuiaEnvio);

        if (recepcion.getTotalProcesados() == 0) {
            return recepcion;
        }

        if (tipoStr == null || tipoStr.isBlank()) {
            return errorRecepcion("Seleccione el tipo de novedad.");
        }

        if ((numeroGuiaEnvio == null || numeroGuiaEnvio.isBlank()) && idPaquete == null) {
            return errorRecepcion(
                    "Indique la guía de envío individual o el ID del paquete " + "para registrar la novedad.");
        }

        if (idPaquete != null && idPaquete <= 0) {
            return errorRecepcion("El ID del paquete debe ser un entero positivo.");
        }

        Envio objetivo = buscarEnvioParaNovedad(numeroGuiaEnvio, idPaquete);

        if (objetivo == null) {
            return errorRecepcion("No se encontró un envío asociado al paquete indicado.");
        }

        if (objetivo.getPaquete() == null) {
            return errorRecepcion("El envío no tiene un paquete registrado.");
        }

        if (idPaquete != null && objetivo.getPaquete().getIdPaquete() != idPaquete) {
            return errorRecepcion("El ID del paquete no corresponde a la guía de envío.");
        }

        if (objetivo.getEstado() != EstadoEnvio.EN_TRANSITO) {
            return errorRecepcion("El paquete no está en tránsito. Estado actual: " + objetivo.getEstado() + ".");
        }
        ResultadoRecepcion pertenencia = validarRecepcion(placa, numeroGuiaRemision, objetivo.getNumeroGuia());

        if (pertenencia.getTotalProcesados() == 0) {
            return pertenencia;
        }

        if (descripcion == null || descripcion.isBlank()) {
            return errorRecepcion("Escriba una descripción de la novedad.");
        }
        return new ResultadoRecepcion(1, "Datos de novedad válidos.");
    }

    private ResultadoRecepcion errorRecepcion(String mensaje) {
        return new ResultadoRecepcion(0, "Recepción no realizada\n" + mensaje);
    }

    private Envio buscarEnvioParaNovedad(String numeroGuiaEnvio, Integer idPaquete) {

        if (numeroGuiaEnvio != null && !numeroGuiaEnvio.isBlank()) {
            return buscarEnvio(numeroGuiaEnvio);
        }

        if (idPaquete != null) {
            for (Envio envio : envioDAO.listarTodos()) {
                if (envio.getPaquete() != null && envio.getPaquete().getIdPaquete() == idPaquete) {
                    return envio;
                }
            }
        }

        return null;
    }

    public boolean registrarNovedadRecepcion(String placa, String numeroGuiaRemision, String numeroGuiaEnvio,
            Integer idPaquete, String tipoStr, String descripcion) {

        ResultadoRecepcion validacion = validarNovedadRecepcion(placa, numeroGuiaRemision, numeroGuiaEnvio, idPaquete,
                tipoStr, descripcion);

        if (validacion.getTotalProcesados() == 0) {
            return false;
        }

        Envio objetivo = buscarEnvioParaNovedad(numeroGuiaEnvio, idPaquete);

        return registrarNovedad(objetivo.getNumeroGuia(), idPaquete, tipoStr, descripcion);
    }

    // comprobar la novedad antes de guardarla y explicar por qué se rechaza
    public boolean registrarNovedad(String numeroGuiaEnvio, Integer idPaquete, String tipoStr, String descripcion) {
        Envio envio = null;
        if (numeroGuiaEnvio != null && !numeroGuiaEnvio.isBlank()) {
            envio = buscarEnvio(numeroGuiaEnvio);
        }

        Paquete paquete = null;
        if (envio != null) {
            paquete = envio.getPaquete();
        } else if (idPaquete != null && idPaquete > 0) {
            paquete = paqueteDAO.buscarPorId(idPaquete);
        }

        if (paquete == null) {
            return false;
        }

        EstadoEnvio estadoActual = envio != null ? envio.getEstado() : paquete.getEstado();

        if (estadoActual != EstadoEnvio.EN_TRANSITO) {
            return false;
        }
        if (descripcion == null || descripcion.isBlank()) {
            return false;
        }
        TipoNovedad tipo = TipoNovedad.OTRO;
        if (tipoStr != null) {
            String t = tipoStr.trim().toUpperCase();
            if (t.contains("FALTANTE"))
                tipo = TipoNovedad.FALTANTE;
            else if (t.contains("DAÑ") || t.contains("DANO"))
                tipo = TipoNovedad.DAÑADO;
            else if (t.contains("PERD") || t.contains("PÉRD")) {
                tipo = TipoNovedad.PERDIDA;
            }
        }

        Novedad nov = new Novedad(tipo, descripcion, LocalDateTime.now());
        paquete.setNovedad(nov);

        paqueteDAO.actualizar(paquete);

        if (envio != null) {
            envioDAO.actualizar(envio);
        }
        return true;
    }

    public static class ResultadoRecepcion {
        private final int totalProcesados;
        private final String detalle;

        public ResultadoRecepcion(int totalProcesados, String detalle) {
            this.totalProcesados = totalProcesados;
            this.detalle = detalle;
        }

        public int getTotalProcesados() {
            return totalProcesados;
        }

        public String getDetalle() {
            return detalle;
        }
    }

    public ResultadoRecepcion confirmarRecepcion(String placa, String numeroGuiaRemision, String numeroGuiaEnvio) {
        return procesarRecepcion(placa, numeroGuiaRemision, numeroGuiaEnvio, false);
    }

    public ResultadoRecepcion validarRecepcion(String placa, String numeroGuiaRemision, String numeroGuiaEnvio) {

        return procesarRecepcion(placa, numeroGuiaRemision, numeroGuiaEnvio, true);
    }

    private ResultadoRecepcion procesarRecepcion(String placa, String numeroGuiaRemision, String numeroGuiaEnvio,
            boolean soloValidar) {

        if (placa != null && !placa.isBlank() && buscarCamion(placa) == null) {
            return new ResultadoRecepcion(0, "RECEPCIÓN NO REALIZADA\nLa placa ingresada no existe.");
        }
        if (numeroGuiaRemision != null && !numeroGuiaRemision.isBlank()) {
            GuiaRemision guia = buscarGuiaRemision(numeroGuiaRemision);

            if (guia == null) {
                return new ResultadoRecepcion(0, "Recepcion no realizada\nLa guía de remisión no existe.");
            }

            Salida salida = guia.getSalida();

            if (salida == null || salida.getCamion() == null) {
                return new ResultadoRecepcion(0,
                        "Recepcion no realizada\n" + "La guía no tiene una salida con camión asignado.");
            }
            if (placa != null && !placa.isBlank() && !salida.getCamion().getPlaca().equalsIgnoreCase(placa.trim())) {
                return new ResultadoRecepcion(0,
                        "Recepcion no realizada\n" + "La placa no corresponde al camión de la guía de remisión.");
            }
        }

        if (numeroGuiaEnvio != null && !numeroGuiaEnvio.isBlank()) {
            Envio envio = buscarEnvio(numeroGuiaEnvio);

            if (envio == null) {
                return new ResultadoRecepcion(0, "Recepcion no realizada\nLa guía de envío no existe.");
            }

            Salida salidaEnvio = envio.getSalida();

            if (numeroGuiaRemision != null && !numeroGuiaRemision.isBlank()) {
                GuiaRemision guia = buscarGuiaRemision(numeroGuiaRemision);

                if (salidaEnvio == null || guia == null || guia.getSalida() == null
                        || salidaEnvio.getIdSalida() != guia.getSalida().getIdSalida()) {
                    return new ResultadoRecepcion(0,
                            "Recepcion no realizada\n" + "El envío no pertenece a la guía de remisión indicada.");
                }
            }

            if (placa != null && !placa.isBlank()) {
                if (salidaEnvio == null || salidaEnvio.getCamion() == null
                        || !salidaEnvio.getCamion().getPlaca().equalsIgnoreCase(placa.trim())) {
                    return new ResultadoRecepcion(0,
                            "Recepcion no realizada\n" + "El envío no pertenece al camión indicado.");
                }
            }
        }

        List<Envio> listaAProcesar = new ArrayList<>();

        // Una guía individual tiene prioridad.
        // La placa y la remisión ya se validaron arriba.
        if (numeroGuiaEnvio != null && !numeroGuiaEnvio.isBlank()) {
            Envio unico = buscarEnvio(numeroGuiaEnvio);

            if (unico != null) {
                listaAProcesar.add(unico);
            }

        } else if (numeroGuiaRemision != null && !numeroGuiaRemision.isBlank()) {

            // Por remisión, recibir solamente los pendientes.
            GuiaRemision gr = buscarGuiaRemision(numeroGuiaRemision);

            if (gr != null && gr.getSalida() != null) {
                for (Envio envio : gr.getSalida().getPaquetes()) {
                    if (envio.getEstado() == EstadoEnvio.EN_TRANSITO) {
                        listaAProcesar.add(envio);
                    }
                }
            }

        } else if (placa != null && !placa.isBlank()) {

            // Por camión, recibir solamente los pendientes.
            for (Envio envio : envioDAO.listarTodos()) {
                Salida salida = envio.getSalida();

                if (salida != null && salida.getCamion() != null
                        && salida.getCamion().getPlaca().equalsIgnoreCase(placa.trim())
                        && envio.getEstado() == EstadoEnvio.EN_TRANSITO) {

                    listaAProcesar.add(envio);
                }
            }
        }

        if (listaAProcesar.isEmpty()) {
            return new ResultadoRecepcion(0, "RECEPCIÓN NO REALIZADA\n" + "No hay envíos en tránsito para recibir "
                    + "con los datos ingresados.");
        }

        // Revisar todos los envíos antes de modificar cualquiera.
        for (Envio envio : listaAProcesar) {
            EstadoEnvio estado = envio.getEstado();

            if (estado != EstadoEnvio.EN_TRANSITO) {
                return new ResultadoRecepcion(0, "RECEPCIÓN NO REALIZADA\n" + "La guía " + envio.getNumeroGuia()
                        + " está en estado " + estado + ".");
            }
        }

        if (soloValidar) {
            return new ResultadoRecepcion(listaAProcesar.size(), "Datos de recepción válidos.");
        }

        int recibidos = 0;
        int conNovedad = 0;

        for (Envio e : listaAProcesar) {
            Paquete p = e.getPaquete();
            if (p != null && p.getNovedad() != null) {
                e.setEstado(EstadoEnvio.CON_NOVEDAD);
                p.setEstado(EstadoEnvio.CON_NOVEDAD);
                conNovedad++;
            } else {
                e.setEstado(EstadoEnvio.RECIBIDO);
                if (p != null)
                    p.setEstado(EstadoEnvio.RECIBIDO);
                recibidos++;
            }
            if (p != null)
                paqueteDAO.actualizar(p);
            envioDAO.actualizar(e);
        }

        StringBuilder sb = new StringBuilder();

        for (Envio envio : listaAProcesar) {
            Salida salida = envio.getSalida();

            if (salida == null || salida.getGuiaRemision() == null) {
                continue;
            }

            boolean todosRecibidos = salida.getPaquetes().stream().allMatch(e -> e.getEstado() == EstadoEnvio.RECIBIDO
                    || e.getEstado() == EstadoEnvio.CON_NOVEDAD || e.getEstado() == EstadoEnvio.ENTREGADO);

            if (todosRecibidos) {
                GuiaRemision gr = salida.getGuiaRemision();
                gr.setEstado(EstadoGuiaRemision.RECIBIDA);
                guiaRemisionDAO.actualizar(gr);
            }
        }

        sb.append("RECEPCIÓN CONFIRMADA EXITOSAMENTE\n");
        sb.append("• Total paquetes procesados: ").append(listaAProcesar.size()).append("\n");
        sb.append("  - Recibidos en buen estado: ").append(recibidos).append("\n");
        sb.append("  - Recibidos con novedad reportada: ").append(conNovedad).append("\n");
        for (Envio e : listaAProcesar) {
            sb.append("  ✓ Guía: ").append(e.getNumeroGuia()).append(" -> Estado: ").append(e.getEstado()).append("\n");
        }

        return new ResultadoRecepcion(listaAProcesar.size(), sb.toString());
    }
}
