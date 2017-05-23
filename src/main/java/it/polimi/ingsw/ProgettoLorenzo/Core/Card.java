package it.polimi.ingsw.ProgettoLorenzo.Core;

import java.util.*;

import com.google.gson.*;


public class Card extends Action {
    public final String cardName;
    public final String cardType;
    public final int cardPeriod;

    private final List<Resources> cardCost = new ArrayList<>();
    public final Map<String, JsonElement> immediateEff = new HashMap<>();
    public final Map<String, JsonElement> permanentEff = new HashMap<>();


    public Card(JsonObject src) {
        this.cardName = src.get("name").getAsString();
        this.cardType = src.get("type").getAsString();
        this.cardPeriod = src.get("period").getAsInt();

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

    public Resources getCardCost() {
        if (this.cardCost.size() > 1) {
            System.out.println("You can choose what to pay:");
            for (Resources item : this.cardCost) {
                System.out.println(item.toString());
            }
            System.out.print("Insert number: ");
            Scanner in = new Scanner(System.in);
            while (!in.hasNextInt()) {
                in.next();
                System.out.println("Please input an int");
            }
            int choice = in.nextInt();
            return this.cardCost.get(choice);
        }
        return this.cardCost.get(0);
    }

    public void costActionBuilder(Player player) {
        this.addAction(
            new ResourcesAction(
                "Card cost", this.getCardCost().inverse(), player
            )
        );
    }

    // FIXME this has to create a BaseAction's for all the actions
        // we're interested in.

    @Override
    public String toString() {
        return String.format("%s (period %d, type %s)",
                this.cardName, this.cardPeriod, this.cardType);
    }
}
