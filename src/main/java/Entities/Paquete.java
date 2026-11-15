package Entities;

import Enumerations.EstadoEnvio;

public class Paquete {
    private int idPaquete;
    private String descripcion;
    private float peso;
    private float alto;
    private float largo;
    private float ancho;
    private Novedad novedad;
    private EstadoEnvio estado;
    
    public Paquete(int idPaquete, String descripcion, float peso, float alto, float largo, float ancho) {
        this.idPaquete = idPaquete;
        this.descripcion = descripcion;
        this.peso = peso;
        this.alto = alto;
        this.largo = largo;
        this.ancho = ancho;
        this.novedad = null;
        this.estado = EstadoEnvio.REGISTRADO;
    }
    
    public int getIdPaquete() {
        return idPaquete;
    }
    public void setIdPaquete(int idPaquete) {
        this.idPaquete = idPaquete;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public float getPeso() {
        return peso;
    }
    public void setPeso(float peso) {
        this.peso = peso;
    }
    public float getAlto() {
        return alto;
    }
    public void setAlto(float alto) {
        this.alto = alto;
    }
    public float getLargo() {
        return largo;
    }
    public void setLargo(float largo) {
        this.largo = largo;
    }
    public float getAncho() {
        return ancho;
    }
    public void setAncho(float ancho) {
        this.ancho = ancho;
    }
    public Novedad getNovedad() {
        return novedad;
    }
    public void setNovedad(Novedad novedad) {
        this.novedad = novedad;
    }
    public EstadoEnvio getEstado() {
        return estado;
    }
    public void setEstado(EstadoEnvio estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Paquete #" + idPaquete + " [" + descripcion + "] " + peso + "kg (" + largo + "x" + ancho + "x" + alto + " cm) - Estado: " + (estado != null ? estado : "Registrado");
    }
}
