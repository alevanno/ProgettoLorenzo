package it.polimi.ingsw.ProgettoLorenzo.Core;

import java.util.*;

import com.google.gson.*;


public abstract class Card extends Action {
    public final String cardName;
    public final String cardType;
    public final String cardPeriod;

    private final List<Resources> cardCost = new ArrayList<>();
    private final Map<String, JsonElement> immediateEff = new HashMap<>();
    private final Map<String, JsonElement> permanentEff = new HashMap<>();

    public Card(JsonObject src) {
        this.cardName = src.get("name").getAsString();
        this.cardType = src.get("type").getAsString();
        this.cardPeriod = src.get("period").getAsString();

        for (int i = 0; i < src.get("cost").getAsJsonArray().size(); i++) {
            // FIXME way too many getAsFoo()....
            this.cardCost.add(Resources.fromJson(src.get("cost").getAsJsonArray().get(i).getAsJsonObject()));
        }

        JsonElement obj = src.get("immediateActions");
        if (obj != null) {
            Iterator imm = obj.getAsJsonObject().entrySet().iterator();
            while (imm.hasNext()) {
                Map.Entry pair = (Map.Entry) imm.next();
                immediateEff.put(pair.getKey().toString(), (JsonElement) pair.getValue());
            }
        }


        obj = src.get("permanentAction");
        if (obj != null) {
            Iterator per = obj.getAsJsonObject().entrySet().iterator();
            while (per.hasNext()) {
                Map.Entry pair = (Map.Entry) per.next();
                permanentEff.put(pair.getKey().toString(), (JsonElement) pair.getValue());
            }
        }
    }

    public Resources getCardCost() {
        // FIXME this crashes if size == 0
        if (this.cardCost.size() > 1) {
            System.out.println("You can choose what to pay:");
            for (Resources item : this.cardCost) {
                System.out.println(item.toString());
            }
            System.out.print("Insert number: ");
            Scanner in = new Scanner(System.in);
            return this.cardCost.get(in.nextInt());
        }
        return this.cardCost.get(0);
    }

    @Override
    public String toString() {
        return this.cardName;
    }
}












