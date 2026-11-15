package encomiendas.ui;
import javafx.scene.control.*;
public class InterfazEmbarque extends PantallaBase {
    private final TextField placa=Ui.texto("Placa del camión"), idSalida=Ui.texto("Identificador de salida");
    private final Label camion=new Label("Aún no se consulta un camión."), salida=new Label("Aún no se consulta una salida."), guia=new Label("La guía de remisión aparecerá al confirmar.");
    private Runnable consultar=()->aviso(), embarcar=()->aviso(), confirmarSalida=()->aviso();
    public InterfazEmbarque(Runnable volver){
        super("Embarcar encomiendas","Identifique el camión y la salida antes de confirmar.",volver);
        setCenter(Ui.pagina(Ui.tarjeta("Datos de transporte",Ui.campo("Placa del camión",placa),Ui.campo("ID de salida",idSalida),
            Ui.secundario("Consultar camión y salida",()->consultar.run())),
            Ui.tarjeta("Registros consultados",camion,salida),
            Ui.tarjeta("Guía de remisión",guia,Ui.primario("Confirmar embarque y emitir guía",()->embarcar.run()),
                Ui.primario("Confirmar salida de paquetes",()->confirmarSalida.run()))));
    }
    // Consultar: buscarCamion, buscarSalida y asignarSalidaACamion.
    public void setAccionConsultar(Runnable r){consultar=r;}
    // Confirmar: confirmarEmbarque y emitirGuiaRemision.
    public void setAccionConfirmar(Runnable r){embarcar=r;}
    // Segundo botón: confirmarSalidaPaquetes.
    public void setAccionConfirmarSalida(Runnable r){confirmarSalida=r;}
    public TextField getTxtPlaca(){return placa;} 
    public TextField getTxtIdSalida(){return idSalida;}
    public void mostrarRegistroCamion(String s){camion.setText(s);} 
    public void mostrarRegistroSalida(String s){salida.setText(s);}
    public void mostrarGuiaRemision(String s){guia.setText("Guía de remisión: "+s);}
    private void aviso(){new Alert(Alert.AlertType.INFORMATION,"Conecte esta acción con EmbarcarEncomienda.").showAndWait();}
}