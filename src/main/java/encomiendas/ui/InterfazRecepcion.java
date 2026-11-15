package encomiendas.ui;
import javafx.scene.control.*;
import javafx.scene.layout.*;
public class InterfazRecepcion extends PantallaBase {
    private final TextField placa=Ui.texto("Placa del camión"), guiaRemision=Ui.texto("Número de guía de remisión"), guiaEnvio=Ui.texto("Número de guía de envío"), idPaquete=Ui.texto("ID del paquete (opcional)");
    private final ComboBox<String> tipo=new ComboBox<>();
    private final TextArea descripcion=new TextArea();
    private final Label registros=new Label("Consulte los datos antes de confirmar la recepción.");
    private Runnable consultar=()->aviso(), confirmar=()->aviso();
    public InterfazRecepcion(Runnable volver){
        super("Recibir encomiendas","Consulte el transporte, registre novedades si existen y confirme la recepción.",volver);
        tipo.getItems().setAll("DAÑO","PÉRDIDA","FALTANTE","OTRO"); tipo.setPromptText("Tipo de novedad");
        descripcion.setPromptText("Descripción de la novedad (opcional)"); descripcion.setPrefRowCount(3);
        GridPane g=Ui.rejilla(); Ui.colocar(g,Ui.campo("Placa del camión",placa),0,0,1);
        Ui.colocar(g,Ui.campo("Guía de remisión",guiaRemision),1,0,1); Ui.colocar(g,Ui.campo("Guía de envío",guiaEnvio),0,1,2);
        VBox novedad=new VBox(12,Ui.campo("ID del paquete",idPaquete),Ui.campo("Tipo de novedad",tipo),Ui.campo("Descripción",descripcion));
        setCenter(Ui.pagina(Ui.tarjeta("Datos de recepción",g,Ui.secundario("Consultar registros",()->consultar.run())),
            Ui.tarjeta("Novedad (opcional)",novedad),Ui.tarjeta("Envíos identificados",registros,Ui.primario("Confirmar recepción",()->confirmar.run()))));
    }
    // Consultar: buscarCamion, buscarGuiaRemision, buscarEnvio e identificarPaquetes.
    public void setAccionConsultar(Runnable r){consultar=r;}
    // Registrar novedad solo si se completaron sus campos; después invocar registrarRecepcion.
    public void setAccionConfirmar(Runnable r){confirmar=r;}
    public TextField getTxtPlaca(){return placa;} 
    public TextField getTxtNumeroGuiaRemision(){return guiaRemision;}
    public TextField getTxtNumeroGuiaEnvio(){return guiaEnvio;} 
    public TextField getTxtIdPaquete(){return idPaquete;}
    public ComboBox<String> getCmbTipoNovedad(){return tipo;}
    public TextArea getTxtDescripcionNovedad(){return descripcion;}
    public void mostrarRegistrosPaquetes(String s){registros.setText(s);} 
    public void mostrarDatosEnvio(String s){registros.setText(s);}
    private void aviso(){new Alert(Alert.AlertType.INFORMATION,"Conecte esta acción con RecibirEncomienda.").showAndWait();}
}