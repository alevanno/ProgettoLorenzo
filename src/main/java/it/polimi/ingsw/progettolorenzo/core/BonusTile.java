package it.polimi.ingsw.progettolorenzo.core;


import com.google.gson.JsonObject;

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

    public int getNumber() {
        return number + 1;
    }

    public Resources getHarvestRes() {
        return harvestRes;
    }

    public Resources getProductionRes() {
        return productionRes;
    }
}

