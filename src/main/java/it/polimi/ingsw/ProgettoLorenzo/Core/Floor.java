package it.polimi.ingsw.ProgettoLorenzo.Core;


public class Floor extends Action {
    private Resources bonus;
    private FamilyMember famMember;
    private Card floorCard;
    private int floorNumber;
    private Tower parentTower;


    public Floor(Resources bonus, Card card, Tower tower, int floorNumber) {
        this.bonus = bonus;
        this.floorCard = card;
        this.parentTower = tower;
        this.floorNumber = floorNumber;
    }

    //player puts there its famMemb & take Card and the eventually bonus
    //FIXME it handles only Card's ResourcesAction
    public void claimFloor(FamilyMember fam) {
        this.famMember = fam;
        System.out.println(this.famMember.getSkinColor() + " family member of "
                + this.famMember.getParent().playerColour
                + " player placed in " + this.floorNumber + " floor of "
                + this.parentTower.getTowerNumber() + " tower");
        this.addAction(new ResourcesAction("floor bonus", this.bonus));
        //FIXME temporary
        this.floorCard.actionBuilder();
        this.famMember.getParent().addCard(this.removeCard(this.floorNumber));
    }

    public Card removeCard(int indx) {
        Card retCard = this.floorCard;
        this.floorCard = null;
        return retCard;
    }
}

