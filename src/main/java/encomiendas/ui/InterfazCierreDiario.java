package encomiendas.ui;

import java.time.LocalDate;

import Controllers.RealizarCierreDiario;
import javafx.scene.control.*;
public class InterfazCierreDiario extends PantallaBase {
    private final RealizarCierreDiario controlador = new RealizarCierreDiario();

    private final DatePicker fecha=new DatePicker(LocalDate.now());
    private final Label reporte=new Label("Seleccione una fecha para generar el reporte.");
    public InterfazCierreDiario(Runnable volver){
        super("Cierre diario","Genere y consulte el reporte correspondiente a una fecha.",volver);
        reporte.getStyleClass().add("texto-card");
        setCenter(Ui.pagina(Ui.tarjeta("Fecha del cierre",Ui.campo("Fecha",fecha),Ui.primario("Generar reporte",this::generar)),
                Ui.tarjeta("Reporte diario",reporte)));
    }
    private void generar() {
        mostrarReporte(controlador.obtenerReporteFormateado(fecha.getValue()));
    }
    public void mostrarReporte(String s){reporte.setText(s);}
}