package it.polimi.ingsw.ProgettoLorenzo.Core;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Tower {
    private int towerNumber;
    private List<Card> cardList = new ArrayList<>();
    private List<Floor> floors = new ArrayList<>();

    public Tower(int towerNumber, List<Card> cardList) throws FileNotFoundException {
        this.towerNumber = towerNumber;
        this.cardList = cardList;
        ClassLoader classLoader = Council.class.getClassLoader();
        String resources = classLoader.getResource("tower.json").getFile();
        JsonArray data = new JsonParser().parse(new FileReader(resources))
                .getAsJsonArray();
        //FIXME make me prettier
        for(int i = 0; i < data.size(); i++) {
            floors.add(new Floor(Resources.fromJson(data.get(i).getAsJsonObject()),
                    cardList.get(i), this, i + 1));
        }
    }

    public int getTowerNumber() {
        return towerNumber;
    }

    public List<Card> getCardList() {
        return cardList;
    }

    public List<Floor> getFloors() {
        return floors;
    }
}
