package it.polimi.ingsw.ProgettoLorenzo.Core;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.FileNotFoundException;

public class BonusTile {
    private int number;
    private Resources productionRes;
    private Resources harvestRes;

    //BonusTile to be set externally

    public BonusTile (JsonObject data) {
        this.number = data.get("number").getAsInt();
        this.productionRes = Resources.fromJson(data.get("productionRes")
                .getAsJsonObject());
        this.harvestRes = Resources.fromJson(data.get("harvestRes")
                .getAsJsonObject());
    }

    public Resources getHarvestRes() {
        return harvestRes;
    }

    public Resources getProductionRes() {
        return productionRes;
    }
}

