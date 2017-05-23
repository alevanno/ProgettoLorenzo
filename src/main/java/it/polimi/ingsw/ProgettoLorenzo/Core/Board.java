package it.polimi.ingsw.ProgettoLorenzo.Core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

public class Board {
    public List<Tower> towers = new ArrayList<>();
    public Production productionArea = new Production();
    public Harvest harvestArea = new Harvest();
    public Council councilPalace = new Council();
    public Market marketSpace = new Market();

    // FIXME
    // of course this has get larger and instantiate an
    // arbitrary number of towers
    public Board(Deck cardList) {
        JsonArray data = Utils.getJsonArray("towers.json");
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
