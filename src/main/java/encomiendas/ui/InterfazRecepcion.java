package encomiendas.ui;

import Controllers.RecibirEncomienda;
import Entities.Camion;
import Entities.Envio;
import Entities.GuiaRemision;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class InterfazRecepcion extends PantallaBase {
    private final TextField placa = Ui.texto("Placa del camión (opcional)");
    private final TextField guiaRemision = Ui.texto("N° Guía de remisión (opcional)");
    private final TextField guiaEnvio = Ui.texto("N° Guía de envío (opcional)");
    private final TextField idPaquete = Ui.texto("ID del paquete (opcional)");
    private final ComboBox<String> tipo = new ComboBox<>();
    private final TextArea descripcion = new TextArea();
    private final Label registros = new Label("Consulte los datos o confirme la recepción directamente.");

    private final RecibirEncomienda controlador = new RecibirEncomienda();

    public InterfazRecepcion(Runnable volver) {
        super("Recibir encomiendas", "Consulte el transporte, registre novedades si existen y confirme la recepción.",
                volver);

        tipo.getItems().setAll("DAÑADO", "PÉRDIDA", "FALTANTE", "OTRO");
        tipo.setPromptText("Tipo de novedad");
        descripcion.setPromptText("Descripción obligatoria si registra una novedad");
        descripcion.setPrefRowCount(3);

        GridPane g = Ui.rejilla();
        Ui.colocar(g, Ui.campo("Placa del camión", placa), 0, 0, 1);
        Ui.colocar(g, Ui.campo("Guía de remisión", guiaRemision), 1, 0, 1);
        Ui.colocar(g, Ui.campo("Guía de envío individual", guiaEnvio), 0, 1, 2);

        VBox novedad = new VBox(12, Ui.campo("ID de paquete para novedad (opcional)", idPaquete),
                Ui.campo("Tipo de novedad", tipo), Ui.campo("Descripción", descripcion));

        Button btnConsultar = Ui.secundario("Consultar registros", this::ejecutarConsulta);
        Button btnConfirmar = Ui.primario("Confirmar recepción y actualizar estados", this::ejecutarConfirmar);

        setCenter(Ui.pagina(Ui.tarjeta("Datos de recepción", g, btnConsultar),
                Ui.tarjeta("Registro de Novedad (Opcional - solo si aplica)", novedad),
                Ui.tarjeta("Resultado de la recepción", registros, btnConfirmar)));
    }

    private void ejecutarConsulta() {
        String p = placa.getText() != null ? placa.getText().trim() : "";
        String gr = guiaRemision.getText() != null ? guiaRemision.getText().trim() : "";
        String ge = guiaEnvio.getText() != null ? guiaEnvio.getText().trim() : "";

        StringBuilder sb = new StringBuilder();
        boolean encontrado = false;

        if (!p.isEmpty()) {
            Camion c = controlador.buscarCamion(p);
            if (c != null) {
                sb.append("• Camión: ").append(c.getPlaca()).append(" (").append(c.getChofer()).append(")\n");
                encontrado = true;
            }
        }
        if (!gr.isEmpty()) {
            GuiaRemision guiaRem = controlador.buscarGuiaRemision(gr);
            if (guiaRem != null) {
                sb.append("• Guía Remisión: ").append(guiaRem.getNumeroGuiaRemision()).append(" - Estado: ")
                        .append(guiaRem.getEstado()).append("\n");
                encontrado = true;
            }
        }
        if (!ge.isEmpty()) {
            Envio env = controlador.buscarEnvio(ge);
            if (env != null) {
                sb.append("• Encomienda: ").append(env.getNumeroGuia()).append(" con destino a ")
                        .append(env.getDestino()).append(" - Estado actual: ").append(env.getEstado()).append("\n");
                encontrado = true;
            }
        }

        if (encontrado) {
            registros.setText("DATOS ENCONTRADOS:\n" + sb.toString());
        } else {
            registros.setText("No se encontraron registros con los datos ingresados. "
                    + "Verifique la placa y los números de guía.");
        }
    }

    private void ejecutarConfirmar() {
        String p = placa.getText() != null ? placa.getText().trim() : "";
        String gr = guiaRemision.getText() != null ? guiaRemision.getText().trim() : "";
        String ge = guiaEnvio.getText() != null ? guiaEnvio.getText().trim() : "";
        // Primero comprobar la recepción, sin modificar datos.
        RecibirEncomienda.ResultadoRecepcion validacion = controlador.validarRecepcion(p, gr, ge);

        if (validacion.getTotalProcesados() == 0) {
            mostrarRegistrosPaquetes(validacion.getDetalle());
            return;
        }

        String tipoNov = tipo.getValue();
        String descNov = descripcion.getText() != null ? descripcion.getText().trim() : "";
        String idPStr = idPaquete.getText() != null ? idPaquete.getText().trim() : "";
        // Registrar la novedad solamente si la recepción es válida.
        if (tipoNov != null && !tipoNov.isBlank()) {
            Integer idPaq = null;

            if (!idPStr.isEmpty()) {
                try {
                    idPaq = Integer.parseInt(idPStr);

                    if (idPaq <= 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException ex) {
                    mostrarRegistrosPaquetes(
                            "RECEPCIÓN NO REALIZADA\n" + "El ID del paquete debe ser un entero positivo.");
                    return;
                }
            }
            boolean registrada = controlador.registrarNovedadRecepcion(p, gr, ge, idPaq, tipoNov, descNov);
            if (!registrada) {
                mostrarRegistrosPaquetes("RECEPCIÓN NO REALIZADA\n" + "No se pudo registrar la novedad. "
                        + "Verifique el paquete y escriba una descripción.");
                return;
            }
        }
        RecibirEncomienda.ResultadoRecepcion resultado = controlador.confirmarRecepcion(p, gr, ge);
        mostrarRegistrosPaquetes(resultado.getDetalle());
    }

    public void setAccionConsultar(Runnable r) {
    }

    public void setAccionConfirmar(Runnable r) {
    }

    public TextField getTxtPlaca() {
        return placa;
    }

    public TextField getTxtNumeroGuiaRemision() {
        return guiaRemision;
    }

    public TextField getTxtNumeroGuiaEnvio() {
        return guiaEnvio;
    }

    public TextField getTxtIdPaquete() {
        return idPaquete;
    }

    public ComboBox<String> getCmbTipoNovedad() {
        return tipo;
    }

    public TextArea getTxtDescripcionNovedad() {
        return descripcion;
    }

    public void mostrarRegistrosPaquetes(String s) {
        registros.setText(s);
    }

    public void mostrarDatosEnvio(String s) {
        registros.setText(s);
    }
}
