package DAOs;

import java.util.ArrayList;
import java.util.List;
import Entities.Tarifa;

public class TarifaDAOLista {
    private static final List<Tarifa> registros = new ArrayList<>();

    static {
        // Tarifas para distintas ciudades
        String[] destinos = {"Quito", "Guayaquil", "Cuenca", "Ambato", "Riobamba", "Loja", "Manta", "Ibarra"};
        int id = 1;
        for (String d : destinos) {
            registros.add(new Tarifa(id++, d, 0.0f, 5.0f, 4.50f));
            registros.add(new Tarifa(id++, d, 5.0f, 15.0f, 8.50f));
            registros.add(new Tarifa(id++, d, 15.0f, 50.0f, 16.00f));
        }
    }

    // CREATE
    public boolean guardar(Tarifa tarifa) {
        if (tarifa == null || buscarPorId(tarifa.getIdTarifa()) != null) {
            return false;
        }
        registros.add(tarifa);
        return true;
    }

    // READ (uno por ID)
    public Tarifa buscarPorId(int idTarifa) {
        for (Tarifa tarifa : registros) {
            if (tarifa.getIdTarifa() == idTarifa) {
                return tarifa;
            }
        }
        return null;
    }

    // READ
    public Tarifa buscarTarifa(String destino, float peso) {
        if (destino == null) return null;
        for (Tarifa tarifa : registros) {
            if (tarifa.getDestino() != null && tarifa.getDestino().equalsIgnoreCase(destino.trim()) && peso > tarifa.getPesoMin() && peso <= tarifa.getPesoMax()) {
                return tarifa;
            }
        }
        return null; // si no entra en los rangos predefinidos
    }

    // READ
    public Tarifa obtenerTarifaMaxima(String destino) {
        if (destino == null) return null;
        Tarifa mayor = null;
        for (Tarifa tarifa : registros) {
            if (tarifa.getDestino() != null && tarifa.getDestino().equalsIgnoreCase(destino.trim())) {
                if (mayor == null || tarifa.getPesoMax() > mayor.getPesoMax()) {
                    mayor = tarifa;
                }
            }
        }
        return mayor; // si la ciudad no tiene tarifas
    }

    // READ (todos)
    public List<Tarifa> listarTodos() {
        return new ArrayList<>(registros);
    }

    // UPDATE
    public boolean actualizar(Tarifa tarifa) {
        if (tarifa == null) return false;
        for (int i = 0; i < registros.size(); i++) {
            if (registros.get(i).getIdTarifa() == tarifa.getIdTarifa()) {
                registros.set(i, tarifa);
                return true;
            }
        }
        return false;
    }

    // DELETE
    public boolean eliminar(int idTarifa) {
        Tarifa tarifa = buscarPorId(idTarifa);
        if (tarifa == null) return false;
        return registros.remove(tarifa);
    }
}