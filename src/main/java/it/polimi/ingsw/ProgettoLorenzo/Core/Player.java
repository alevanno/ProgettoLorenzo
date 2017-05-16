package it.polimi.ingsw.ProgettoLorenzo.Core;

import java.util.ArrayList;
import java.util.List;

public class Player {
    public final String playerName;
    public final String playerColour;
    public Resources currentRes;
    private List<FamilyMember> famMemberList = new ArrayList<>();
    private Deck cards = new Deck();
    public BonusTile bonusT;

    public Player(String name, String colour) {
        this.playerName = name;
        this.playerColour = colour;
        this.currentRes = new Resources.ResBuilder().build();  // 0 resources
    }

    public void famMembersBirth() {
        for (int i=0; i<4; i++) {
            this.famMemberList.add(
                    new FamilyMember(this, 1, "Orange")
            );
        }
    }

    public void setBonusTile(BonusTile bt) {this.bonusT = bt;}

    protected void addCard(Card toadd) {
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
    //public void finalCount() {}
}
