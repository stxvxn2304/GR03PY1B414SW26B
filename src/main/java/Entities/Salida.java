package Entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Salida {
    private int idSalida;
    private String destino;
    private LocalDateTime fechaHora;
    private GuiaRemision guiaRemision;
    private List<Envio> paquetes;
    private Camion camion;
    
    public Salida(int idSalida, String destino, LocalDateTime fechaHora) {
        this.idSalida = idSalida;
        this.destino = destino;
        this.fechaHora = fechaHora;
        this.guiaRemision = null;
        this.paquetes = new ArrayList<>();
        this.camion = null;
    }
    public int getIdSalida() {
        return idSalida;
    }
    public void setIdSalida(int idSalida) {
        this.idSalida = idSalida;
    }
    public String getDestino() {
        return destino;
    }
    public void setDestino(String destino) {
        this.destino = destino;
    }
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
    public GuiaRemision getGuiaRemision() {
        return guiaRemision;
    }
    public void setGuiaRemision(GuiaRemision guiaRemision) {
        this.guiaRemision = guiaRemision;
    }
    public List<Envio> getPaquetes() {
        if (paquetes == null) paquetes = new ArrayList<>();
        return paquetes;
    }
    public void setPaquetes(List<Envio> paquetes) {
        this.paquetes = paquetes;
    }
    public Camion getCamion() {
        return camion;
    }
    public void setCamion(Camion camion) {
        this.camion = camion;
    }

    @Override
    public String toString() {
        String infoCamion = (camion != null) ? " [" + camion.getPlaca() + "]" : " [Sin camión]";
        return "Salida #" + idSalida + " -> " + destino + " (" + fechaHora + ")" + infoCamion;
    }
}
