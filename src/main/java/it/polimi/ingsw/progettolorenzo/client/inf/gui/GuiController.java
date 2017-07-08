package it.polimi.ingsw.progettolorenzo.client.inf.gui;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.progettolorenzo.core.Card;
import it.polimi.ingsw.progettolorenzo.core.Resources;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.util.logging.Logger;

public class GuiController {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private GuiInterface inf;
    private static String msgIn;
    private static final Object msgInObserver = new Object();

    @FXML private TextArea mainLabel;
    @FXML private TextField userTextField;
    @FXML private GridPane towers;
    @FXML private AnchorPane bigPane;
    @FXML private Label playerName;
    @FXML private Label currCoin;
    @FXML private Label currWood;
    @FXML private Label currStone;
    @FXML private Label currServant;
    @FXML private Label currFaith;
    @FXML private Label currMilitary;
    @FXML private Label currVictory;

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

    protected void handleCardBtnPress(ActionEvent event) {
        int cardId = ((UpdateBoard.NotedButton) event.getSource()).CardId;

        Image img = new Image(
            String.format("Gui/cards/%d.png", cardId),
            1000.0,  // arbitrary big
            300.0,
            true,
            false,
            true
        );

        this.bigPane.setBackground(new Background(new BackgroundImage(img,
            BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)
        ));
    }

    private class UpdateBoard implements Runnable {
        private JsonObject gameIn;

        private class NotedButton extends Button {
            public int CardId;

            NotedButton() {
                super();
            }
        }

        UpdateBoard(String input) {
            this.gameIn = new Gson().fromJson(input, JsonObject.class);
        }

        @Override
        public void run() {
            this.updateBoard(gameIn.get("board").getAsJsonObject());
            // TODO display "players" too, somewhere
            this.updatePlayer(gameIn.get("you").getAsJsonObject());
        }

        private void updatePlayer(JsonObject plJ) {
            // TODO excomm, cards, colour
            playerName.setText(plJ.get("playerName").getAsString());
            Resources curRes = Resources.fromJson(plJ.get("resources"));
            currCoin.setText(String.valueOf(curRes.coin));
            currWood.setText(String.valueOf(curRes.wood));
            currStone.setText(String.valueOf(curRes.stone));
            currServant.setText(String.valueOf(curRes.servant));
            currFaith.setText(String.valueOf(curRes.faithPoint));
            currMilitary.setText(String.valueOf(curRes.militaryPoint));
            currVictory.setText(String.valueOf(curRes.victoryPoint));
        }

        private void updateBoard(JsonObject boardJ) {
            JsonArray towersJ = boardJ.get("towers").getAsJsonArray();
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

            Image img = new Image(
                String.format("Gui/cards/%d.png", card.id),
                1000.0,  // arbitrary big
                170.0,
                true,
                false,
                true
            );

            NotedButton btn = new NotedButton();
            btn.setBackground(new Background(new BackgroundImage(img,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)
            ));
            btn.CardId = card.id;
            btn.setOnAction(e -> handleCardBtnPress(e));

            AnchorPane a = new AnchorPane(btn);
            AnchorPane.setBottomAnchor(btn, 0.0);
            AnchorPane.setTopAnchor(btn, 0.0);
            AnchorPane.setRightAnchor(btn, 0.0);
            AnchorPane.setLeftAnchor(btn, 0.0);
            return a;
        }
    }

}
