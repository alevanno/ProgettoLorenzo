package it.polimi.ingsw.ProgettoLorenzo.Core;

import java.util.ArrayList;
import java.util.List;

public class Player {
    public final String playerName;
    public final String playerColour;
    public Resources currentRes;
    private List<FamilyMember> familyMemberList = new ArrayList<>();
    private Deck cards = new Deck();

    public Player(String name, String colour) {
        this.playerName = name;
        this.playerColour = colour;
        this.currentRes = new Resources.ResBuilder().build();  // 0 resources
    }

    protected void familyMembersBirth() {
        for (int i=0; i<4; i++) {
            this.familyMemberList.add(
                    new FamilyMember(this, 1, "orange")
            );
        }
    }

    protected void addCard(Card toadd) {
        this.cards.add(toadd);
    }

    public Deck listCards() {
        return this.cards.listCards();
    }

    protected Card takeCard(int idx) {
        return this.cards.remove(idx);
    }

    public List<FamilyMember> getAvaliableFamilyMembers() {
        return this.familyMemberList;
    }
    //public void finalCount() {}
}
