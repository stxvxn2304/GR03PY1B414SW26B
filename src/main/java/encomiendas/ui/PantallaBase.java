package encomiendas.ui;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
public abstract class PantallaBase extends BorderPane {
    protected PantallaBase(String titulo, String subtitulo, Runnable volver) {
        Button b = Ui.secundario("← Menú principal", volver);
        VBox top = new VBox(14,b,Ui.encabezado(titulo,subtitulo)); top.getStyleClass().add("encabezado");
        setTop(top); getStyleClass().add("fondo");
    }
    public Parent vista() { return this; }
}