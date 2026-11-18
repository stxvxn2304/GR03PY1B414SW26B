
package encomiendas.ui;

import java.util.HashMap;
import java.util.Map;

import Controllers.GestorEntrega;
import Controllers.GestorRastreo;
import Entities.Envio;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Aplicacion extends Application {

    private Stage stage;
    private Scene scene;
    private final Map<String, Parent> pantallas = new HashMap<>();

    @Override
    public void start(Stage stage) {    
        this.stage = stage;

        pantallas.put("menu", new MenuPrincipal(this::mostrar));

        Runnable volver = () -> mostrar("menu");

        // ==========================================
        // COTIZAR, ENVIAR Y CIERRE DIARIO
        // ==========================================

        pantallas.put(
            "ingresarPesoMedidasDestino",
            new InterfazCotizarEncomienda(volver)
        );

        pantallas.put(
            "ingresarDatosRemitenteDestinatario",
            new InterfazEnvio(volver)
        );

        pantallas.put(
            "solicitarReporte",
            new InterfazCierreDiario(volver)
        );

        // ==========================================
        // ENTREGAR, RASTREAR Y GPS
        // ==========================================

        GestorEntrega gestorEntrega = new GestorEntrega();
        GestorRastreo gestorRastreo = new GestorRastreo();

        // PANTALLA: ENTREGAR ENCOMIENDA

        InterfazEntregaEncomienda pantallaEntrega =
                new InterfazEntregaEncomienda(volver);

        pantallaEntrega.setAccionConsultar(() -> {

            String guia = pantallaEntrega
                    .getTxtNumeroGuia().getText();

            String documento = pantallaEntrega
                    .getTxtDocumento().getText();

            Envio envio = gestorEntrega.buscarPaquete(
                    guia, documento
            );

            if (envio == null) {

                pantallaEntrega.mostrarRechazo(
                    "Verifique el número de guía, "
                    + "la identificación del destinatario "
                    + "y que la encomienda esté RECIBIDA."
                );

            } else {

                pantallaEntrega.mostrarInformacionEnvio(
                    "Número de guía: " + envio.getNumeroGuia()
                    + "\nDestinatario: "
                    + envio.getDestinatario().getNombre()
                    + "\nDestino: " + envio.getDestino()
                    + "\nEstado: " + envio.getEstado()
                );

            }
        });

        pantallaEntrega.setAccionConfirmar(() -> {

            String guia = pantallaEntrega
                    .getTxtNumeroGuia().getText();

            String documento = pantallaEntrega
                    .getTxtDocumento().getText();

            boolean entregado = gestorEntrega.confirmarEntrega(
                    guia, documento
            );

            if (entregado) {

                pantallaEntrega.mostrarConfirmacionRetiro(guia);

            } else {

                pantallaEntrega.mostrarRechazo(
                    "No se pudo confirmar la entrega. "
                    + "Verifique los datos y el estado del envío."
                );

            }
        });

        pantallas.put(
            "ingresarGuiayDocumento",
            pantallaEntrega
        );

        // PANTALLA: RASTREAR ENVÍO

        InterfazRastreoEnvio pantallaRastreo =
                new InterfazRastreoEnvio(volver);

        pantallaRastreo.setAccionBuscar(() -> {

            String guia = pantallaRastreo
                    .getTxtNumeroGuia().getText();

            String resultado = gestorRastreo.consultarRastreo(guia);

            pantallaRastreo.mostrarEstadoYUbicacion(resultado);

        });

        pantallas.put(
            "ingresarNumeroGuia",
            pantallaRastreo
        );

        // ==========================================
        //  EMBARCAR Y RECIBIR
        // ==========================================

        pantallas.put(
            "ingresarDatosEmbarque",
            new InterfazEmbarque(volver)
        );

        pantallas.put(
            "ingresarDatosRecibido",
            new InterfazRecepcion(volver)
        );

        // ==========================================
        // CONFIGURACIÓN ORIGINAL DE LA VENTANA
        // ==========================================

        Parent rootInicial = pantallas.get("menu");

        scene = new Scene(rootInicial, 1100, 760);

        scene.getStylesheets().add(
            getClass().getResource("estilos.css").toExternalForm()
        );

        stage.setTitle("Sistema de gestión de encomiendas");

        stage.setMinWidth(900);
        stage.setMinHeight(650);

        stage.setScene(scene);
        stage.show();
    }

    private void mostrar(String id) {

        Parent proximaPantalla = pantallas.get(id);

        if (proximaPantalla != null) {
            scene.setRoot(proximaPantalla);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
