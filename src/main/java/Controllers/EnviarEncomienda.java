package Controllers;

import java.time.LocalDateTime;
import DAOs.ClienteDAO;
import DAOs.CotizacionDAO;
import DAOs.EnvioDAO;
import DAOs.PaqueteDAO;
import DAOs.SalidaDAO;
import Entities.Cliente;
import Entities.Cotizacion;
import Entities.Envio;
import Entities.Paquete;
import Entities.Salida;
import Enumerations.EstadoEnvio;

/**
 * Controlador para el caso de uso: Enviar Encomienda
 */
public class EnviarEncomienda {
    private final ClienteDAO clienteDAO;
    private final PaqueteDAO paqueteDAO;
    private final EnvioDAO envioDAO;
    private final CotizacionDAO cotizacionDAO;
    private final SalidaDAO salidaDAO;

    private static int secuenciaCliente = 10;
    private static int secuenciaPaquete = 100;
    private static int secuenciaEnvio = 1000;
    private static int secuenciaSalida = 10;
    //metodo para consultar la cotizacion antes de enviar, es obligatorio
    public Cotizacion consultarCotizacion(int idCotizacion) {
        Cotizacion cot = cotizacionDAO.buscarPorId(idCotizacion);
        if (cot == null) {
            throw new IllegalArgumentException("Debe cotizar antes de enviar.");
        }
        return cot;
    }

    public EnviarEncomienda() {
        this.clienteDAO = new ClienteDAO();
        this.paqueteDAO = new PaqueteDAO();
        this.envioDAO = new EnvioDAO();
        this.cotizacionDAO = new CotizacionDAO();
        this.salidaDAO = new SalidaDAO();
    }

    public Cliente registrarRemitente(String identificacion, String nombre, String telefono, String direccion) {
        if (identificacion == null || identificacion.isBlank()) {
            throw new IllegalArgumentException("La cédula/RUC del remitente es obligatoria.");
        }
        Cliente existente = clienteDAO.buscarPorIdentificacion(identificacion);
        if (existente != null) {
            if (nombre != null && !nombre.isBlank()) existente.setNombre(nombre);
            if (telefono != null && !telefono.isBlank()) existente.setTelefono(telefono);
            if (direccion != null && !direccion.isBlank()) existente.setDireccion(direccion);
            clienteDAO.actualizar(existente);
            return existente;
        }
        Cliente nuevo = new Cliente(++secuenciaCliente, identificacion.trim(), nombre, telefono, direccion);
        clienteDAO.guardar(nuevo);
        return nuevo;
    }

    public Cliente registrarDestinatario(String identificacion, String nombre, String telefono, String direccion) {
        if (identificacion == null || identificacion.isBlank()) {
            throw new IllegalArgumentException("La cédula/RUC del destinatario es obligatoria.");
        }
        Cliente existente = clienteDAO.buscarPorIdentificacion(identificacion);
        if (existente != null) {
            if (nombre != null && !nombre.isBlank()) existente.setNombre(nombre);
            if (telefono != null && !telefono.isBlank()) existente.setTelefono(telefono);
            if (direccion != null && !direccion.isBlank()) existente.setDireccion(direccion);
            clienteDAO.actualizar(existente);
            return existente;
        }
        Cliente nuevo = new Cliente(++secuenciaCliente, identificacion.trim(), nombre, telefono, direccion);
        clienteDAO.guardar(nuevo);
        return nuevo;
    }

    public Paquete registrarPaquete(Cotizacion cot, String descripcion) {
        int id = ++secuenciaPaquete;
        Paquete p = new Paquete(id, descripcion, cot.getPeso(), cot.getAlto(), cot.getLargo(), cot.getAncho());
        p.setEstado(EstadoEnvio.REGISTRADO);
        paqueteDAO.guardar(p);
        return p;
    }

    public Salida asignarSalida(String destino) {
        // Busca una salida programada hacia ese destino
        for (Salida s : salidaDAO.listarTodos()) {
            if (s.getDestino() != null && s.getDestino().equalsIgnoreCase(destino) && s.getGuiaRemision() == null) {
                return s;
            }
        }
        // Si no existe, genera una nueva salida planificada
        Salida nueva = new Salida(++secuenciaSalida, destino, LocalDateTime.now().plusHours(3));
        salidaDAO.guardar(nueva);
        return nueva;
    }

    public Envio finalizarEnvio(Cotizacion cot, Cliente remitente, Cliente destinatario, Paquete paquete) {
        if (cot == null) {
            throw new IllegalArgumentException("Debe cotizar antes de enviar.");
        }
        Salida salida = asignarSalida(cot.getDestino());
        int idEnvio = ++secuenciaEnvio;
        String numeroGuia = "GUI-" + String.format("%04d", idEnvio);

        Envio envio = new Envio(idEnvio, cot, paquete, cot.getDestino(), remitente, destinatario, salida, numeroGuia, LocalDateTime.now());
        envio.setEstado(EstadoEnvio.REGISTRADO);
        envioDAO.guardar(envio);

        salida.getPaquetes().add(envio);
        salidaDAO.actualizar(salida);
        return envio;
    }
}
