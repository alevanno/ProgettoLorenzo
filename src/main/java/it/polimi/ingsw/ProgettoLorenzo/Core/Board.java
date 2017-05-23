package it.polimi.ingsw.ProgettoLorenzo.Core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

public class Board {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    public List<Tower> towers = new ArrayList<>();
    public Production productionArea = new Production();
    public Harvest harvestArea = new Harvest();
    public Council councilPalace = new Council();
    public Market marketSpace = new Market();

    // FIXME instantiate all the other things
    public Board(Deck cardList) {
        JsonArray data = Utils.getJsonArray("towers.json");
        log.fine(String.format("Instantiation %d towers…", data.size()));
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
}
