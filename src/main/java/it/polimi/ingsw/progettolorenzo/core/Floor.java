package it.polimi.ingsw.progettolorenzo.core;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.progettolorenzo.Game;

import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;

public class Floor extends Action {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private final Resources bonus;
    private final Tower parentTower;
    private FamilyMember famMember;
    private Card floorCard;
    private int floorValue;


    public Floor(Resources bonus, Card card, Tower tower, int floorValue) {
        this.bonus = bonus;
        this.floorCard = card;
        this.parentTower = tower;
        this.floorValue = floorValue;
        log.fine(String.format(
                "Floor instantiated <bonus: %s, card: %s, tower: %s>",
                bonus, card, tower));
    }

    // player puts here its famMemb & take the Card and the eventual bonus;
    public boolean claimFloor(FamilyMember fam) {
        int value = fam.getActionValue();
        Player p = fam.getParent();
        Resources tmpRes = p.currentRes;
        Resources cardCost = this.floorCard.getCardCost(); //TODO if a discount is present...
        //TODO value should be affected also by an excommunication
        for (Card c : p.listCards()) {
            JsonElement permEff = c.permanentEff.get("towerBonus");
            if(permEff != null) {
                if (permEff.getAsJsonObject().get("type").getAsString().equals(floorCard.cardType)) {
                    value += c.permanentEff.get("towerBonus").getAsJsonObject().get("plusValue").getAsInt();
                }
            }
        }//TODO the floor bonus can be used to pay for the card you're taking
        if (fam.getParent().getExcommunications().get(1).has("valMalus")) {
            if (fam.getParent().getExcommunications().get(1).get("type").getAsString() == parentTower.getType()) {
                int valMalus = fam.getParent().getExcommunications().get(1).get("valMalus").getAsInt();
                fam.getParent().sOut("Your excommunication lowers the value of this action by " + valMalus);
                value -= valMalus;
            }
        }
        if (value < this.floorValue) {
            return false;
        }
        if(tmpRes.militaryPoint >= floorCard.minMilitaryPoint
                && !tmpRes.merge(cardCost).isNegative()) {
            this.addAction(new TakeFamilyMember(fam));
            this.addAction(new PlaceFamilyMemberInFloor(fam, this));
            this.addAction(new ResourcesAction("floor bonus", this.bonus, p));
            this.addAction(new NestedAction(this.floorCard));
            this.floorCard.costActionBuilder(p);
            this.addAction(new NestedAction(
                new CardImmediateAction(this.floorCard, p)));
            this.addAction(new CardFromFloorAction(this.floorCard, this, p));
            return true;
        }
        return false;
    }

    protected void placeFamilyMember(FamilyMember f) {
        this.famMember = f;
    }

    public FamilyMember getFamMember() {
        return this.famMember;
    }

    public boolean isBusy() {
        return this.famMember != null;
    }

    public void removeCard() {
        this.floorCard = null;
    }

    public Card getCard() {
        return this.floorCard;
    }

    public JsonObject serialize() {
        Map<String, Object> ret = new HashMap<>();
        if (this.floorCard != null) {
            ret.put("card", this.floorCard.serialize());
        }
        if (this.famMember != null) {
            ret.put("famMember", this.famMember.serialize());
        }
        ret.put("value", this.floorValue);
        ret.put("bonus", this.bonus);
        return new Gson().fromJson(new Gson().toJson(ret), JsonObject.class);
    }
}
