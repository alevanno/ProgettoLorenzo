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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.logging.Level;
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
    @FXML private Label blackDice;
    @FXML private Label whiteDice;
    @FXML private Label orangeDice;

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

    protected void bigError(String msg) {
        Label lbl = new Label(msg);
        lbl.setTextFill(Color.RED);
        lbl.setFont(Font.font("bold", 14));
        Platform.runLater(() -> {
            bigPane.setBackground(Background.EMPTY);
            bigPane.getChildren().add(lbl);
        });
    }

    protected String readForm() {
        synchronized (msgInObserver) {
            try {
                msgInObserver.wait();
                return msgIn;
            } catch (InterruptedException e) {
                this.bigError(
                    "A severe error happened, the game might misbehave"
                );
                log.log(Level.SEVERE, e.getMessage(), e);
                Thread.currentThread().interrupt();
                return "";  //FIXME
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
        int cardId = ((UpdateBoard.NotedButton) event.getSource()).getCardId();

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
            private int cardId;

            NotedButton() {
                super();
            }

            public int getCardId() {
                return cardId;
            }

            public void setCardId(int cardId) {
                this.cardId = cardId;
            }
        }

        UpdateBoard(String input) {
            this.gameIn = new Gson().fromJson(input, JsonObject.class);
        }

        @Override
        public void run() {
            this.updateBoard(gameIn.get("board").getAsJsonObject());
            this.updateDices(gameIn.get("famValues"));
            // TODO display "players" too, somewhere
            this.updatePlayer(gameIn.get("you").getAsJsonObject());
        }

        private void updatePlayer(JsonObject plJ) {
            // TODO excomm, cards
            playerName.setText(plJ.get("playerName").getAsString());
            playerName.setTextFill(Color.valueOf(plJ.get("playerColour").getAsString()));
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
            this.updateTowers(towersJ);
        }

        private void updateDices(JsonElement famValues) {
            if (famValues == null) {
                return;
            }
            JsonObject dicesValues = famValues.getAsJsonObject();
            blackDice.setText(dicesValues.get("Black").getAsString());
            whiteDice.setText(dicesValues.get("White").getAsString());
            orangeDice.setText(dicesValues.get("Orange").getAsString());
        }

        private void updateTowers(JsonArray towersJ) {
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
                        Label lbl = new Label("No player here!");
                        lbl.setWrapText(true);
                        floorPane.getItems().add(new AnchorPane(lbl));
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
            btn.setCardId(card.id);
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
