package it.polimi.ingsw.ProgettoLorenzo.Core;

import com.google.gson.JsonObject;

/**
 * Created by Alessandro on 10/05/2017.
 */
public class TerrainCard extends Card {

    public TerrainCard(JsonObject src) {
        super(src);
        //this.card
    }

    public void print() {
        System.out.println(this.cardName);
        System.out.println(this.getCardCost());
    }
}