package DAOs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import Entities.Camion;
import Entities.Salida;

public class SalidaDAO {
    private static final List<Salida> registros = new ArrayList<>();

    static {
        CamionDAO camionDAO = new CamionDAO();

        Salida s1 = new Salida(
            1, "Guayaquil", LocalDateTime.now().plusHours(2)
        );
        s1.setCamion(camionDAO.buscarPorPlaca("ABC-1234"));
        registros.add(s1);

        Salida s2 = new Salida(
            2, "Cuenca", LocalDateTime.now().plusHours(4)
        );
        s2.setCamion(camionDAO.buscarPorPlaca("XYZ-9876"));
        registros.add(s2);

        Salida s3 = new Salida(
            3, "Quito", LocalDateTime.now().plusHours(1)
        );
        s3.setCamion(camionDAO.buscarPorPlaca("PBA-5544"));
        registros.add(s3);
    }

    // CREATE
    public boolean guardar(Salida salida) {
        if (salida == null || buscarPorId(salida.getIdSalida()) != null) {
            return false; 
        }
        registros.add(salida);
        return true;
    }

    // READ (uno)
    public Salida buscarPorId(int idSalida) {
        for (Salida salida : registros) {
            if (salida.getIdSalida() == idSalida) {
                return salida;
            }
        }
        return null;
    }

    public List<Salida> listarPorFecha(LocalDate fecha) {
        List<Salida> lista = new ArrayList<>();
        if (fecha == null) return lista;
        for (Salida salida : registros) {
            if (salida.getFechaHora() != null && salida.getFechaHora().toLocalDate().isEqual(fecha)) {
                lista.add(salida);
            }
        }
        return lista;
    }

    // READ (todos)
    public List<Salida> listarTodos() {
        return new ArrayList<>(registros);
    }

    // UPDATE
    public boolean actualizar(Salida salida) {
        if (salida == null) return false;

        for (int i = 0; i < registros.size(); i++) {
            if (registros.get(i).getIdSalida() == salida.getIdSalida()) {
                registros.set(i, salida);
                return true;
            }
        }
        return false; 
    }

    // DELETE
    public boolean eliminar(int idSalida) {
        Salida salida = buscarPorId(idSalida);
        if (salida == null) return false;
        return registros.remove(salida);
    }
}
