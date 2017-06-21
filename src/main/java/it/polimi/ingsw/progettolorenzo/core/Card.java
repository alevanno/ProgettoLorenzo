package it.polimi.ingsw.progettolorenzo.core;

import java.util.*;

import com.google.gson.*;


public class Card extends Action {
    public final String cardName;
    public final String cardType;
    public final int cardPeriod;
    public final int minMilitaryPoint;

    private final List<Resources> cardCost = new ArrayList<>();
    public final Map<String, JsonElement> immediateEff = new HashMap<>();
    public final Map<String, JsonElement> permanentEff = new HashMap<>();


    public Card(JsonObject src) {
        this.cardName = src.get("name").getAsString();
        this.cardType = src.get("type").getAsString();
        this.cardPeriod = src.get("period").getAsInt();
        JsonElement tmp = src.get("minMilitaryPoint");
        if (tmp != null) {
            this.minMilitaryPoint = tmp.getAsInt();
        } else {
            this.minMilitaryPoint = 0;
        }
        JsonElement obj = src.get("cost");
        if (obj != null) {
            if (obj.getAsJsonArray().size() == 0) {
                this.cardCost.add(new Resources.ResBuilder().build());
            } else {
                Iterator cos = obj.getAsJsonArray().iterator();
                while (cos.hasNext()) {
                    this.cardCost.add(
                            Resources.fromJson((JsonObject) cos.next())
                    );
                }
            }
        }

        obj = src.get("immediateAction");
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

    public String getCardName() {
        return cardName;
    }

    public Resources getCardCost(Player pl) {
        if (this.cardCost.size() > 1) {
            System.out.println("You can choose what to pay:");
            for (Resources item : this.cardCost) {
                System.out.println(item.toString());
            }
            int choice = pl.sInPrompt(0, this.cardCost.size() - 1);
            return this.cardCost.get(choice);
        }
        return this.cardCost.get(0);
    }

    public void costActionBuilder(Player pl) {
        int discount = 0;
        if (pl.leaderIsActive("Pico Della Mirandola")){
            if(this.getCardCost(pl).coin >= 3) {
                discount = 3;
            } else if (this.getCardCost(pl).coin < 3 &&
                    this.getCardCost(pl).coin > 0) {
                discount = this.getCardCost(pl).coin;
            }
        }
        this.addAction(
            new ResourcesAction(
                "Card cost", this.getCardCost(pl)
                    .merge(new Resources.ResBuilder()
                            .coin(discount).build().inverse()).inverse(), pl
            )
        );
    }

    public JsonObject serialize() {
        Map<String,Object> ret = new HashMap<>();
        ret.put("name", this.cardName);
        ret.put("period", this.cardPeriod);
        ret.put("type", this.cardType);
        ret.put("cost", this.cardCost);
        return new Gson().fromJson(new Gson().toJson(ret), JsonObject.class);
    }

    @Override
    public String toString() {
        return String.format("%s (period %d, type %s)",
                this.cardName, this.cardPeriod, this.cardType);
    }
}
