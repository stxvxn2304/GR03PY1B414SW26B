package Controllers;

import java.time.LocalDateTime;
import java.util.List;
import DAOs.CamionDAO;
import DAOs.EnvioDAO;
import DAOs.GuiaRemisionDAO;
import DAOs.PaqueteDAO;
import DAOs.SalidaDAO;
import Entities.Camion;
import Entities.Envio;
import Entities.GuiaRemision;
import Entities.Paquete;
import Entities.Salida;
import Enumerations.EstadoEnvio;
import Enumerations.EstadoGuiaRemision;

/**
 * Controlador según el Diagrama de Comunicación: Embarcar Encomienda
 * Interacciones: 1.1 Buscar Camión -> Obtener registro Camión 2.1 Buscar Salida
 * -> Obtener registro Salida 3.1 Asignar Salida a Camión 3.2 Identificar
 * Paquetes de Salida -> Obtener registros de Paquetes 3.3 Emitir Guía de
 * Remisión -> Registrar Guía de Remisión 4.1 Registrar salida de Paquetes ->
 * Actualizar Estado de Paquetes (EN_TRÁNSITO)
 */
public class EmbarcarEncomienda {
    private final CamionDAO camionDAO;
    private final SalidaDAO salidaDAO;
    private final GuiaRemisionDAO guiaRemisionDAO;
    private final EnvioDAO envioDAO;
    private final PaqueteDAO paqueteDAO;
    private static int secuenciaGuiaRemision = 100;

    public EmbarcarEncomienda() {
        this.camionDAO = new CamionDAO();
        this.salidaDAO = new SalidaDAO();
        this.guiaRemisionDAO = new GuiaRemisionDAO();
        this.envioDAO = new EnvioDAO();
        this.paqueteDAO = new PaqueteDAO();
    }

    public Camion buscarCamion(String placa) {
        if (placa == null || placa.isBlank())
            return null;
        return camionDAO.buscarPorPlaca(placa);
    }

    public Salida buscarSalida(int idSalida) {
        return salidaDAO.buscarPorId(idSalida);
    }

    public List<Salida> listarSalidasPendientes() {
        return salidaDAO.listarTodos().stream().filter(salida -> salida.getGuiaRemision() == null).toList();
    }

    public GuiaRemision confirmarEmbarque(String placa, int idSalida) {
        return confirmarEmbarque(placa, idSalida, false);
    }

    public GuiaRemision confirmarEmbarque(String placa, int idSalida, boolean autorizarCambioCamion) {
        Camion camion = buscarCamion(placa);
        if (camion == null) {
            throw new IllegalArgumentException("No se encontró un camión con la placa: " + placa);
        }

        Salida salida = buscarSalida(idSalida);
        if (salida == null) {
            throw new IllegalArgumentException("No se encontró la salida con ID: " + idSalida);
        }

        if (salida.getGuiaRemision() != null) {
            throw new IllegalArgumentException("Esta salida ya tiene la guía de remisión "
                    + salida.getGuiaRemision().getNumeroGuiaRemision() + ". No se puede emitir otra.");
        }

        if (salida.getCamion() != null && !salida.getCamion().getPlaca().equalsIgnoreCase(camion.getPlaca())
                && !autorizarCambioCamion) {

            throw new IllegalArgumentException("La salida #" + idSalida + " está asignada al camión "
                    + salida.getCamion().getPlaca() + ". Confirme el cambio de camión antes de embarcar.");
        }
        boolean hayEnvios = envioDAO.listarTodos().stream()
                .anyMatch(envio -> envio.getEstado() == EstadoEnvio.REGISTRADO && envio.getDestino() != null
                        && envio.getDestino().equalsIgnoreCase(salida.getDestino())
                        && (envio.getSalida() == null || envio.getSalida().getIdSalida() == salida.getIdSalida()));

        if (!hayEnvios) {
            throw new IllegalArgumentException(
                    "No hay encomiendas registradas para embarcar hacia " + salida.getDestino() + ".");
        }

        // 3.1 Asignar Salida a Camión
        salida.setCamion(camion);

        // 3.2 Identificar Paquetes de Salida
        // Asociar envíos que tengan como destino el de la salida y que aún no hayan
        // salido
        List<Envio> todos = envioDAO.listarTodos();
        for (Envio e : todos) {
            if (e.getDestino() != null && e.getDestino().equalsIgnoreCase(salida.getDestino())
                    && e.getEstado() == EstadoEnvio.REGISTRADO
                    && (e.getSalida() == null || e.getSalida().getIdSalida() == salida.getIdSalida())) {

                if (!salida.getPaquetes().contains(e)) {
                    salida.getPaquetes().add(e);
                    e.setSalida(salida);
                    envioDAO.actualizar(e);
                }
            }
        }

        // 3.3 Emitir Guía de Remisión
        int num = ++secuenciaGuiaRemision;
        String numGR = "GR-" + String.format("%04d", num);
        GuiaRemision gr = new GuiaRemision(numGR, LocalDateTime.now(), salida);
        gr.setEstado(EstadoGuiaRemision.EMITIDA);

        salida.setGuiaRemision(gr);
        salidaDAO.actualizar(salida);
        guiaRemisionDAO.guardar(gr);

        return gr;
    }

    public int confirmarSalidaPaquetes(int idSalida) {
        Salida salida = buscarSalida(idSalida);
        if (salida == null) {
            throw new IllegalArgumentException("Salida no encontrada: " + idSalida);
        }

        int actualizados = 0;

        if (salida.getGuiaRemision() == null) {
            throw new IllegalArgumentException("Primero debe confirmar el embarque y emitir la guía de remisión.");
        }

        if (salida.getPaquetes().isEmpty()) {
            throw new IllegalArgumentException("La salida no tiene encomiendas para despachar.");
        }

        boolean todosSalieron = salida.getPaquetes().stream()
                .allMatch(envio -> envio.getEstado() != EstadoEnvio.REGISTRADO);

        if (todosSalieron) {
            throw new IllegalArgumentException(
                    "La salida #" + idSalida + " ya fue despachada. No se puede repetir el despacho.");
        }

        boolean algunoSalio = salida.getPaquetes().stream()
                .anyMatch(envio -> envio.getEstado() != EstadoEnvio.REGISTRADO);

        if (algunoSalio) {
            throw new IllegalArgumentException("La salida #" + idSalida + " contiene envíos que ya salieron. "
                    + "Revise sus estados antes de despachar.");
        }
        for (Envio envio : salida.getPaquetes()) {
            if (envio.getEstado() != EstadoEnvio.REGISTRADO) {
                throw new IllegalArgumentException("No se puede despachar: la guía " + envio.getNumeroGuia()
                        + " está en estado " + envio.getEstado() + ".");
            }
        }

        for (Envio e : salida.getPaquetes()) {
            e.setEstado(EstadoEnvio.EN_TRANSITO);
            envioDAO.actualizar(e);

            Paquete p = e.getPaquete();
            if (p != null) {
                p.setEstado(EstadoEnvio.EN_TRANSITO);
                paqueteDAO.actualizar(p);
            }
            actualizados++;
        }

        return actualizados;
    }
}
