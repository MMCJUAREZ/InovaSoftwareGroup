package mx.uam.ayd.proyecto.presentacion.alertas;

import java.util.List;

import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

@Component
public class VentanaAlerta {

    private ControlAlerta control;
    private Stage stage;
    private ListView<String> listView;

    public VentanaAlerta() {
    }

    public void setControl(ControlAlerta control) {
        this.control = control;
    }

    private void crearYMostrar() {
        Platform.runLater(() -> {
            stage = new Stage();
            stage.setTitle("Alertas de Productos");

            VBox root = new VBox(10);
            root.setStyle("-fx-padding: 10;");

            Button btnRevisar = new Button("Revisar stock y generar alertas");
            btnRevisar.setOnAction(e -> {
                List<String> alertas = control.revisarStock();
                listView.getItems().clear();
                listView.getItems().addAll(alertas);
            });

            listView = new ListView<>();

            root.getChildren().addAll(btnRevisar, listView);

            Scene scene = new Scene(root, 400, 300);
            stage.setScene(scene);
            stage.show();
        });
    }

    public void muestra() {
        if (stage != null) {
            Platform.runLater(() -> {
                if (!stage.isShowing()) {
                    stage.show();
                }
                stage.toFront();
            });
        } else {
            crearYMostrar();
        }
    }
}
