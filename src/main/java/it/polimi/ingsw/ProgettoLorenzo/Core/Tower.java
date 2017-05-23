package it.polimi.ingsw.ProgettoLorenzo.Core;


import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.List;

public class Tower {
    private final int towerNumber;
    private final List<Floor> floors = new ArrayList<>();

    public Tower(int towerNumber, Deck cardList) {
        this.towerNumber = towerNumber;
        JsonArray data = Utils.getJsonArray("tower.json");
        //FIXME make me prettier
        for(int i = 0; i < data.size(); i++) {
            floors.add(new Floor(
                    Resources.fromJson(data.get(i).getAsJsonObject()),
                    cardList.remove(0), this);
        }
    }

    public int getTowerNumber() {
        return towerNumber;
    }

    public List<Floor> getFloors() {
        return floors;
    }
}
