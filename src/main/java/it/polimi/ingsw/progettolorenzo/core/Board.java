package it.polimi.ingsw.progettolorenzo.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.progettolorenzo.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

public class Board {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    public final List<Tower> towers = new ArrayList<>();
    public final Production productionArea = new Production();
    public final Harvest harvestArea = new Harvest();
    public final Council councilPalace = new Council();
    public final Market marketSpace = new Market();
    private Game game;
    // FIXME instantiate all the other things
    public Board(Deck cardList, Game game) {
        this.game = game;
        JsonArray data = Utils.getJsonArray("towers.json");
        log.fine(String.format("Instantiating %d towers…", data.size()));
        for (JsonElement i : data) {
            JsonObject tdata = i.getAsJsonObject();
            String ttype = tdata.get("type").getAsString();
            JsonArray tfloors = tdata.get("floors").getAsJsonArray();
            Deck tcards = StreamSupport.stream(cardList.spliterator(), false)
                .filter(c -> c.cardType.equals(ttype))
                .limit(tfloors.size())
                .collect(Deck::new, Deck::add, Deck::addAll);

            this.towers.add(new Tower(ttype, tfloors, tcards));
        }
    }

    public void displayBoard() {
        //TODO display all the things, now only cards;
        Player currPlayer = game.getCurrPlayer();
        for (Tower t : this.towers) {
            System.out.println(t.getTowerCardsName());
            currPlayer.getSocketOut().printf("%s\n", t.getTowerCardsName());
            currPlayer.getSocketOut().flush();
        }
    }
}
