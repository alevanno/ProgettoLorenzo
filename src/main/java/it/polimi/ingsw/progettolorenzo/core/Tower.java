package it.polimi.ingsw.progettolorenzo.core;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class Tower {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private final List<Floor> floors = new ArrayList<>();
    private final String type;

    public Tower(String type, JsonArray floors, Deck cardList) {
        this.type = type;

        log.fine(String.format(
            "Instantiating a tower [type %s, %d floors]…",
            type, floors.size()));

        Iterator<JsonElement> fit = floors.iterator();
        Iterator<Card> cit = cardList.iterator();
        while (fit.hasNext() && cit.hasNext()) {
            JsonObject f = fit.next().getAsJsonObject();
            Card c = cit.next();
            int floorValue = f.get("floorValue").getAsInt();
            JsonElement fjbonus = f.get("bonus");
            Resources fbonus;
            if (fjbonus != null) {
                fbonus = Resources.fromJson(fjbonus);
            } else {  // the floor doesn't specify any bonus
                fbonus = new Resources.ResBuilder().build();
            }
            this.floors.add(new Floor(fbonus, c, this, floorValue));
        }
    }

    public List<Floor> getFloors() {
        return floors;
    }

    public List<String> getTowerCardsName() {
        List<String> nameList = new ArrayList<>();

        for (Floor fl : this.floors) {
            if(fl.getCard() == null) {
                continue;
            }
            nameList.add(fl.getCard().getCardName());
        }
        return nameList;
    }

}
