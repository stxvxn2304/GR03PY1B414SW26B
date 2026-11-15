package encomiendas.ui;
import javafx.scene.control.*;
public class InterfazRastreoEnvio extends PantallaBase {
    private final TextField guia=Ui.texto("Número de guía");
    private final Label estado=new Label("Ingrese una guía para consultar el estado.");
    private Runnable accion=()->aviso();
    public InterfazRastreoEnvio(Runnable volver){
        super("Rastrear envío","Consulte el estado y la ubicación registrada.",volver);
        setCenter(Ui.pagina(Ui.tarjeta("Consulta",Ui.campo("Número de guía",guia),Ui.primario("Consultar rastreo",()->accion.run())),
            Ui.tarjeta("Estado y ubicación",estado)));
    }
    // Conectar con GestorRastreo.buscarPaquete, revisarEstado y la consulta GPS.
    public void setAccionBuscar(Runnable r){accion=r;} 
    public TextField getTxtNumeroGuia(){return guia;}
    // Sustituir el texto por EstadoEnvio y PosicionGPS al integrar el dominio.
    public void mostrarEstadoYUbicacion(String detalle){estado.setText(detalle);}
    private void aviso(){new Alert(Alert.AlertType.INFORMATION,"Conecte esta acción con GestorRastreo.").showAndWait();}
}