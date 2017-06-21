package it.polimi.ingsw.progettolorenzo.client;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class GuiFx extends Application {
    @Override
    public void start(Stage stage) {
        initUI(stage);
    }

    private void initUI(Stage stage) {
        StackPane root = new StackPane();

        Scene scene = new Scene(root, 1400, 900);

        Label lbl = new Label("Lorenzo");
        lbl.setFont(Font.font("Serif", FontWeight.NORMAL, 20));
        root.getChildren().add(lbl);

        stage.setTitle("Lorenzo il Magnifico");
        stage.setScene(scene);
        stage.show();
    }
}
