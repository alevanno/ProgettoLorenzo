package it.polimi.ingsw.ProgettoLorenzo.Core;


import com.google.gson.JsonObject;

import java.io.FileNotFoundException;

public class BonusTile {
    private Player owner; //forse va istanziato in player, piuttosto che inizializzarlo qui
    private int number;
    private Resources productionRes;
    private Resources harvestRes;

    public BonusTile (JsonObject data) throws FileNotFoundException {
        this.number = data.get("number").getAsInt();
        this.productionRes = Resources.fromJson(data.get("productionRes")
                .getAsJsonObject());
        this.harvestRes = Resources.fromJson(data.get("harvestRes")
                .getAsJsonObject());
    }


}

