package it.polimi.ingsw.progettolorenzo.core;


import com.google.gson.JsonObject;

/**
 * It basically is a data structure containing the {@link Production}
 * and {@link Harvest} bonus resources that a player can receive by
 * attempting one of this two actions.
 * @see Resources
 */
public class BonusTile {
    private int number;
    private Resources productionRes;
    private Resources harvestRes;

    /**
     * Class fields are initialized from file, then
     * the BonusTile object is ready to be set externally.
     * @param data the JsonObject containing the bonustile's information
     */
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

    /**
     * @return the resources to be merged to the player's resources in an Harvest action.
     */
    public Resources getHarvestRes() {
        return harvestRes;
    }

    /**
     * @return the resources to be merged to the player's resources in an Harvest action.
     */
    public Resources getProductionRes() {
        return productionRes;
    }

    public String toString() {
        return String.format("%d: prod=%s | harv=%s",
                this.number, this.productionRes, this.harvestRes);
    }
}

