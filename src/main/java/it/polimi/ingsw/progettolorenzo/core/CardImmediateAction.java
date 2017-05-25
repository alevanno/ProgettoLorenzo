package it.polimi.ingsw.progettolorenzo.core;


import com.google.gson.JsonObject;

import java.util.Set;
import java.util.logging.Logger;

public class CardImmediateAction extends Action {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    public CardImmediateAction(Card card, Player pl) {

        if (card.immediateEff.containsKey("resources")) {
            Resources tmp = Resources.fromJson(card.immediateEff.get("resources").getAsJsonObject());
            this.addAction(new ResourcesAction("ImmResources", tmp, pl));
            log.info("ImmediateAction: Card " + card.getCardName() + " gave " + tmp.toString());
        }

        if (card.immediateEff.containsKey("councilPrivilege")) {
            int priv = card.immediateEff.get("councilPrivilege").getAsInt();
            Set<Resources> privRes = new Council().chooseMultiPrivilege(priv);
            for (Resources r : privRes) {
                this.addAction(new ResourcesAction(
                        "ImmActCouncilPrivilege", r, pl));
                log.info("ImmediateAction: Card " + card.getCardName() + " gave " + r.toString());
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
        //TODO this action's value can be increased with servants
        //TODO you have to pay the 3 coins if the tower is already occupied
        if(card.immediateEff.containsKey("pickCard")) {
            String type = card.immediateEff.get("pickCard").getAsJsonObject().get("type").getAsString();
            int value = card.immediateEff.get("pickCard").getAsJsonObject().get("value").getAsInt();
            Resources discount = Resources.fromJson(card.immediateEff.get("pickCard").getAsJsonObject().get("discount"));
            //TODO tower type? deve chiamare in qualche modo claimFloorWithCard(Player player, Tower parentTower, int value, Resources discount)
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
            log.info("ImmediateAction: Multiplier Card " + card.getCardName() + " gave " + tmpRes.toString());
        }
    }
}
