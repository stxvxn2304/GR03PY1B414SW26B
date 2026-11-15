package DAOs;

import java.util.ArrayList;
import java.util.List;
import Entities.Camion;

public class CamionDAO {
    private static final List<Camion> registros = new ArrayList<>();

    static {
        // Datos iniciales de prueba
        registros.add(new Camion("ABC-1234", "Carlos Pérez"));
        registros.add(new Camion("XYZ-9876", "Juan Mendoza"));
        registros.add(new Camion("PBA-5544", "Luis Torres"));
    }

    // CREATE
    public boolean guardar(Camion camion) {
        if (camion == null || buscarPorPlaca(camion.getPlaca()) != null) {
            return false; 
        }
        registros.add(camion);
        return true;
    }

    // READ (uno)
    public Camion buscarPorPlaca(String placa) {
        if (placa == null) return null;
        for (Camion camion : registros) {
            if (camion.getPlaca().equalsIgnoreCase(placa.trim())) {
                return camion;
            }
        }
        return null;
    }

    // READ (todos)
    public List<Camion> listarTodos() {
        return new ArrayList<>(registros);
    }

    // UPDATE
    public boolean actualizar(Camion camion) {
        if (camion == null) return false;

        for (int i = 0; i < registros.size(); i++) {
            if (registros.get(i).getPlaca().equalsIgnoreCase(camion.getPlaca().trim())) {
                registros.set(i, camion);
                return true;
            }
        }
        return false; 
    }

    // DELETE
    public boolean eliminar(String placa) {
        Camion camion = buscarPorPlaca(placa);
        if (camion == null) return false;
        return registros.remove(camion);
    }
}
