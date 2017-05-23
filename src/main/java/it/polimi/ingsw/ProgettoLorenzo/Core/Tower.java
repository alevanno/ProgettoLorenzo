package it.polimi.ingsw.ProgettoLorenzo.Core;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Tower {
    private final List<Floor> floors = new ArrayList<>();
    private final String type;

    public Tower(String type, JsonArray floors, Deck cardList) {
        this.type = type;

        Iterator<JsonElement> fit = floors.iterator();
        Iterator<Card> cit = cardList.iterator();
        while (fit.hasNext() && cit.hasNext()) {
            JsonObject f = fit.next().getAsJsonObject();
            Card c = cit.next();
            JsonElement fjbonus = f.get("bonus");
            Resources fbonus;
            if (fjbonus != null) {
                fbonus = Resources.fromJson(fjbonus);
            } else {  // the floor doesn't specify any bonus
                fbonus = new Resources.ResBuilder().build();
            }
            this.floors.add(new Floor(fbonus, c, this));
        }
    }

    public List<Floor> getFloors() {
        return floors;
    }
}
