package encomiendas.ui;

import Controllers.EmbarcarEncomienda;
import Entities.Camion;
import Entities.GuiaRemision;
import Entities.Salida;
import javafx.scene.control.*;

public class InterfazEmbarque extends PantallaBase {
    private final TextField placa = Ui.texto("Ej. ABC-1234");
    private final TextField idSalida = Ui.texto("Ej. 1");
    private final ComboBox<Salida> salidasPendientes = new ComboBox<>();
    private final Label camion = new Label("Aún no se consulta un camión.");
    private final Label salida = new Label("Aún no se consulta una salida.");
    private final Label guia = new Label("La guía de remisión aparecerá al confirmar el embarque.");

    private final EmbarcarEncomienda controlador = new EmbarcarEncomienda();

    public InterfazEmbarque(Runnable volver) {
        super("Embarcar encomiendas", "Identifique el camión y la salida programada antes de confirmar el embarque.",
                volver);

        Button btnConsultar = Ui.secundario("Consultar camión y salida", this::ejecutarConsulta);

        Button btnEmbarcar = Ui.primario("Confirmar embarque y emitir guía de remisión", this::ejecutarEmbarque);

        Button btnConfirmarSalida = Ui.secundario("Confirmar salida de paquetes (Despachar ruta)",
                this::ejecutarConfirmarSalida);

        salidasPendientes.setPromptText("Seleccione una salida pendiente");

        salidasPendientes.valueProperty().addListener((observable, anterior, seleccionada) -> {
            if (seleccionada != null) {
                idSalida.setText(String.valueOf(seleccionada.getIdSalida()));
            }
        });

        actualizarSalidasPendientes();

        Button btnActualizarSalidas = Ui.secundario("Actualizar salidas pendientes", this::actualizarSalidasPendientes);

        setCenter(Ui.pagina(
                Ui.tarjeta("Datos de transporte", Ui.campo("Placa del camión", placa),
                        Ui.campo("Salidas pendientes de embarque", salidasPendientes), btnActualizarSalidas,
                        Ui.campo("ID de salida planificada", idSalida), btnConsultar),
                Ui.tarjeta("Registros consultados", camion, salida),
                Ui.tarjeta("Guía de remisión y despacho", guia, btnEmbarcar, btnConfirmarSalida)));
    }

    private void actualizarSalidasPendientes() {
        salidasPendientes.getItems().setAll(controlador.listarSalidasPendientes());
    }

    private void ejecutarConsulta() {
        String p = placa.getText() != null ? placa.getText().trim() : "";
        String sId = idSalida.getText() != null ? idSalida.getText().trim() : "";

        if (!p.isEmpty()) {
            Camion c = controlador.buscarCamion(p);
            if (c != null) {
                mostrarRegistroCamion(
                        "🚛 Camión identificado: Placa [" + c.getPlaca() + "] - Conductor: " + c.getChofer());
            } else {
                mostrarRegistroCamion("⚠️ No se encontró camión con placa: " + p
                        + " (Ejemplos registrados: ABC-1234, XYZ-9876, PBA-5544)");
            }
        }

        if (!sId.isEmpty()) {
            try {
                int id = Integer.parseInt(sId);
                Salida s = controlador.buscarSalida(id);
                if (s != null) {
                    mostrarRegistroSalida("📍 Salida #" + s.getIdSalida() + " con destino a " + s.getDestino()
                            + " | Hora: " + s.getFechaHora().toLocalTime() + " | Paquetes asignados: "
                            + s.getPaquetes().size());
                } else {
                    mostrarRegistroSalida("Salida #" + id + " no existe. "
                            + "Pulse Actualizar salidas pendientes y seleccione una salida.");
                }
            } catch (NumberFormatException e) {
                mostrarRegistroSalida("El ID de salida debe ser un número entero.");
            }
        }
    }

    private void ejecutarEmbarque() {
        String p = placa.getText() != null ? placa.getText().trim() : "";
        String sId = idSalida.getText() != null ? idSalida.getText().trim() : "";

        if (p.isEmpty() || sId.isEmpty()) {
            mostrarAlerta("Campos requeridos", "Debe ingresar la placa del camión y el ID de salida.");
            return;
        }

        try {
            int id = Integer.parseInt(sId);
            Salida seleccionada = controlador.buscarSalida(id);
            boolean autorizarCambio = false;

            if (seleccionada != null && seleccionada.getGuiaRemision() == null && seleccionada.getCamion() != null
                    && !seleccionada.getCamion().getPlaca().equalsIgnoreCase(p)) {

                Alert aviso = new Alert(Alert.AlertType.CONFIRMATION);
                aviso.setTitle("Confirmar cambio de camión");
                aviso.setHeaderText("La salida #" + id + " tiene otro camión asignado.");
                aviso.setContentText("Camión actual: " + seleccionada.getCamion().getPlaca() + "\nNuevo camión: "
                        + p.toUpperCase() + "\n¿Desea cambiarlo y confirmar el embarque?");

                boolean aceptado = aviso.showAndWait().filter(ButtonType.OK::equals).isPresent();

                if (!aceptado) {
                    mostrarGuiaRemision("Embarque cancelado. Se conserva el camión asignado.");
                    return;
                }
                autorizarCambio = true;
            }

            GuiaRemision gr = controlador.confirmarEmbarque(p, id, autorizarCambio);
            actualizarSalidasPendientes();
            mostrarGuiaRemision(
                    "✅ GUÍA DE REMISIÓN EMITIDA EXITOSAMENTE\n" + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n"
                            + "• Número: " + gr.getNumeroGuiaRemision() + "\n" + "• Fecha/Hora emisión: "
                            + gr.getFechaEmision() + "\n" + "• Estado: " + gr.getEstado() + "\n" + "• Salida #" + id
                            + " asignada al camión " + p.toUpperCase() + "\n" + "• Total encomiendas embarcadas: "
                            + (gr.getSalida() != null ? gr.getSalida().getPaquetes().size() : 0) + "\n"
                            + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n"
                            + "Presione 'Confirmar salida de paquetes' al iniciar el viaje.");
        } catch (Exception e) {
            mostrarAlerta("Error en embarque", e.getMessage());
        }
    }

    private void ejecutarConfirmarSalida() {
        String sId = idSalida.getText() != null ? idSalida.getText().trim() : "";
        if (sId.isEmpty()) {
            mostrarAlerta("ID requerido", "Ingrese el ID de la salida.");
            return;
        }

        try {
            int id = Integer.parseInt(sId);
            int count = controlador.confirmarSalidaPaquetes(id);
            mostrarGuiaRemision(
                    "🚛 ¡VEHÍCULO Y PAQUETES DESPACHADOS!\n" + "• Total de encomiendas actualizadas a 'En tránsito': "
                            + count + "\n" + "• Las encomiendas ahora pueden ser rastreadas satelitalmente vía GPS.");
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    public void setAccionConsultar(Runnable r) {
    }

    public void setAccionConfirmar(Runnable r) {
    }

    public void setAccionConfirmarSalida(Runnable r) {
    }

    public TextField getTxtPlaca() {
        return placa;
    }

    public TextField getTxtIdSalida() {
        return idSalida;
    }

    public void mostrarRegistroCamion(String s) {
        camion.setText(s);
    }

    public void mostrarRegistroSalida(String s) {
        salida.setText(s);
    }

    public void mostrarGuiaRemision(String s) {
        guia.setText(s);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
