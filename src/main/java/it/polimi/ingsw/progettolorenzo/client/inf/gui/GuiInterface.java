package it.polimi.ingsw.progettolorenzo.client.inf.gui;

import it.polimi.ingsw.progettolorenzo.client.inf.Interface;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Logger;

public class GuiInterface extends Application implements Interface {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private static boolean guiStarted = false;
    private static final Object guiMonitor = new Object();
    private static GuiController controller;


    @Override
    public void start(Stage stage) throws IOException {
        log.info("Starting the GUI...");

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ClassLoader.class.getResource("/Gui/GuiFx.fxml"));
        Pane root = loader.load();
        controller = loader.getController();
        controller.setInf(this);

        synchronized (guiMonitor) {
            guiStarted = true;
            log.info("notifying…");
            guiMonitor.notify();
        }
        log.info("done notify");

        stage.setTitle("Lorenzo il Magnifico");
        Scene scene = new Scene(root, 1400, 900);
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
                try {
                    log.info("going to wait for gui…");
                    guiMonitor.wait();
                    log.info("done waiting");
                } catch (InterruptedException e) {
                    log.severe("Failed to start the GUI");
                }
            }
        }
        log.info("attempting delivery");
        controller.updateMainLabel(String.format(format, args));
    }

    @Override
    public String readLine() {
        String msgIn = controller.readForm();
        log.info("Sending over ["+msgIn+"]");
        return msgIn;
    }

    @Override
    public String readLine(String format, Object... args) {
        this.printLine(format, args);
        return this.readLine();
    }

}
