package it.polimi.ingsw.progettolorenzo.core;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.progettolorenzo.Game;

import java.util.*;
import java.util.logging.Logger;

/**
 * This is the class representing the floor of a tower.
 * It handles by itself the access to the floor (i.e, checks
 * of value and resources.
 * It inherits all the characteristics of Action class.
 *
 * @see Action
 * @see Tower
 */
public class Floor extends Action {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private final Resources bonus;
    private final Tower parentTower;
    private FamilyMember famMember;
    private Card floorCard;
    private int floorValue;
    private Floor callerFl; //this is needed for floorActionWithCard

    /**
     * The constructor that generates a new Floor object
     * @param bonus the resources that a player could obtain accessing to the floor.
     * @param card the development card that could be taken from the floor.
     * @param tower the parent tower of the floor.
     * @param floorValue the minValue to access the floor.
     */
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

    /**
     * It handles the access to the floor.
     * If the player is owning the Filippo Brunelleschi leader Card and the tower isn't already occupied by the player
     * itself, he can skip the payment of the additional coins.
     * @see FilippoBrunelleschi#permanentAbility()
     * @param pl the player wanting to access the floor
     * @param towerOcc the boolean value representing the parent tower's occupancy (from pl itself or other player)
     * @return the boolean value representing the access possibility. The floor can be occupied, free with
     * coin to pay or inaccessible due to tower occupancy rules.
     */
    public boolean accessFloor(Player pl, int towerOcc){ //returns true if the floor is accessed and takes the coins from the player if necessary
        for(LeaderCard leader : pl.getLeaderCards()){
            if("Filippo Brunelleschi".equals(leader.getName()) && leader.isActivated() && towerOcc != 2) {
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

    /**
     * It checks the satisfaction of the minValue to access the floor.
     * It could be increased or decreased due to card's permanent effects and excommunication card.
     * @see Card
     * @param fam the family member which want to attempt the floor access.
     * @return the boolean value of the value satisfaction.
     */
    protected boolean checkEnoughValue(FamilyMember fam) {
        // check that the action value is sufficient
        int value = fam.getActionValue();
        Player p = fam.getParent();

        // search for permanent value bonus (cards)
        for (Card c : p.listCards()) {
            JsonElement permTowerBonus = c.permanentEff.get("towerBonus");
            if (permTowerBonus != null &&
                permTowerBonus.getAsJsonObject().get("type").getAsString().equals(floorCard.cardType)) {
                    int valBonus = permTowerBonus.getAsJsonObject().get("plusValue").getAsInt();
                    p.sOut("Card " + c.cardName + " increases the value of this action by " + valBonus);
                    value += valBonus;
            }
        }
        // search for permanent value malus (excommunication)
        if (p.getExcommunications().get(1).has("valMalus") &&
            p.getExcommunications().get(1).get("type").getAsString().equals(parentTower.getType())) {
                int valMalus = p.getExcommunications().get(1).get("valMalus").getAsInt();
                p.sOut("Your excommunication lowers the value of this action by " + valMalus);
                value -= valMalus;
        }
        if (value < this.floorValue) {
            p.sOut("Insufficient value");
            return false;
        }
        return true;
    }

    /**
     * It checks that player's current res are > than the card's cost.
     * The boycottBonus represent one of the development card permanent effects.
     * If it is != null, the eventual floor bonus can't be used to satisfy the payment of the floor.
     * @param p the player who's accessing the floor.
     * @return the boolean value of the resources satisfaction.
     * It return false if the player has insufficient resources or it has insufficient minimum required
     * military point (for Terrain and ventures card).
     * If the player owns Cesare Borgia leader and it is activated, he can avoid the check for terrain cards.
     */
    protected boolean checkEnoughRes(Player p) {
        Resources cardCost = this.floorCard.getCardCost(p);
        boolean boycottBonus = false;
        for (Card c : p.listCards()) { //searches for the boycottBonus permanent effect
            boycottBonus = c.permanentEff.containsKey("boycottInstantTowerBonus");
        }
        //resources requirement for taking the card
        Resources checkEnoughRes = p.getCurrentRes().merge(cardCost.inverse());
        if (!this.equals(callerFl)) {
            checkEnoughRes = checkEnoughRes.merge(callerFl.floorCard.getCardCost(p).inverse());
        }
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
                if ("territories".equals(i.cardType)) {
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

    /**
     * it checks if the player has more than 6 cards of the same type of the floor card.
     * @param p the player's attempting to access the floor
     * @return the boolean value representing the check.
     */
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

    /**
     * By the calling of this method, the player take the card with another card.
     * It is invoked by an ImmediateAction.
     * @see CardImmediateAction
     * @see #claimFloor(FamilyMember)
     * @param fam the family member claiming the floor.
     * @param callerFloor the floor containing the card that invoked this action
     * @return the boolean value representing the success (or not) of the claim.
     */
    public boolean claimFloorWithCard(FamilyMember fam, Floor callerFloor) {
        callerFl = callerFloor;
        boolean ret = claimFloor(fam);
        if (!ret) {
            callerFl = this;
        }
        return ret;
    }

    /**
     * This represent the main floor claim.
     * First it checks if the action is possible by a boolean condition of all the checks defined in this class.
     * If it pass them , it add to the callerFloor the series of all the base actions needed to claim a Floor.
     * @see BaseAction
     * @param fam the family member claiming the floor
     * @return the boolean value representing the success (or not) of the floor claiming.
     */
    // player puts here its famMemb & takes the Card and the eventual bonus
    public boolean claimFloor(FamilyMember fam) {
        Player p = fam.getParent();
        // fail the action if the action value, the resources or the
        // militaryPoints are not sufficient
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
        this.floorCard.costActionBuilder(p);
        callerFl.addAction(new NestedAction(this.floorCard));
        callerFl.addAction(new NestedAction(
            new CardImmediateAction(this.floorCard, p)));
        callerFl.addAction(new CardFromFloorAction(this.floorCard, this, p));
        return true;
    }

    protected void placeFamilyMember(FamilyMember f) {
        this.famMember = f;
    }

    /**
     * Simple method to get the occupant family member
     * @return the {@link FamilyMember} occupying the Floor
     */
    public FamilyMember getFamMember() {
        return this.famMember;
    }

    /**
     * It checks the occupancy of the floor
     * @return the boolean value representing the presence of a family member
     */
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

    /**
     * It serialize floor information to send to Client.
     * @see Game#displayGame()
     * @return The JsonObject containing all the needed floor information.
     */
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
