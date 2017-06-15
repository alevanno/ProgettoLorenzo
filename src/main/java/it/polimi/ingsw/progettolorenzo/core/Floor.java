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
    private static Floor callerFl; //this is needed for floorActionWithCard


    public Floor(Resources bonus, Card card, Tower tower, int floorValue) {
        this.bonus = bonus;
        this.floorCard = card;
        this.parentTower = tower;
        this.floorValue = floorValue;
        log.fine(String.format(
                "Floor instantiated <bonus: %s, card: %s, tower: %s>",
                bonus, card, tower));
    }

    public boolean accessFloor(Player pl, int towerOcc){ //returns true if the floor is accessed and takes the coins from the player if necessary
        for(LeaderCard leader : pl.getLeaderCards()){
            if("Filippo Brunelleschi".equals(leader.getName()) && leader.isActivated()) {
                // it allows to avoid additional payment
                return true;
            }
        }
        if (towerOcc == 0) {
            pl.sOut("Tower is free");
            return true;
        } else if (towerOcc == 1) {
            pl.sOut("Tower already occupied: ");
            pl.sOut("Current Res:" + pl.getCurrentRes().toString());
            if (pl.getCurrentRes().coin < 3) {
                pl.sOut("You don't have enough coins to access this Floor");
                return  false;
            } else {
                pl.sOut("Do you want to pay an additional 3 coins to complete your action?: y/n");
                if (pl.sInPromptConf()) {
                    Resources coinToPay = new Resources.ResBuilder().coin(3).build().inverse();
                    this.addAction(new ResourcesAction("Floor access token", coinToPay, pl));
                    return true;
                }
            }
        } else if (towerOcc == 2) {
            pl.sOut("This floor is not available to you");
            return false;
        }
        return true;
    }

    // player puts here its famMemb & takes the Card and the eventual bonus;
    public boolean claimFloor(FamilyMember fam) {
        int value = fam.getActionValue();
        Player p = fam.getParent();
        Resources tmpRes = p.getCurrentRes();
        Resources cardCost = this.floorCard.getCardCost(p); //TODO if a discount is present...
        boolean boycottBonus = false;
        //TODO value should be affected also by an excommunication
        for (Card c : p.listCards()) {
            boycottBonus = c.permanentEff.containsKey("boycottInstantTowerBonus");
            JsonElement permTowerBonus = c.permanentEff.get("towerBonus");
            if(permTowerBonus != null) {
                if (permTowerBonus.getAsJsonObject().get("type").getAsString().equals(floorCard.cardType)) {
                    value += permTowerBonus.getAsJsonObject().get("plusValue").getAsInt();
                }
            }
        }//TODO the floor bonus can be used to pay for the card you're taking
        if (fam.getParent().getExcommunications().get(1).has("valMalus")) {
            if (fam.getParent().getExcommunications().get(1).get("type").getAsString().equals(parentTower.getType())) {
                int valMalus = fam.getParent().getExcommunications().get(1).get("valMalus").getAsInt();
                fam.getParent().sOut("Your excommunication lowers the value of this action by " + valMalus);
                value -= valMalus;
            }
        }
        if (value < this.floorValue) {
            fam.getParent().sOut("Insufficient value");
            return false;
        }
        if (fam.getParent().getCurrentRes().merge(cardCost.inverse()).isNegative()) {
            fam.getParent().sOut("Insufficient resources");
            return false;
        }
        LeaderCard Borgia = null;
        for (LeaderCard leader : fam.getParent().getLeaderCards()) {
            if("Ludovico Ariosto".equals(leader.getName())
                    && leader.isActivated()) {
                Borgia = leader;
                break;
            }
        }
        if (!"Dummy".equals(fam.getSkinColour())) {
            callerFl = this;
        }
        if(tmpRes.militaryPoint >= floorCard.minMilitaryPoint
                && !tmpRes.merge(cardCost).isNegative() || Borgia != null) {
            callerFl.addAction(new TakeFamilyMember(fam));
            callerFl.addAction(new PlaceFamilyMemberInFloor(fam, this));
            if (!boycottBonus) {
                this.addAction(new ResourcesAction("Floor entry bonus", this.bonus, p));
                log.info("Floor entry bonus: " + this.bonus);
            }
            callerFl.addAction(new NestedAction(this.floorCard));
            callerFl.floorCard.costActionBuilder(p);
            callerFl.addAction(new NestedAction(
                new CardImmediateAction(this.floorCard, p)));
            callerFl.addAction(new CardFromFloorAction(this.floorCard, this, p));
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

    public Tower getParentTower() {
        return parentTower;
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
