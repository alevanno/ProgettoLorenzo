package it.polimi.ingsw.progettolorenzo.client.inf.gui;

import it.polimi.ingsw.progettolorenzo.client.inf.Interface;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GuiInterface extends Application implements Interface {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private static boolean guiStarted = false;
    private static final Object guiMonitor = new Object();
    private static GuiController controller;

    public static void main(String[] args) throws Exception {
        throw new Exception("Don't start this class, please start " +
            "`it.polimi.ingsw.progettolorenzo.client.Client` instead");
    }

    @Override
    public void start(Stage stage) throws IOException {
        log.info("Starting the GUI...");

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ClassLoader.class.getResource("/Gui/GuiFx.fxml"));
        Pane root = loader.load();
        controller = loader.getController();

        synchronized (guiMonitor) {
            guiStarted = true;
            log.finest("GUI started, notifying the monitor…");
            guiMonitor.notify();
        }
        log.finest("GUI started, the monitor has been notified");

        stage.setTitle("Lorenzo il Magnifico");
        Scene scene = new Scene(root, 1303, 937);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void printLine(String format, Object... args) {
        log.fine("new message arrived: ["+format+"]");
        synchronized (guiMonitor) {
            while (!guiStarted) {
                log.finest("GUI not yet started, spawning the thread…");
                Runnable t = () ->
                    javafx.application.Application.launch(this.getClass());
                new Thread(t).start();
                try {
                    log.finest("GUI thread started, waiting for ACK");
                    guiMonitor.wait();
                    log.finest("GUI thread started, ACK received, continuing…");
                } catch (InterruptedException e) {
                    log.log(Level.SEVERE, "Failed to start the GUI", e);
                    Thread.currentThread().interrupt();
                }
            }
        }
        controller.updateMainLabel(String.format(format, args));
    }

    @Override
    public String readLine() {
        String msgIn = controller.readForm();
        log.finer("Sending over the message: ["+msgIn+"]");
        return msgIn;
    }

    @Override
    public String readLine(String format, Object... args) {
        this.printLine(format, args);
        return this.readLine();
    }

}
