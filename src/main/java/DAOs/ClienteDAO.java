package DAOs;

import java.util.ArrayList;
import java.util.List;
import Entities.Cliente;

public class ClienteDAO {
    private static final List<Cliente> registros = new ArrayList<>();

    static {
        registros.add(new Cliente(1, "1710000001", "Juan Pérez", "0991112222", "Av. Amazonas 100"));
        registros.add(new Cliente(2, "0920000002", "María López", "0983334444", "Calle 9 de Octubre 200"));
    }

    // CREATE
    public boolean guardar(Cliente cliente) {
        if (cliente == null || buscarPorId(cliente.getIdCliente()) != null) {
            return false; 
        }
        registros.add(cliente);
        return true;
    }

    // READ (uno)
    public Cliente buscarPorId(int idCliente) {
        for (Cliente cliente : registros) {
            if (cliente.getIdCliente() == idCliente) {
                return cliente;
            }
        }
        return null;
    }

    public Cliente buscarPorIdentificacion(String identificacion) {
        if (identificacion == null) return null;
        for (Cliente cliente : registros) {
            if (cliente.getIdentificacion() != null && cliente.getIdentificacion().trim().equalsIgnoreCase(identificacion.trim())) {
                return cliente;
            }
        }
        return null;
    }

    // READ (todos)
    public List<Cliente> listarTodos() {
        return new ArrayList<>(registros);
    }

    // UPDATE
    public boolean actualizar(Cliente cliente) {
        if (cliente == null) return false;

        for (int i = 0; i < registros.size(); i++) {
            if (registros.get(i).getIdCliente() == cliente.getIdCliente()) {
                registros.set(i, cliente);
                return true;
            }
        }
        return false; 
    }

    // DELETE
    public boolean eliminar(int idCliente) {
        Cliente cliente = buscarPorId(idCliente);
        if (cliente == null) return false;
        return registros.remove(cliente);
    }
}
