package it.polimi.ingsw.progettolorenzo.core;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;
import java.util.logging.Logger;

public class Floor extends Action {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private final Resources bonus;
    private final Tower parentTower;
    private FamilyMember famMember;
    private Card floorCard;
    private int floorValue;
    private Floor callerFl; //this is needed for floorActionWithCard


    public Floor(Resources bonus, Card card, Tower tower, int floorValue) {
        this.bonus = bonus;
        this.floorCard = card;
        this.parentTower = tower;
        this.floorValue = floorValue;
        this.callerFl = this;
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

    //TODO testing
    private boolean checkEnoughValue(FamilyMember fam) { //checks that the action value is sufficient
        int value = fam.getActionValue();
        Player p = fam.getParent();
        for (Card c : p.listCards()) { //searches for permanent value bonus (cards)
            JsonElement permTowerBonus = c.permanentEff.get("towerBonus");
            if(permTowerBonus != null) {
                if (permTowerBonus.getAsJsonObject().get("type").getAsString().equals(floorCard.cardType)) {
                    int valBonus = permTowerBonus.getAsJsonObject().get("plusValue").getAsInt();
                    p.sOut("Card " + c.cardName + " increases the value of this action by " + valBonus);
                    value += valBonus;
                }
            }
        }
        if (p.getExcommunications().get(1).has("valMalus")) { //searches for permanent value malus (excomms)
            if (p.getExcommunications().get(1).get("type").getAsString().equals(parentTower.getType())) {
                int valMalus = p.getExcommunications().get(1).get("valMalus").getAsInt();
                p.sOut("Your excommunication lowers the value of this action by " + valMalus);
                value -= valMalus;
            }
        }
        if (value < this.floorValue) {
            p.sOut("Insufficient value");
            return false;
        }
        return true;
    }

    //TODO testing
    private boolean checkEnoughRes(Player p) {
        Resources cardCost = this.floorCard.getCardCost(p);
        boolean boycottBonus = false;
        for (Card c : p.listCards()) { //searches for the boycottBonus permanent effect
            boycottBonus = c.permanentEff.containsKey("boycottInstantTowerBonus");
        }
        //resources requirement for taking the card
        Resources checkEnoughRes = p.getCurrentRes().merge(cardCost.inverse());
        if (!boycottBonus) {
            checkEnoughRes = checkEnoughRes.merge(bonus);
        }
        if (checkEnoughRes.isNegative()) {
            p.sOut("Insufficient resources");
            return false;
        }
        //militaryPoint requirement for ventures cards
        if (!(p.getCurrentRes().militaryPoint >= floorCard.minMilitaryPoint)) {
            p.sOut("Insufficient militaryPoint");
            return false;
        }
        //militaryPoint requirement for territories cards
        if ("territories".equals(floorCard.cardType)) {
            int countTerritories = 0;
            for (Card i : p.listCards()) {
                if (i.cardType.equals("territories")) {
                    countTerritories++;
                }
            }
            List<Integer> territoriesMilitaryReq = Arrays.asList(0, 0, 3, 7, 12, 18);
            if (!(p.getCurrentRes().militaryPoint >= territoriesMilitaryReq.get(countTerritories))
                    && !p.leaderIsActive("Cesare Borgia")) {
                p.sOut("Insufficient militaryPoint");
                return false;
            }
        }
        return true;
    }

    private boolean checkNotExceedingCard(Player p) {
        int count = 0;
        for (Card c : p.listCards()) {
            if (this.getCard().cardType.equals(c.cardType)) {
                count++;
            }
        }
        if (count < 6) {
            return true;
        } else {
            p.sOut("You cannot have more than 6 cards per type");
            return false;
        }
    }

    public boolean claimFloorWithCard(FamilyMember fam, Floor callerFloor) {
        callerFl = callerFloor;
        boolean ret = claimFloor(fam);
        if (!ret) { callerFl = this; }
        return ret;
    }

    // player puts here its famMemb & takes the Card and the eventual bonus;
    public boolean claimFloor(FamilyMember fam) {
        Player p = fam.getParent();
        //if the action value, the resources or the militaryPoints are not sufficient the action fails
        if (!checkEnoughValue(fam) || !checkEnoughRes(p) || !checkNotExceedingCard(p)) {
            return false;
        }
        boolean boycottBonus = false;
        for (Card c : p.listCards()) { //searches for the boycottBonus permanent effect
            boycottBonus = c.permanentEff.containsKey("boycottInstantTowerBonus");
        }
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
