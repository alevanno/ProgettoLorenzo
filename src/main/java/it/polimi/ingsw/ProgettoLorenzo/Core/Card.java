package it.polimi.ingsw.ProgettoLorenzo.Core;

import java.util.*;

import com.google.gson.*;


public abstract class Card extends Action {
    public final String cardName;  // FIXME make me private
    private final List<Resources> cardCost = new ArrayList<>();

    private final String cardType;
    private final String cardPeriod;
    public final Map<String, JsonElement> immediateEff = new HashMap<>();
    public final Map<String, JsonElement> permanentEff = new HashMap<>();

    public Card(JsonObject src) {
        this.cardName = src.get("name").getAsString();
        this.cardType = src.get("type").getAsString();
        this.cardPeriod = src.get("period").getAsString();

        for (int i=0; i < src.get("cost").getAsJsonArray().size() ; i++) {
            // FIXME way too many getAsFoo()....
            this.cardCost.add(Resources.fromJson(src.get("cost").getAsJsonArray().get(i).getAsJsonObject()));
        }

        Iterator imm = src.get("immediateActions").getAsJsonObject().entrySet().iterator();
        while(imm.hasNext()){
            Map.Entry pair = (Map.Entry) imm.next();
            immediateEff.put(pair.getKey().toString(), (JsonElement)pair.getValue());
        }


        Iterator per = src.get("permanentActions").getAsJsonObject().entrySet().iterator();
        while(per.hasNext()){
            Map.Entry pair = (Map.Entry) per.next();
            permanentEff.put(pair.getKey().toString(), (JsonElement)pair.getValue());
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












