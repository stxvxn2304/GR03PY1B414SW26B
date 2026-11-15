package encomiendas.ui;
import javafx.scene.control.*;
import javafx.scene.layout.*;
public class InterfazCotizarEncomienda extends PantallaBase {
    private final TextField peso=Ui.texto("Ej. 2.5"), medidas=Ui.texto("Ej. 30 x 20 x 15 cm");
    private final ComboBox<String> destino=new ComboBox<>();
    private final Label resultado=new Label("Aquí se mostrará el valor de la cotización.");
    private Runnable accion=()->aviso();
    public InterfazCotizarEncomienda(Runnable volver) {
        super("Cotizar encomienda","Ingrese peso, medidas y destino.",volver);
        destino.getItems().setAll(Ui.DESTINOS); destino.setPromptText("Seleccione un destino");
        GridPane g=Ui.rejilla(); Ui.colocar(g,Ui.campo("Peso (kg)",peso),0,0,1);
        Ui.colocar(g,Ui.campo("Medidas",medidas),1,0,1); Ui.colocar(g,Ui.campo("Destino",destino),0,1,2);
        resultado.getStyleClass().add("resultado");
        setCenter(Ui.pagina(Ui.tarjeta("Datos del paquete",g,Ui.primario("Cotizar envío",()->accion.run())),Ui.tarjeta("Resultado",resultado)));
    }
    // Conectar el botón con CotizarEncomienda.cotizar(...).
    public void setAccionCotizar(Runnable r){accion=r;}
    public TextField getTxtPeso(){return peso;} public TextField getTxtMedidas(){return medidas;}
    public ComboBox<String> getCmbDestino(){return destino;}
    // Sustituir String por Cotizacion cuando la entidad esté disponible.
    public void mostrarCotizacion(String detalle){resultado.setText(detalle);}
    private void aviso(){new Alert(Alert.AlertType.INFORMATION,"Conecte este botón con CotizarEncomienda.cotizar(...).").showAndWait();}
}