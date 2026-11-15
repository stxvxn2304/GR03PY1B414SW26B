package encomiendas.ui;

import Controllers.EnviarEncomienda;
import Entities.Cliente;
import Entities.Cotizacion;
import Entities.Envio;
import Entities.Paquete;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class InterfazEnvio extends PantallaBase {
    private final EnviarEncomienda controlador = new EnviarEncomienda();
    public enum TipoContenido { DOCUMENTOS, ROPA, ELECTRONICOS, ALIMENTOS, FRAGIL, OTROS }
    private final TextField idCot=Ui.texto("Identificador de cotización");
    private final Label resumenCot = new Label("Ingrese el ID de la cotización y presione \"Cargar cotización\".");
    private final TextField remId=Ui.texto("Identificación"), remNom=Ui.texto("Nombre completo"), remTel=Ui.texto("Teléfono"), remDir=Ui.texto("Dirección");
    private final TextField desId=Ui.texto("Identificación"), desNom=Ui.texto("Nombre completo"), desTel=Ui.texto("Teléfono"), desDir=Ui.texto("Dirección");
    private final ComboBox<TipoContenido> cmbTipo=new ComboBox<>();
    private final ObservableList<TipoContenido> tipos=FXCollections.observableArrayList();
    private final ListView<TipoContenido> lstTipos=new ListView<>(tipos);
    private final Label resultado=new Label("Complete los datos para registrar el envío.");
    public InterfazEnvio(Runnable volver) {
        super("Registrar envío","Formulario unificado: remitente, destinatario y paquete.",volver);
        cmbTipo.getItems().addAll(TipoContenido.values());
        cmbTipo.setPromptText("Tipo de contenido");
        lstTipos.setPrefHeight(100);
        Button btnAgregar=new Button("Agregar tipo");
        btnAgregar.setOnAction(e->agregarTipo());
        Label lblDescripcion=new Label("Descripción del paquete");
        lblDescripcion.getStyleClass().add("etiqueta");
        VBox cajaTipos=new VBox(6,lblDescripcion,new HBox(8,cmbTipo,btnAgregar),lstTipos);
        GridPane g=Ui.rejilla();
        Ui.colocar(g, Ui.campo("ID de cotización", idCot), 0, 0, 2);
        Ui.colocar(g, Ui.secundario("Cargar cotización", () -> cargarCotizacion()), 0, 1, 2);
        resumenCot.getStyleClass().add("texto-card");
        Ui.colocar(g, resumenCot, 0, 2, 2);
        Ui.colocar(g,Ui.campo("Identificación del remitente",remId),0,3,1); Ui.colocar(g,Ui.campo("Nombre del remitente",remNom),1,3,1);
        Ui.colocar(g,Ui.campo("Teléfono del remitente",remTel),0,4,1); Ui.colocar(g,Ui.campo("Dirección del remitente",remDir),1,4,1);
        Ui.colocar(g,Ui.campo("Identificación del destinatario",desId),0,5,1); Ui.colocar(g,Ui.campo("Nombre del destinatario",desNom),1,5,1);
        Ui.colocar(g,Ui.campo("Teléfono del destinatario",desTel),0,6,1); Ui.colocar(g,Ui.campo("Dirección del destinatario",desDir),1,6,1);
        Ui.colocar(g,cajaTipos,0,7,2);
        resultado.getStyleClass().add("resultado");
        setCenter(Ui.pagina(Ui.tarjeta("Datos del envío",g,Ui.primario("Enviar datos y registrar encomienda",this::registrar)),Ui.tarjeta("Resultado / guía",resultado)));
    }
    private int leerIdCotizacion() {
        try { return Integer.parseInt(idCot.getText().trim()); }
        catch (RuntimeException e) {
            throw new IllegalArgumentException("Ingrese el ID de la cotización (solo números). Si aún no cotizó, hágalo primero en Cotizar encomienda.");
        }
    }
    private void cargarCotizacion() {
        try {
            Cotizacion c = controlador.consultarCotizacion(leerIdCotizacion());
            resumenCot.setText("Cotización #" + c.getIdCotizacion()
                    + " | Destino: " + c.getDestino()
                    + " | Peso a cobrar: " + String.format("%.2f", c.getPesoCobrado()) + " kg | " + c.getLargo() + " x " + c.getAncho() + " x " + c.getAlto() + " cm"
                    + " | Valor: $" + String.format("%.2f", c.getValorEstimado()));
        } catch (IllegalArgumentException ex) {
            resumenCot.setText("Sin cotización válida.");
            new Alert(Alert.AlertType.WARNING, ex.getMessage()).showAndWait();
        }
    }
    private void registrar() {
        try {
            Cotizacion cot = controlador.consultarCotizacion(leerIdCotizacion());
            Cliente rem = controlador.registrarRemitente(remId.getText(), remNom.getText(), remTel.getText(), remDir.getText());
            String descripcion = tipos.isEmpty() ? "Paquete" : tipos.toString();
            Paquete p = controlador.registrarPaquete(cot, descripcion);
            Cliente des = controlador.registrarDestinatario(desId.getText(), desNom.getText(), desTel.getText(), desDir.getText());
            Envio e = controlador.finalizarEnvio(cot, rem, des, p);
            resultado.setText("Envío registrado. Número de guía: " + e.getNumeroGuia()
                    + "\nDestino: " + e.getDestino()
                    + "\nSalida asignada: #" + e.getSalida().getIdSalida());
        } catch (IllegalArgumentException ex) {
            new Alert(Alert.AlertType.WARNING, ex.getMessage()).showAndWait();
        }
    }
    private void agregarTipo(){
        TipoContenido t=cmbTipo.getValue();
        if(t!=null && !tipos.contains(t)){ // Evitamos agregar tipos vacios o repetidos
            tipos.add(t);
        }
        cmbTipo.getSelectionModel().clearSelection();
    }
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
}