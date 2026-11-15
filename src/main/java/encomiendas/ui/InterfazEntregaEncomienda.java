package encomiendas.ui;
import javafx.scene.control.*;
import javafx.scene.layout.*;
public class InterfazEntregaEncomienda extends PantallaBase {
    private final TextField guia=Ui.texto("Número de guía"), documento=Ui.texto("Identificación de quien retira");
    private final Label informacion=new Label("Ingrese guía e identificación para consultar.");
    private Runnable accionConsultar=()->aviso(), accionConfirmar=()->aviso();
    private final Button confirmar=Ui.primario("Confirmar retiro / entrega",()->accionConfirmar.run());
   
    public InterfazEntregaEncomienda(Runnable volver) {
        super("Retirar encomienda","Consulte el envío y confirme el retiro por el destinatario.",volver);
        confirmar.setDisable(true);
        setCenter(Ui.pagina(Ui.tarjeta("Datos para el retiro",Ui.campo("Número de guía",guia),Ui.campo("Documento de identidad",documento),
            Ui.secundario("Consultar envío",()->accionConsultar.run())),
            Ui.tarjeta("Información del envío",informacion,confirmar)));
    }
    // Consultar debe invocar GestorEntrega.buscarPaquete(numeroGuia, documento).
    public void setAccionConsultar(Runnable r){accionConsultar=r;}
    // Confirmar debe invocar el proceso de retiro/entrega del controlador, sin autorización.
    public void setAccionConfirmar(Runnable r){accionConfirmar=r;}
    public TextField getTxtNumeroGuia(){return guia;} public TextField getTxtDocumento(){return documento;}
    public void mostrarInformacionEnvio(String texto){informacion.setText(texto);confirmar.setDisable(false);}
    public void mostrarRechazo(String motivo){informacion.setText("No se puede realizar el retiro: "+motivo);confirmar.setDisable(true);}
    public void mostrarConfirmacionRetiro(String numero){informacion.setText("Retiro confirmado para la guía "+numero);confirmar.setDisable(true);}
    private void aviso(){new Alert(Alert.AlertType.INFORMATION,"Conecte esta acción con GestorEntrega.").showAndWait();}
}