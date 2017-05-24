package it.polimi.ingsw.ProgettoLorenzo.Core;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Player {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    public final String playerName;
    public final String playerColour;
    public Resources currentRes;  // FIXME make private
    private List<FamilyMember> famMemberList = new ArrayList<>();
    private Deck cards = new Deck();
    public BonusTile bonusT;

    public Player(String name, String colour) {
        this.playerName = name;
        this.playerColour = colour;
        this.currentRes = new Resources.ResBuilder().build();  // 0 resources
        log.info(String.format(
                "New player: %s (colour: %s, resources: %s)",
                name, colour, this.currentRes));
    }

    public void famMembersBirth() {
        // FIXME - sanely set actionValue
        this.famMemberList.add(
                new FamilyMember(this, 10, "Orange"));
        this.famMemberList.add(
                new FamilyMember(this, 10, "Black"));
        this.famMemberList.add(
                new FamilyMember(this, 10, "White"));
        this.famMemberList.add(
                new FamilyMember(this, 10, "Blank"));
        log.fine("4 family members attached to " + this);
    }

    public void setBonusTile(BonusTile bt) {
        this.bonusT = bt;
    }

    public void addCard(Card toadd) {
        this.cards.add(toadd);
    }

    public Deck listCards() {
        return this.cards.listCards();
    }

    protected Card takeCard(int idx) {
        return this.cards.remove(idx);
    }

    public List<FamilyMember> getAvailableFamMembers() {
        return this.famMemberList;
    }

    protected void takeFamilyMember(FamilyMember famMember) {
        if (!this.famMemberList.remove(famMember)) {
            System.exit(1);
        }
    }
    //public void finalCount() {}
}
