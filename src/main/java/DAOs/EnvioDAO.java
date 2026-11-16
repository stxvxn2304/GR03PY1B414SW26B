package DAOs;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import Entities.Envio;

public class EnvioDAO {
    private static final List<Envio> registros = new ArrayList<>();
    // CREATE
    public boolean guardar(Envio envio) {
        if (envio == null || buscarPorId(envio.getIdEnvio()) != null) {
            return false; 
        }
        registros.add(envio);
        return true;
    }
    // READ (uno)
    public Envio buscarPorId(int idEnvio) {
        for (Envio envio : registros) {
            if (envio.getIdEnvio() == idEnvio) {
                return envio;
            }
        }
        return null;
    }
    public Envio buscarPorNumeroGuia(String numeroGuia) {
        if (numeroGuia == null) return null;
        for (Envio envio : registros) {
            if (envio.getNumeroGuia() != null && envio.getNumeroGuia().trim().equalsIgnoreCase(numeroGuia.trim())) {
                return envio;
            }
        }
        return null;
    }
    // READ (todos)
    public List<Envio> listarTodos() {return new ArrayList<>(registros);}
    public List<Envio> listarPorFechaRegistro(LocalDate fecha) {
        List<Envio> lista = new ArrayList<>();
        if (fecha == null) return lista;
        for (Envio envio : registros) {
            if (envio.getFechaRegistro() != null && envio.getFechaRegistro().toLocalDate().isEqual(fecha)) {
                lista.add(envio);
            } }return lista;
    } 
    public List<Envio> listarPorFechaRetiro(LocalDate fecha) {
        List<Envio> lista = new ArrayList<>();
        if (fecha == null) return lista;
        for (Envio envio : registros) {
            if (envio.getFechaRetiro() != null && envio.getFechaRetiro().toLocalDate().isEqual(fecha)) {
                lista.add(envio);
            } } return lista;
    }

    // UPDATE
    public boolean actualizar(Envio envio) {
        if (envio == null) return false;
        for (int i = 0; i < registros.size(); i++) {
            if (registros.get(i).getIdEnvio() == envio.getIdEnvio()) {
                registros.set(i, envio);
                return true;
            } } return false; 
    }

    // DELETE
    public boolean eliminar(int idEnvio) {
        Envio envio = buscarPorId(idEnvio);
        if (envio == null) return false;
        return registros.remove(envio);
    }
}
