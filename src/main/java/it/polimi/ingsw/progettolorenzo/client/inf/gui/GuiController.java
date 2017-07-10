package it.polimi.ingsw.progettolorenzo.client.inf.gui;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.progettolorenzo.core.Card;
import it.polimi.ingsw.progettolorenzo.core.Resources;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GuiController {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private static String msgIn;
    private static final Object msgInObserver = new Object();
    private Map<String, Color> colourMapper = new HashMap<>();

    // variables saving the state of the player, so it can easily be used by
    // other methods not accessing all the player info
    private Pane bonusTile = new Pane(
        new Label("The bonus tile\nis not initialized yet"));
    private VBox council = new VBox(
        new Label("The council palace\nis not initialized yet"));
    private HBox excomm = new HBox(
        new Label("The excommunications\nare not initialized yet"));
    private Set<JsonObject> excomms = new HashSet<>();

    @FXML private TextArea mainLabel;
    @FXML private TextField userTextField;
    @FXML private Button sendBtn;
    @FXML private GridPane towers;
    @FXML private AnchorPane bigPane;
    @FXML private GridPane playerCards;
    @FXML private Label playerName;
    @FXML private VBox playersList;
    @FXML private Label playerStatus;
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
    @FXML private GridPane famMemHome;
    @FXML private StackPane prodSpace;
    @FXML private GridPane secondaryProd;
    @FXML private StackPane harvSpace;
    @FXML private GridPane secondaryHarv;
    @FXML private StackPane marketBooth1;
    @FXML private StackPane marketBooth2;
    @FXML private StackPane marketBooth3;
    @FXML private StackPane marketBooth4;
    private List<StackPane> market;

    

    @FXML
    public void initialize() {
        this.market = Arrays.asList(
            this.marketBooth1, this.marketBooth2,
            this.marketBooth3, this.marketBooth4
        );

        this.colourMapper.put("Blue", Color.web("#095599"));
        this.colourMapper.put("Red", Color.web("#990909"));
        this.colourMapper.put("Yellow", Color.web("#997709"));
        this.colourMapper.put("Green", Color.web("#099934"));
        this.colourMapper.put("Brown", Color.web("#592c06"));
        this.colourMapper.put("Violet", Color.web("#660999"));

        this.mainLabel.textProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue,
                                Object newValue) {
                // this will scroll to the bottom
                mainLabel.setScrollTop(Double.MAX_VALUE);
            }
        });
    }

    protected void updateMainLabel(String msg) {
        Runnable op;
        if (msg.startsWith("☃")) {
            op = new UpdateBoard(msg.substring(1));
        } else if (msg.startsWith("Please choose your colour")) {
            op = () -> {
                mainLabel.appendText("\n" + msg);
                this.btnPromptColour();
            };
        } else if (msg.startsWith("RMI or socket")) {
            op = () -> {
                mainLabel.appendText("\n" + msg);
                this.btnPromptConnection();
            };
        } else if (msg.startsWith("Basic or advanced")) {
            op = () -> {
                mainLabel.appendText("\n" + msg);
                this.btnPromptRules();
            };
        } else if (msg.startsWith("Input an int between")) {
            String[] minMax = msg.split(" ");
            int min = Integer.parseInt(minMax[4]);
            int max = Integer.parseInt(minMax[6]);
            op = () -> this.btnPromptInt(min, max);
        } else if (msg.startsWith("Input 'y'")) {
            op = this::btnPromptConf;
        } else {
            op = () -> mainLabel.appendText("\n" + msg);
        }
        Platform.runLater(op);
        log.finest("Successfully updated the label");
    }

    protected AnchorPane btnStartCommon() {
        useFadeOutTransition(Duration.millis(100), userTextField);
        sendBtn.setVisible(false);
        return (AnchorPane) userTextField.getParent();
    }

    protected void twoBtnCommon(Button first, Button second) {
        AnchorPane anc = btnStartCommon();
        HBox b = new HBox(50.0, first, second);
        b.setLayoutY(133.0);
        b.setLayoutX(129.0);
        anc.getChildren().add(b);
        userTextField.setVisible(false);
        useFadeInTransition(Duration.millis(300), b);
        first.setOnMouseClicked(e -> handlePromptBtnPress(e, b));
        second.setOnMouseClicked(e -> handlePromptBtnPress(e, b));
    }

    protected void btnPromptInt(int min, int max) {
        AnchorPane anc = btnStartCommon();
        HBox b = new HBox(50.0);
        b.setLayoutX(13.0);
        b.setLayoutY(133.0);
        for (int i = min; i <= max; i++) {
            Button btn = new Button(String.valueOf(i));
            btn.setOnMouseClicked(e -> handlePromptBtnPress(e, b));
            b.getChildren().add(btn);
        }
        //btn.setMinWidth(133.0);
        anc.getChildren().add(b);
        useFadeInTransition(Duration.millis(300), b);
    }

    protected void btnPromptConf() {
        Button yesBtn = new Button("Yes");
        yesBtn.setStyle("-fx-base: #90EE90;");
        yesBtn.setMinWidth(133.0);
        Button noBtn = new Button("No");
        noBtn.setStyle("-fx-base: #FF6666;");
        noBtn.setMinWidth(133.0);
        twoBtnCommon(yesBtn, noBtn);
    }

    protected void btnPromptColour() {
        AnchorPane anc = btnStartCommon();
        HBox btnSpace = new HBox(50.0);
        btnSpace.setLayoutX(13.0);
        btnSpace.setLayoutY(133.0);
        this.colourMapper.forEach((x, y) -> {
            Button btn = new Button(x);
            btn.setStyle(
                String.format(
                "-fx-base: #%02x%02x%02x;",
                    (int) (y.getRed() * 255),
                    (int) (y.getGreen() * 255),
                    (int) (y.getBlue() * 255)
            ));
            if ("Yellow".equals(x)) {
                btn.setTextFill(Color.WHITE);
            }
            btn.setOnMouseClicked(e -> handlePromptBtnPress(e, btnSpace));
            btnSpace.getChildren().add(btn);
        });
        anc.getChildren().add(btnSpace);
        useFadeInTransition(Duration.millis(300), btnSpace);
    }

    protected void btnPromptConnection() {
        Button socket = new Button("Socket");
        socket.setStyle("-fx-base: #ffc37f;");
        socket.setMinWidth(133.0);
        Button rmi = new Button("Rmi");
        rmi.setStyle("-fx-base: #bf7fff;");
        rmi.setMinWidth(133.0);
        twoBtnCommon(socket, rmi);
    }

    protected void btnPromptRules() {
        Button basic = new Button("Basic");
        basic.setStyle("-fx-base: #c7dde2;");
        basic.setMinWidth(133.0);
        Button advanced = new Button("Advanced");
        advanced.setStyle("-fx-base: #517077;");
        advanced.setTextFill(Color.WHITE);
        advanced.setMinWidth(133.0);
        twoBtnCommon(basic, advanced);
    }

    protected void handlePromptBtnPress (MouseEvent e, HBox b) {
        Button btn = (Button) e.getSource();
        userTextField.setText(btn.getText());
        sendBtn.fire();
        AnchorPane anc = (AnchorPane) b.getParent();
        anc.getChildren().removeAll(b);
        sendBtn.setVisible(true);
        userTextField.setVisible(true);
        useFadeInTransition(Duration.millis(100), userTextField);
    }

    protected void useFadeInTransition(Duration duration, Node node) {
        FadeTransition ft = new FadeTransition(duration, node);
        ft.setFromValue(0.1);
        ft.setToValue(1.0);
        ft.setCycleCount(1);
        ft.setAutoReverse(false);
        ft.play();
    }

    protected void useFadeOutTransition(Duration duration, Node node) {
        FadeTransition ft = new FadeTransition(duration, node);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setCycleCount(1);
        ft.setAutoReverse(false);
        ft.play();
    }

    protected void updateBigPane(Label lbl) {
        this.bigPane.getChildren().clear();
        lbl.setWrapText(true);
        lbl.setFont(Font.font("bold", 14));
        Platform.runLater(() -> {
            bigPane.getChildren().add(lbl);
        });
        useFadeInTransition(Duration.millis(300), bigPane);

    }

    protected String readForm() {
        synchronized (msgInObserver) {
            try {
                msgInObserver.wait();
                return msgIn;
            } catch (InterruptedException e) {
                Label lbl = new Label(
                    "A severe error happened, the game might misbehave"
                );
                lbl.setTextFill(Color.RED);
                this.updateBigPane(lbl);
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

    protected void handleCardBtnPress(MouseEvent event) {
        Card card = ((UpdateBoard.NotedButton) event.getSource()).getCard();

        if (event.getClickCount() == 2) {
            userTextField.setText(card.cardName);
            sendBtn.fire();
            return;
        }

        Image img = new Image(
            String.format("Gui/cards/%d.png", card.id),
            1000.0,  // arbitrary big
            317.0,
            true,
            false,
            true
        );
        ImageView im = new ImageView(img);
        this.bigPane.getChildren().clear();
        this.bigPane.getChildren().add(im);
        useFadeInTransition(Duration.millis(300), bigPane);
    }

    @FXML
    protected void showBonusTile(ActionEvent event) {
        this.bigPane.getChildren().clear();
        this.bigPane.getChildren().add(this.bonusTile);
        useFadeInTransition(Duration.millis(300), bigPane);
    }

    @FXML
    protected void showCouncil(ActionEvent event) {
        this.bigPane.getChildren().clear();
        this.bigPane.getChildren().add(this.council);
        useFadeInTransition(Duration.millis(300), bigPane);
    }

    @FXML
    protected void showExcomm(ActionEvent event) {
        this.bigPane.getChildren().clear();
        this.bigPane.getChildren().add(this.excomm);
        useFadeInTransition(Duration.millis(300), bigPane);
    }

    private class UpdateBoard implements Runnable {
        private JsonObject gameIn;

        private class NotedButton extends Button {
            private Card card;

            NotedButton() {
                super();
            }

            public Card getCard() {
                return card;
            }

            public void setCard(Card card) {
                this.card = card;
            }
        }

        UpdateBoard(String input) {
            this.gameIn = new Gson().fromJson(input, JsonObject.class);
        }

        @Override
        public void run() {
            this.updateBoard(gameIn.get("board").getAsJsonObject());
            this.updateDices(gameIn.get("famValues"));
            this.updatePlayer(gameIn.get("you").getAsJsonObject());
            this.updateExcomms(gameIn.get("excomms").getAsJsonArray());
            this.updatePlayers(gameIn.get("players").getAsJsonArray());
            this.updateCurPlayer(gameIn.get("currentPlayer").getAsJsonObject());
        }

        private void updateCurPlayer(JsonObject plJ) {
            playerStatus.setText(plJ.get("playerName").getAsString());
            playerStatus.setTextFill(colourMapper.get(plJ.get("playerColour").getAsString()));
        }

        private void updatePlayers(JsonArray playersListJ) {
            VBox vb = new VBox();
            vb.setAlignment(Pos.TOP_RIGHT);
            playersListJ.forEach(plJ -> {
                JsonObject p = plJ.getAsJsonObject();
                Label lbl = new Label(p.get("playerName").getAsString());
                lbl.setTextFill(colourMapper.get(p.get("playerColour").getAsString()));
                vb.getChildren().add(lbl);
            });
            playersList.getChildren().clear();
            playersList.getChildren().add(vb);
        }

        private void updateExcomms(JsonArray excommsJ){
           HBox h = new HBox();
           excommsJ.forEach(e ->
               h.getChildren().add(new ImageView(
                   new Image(
                       String.format(
                           "Gui/excomms/excomm_%d_%d.png",
                           e.getAsJsonObject().get("period").getAsBigInteger(),
                           e.getAsJsonObject().get("number").getAsBigInteger()
                       ),
                       1000.0,  // arbitrary big
                       140.0,
                       true,
                       false,
                       true
                   )
               ))
           );
            excomm = h;
        }

        private void updatePlayer(JsonObject plJ) {
            playerName.setText(plJ.get("playerName").getAsString());
            playerName.setTextFill(colourMapper.get(plJ.get("playerColour").getAsString()));
            updatePlayerCards(plJ.get("cards").getAsJsonArray());
            updateFamMember(plJ.get("famMembers").getAsJsonArray());
            Resources curRes = Resources.fromJson(plJ.get("resources"));
            currCoin.setText(String.valueOf(curRes.coin));
            currWood.setText(String.valueOf(curRes.wood));
            currStone.setText(String.valueOf(curRes.stone));
            currServant.setText(String.valueOf(curRes.servant));
            currFaith.setText(String.valueOf(curRes.faithPoint));
            currMilitary.setText(String.valueOf(curRes.militaryPoint));
            currVictory.setText(String.valueOf(curRes.victoryPoint));
            bonusTile.getChildren().clear();
            bonusTile.getChildren().add(new ImageView(new Image(
               String.format(
                   "Gui/bonusTiles/%d.png",
                   plJ.get("bonusTile").getAsJsonObject().get("id").getAsInt()
               ),
               1000.0,  // arbitrary big
               317.0,
               true,
               false,
               true
               )
           ));
            plJ.get("excomms").getAsJsonArray().forEach(e ->
                excomms.add(e.getAsJsonObject())
            );
        }

        private void updateFamMember(JsonArray famList) {
            famMemHome.getChildren().clear();
            int row = 0;
            for (JsonElement famJ : famList) {
                HBox famMemIcon = addFamMember(famJ.getAsJsonObject());
                famMemHome.add(famMemIcon, 0, row);
                famMemHome.setHalignment(famMemIcon, HPos.CENTER);
                row++;

            }
            useFadeInTransition(Duration.millis(1000), famMemHome);
        }

        private void updatePlayerCards(JsonArray cardsJ) {
            int territories = 0;
            int characters = 0;
            int buildings = 0;
            int ventures = 0;
            double height = 80.0;

            for (JsonElement c : cardsJ) {
                Card card = new Card(c.getAsJsonObject());
                switch (card.cardType) {
                    case "territories":
                        playerCards.add(addCard(card, height), territories, 0);
                        territories++;
                        break;
                    case "characters":
                        playerCards.add(addCard(card, height), characters, 1);
                        characters++;
                        break;
                    case "buildings":
                        playerCards.add(addCard(card, height), buildings, 2);
                        buildings++;
                        break;
                    case "ventures":
                        playerCards.add(addCard(card, height), ventures, 3);
                        ventures++;
                        break;
                    default:
                        log.severe("unknown type of card, no idea how come…");
                        break;
                }
            }
        }

        private void updateBoard(JsonObject boardJ) {
            this.updateTowers(boardJ.get("towers").getAsJsonArray());
            this.updateMarket(boardJ.get("market").getAsJsonArray());
            this.updateProdHarv(boardJ.get("production").getAsJsonObject(), prodSpace, secondaryProd);
            this.updateProdHarv(boardJ.get("harvest").getAsJsonObject(), harvSpace, secondaryHarv);
            this.updateCouncil(boardJ.get("council").getAsJsonArray());
        }

        private void updateCouncil(JsonArray councilJ) {
            VBox councilVB = new VBox();
            councilVB.setAlignment(Pos.CENTER);

            if (councilJ.size() == 0) {
                updateBigPane(new Label("The council is empty!"));
            }
            councilJ.forEach(famJ ->
                councilVB.getChildren().add(addFamMember(famJ.getAsJsonObject()))
            );
            councilVB.setSpacing(35);
            councilVB.setLayoutX(107);
            councilVB.setLayoutY(40);

            council = councilVB;
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
            if (towersJ.size() > 4) {
                log.severe("more than 4 towers are not supported here!");
            }
            towers.getChildren().clear();
            for (int i = 0; i < towersJ.size(); i++) {
                JsonObject towerJ = towersJ.get(i).getAsJsonObject();
                JsonArray floorsJ = towerJ.get("floors").getAsJsonArray();
                if (floorsJ.size() > 4) {
                    log.severe("more than 4 floors are not supported here!");
                }
                for (int j = 0; j < floorsJ.size(); j++) {
                    SplitPane floorPane = new SplitPane();
                    floorPane.setOrientation(Orientation.HORIZONTAL);
                    floorPane.setDividerPosition(0, 54.5454);
                    floorPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");

                    JsonObject floorJ = floorsJ.get(j).getAsJsonObject();
                    // card
                    JsonElement card = floorJ.get("card");
                    AnchorPane p = new AnchorPane();
                    if (card != null) {
                        p = addCard(new Card(card.getAsJsonObject()), 155.0);
                    }
                    DoubleBinding width = floorPane.widthProperty()
                        .multiply(0.54545454545454);
                    p.maxWidthProperty().bind(width);
                    p.minWidthProperty().bind(width);
                    floorPane.getItems().add(p);
                    // fam member
                    JsonElement famMember = floorJ.get("famMember");
                    if (famMember != null) {
                        floorPane.getItems().add(
                            addFamMember(famMember.getAsJsonObject())
                        );
                    }
                    useFadeInTransition(Duration.millis(800), towers);
                    towers.add(floorPane, i, j);
                }
            }
        }

        private void updateMarket(JsonArray marketJ) {
            for (int i=0; i < marketJ.size(); i++) {
                market.get(i).getChildren().clear();
                JsonObject boothJ = marketJ.get(i).getAsJsonObject();
                JsonElement fam = boothJ.get("famMember");
                if (fam != null) {
                    market.get(i).getChildren().add(
                        addFamMember(fam.getAsJsonObject())
                    );
                }
            }
        }

        private void updateProdHarv(JsonObject prodJ, StackPane mainSpace, GridPane secondarySpace) {
            mainSpace.getChildren().clear();
            secondarySpace.getChildren().clear();
            JsonElement mainE = prodJ.get("main");
            if (mainE != null) {
                mainSpace.getChildren().add(addFamMember(mainE.getAsJsonObject()));
            }
            JsonArray secondaryJ = prodJ.get("secondary").getAsJsonArray();
            for (int i=0; i < secondaryJ.size(); i++) {
                secondarySpace.add(
                    addFamMember(secondaryJ.get(i).getAsJsonObject()), i, 0);
            }
        }

        private AnchorPane addCard(Card card, double height) {
            Image img = new Image(
                String.format("Gui/cards/%d.png", card.id),
                1000.0,  // arbitrary big
                height,
                true,
                false,
                true
            );

            NotedButton btn = new NotedButton();
            btn.setBackground(new Background(new BackgroundImage(img,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)
            ));
            btn.setCard(card);
            btn.setOnMouseClicked(e -> handleCardBtnPress(e));

            AnchorPane a = new AnchorPane(btn);
            AnchorPane.setBottomAnchor(btn, 0.0);
            AnchorPane.setTopAnchor(btn, 0.0);
            AnchorPane.setRightAnchor(btn, 0.0);
            AnchorPane.setLeftAnchor(btn, 0.0);
            return a;
        }

        private HBox addFamMember(JsonObject famMember) {
            Circle external = new Circle(
                18.0,
                colourMapper.get(famMember.get("parentColour").getAsString())
            );
            String skinColour = famMember.get("skinColour").getAsString();
            Paint c;
            if ("Blank".equals(skinColour)) {
                c = new Color(1, 1, 1, 0.5);
            } else if ("Dummy".equals(skinColour)){
                return new HBox();
            } else {
                c = Paint.valueOf(skinColour);
            }
            Circle internal = new Circle(11.0, c);
            AnchorPane a = new AnchorPane(external, internal);
            a.setCenterShape(true);
            VBox vb = new VBox(a);
            vb.setAlignment(Pos.CENTER);
            vb.setMinHeight(vb.getMaxHeight());
            HBox hb = new HBox(vb);
            hb.setAlignment(Pos.CENTER);
            hb.setMinWidth(vb.getMaxWidth());
            return hb;
        }
    }

}
