package encomiendas.ui;

import encomiendas.ui.*;
import encomiendas.ui.MenuPrincipal;

import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Aplicacion extends Application {
    private Stage stage;
    private Scene scene; // <-- Keep a reference to the main scene
    private final Map<String, Parent> pantallas = new HashMap<>();

    @Override 
    public void start(Stage stage) {
        this.stage = stage;
        
        pantallas.put("menu", new MenuPrincipal(this::mostrar));
        Runnable volver = () -> mostrar("menu");
        pantallas.put("ingresarPesoMedidasDestino", new InterfazCotizarEncomienda(volver));
        pantallas.put("ingresarDatosRemitenteDestinatario", new InterfazEnvio(volver));
        pantallas.put("ingresarGuiayDocumento", new InterfazEntregaEncomienda(volver));
        pantallas.put("ingresarNumeroGuia", new InterfazRastreoEnvio(volver));
        pantallas.put("ingresarDatosEmbarque", new InterfazEmbarque(volver));
        pantallas.put("ingresarDatosRecibido", new InterfazRecepcion(volver));
        pantallas.put("solicitarReporte", new InterfazCierreDiario(volver));

        // Create the scene once with an initial root (e.g., "menu")
        Parent rootInicial = pantallas.get("menu");
        scene = new Scene(rootInicial, 1100, 760);
        scene.getStylesheets().add(getClass().getResource("estilos.css").toExternalForm());

        stage.setTitle("Sistema de gestión de encomiendas"); 
        stage.setMinWidth(900); 
        stage.setMinHeight(650);
        stage.setScene(scene);
        stage.show();
    }

    private void mostrar(String id) {
        Parent proximaPantalla = pantallas.get(id);
        if (proximaPantalla != null) {
            scene.setRoot(proximaPantalla); // <-- Replaces the root instead of making a new scene
        }
    }

    public static void main(String[] args) { 
        launch(args); 
    }
}