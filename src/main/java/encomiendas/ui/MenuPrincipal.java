package encomiendas.ui;
import java.util.function.Consumer;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
public class MenuPrincipal extends VBox {
    public MenuPrincipal(Consumer<String> abrir) {
        setSpacing(22); getStyleClass().add("pagina");
        Label t = new Label("Gestión de encomiendas"); t.getStyleClass().add("titulo-pagina");
        Label s = new Label("Seleccione la operación que desea realizar"); s.getStyleClass().add("subtitulo");
        GridPane g = Ui.rejilla();
        opcion(g,Ui.ICONO_DINERO,"Cotizar encomienda","Calcular el valor del envío",0,0,1,abrir,"ingresarPesoMedidasDestino");
        opcion(g,Ui.ICONO_ENVIO,"Registrar envío","Remitente, destinatario y paquetes",1,0,1,abrir,"ingresarDatosRemitenteDestinatario");
        opcion(g,Ui.ICONO_ENTREGAR,"Retirar encomienda","Consultar y confirmar el retiro",0,1,1,abrir,"ingresarGuiayDocumento");
        opcion(g,Ui.ICONO_RASTREO,"Rastrear envío","Consultar estado y ubicación",1,1,1,abrir,"ingresarNumeroGuia");
        opcion(g,Ui.ICONO_CAMION,"Embarcar encomiendas","Asignar camión y salida",0,2,1,abrir,"ingresarDatosEmbarque");
        opcion(g,Ui.ICONO_RECIBIR,"Recibir encomiendas","Registrar recepción y novedades",1,2,1,abrir,"ingresarDatosRecibido");
        opcion(g,Ui.ICONO_CIERRE,"Cierre diario","Consultar reporte por fecha",0,3,2,abrir,"solicitarReporte");
        getChildren().addAll(t,s,Ui.tarjeta("Operaciones",g));
    }
    private void opcion(GridPane g,String icono,String titulo,String detalle,int c,int f,int ancho,Consumer<String> abrir,String id) {
        StackPane circulo=new StackPane(Ui.icono(icono)); circulo.getStyleClass().add("opcion-icono");
        Label t=new Label(titulo); t.getStyleClass().add("opcion-titulo");
        Label d=new Label(detalle); d.getStyleClass().add("opcion-detalle");
        Label flecha=new Label("›"); flecha.getStyleClass().add("opcion-flecha");
        Region espacio=new Region(); HBox.setHgrow(espacio,Priority.ALWAYS);
        HBox contenido=new HBox(14,circulo,new VBox(2,t,d),espacio,flecha);
        contenido.setAlignment(Pos.CENTER_LEFT);
        Button b=new Button(); b.setGraphic(contenido);
        b.getStyleClass().add("opcion-menu");
        b.setMaxWidth(Double.MAX_VALUE);
        contenido.prefWidthProperty().bind(b.widthProperty().subtract(40)); // Ajustamos el contenido al ancho del boton para que la flecha quede a la derecha
        b.setOnAction(e->abrir.accept(id));
        Ui.colocar(g,b,c,f,ancho);
    }
}