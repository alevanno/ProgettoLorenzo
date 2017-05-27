package it.polimi.ingsw.progettolorenzo.core;


import it.polimi.ingsw.progettolorenzo.Game;

import java.util.logging.Logger;

public class Floor extends Action {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private final Resources bonus;
    private final Tower parentTower;
    private FamilyMember famMember;
    private Card floorCard;
    private int floorValue;
    private Game game;


    public Floor(Resources bonus, Card card, Tower tower, int floorValue) {
        this.bonus = bonus;
        this.floorCard = card;
        this.parentTower = tower;
        this.floorValue = floorValue;
        log.fine(String.format(
                "Floor instantiated <bonus: %s, card: %s, tower: %s>",
                bonus, card, tower));
    }

    //TODO this should be called at an higher level
    public boolean claimFloorWithCard(Player currPlayer, int value, Resources discount) {
        FamilyMember dummy = new FamilyMember(currPlayer, value, null);

        //TODO handling of the discount (could be temporarily added here, but should be removed later in some way if not used)
        //TODO we could also intervene on the cardcost, but claimFloor should be modified
        return true;
    }

    //TODO claimFloorWithCard should pass a dummy familymember to the main claimFloor function


    // player puts here its famMemb & take the Card and the eventual bonus;
    //TODO Game should handle the return value;
    public boolean claimFloor(FamilyMember fam) {
        int value = fam.getActionValue();
        Player p = fam.getParent();
        Resources tmpRes = p.currentRes;
        Resources cardCost = this.floorCard.getCardCost(); //TODO if a discount is present...
        //TODO value should be affected also by an excommunication
        for(Card c : p.listCards()) {
            if(c.permanentEff.get("towerBonus").getAsJsonObject()
                    .get("type").getAsString().equals(floorCard.cardType)) {
                value += c.permanentEff.get("towerBonus").getAsJsonObject().get("plusValue").getAsInt();
            }
        }//TODO the floor bonus can be used to pay for the card you're taking
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


}
