package it.polimi.ingsw.ProgettoLorenzo.Core;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.gson.*;


public abstract class Card extends Action {
    public final String cardName;  // FIXME make me private
    private final List<Resources> cardCost = new ArrayList<>();

    private final String cardType;
    private final String cardPeriod;
    //private final JsonArray immediateEff;
    //private final JsonArray permanentEff;

    public Card(JsonObject src) {
        this.cardName = src.get("name").getAsString();
        this.cardType = src.get("type").getAsString();
        this.cardPeriod = src.get("period").getAsString();
        for (int i=0; i < src.get("cost").getAsJsonArray().size() ; i++) {
            // FIXME way too many getAsFoo()....
            this.cardCost.add(Resources.fromJson(src.get("cost").getAsJsonArray().get(i).getAsJsonObject()));
        }
    }

    public Resources getCardCost() {
        // FIXME this crashes if size == 0
        if (this.cardCost.size() > 1) {
            System.out.println("You can choose what to pay:");
            for(Resources item: this.cardCost) {
               System.out.println(item.toString());
            }
            System.out.print("Insert number: ");
            Scanner in = new Scanner(System.in);
            return this.cardCost.get(in.nextInt());
        }
        return this.cardCost.get(0);
    }
}


