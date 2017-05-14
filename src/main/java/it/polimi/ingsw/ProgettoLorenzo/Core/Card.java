package it.polimi.ingsw.ProgettoLorenzo.Core;

import java.util.*;

import com.google.gson.*;


public abstract class Card extends Action {
    public final String cardName;
    public final String cardType;
    public final String cardPeriod;

    private final List<Resources> cardCost = new ArrayList<>();
    public final Map<String, JsonElement> immediateEff = new HashMap<>();
    public final Map<String, JsonElement> permanentEff = new HashMap<>();

    public Card(JsonObject src) {
        this.cardName = src.get("name").getAsString();
        this.cardType = src.get("type").getAsString();
        this.cardPeriod = src.get("period").getAsString();

        JsonElement obj = src.get("cost");
        if (obj != null) {
            Iterator cos = obj.getAsJsonArray().iterator();
            while (cos.hasNext()) {
                this.cardCost.add(
                        Resources.fromJson((JsonObject)cos.next())
                );
            }
        }

        obj = src.get("immediateActions");
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

    public void actionBuilder() {
        this.addAction(
                new ResourcesAction("cost", this.getCardCost().inverse())
        );

        // FIXME this has to create a BaseAction's for all the actions
        // we're interested in.
    }

    @Override
    public String toString() {
        return this.cardName;
    }
}












