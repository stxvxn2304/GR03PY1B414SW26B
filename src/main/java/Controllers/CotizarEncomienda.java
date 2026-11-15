package Controllers;

import java.time.LocalDate;
import DAOs.CotizacionDAO;
import DAOs.TarifaDAOLista;
import Entities.Cotizacion;
import Entities.Tarifa;

/**
 * Controlador para el caso de uso cotizar envío
 */
public class CotizarEncomienda {
    private final CotizacionDAO cotizacionDAO;
    private final TarifaDAOLista tarifaDAO;
    private static int secuenciaId = 100;
    // Divisor del peso volumétrico
    private static final float DIVISOR_VOLUMETRICO = 5000.0f;
    // Recargo por cada kg que excede el último rango de la tarifa.
    private static final float RECARGO_KG_EXTRA = 1.50f;

    public CotizarEncomienda() {
        this.cotizacionDAO = new CotizacionDAO();
        this.tarifaDAO = new TarifaDAOLista();
    }
    //metodos para parsear el contenido de las medidas y peso
    public static float parsearPeso(String t) {
        try { return Float.parseFloat(t.trim().replace(',', '.')); }
        catch (RuntimeException e) { throw new IllegalArgumentException("Ingrese el peso como número, ej. 2.5"); }
    }
    public static float[] parsearMedidas(String t) {
        String msg = "Ingrese las medidas como largo x ancho x alto, ej. 30 x 20 x 15";
        if (t == null || t.isBlank()) throw new IllegalArgumentException(msg);
        String[] p = t.toLowerCase().replace("cm", "").trim().split("\\s*[x×*]\\s*");
        if (p.length != 3) throw new IllegalArgumentException(msg);
        try { return new float[]{ Float.parseFloat(p[0].trim()), Float.parseFloat(p[1].trim()), Float.parseFloat(p[2].trim()) }; }
        catch (NumberFormatException e) { throw new IllegalArgumentException(msg); }
    }

    public Cotizacion cotizar(float peso, float largo, float ancho, float alto, String destino) {
        if (destino == null || destino.isBlank()) {
            throw new IllegalArgumentException("Debe seleccionar un destino.");
        }
        if (peso <= 0 || largo <= 0 || ancho <= 0 || alto <= 0) {
            throw new IllegalArgumentException("El peso y las dimensiones deben ser mayores a 0.");
        }

        // calcular el pesoVolumentrico y sacar el mayor entre pesoVolumetrico y peso en kg
        float pesoVolumetrico = (largo * ancho * alto) / DIVISOR_VOLUMETRICO;
        float pesoACobrar = Math.max(peso, pesoVolumetrico);

        // Tarifa del destino para el peso a cobrar
        Tarifa tarifa = tarifaDAO.buscarTarifa(destino, pesoACobrar);
        float valor;

        if (tarifa != null) {
            valor = tarifa.getValor();
        } else {
            // El peso se pasa del último rango: tarifa máxima del destino + recargo por kg extra
            tarifa = tarifaDAO.obtenerTarifaMaxima(destino);
            if (tarifa == null) {
                throw new IllegalArgumentException("No hay tarifas registradas para el destino: " + destino);
            }
            float kgAdicional = pesoACobrar - tarifa.getPesoMax();
            valor = tarifa.getValor() + (kgAdicional * RECARGO_KG_EXTRA);
        }

        int id = ++secuenciaId;
        Cotizacion cot = new Cotizacion(id, LocalDate.now(), peso, alto, largo, ancho, destino.trim());
        cot.setTarifa(tarifa);
        cot.setPesoCobrado(pesoACobrar);
        cot.setValorEstimado(Math.round(valor * 100.0f) / 100.0f);

        cotizacionDAO.guardar(cot);
        return cot;
    }
}