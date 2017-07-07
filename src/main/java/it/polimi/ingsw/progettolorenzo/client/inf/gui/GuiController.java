package it.polimi.ingsw.progettolorenzo.client.inf.gui;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.progettolorenzo.core.Card;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.logging.Logger;

public class GuiController {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private GuiInterface inf;
    private static String msgIn;
    private static final Object msgInObserver = new Object();

    @FXML private TextArea mainLabel;
    @FXML private TextField userTextField;
    @FXML private GridPane towers;

    @FXML
    public void initialize() {
        this.mainLabel.textProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue,
                                Object newValue) {
                // this will scroll to the bottom
                mainLabel.setScrollTop(Double.MAX_VALUE);
            }
        });
    }

    protected void setInf(GuiInterface inf) {
        this.inf = inf;
    }

    protected void updateMainLabel(String msg) {
        Runnable op;
        if (msg.startsWith("☃")) {
            op = new UpdateBoard(msg.substring(1));
        } else {
            op = () -> mainLabel.appendText("\n"+msg);
        }
        Platform.runLater(op);
        log.finest("Successfully updated the label");
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

    private class UpdateBoard implements Runnable {
        private JsonObject boardIn;

        UpdateBoard(String input) {
            this.boardIn = new Gson().fromJson(input, JsonObject.class);
        }

        @Override
        public void run() {
            JsonArray towersJ = this.boardIn.get("towers").getAsJsonArray();
            if(towersJ.size() > 4) {
                log.severe("more than 4 towers are not supported here!");
            }
            for(int i=0; i<towersJ.size(); i++) {
                JsonObject towerJ = towersJ.get(i).getAsJsonObject();
                JsonArray floorsJ = towerJ.get("floors").getAsJsonArray();
                if(floorsJ.size() > 4) {
                    log.severe("more than 4 floors are not supported here!");
                }
                for(int j=0; j<floorsJ.size(); j++) {
                    SplitPane floorPane = new SplitPane();
                    floorPane.setOrientation(Orientation.HORIZONTAL);
                    floorPane.setDividerPosition(0, 54.5454);
                    floorPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");

                    JsonObject floorJ = floorsJ.get(j).getAsJsonObject();
                    JsonElement card = floorJ.get("card");
                    if (card != null) {
                        floorPane.getItems().add(
                            addCard(card.getAsJsonObject())
                        );
                    } else {
                        floorPane.getItems().add(
                            new AnchorPane(
                                new Label("No card here!")
                            )
                        );
                    }
                    JsonElement famMember = floorJ.get("famMember");
                    if (famMember != null) {
                        floorPane.getItems().add(
                            new Label(famMember.getAsJsonObject().toString())
                        );
                    } else {
                        floorPane.getItems().add(
                            new AnchorPane(
                                new Label("No player here!")
                            )
                        );
                    }

                    towers.add(floorPane, i, j);
                }
            }
        }

        private AnchorPane addCard(JsonObject cardJ) {
            Card card = new Card(cardJ);

            VBox ret = new VBox(
                new Label(card.cardName),
                new HBox(
                    new Label("period: "),
                    new Label(String.valueOf(card.cardPeriod))
                ),
                new HBox(
                    new Label("type: "),
                    new Label(card.cardType)
                )
            );
            HBox ccost = new HBox(new Label("cost: "));
            card.getCardCosts().forEach(r ->
                ccost.getChildren().add(new Label(r.toString()))
            );
            ret.getChildren().add(ccost);
            return new AnchorPane(ret);
        }
    }

}
