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

/**
 * This class collect and instantiate all the board's elements:
 * towers list, the {@link Production} area, the {@link Harvest}
 * area, the {@link Council} palace and the {@link Market} area.
 * All the towers are also serialized to be handled by the Client view.
 *
 * @see Game
 */
public class Board {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    public final List<Tower> towers = new ArrayList<>();
    public final Production productionArea = new Production();
    public final Harvest harvestArea = new Harvest();
    public final Council councilPalace = new Council();
    public final Market marketSpace = new Market();
    private Game game;

    /**
     * All the parameters to instantiate the towers are loaded from file.
     * The constructor initialize 4 towers by the development card's type and
     * the floor type.
     * The Deck representing the the cards displayed in the turn is split by card's type.
     * @param cardList the {@link Deck} that arrives from {@link Game}.
     * @param game the current game.
     */
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

    /**
     * it calls every single {@link Tower#serialize()} and add the info in a map, which
     * is serialized and sent to Client.
     * @return the JsonObject representing the serialized towers info
     */
    public JsonObject serialize() {
        // TODO council…
        Map<String, Object> ret = new HashMap<>();
        List<JsonObject> towersJ = new ArrayList<>();
        this.towers.forEach(
                t -> towersJ.add(t.serialize())
        );
        ret.put("towers", towersJ);
        ret.put("market", this.marketSpace.serialize());
        ret.put("harvest", this.harvestArea.serialize());
        ret.put("production", this.productionArea.serialize());
        return new Gson().fromJson(new Gson().toJson(ret), JsonObject.class);
    }
}
