package encomiendas.ui;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.*;
public class InterfazEnvio extends PantallaBase {
    public enum TipoContenido { DOCUMENTOS, ROPA, ELECTRONICOS, ALIMENTOS, FRAGIL, OTROS }
    private final TextField idCot=Ui.texto("Identificador de cotización");
    private final TextField remId=Ui.texto("Identificación"), remNom=Ui.texto("Nombre completo"), remTel=Ui.texto("Teléfono"), remDir=Ui.texto("Dirección");
    private final TextField desId=Ui.texto("Identificación"), desNom=Ui.texto("Nombre completo"), desTel=Ui.texto("Teléfono"), desDir=Ui.texto("Dirección");
    private final TextField peso=Ui.texto("Peso en kg"), medidas=Ui.texto("Largo x ancho x alto");
    private final ComboBox<TipoContenido> cmbTipo=new ComboBox<>();
    private final ObservableList<TipoContenido> tipos=FXCollections.observableArrayList();
    private final ListView<TipoContenido> lstTipos=new ListView<>(tipos);
    private final Label resultado=new Label("Complete los datos para registrar el envío.");
    private Runnable accion=()->aviso();
    public InterfazEnvio(Runnable volver) {
        super("Registrar envío","Formulario unificado: remitente, destinatario y paquete.",volver);
        cmbTipo.getItems().addAll(TipoContenido.values());
        cmbTipo.setPromptText("Tipo de contenido");
        lstTipos.setPrefHeight(100);
        Button btnAgregar=new Button("Agregar tipo");
        btnAgregar.setOnAction(e->agregarTipo());
        VBox cajaTipos=new VBox(6,new Label("Descripción del paquete"),new HBox(8,cmbTipo,btnAgregar),lstTipos);
        GridPane g=Ui.rejilla();
        Ui.colocar(g,Ui.campo("ID de cotización",idCot),0,0,2);
        Ui.colocar(g,Ui.campo("Identificación del remitente",remId),0,1,1); Ui.colocar(g,Ui.campo("Nombre del remitente",remNom),1,1,1);
        Ui.colocar(g,Ui.campo("Teléfono del remitente",remTel),0,2,1); Ui.colocar(g,Ui.campo("Dirección del remitente",remDir),1,2,1);
        Ui.colocar(g,Ui.campo("Identificación del destinatario",desId),0,3,1); Ui.colocar(g,Ui.campo("Nombre del destinatario",desNom),1,3,1);
        Ui.colocar(g,Ui.campo("Teléfono del destinatario",desTel),0,4,1); Ui.colocar(g,Ui.campo("Dirección del destinatario",desDir),1,4,1);
        Ui.colocar(g,cajaTipos,0,5,2);
        Ui.colocar(g,Ui.campo("Peso (kg)",peso),0,6,1); Ui.colocar(g,Ui.campo("Medidas",medidas),1,6,1);
        resultado.getStyleClass().add("resultado");
        setCenter(Ui.pagina(Ui.tarjeta("Datos del envío",g,Ui.primario("Enviar datos y registrar encomienda",()->accion.run())),Ui.tarjeta("Resultado / guía",resultado)));
    }
    private void agregarTipo(){
        TipoContenido t=cmbTipo.getValue();
        if(t!=null && !tipos.contains(t)){ // Evitamos agregar tipos vacios o repetidos
            tipos.add(t);
        }
        cmbTipo.getSelectionModel().clearSelection();
    }
    // Este botón se conecta con EnviarEncomienda: registrarRemitente, registrarDestinatario,
    // registrarPaquete, asignarSalida y finalizarEnvio. El controlador coordina el orden.
    public void setAccionEnviar(Runnable r){accion=r;}
    public TextField getTxtIdCotizacion(){return idCot;}
    public TextField getTxtRemitenteId(){return remId;} 
    public TextField getTxtRemitenteNombre(){return remNom;}
    public TextField getTxtRemitenteTelefono(){return remTel;} 
    public TextField getTxtRemitenteDireccion(){return remDir;}
    public TextField getTxtDestinatarioId(){return desId;} 
    public TextField getTxtDestinatarioNombre(){return desNom;}
    public TextField getTxtDestinatarioTelefono(){return desTel;} 
    public TextField getTxtDestinatarioDireccion(){return desDir;}
    public ObservableList<TipoContenido> getTiposContenido(){return tipos;}
    public TextField getTxtPeso(){return peso;} public TextField getTxtMedidas(){return medidas;}
    // Sustituir String por GuiaEnvio cuando se integre la entidad.
    public void mostrarGuia(String numero){resultado.setText("Envío registrado. Número de guía: "+numero);}
    private void aviso(){new Alert(Alert.AlertType.INFORMATION,"Conecte este botón con los métodos de EnviarEncomienda.").showAndWait();}
}