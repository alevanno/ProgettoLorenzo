package it.polimi.ingsw.progettolorenzo.client.inf.gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.logging.Logger;

public class GuiController {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private GuiInterface inf;
    private static String msgIn;
    private static final Object msgInObserver = new Object();

    @FXML private Label mainLabel;
    @FXML private TextField userTextField;

    protected void setInf(GuiInterface inf) {
        log.info("set inf");
        this.inf = inf;
    }

    protected void updateMainLabel(String msg) {
        Platform.runLater(() -> mainLabel.setText(msg));
    }

    protected String readForm() {
        synchronized (msgInObserver) {
            try {
                msgInObserver.wait();
                return msgIn;
            } catch (InterruptedException e) {
                return "";  // FIXME
            }
        }

    }

    @FXML
    protected void handleBtnPress(ActionEvent event) {
        synchronized (msgInObserver) {
            msgIn = this.userTextField.getText();
            msgInObserver.notify();
        }
        this.userTextField.clear();
    }

}
