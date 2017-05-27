package it.polimi.ingsw.progettolorenzo.core;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.progettolorenzo.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public JsonObject serialize() {
        // TODO display all the things, now only the towers;
        Map<String, Object> ret = new HashMap<>();
        List<JsonObject> towers = new ArrayList<>();
        this.towers.forEach(
                t -> towers.add(t.serialize())
        );
        ret.put("towers", towers);
        return new Gson().fromJson(new Gson().toJson(ret), JsonObject.class);
    }

    public void displayBoard() {
        Player currPlayer = game.getCurrPlayer();
        currPlayer.sOut("☃" + new Gson().toJson(this.serialize()));
    }
}
