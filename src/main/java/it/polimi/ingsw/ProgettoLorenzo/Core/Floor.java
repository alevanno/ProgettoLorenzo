package it.polimi.ingsw.ProgettoLorenzo.Core;

import java.util.logging.Logger;

public class Floor extends Action {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private final Resources bonus;
    private final Tower parentTower;
    private FamilyMember famMember;
    private Card floorCard;


    public Floor(Resources bonus, Card card, Tower tower) {
        this.bonus = bonus;
        this.floorCard = card;
        this.parentTower = tower;
        log.fine(String.format(
            "Floor instantiated <bonus: %s, card: %s, tower: %s>",
            bonus, card, tower));
    }

    // player puts here its famMemb & take the Card and the eventual bonus;
    //TODO Game should handle the return value;
    public boolean claimFloor(FamilyMember fam) {
        Player p = fam.getParent();
        Resources tmpRes = p.currentRes;
        Resources cardCost = this.floorCard.getCardCost();
        if(tmpRes.militaryPoint > floorCard.minMilitaryPoint) {
            if (tmpRes.merge(cardCost).isNegative()) {
                this.addAction(new TakeFamilyMember(fam));
                this.addAction(new PlaceFamilyMemberInFloor(fam, this));
                this.addAction(new ResourcesAction("floor bonus", this.bonus, p));
                this.addAction(new NestedAction(this.floorCard));
                this.floorCard.costActionBuilder(p);
                new CardImmediateAction(this.floorCard, p);
                this.addAction(new CardFromFloorAction(this.floorCard, this, p));
                return true;
            }
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
}
