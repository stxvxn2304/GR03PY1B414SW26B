package encomiendas.ui;

import java.util.List;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;

public final class Ui {
    public static final PseudoClass ACTIVO = PseudoClass.getPseudoClass("activo");
    // Sustituir por los destinos reales definidos en el programa.
    public static final List<String> DESTINOS = List.of("Quito", "Guayaquil", "Cuenca", "Ambato", "Riobamba", "Loja", "Manta", "Ibarra");
    public static final String ICONO_ENVIO = "M21 8l-9-5-9 5v8l9 5 9-5z M3 8l9 5 9-5 M12 13v8";
    public static final String ICONO_RECIBIR = "M4 14v6h16v-6 M12 3v12 M7 10l5 5 5-5";
    public static final String ICONO_ENTREGAR = "M12 22V12 M16 17l2 2 4-4 M21 11.127V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.729l7 4a2 2 0 0 0 2 .001l1.32-.753 M3.29 7 12 12l8.71-5 M7.5 4.27l8.997 5.148";
    public static final String ICONO_CAMION = "M2 6h11v10H2z M13 9h4l3 3v4h-7z M5 16.5a1.5 1.5 0 1 0 3 0a1.5 1.5 0 1 0-3 0 M15 16.5a1.5 1.5 0 1 0 3 0a1.5 1.5 0 1 0-3 0";
    public static final String ICONO_CIERRE = "M6 3h9l4 4v14H6z M9 12h7 M9 16h7";
    public static final String ICONO_RASTREO = "M12 21s7-6.5 7-12a7 7 0 1 0-14 0c0 5.5 7 12 7 12z M9.5 9a2.5 2.5 0 1 0 5 0a2.5 2.5 0 0 0-5 0";
    public static final String ICONO_DINERO = "M12 2v20 M17 6H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6";
    private Ui() {}
    public static SVGPath icono(String contenido) { SVGPath p = new SVGPath(); p.setContent(contenido); p.getStyleClass().add("icono"); return p; }
    public static boolean hayVacios(String... campos) { for (String c : campos) if (c == null || c.isBlank()) return true; return false; }
    public static VBox encabezado(String titulo, String subtitulo) {
        Label t = new Label(titulo); t.getStyleClass().add("titulo-pagina");
        Label s = new Label(subtitulo); s.getStyleClass().add("subtitulo"); return new VBox(6, t, s);
    }
    public static VBox tarjeta(String titulo, Node... hijos) {
        VBox v = new VBox(14); v.getStyleClass().add("card");
        if (titulo != null) { Label l = new Label(titulo); l.getStyleClass().add("card-titulo"); v.getChildren().add(l); }
        v.getChildren().addAll(hijos); return v;
    }
    public static VBox campo(String etiqueta, Control control) {
        Label l = new Label(etiqueta); l.getStyleClass().add("etiqueta"); control.setMaxWidth(Double.MAX_VALUE); return new VBox(6, l, control);
    }
    public static TextField texto(String prompt) { TextField t = new TextField(); t.setPromptText(prompt); return t; }
    public static Button primario(String texto, Runnable accion) { return boton(texto, "btn-primario", accion); }
    public static Button secundario(String texto, Runnable accion) { return boton(texto, "btn-secundario", accion); }
    private static Button boton(String texto, String clase, Runnable accion) {
        Button b = new Button(texto); b.getStyleClass().add(clase); b.setMaxWidth(Double.MAX_VALUE); b.setOnAction(e -> accion.run()); return b;
    }
    public static GridPane rejilla() {
        GridPane g = new GridPane(); g.setHgap(16); g.setVgap(14);
        ColumnConstraints a = new ColumnConstraints(); a.setPercentWidth(50);
        ColumnConstraints b = new ColumnConstraints(); b.setPercentWidth(50); g.getColumnConstraints().addAll(a,b); return g;
    }
    public static void colocar(GridPane g, Node n, int col, int fila, int ancho) { g.add(n,col,fila,ancho,1); }
    public static HBox fila(Node... hijos) { HBox h = new HBox(12,hijos); h.setAlignment(Pos.CENTER_LEFT); return h; }
    public static ScrollPane pagina(Node... hijos) {
        VBox v = new VBox(22,hijos); v.getStyleClass().add("pagina"); ScrollPane p = new ScrollPane(v); p.setFitToWidth(true); p.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); return p;
    }
    public static void mostrar(Node n, boolean visible) { n.setVisible(visible); n.setManaged(visible); }
}