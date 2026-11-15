package encomiendas.ui;

import java.time.LocalDate;

import Controllers.RealizarCierreDiario;
import encomiendas.ui.PantallaBase;
import encomiendas.ui.Ui;
import javafx.scene.control.*;
public class InterfazCierreDiario extends PantallaBase {
    private final RealizarCierreDiario controlador = new RealizarCierreDiario();

    private final DatePicker fecha=new DatePicker(LocalDate.now());
    private final Label reporte=new Label("Seleccione una fecha para generar el reporte.");
    private Runnable accion=()->generar();
    public InterfazCierreDiario(Runnable volver){
        super("Cierre diario","Genere y consulte el reporte correspondiente a una fecha.",volver);
        reporte.getStyleClass().add("texto-card");
        setCenter(Ui.pagina(Ui.tarjeta("Fecha del cierre",Ui.campo("Fecha",fecha),Ui.primario("Generar reporte",()->accion.run())),
            Ui.tarjeta("Reporte diario",reporte)));
    }
    private void generar() {
        mostrarReporte(controlador.obtenerReporteFormateado(fecha.getValue()));
    }
    public void setAccionGenerar(Runnable r){accion=r;}
    public DatePicker getDpFecha(){return fecha;}
    // Sustituir String por ReporteDiario cuando exista la entidad.
    public void mostrarReporte(String s){reporte.setText(s);}
    private void aviso(){new Alert(Alert.AlertType.INFORMATION,"Conecte este botón con RealizarCierreDiario.generarReporte(...).").showAndWait();}
}