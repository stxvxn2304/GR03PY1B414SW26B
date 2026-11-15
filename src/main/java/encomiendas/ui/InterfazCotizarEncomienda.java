package encomiendas.ui;
import Entities.Cotizacion;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import Controllers.CotizarEncomienda;
public class InterfazCotizarEncomienda extends PantallaBase {
    private final CotizarEncomienda controlador = new CotizarEncomienda();

    private final TextField peso=Ui.texto("Ej. 2.5"), medidas=Ui.texto("Ej. 30 x 20 x 15 cm");
    private final ComboBox<String> destino=new ComboBox<>();
    private final Label resultado=new Label("Aquí se mostrará el valor de la cotización.");

    public InterfazCotizarEncomienda(Runnable volver) {
        super("Cotizar encomienda","Ingrese peso, medidas y destino.",volver);
        destino.getItems().setAll(Ui.DESTINOS); destino.setPromptText("Seleccione un destino");
        GridPane g=Ui.rejilla(); Ui.colocar(g,Ui.campo("Peso (kg)",peso),0,0,1);
        Ui.colocar(g,Ui.campo("Medidas",medidas),1,0,1); Ui.colocar(g,Ui.campo("Destino",destino),0,1,2);
        resultado.getStyleClass().add("resultado");
        setCenter(Ui.pagina(Ui.tarjeta("Datos del paquete",g,Ui.primario("Cotizar envío",this::cotizar)),Ui.tarjeta("Resultado",resultado)));
    }
    private void cotizar() {
        try {
            float kg = CotizarEncomienda.parsearPeso(peso.getText());
            float[] m = CotizarEncomienda.parsearMedidas(medidas.getText());
            Cotizacion c = controlador.cotizar(kg, m[0], m[1], m[2], destino.getValue());
            mostrarCotizacion("Cotización #" + c.getIdCotizacion() + " | " + c.getDestino()
                    + " | Peso a cobrar: " + String.format("%.2f", c.getPesoCobrado()) + " kg"
                    + " | $" + String.format("%.2f", c.getValorEstimado()));
        } catch (IllegalArgumentException ex) {
            new Alert(Alert.AlertType.WARNING, ex.getMessage()).showAndWait();
        }
    }
    public void mostrarCotizacion(String detalle){resultado.setText(detalle);}
}