package it.polimi.ingsw.progettolorenzo.client.inf;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.logging.Logger;

public class GuiInterface extends Application implements Interface {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private static String msgIn;
    private static String msgOut;
    private static final Object msgInMonitor = new Object();
    private static final Object msgOutMonitor = new Object();
    private static boolean guiStarted = false;
    private static final Object guiMonitor = new Object();

    @Override
    public void start(Stage stage) {
        log.info("Starting the GUI...");
        GridPane root = new GridPane();
        root.setAlignment(Pos.CENTER);
        root.setHgap(10);
        root.setVgap(10);
        root.setPadding(new Insets(25, 25, 25, 25));
        Scene scene = new Scene(root, 1400, 900);

        BackgroundImage myBI= new BackgroundImage(new Image("Gui/cathedral.jpg",1400,900,false,true),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        root.setBackground(new Background(myBI));

        Label text = new Label("foo");
        text.setFont(Font.font("Serif", FontWeight.NORMAL, 30));
        text.setTextFill(Color.BEIGE);

        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws InterruptedException {
               log.info("inside the task");
                    while (true) {
                            synchronized (msgOutMonitor) {
                            log.info("here, waiting for msgOut");
                            msgOutMonitor.wait();
                            log.info("updating msgOut");
                            updateMessage(msgOut);
                            log.info("msgOut updated");
                        }
                    }
            }
        };
        text.textProperty().bind(task.messageProperty());
        task.setOnSucceeded(e -> {
            text.textProperty().unbind();
            text.setText("Disconnected successfully from task");
        });
        task.setOnFailed(e -> {
            text.textProperty().unbind();
            task.getException().printStackTrace();
            text.setText("Disconnected from task [FAILED]");
        });
        task.setOnCancelled(e -> {
            text.textProperty().unbind();
            text.setText("Disconnected from task [CANCELLED]");
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
        root.add(text, 0, 0);


        TextField userTextField = new TextField();
        root.add(userTextField, 0, 1);

        Button btn = new Button();
        btn.setText("Confirm");
        root.add(btn, 1, 1);
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                System.out.println(userTextField.getText());
                synchronized (msgInMonitor) {
                    msgIn = userTextField.getText();
                    msgInMonitor.notify();
                }
            }
        });

        guiStarted = true;
        synchronized (guiMonitor) {
            guiStarted = true;
            log.info("notifying…");
            guiMonitor.notify();
        }
        log.info("done notify");

        stage.setTitle("Lorenzo il Magnifico");
        stage.setScene(scene);
        stage.show();
    }


    @Override
    public void printLine(String format, Object... args) {
        log.info("new message arrived: '"+format+"'");
        synchronized (guiMonitor) {
           if (!guiStarted) {
                 Runnable t = () ->
                        javafx.application.Application.launch(this.getClass());
                new Thread(t).start();
                //try {
                    log.info("going to wait for gui…");
                    //this.guiMonitor.wait();
                    log.info("done waiting");
               // } catch (InterruptedException e) {
               //     log.severe("Failed to start the GUI");
               // }
            }
        }
        log.info("attempting delivery");
        synchronized (msgOutMonitor) {
            log.info("inside the sync");
            msgOut = String.format(format, args);
            msgOutMonitor.notify();
            log.info("delivery succeeded");
        }
    }

    @Override
    public String readLine(String format, Object... args) {
        this.printLine(format, args);
        try {
            synchronized (msgInMonitor) {
                msgInMonitor.wait();
                return msgIn;
            }
        } catch (InterruptedException e) {
            return "";// FIXME
        }
    }
}
