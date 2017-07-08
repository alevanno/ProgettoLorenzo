package it.polimi.ingsw.progettolorenzo.core;

import java.util.*;

import com.google.gson.*;

/**
 * The Card class extends {@link Action} to implement the {@link #costActionBuilder(Player)}
 * method. It replicate in the best possible way a real game card.
 * The id field represent the number identifying the card.
 * The cardName, cardType, cardPeriod characterize the card.
 * the minMilitaryPoint indicates the minimum military points required to
 * obtain the card (that is a different concept from the military points to satisfy).
 * The resources representing the cardCost are added to a list; there can be a cost choice.
 * The immediate and permanent effects are represented in a Map, in order to retrieve them
 * in a simple and fast way.
 */
public class Card extends Action {
    public final int id;
    public final String cardName;
    public final String cardType;
    public final int cardPeriod;
    public final int minMilitaryPoint;

    private final List<Resources> cardCost = new ArrayList<>();
    public final Map<String, JsonElement> immediateEff = new HashMap<>();
    public final Map<String, JsonElement> permanentEff = new HashMap<>();

    /**
     * All the class fields are initialized from file.
     * @param src the json object containing the card's info.
     */
    public Card(JsonObject src) {
        this.id = src.get("id").getAsInt();
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

    /**
     *
     * @param pl the player who's has to be displayed the cost info.
     * @return
     */
    public Resources getCardCost(Player pl) {
        if (this.cardCost.size() > 1) {
            pl.sOut("You can choose what to pay:");
            for (Resources item : this.cardCost) {
                pl.sOut(item.toString());
            }
            int choice = pl.sInPrompt(0, this.cardCost.size() - 1);
            return this.cardCost.get(choice);
        }
        return this.cardCost.get(0);
    }

    public List<Resources> getCardCosts() {
        return this.cardCost;
    }

    /**
     * The {@link ResourcesAction} is added in the actions Card's list.
     * There's a discount on the cost by owning and activating Pico della Mirandola
     * leader card.
     * @see ResourcesAction
     * @see Action
     * @param pl the player having to pay the cost
     */
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

    /**
     * It serialize in a JsonObject the card's info to be sent to the Client
     * @return the JsonObject to be sent away.
     */
    public JsonObject serialize() {
        Map<String,Object> ret = new HashMap<>();
        ret.put("id", this.id);
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
