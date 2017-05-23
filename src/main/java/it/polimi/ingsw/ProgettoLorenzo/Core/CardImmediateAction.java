package it.polimi.ingsw.ProgettoLorenzo.Core;


import com.google.gson.JsonObject;

import java.util.List;
import java.util.Set;

public class CardImmediateAction extends Action {

    public CardImmediateAction(Card card, Player pl) {

        if (card.immediateEff.containsKey("resources")) {
            Resources tmp = Resources.fromJson(card.immediateEff.get("resources").getAsJsonObject());
            this.addAction(new ResourcesAction("ImmResources", tmp, pl));
            System.out.println("ImmediateAction: Card " + card.getCardName() + " gave " + tmp.toString());
        }

        if (card.immediateEff.containsKey("councilPrivilege")) {
            int priv = card.immediateEff.get("councilPrivilege").getAsInt();
            Set<Resources> privRes = (new Council().chooseMultiPrivilege(priv));
            for (Resources r : privRes) {
                this.addAction(new ResourcesAction(
                        "ImmActCouncilPrivilege", r, pl));
                System.out.println("ImmediateAction: Card " + card.getCardName() + " gave " + r.toString());
            }
        }

        if (card.immediateEff.containsKey("immediateProd")) {
            int value = card.immediateEff.get("immediateProd").getAsInt();
            new Production().prod(pl, value);
        }

        if (card.immediateEff.containsKey("immediateHarv")) {
            int value = card.immediateEff.get("immediateHarv").getAsInt();
            new Production().prod(pl, value);
        }


        //TODO to handle pickCard we have first to discuss the cardCostHandling
        if(card.immediateEff.containsKey("pickCard")) {
            pl.currentRes.merge(card.getCardCost());
            List<Integer> plResList = pl.currentRes.getAsList();
            for(int i : plResList) {
                if (i < 0) {
                    System.out.println("Your resources don't satisfy the card cost");
                    break;
                }
            }
            //TODO
        }

        if(card.immediateEff.containsKey("multiplier")) {
            Resources prodRes = new Resources.ResBuilder().build();
            JsonObject mult = card.immediateEff.get("multiplier").getAsJsonObject();
            String tmpType = mult.get("type").getAsString();
            Resources tmpRes = Resources.fromJson(mult.get("bonus").getAsJsonObject());
            int count = 0;
            for (Card c : pl.listCards()) {
                if (c.cardType.equals(tmpType)) { count++; }
            }
            prodRes.merge(tmpRes.multiplyRes(count));
            this.addAction(new ResourcesAction(
                    "ImmMultiplier", tmpRes.multiplyRes(count), pl));
            System.out.println("ImmediateAction: Multiplier Card " + card.getCardName() + " gave " + tmpRes.toString());
        }
    }
}
