package it.polimi.ingsw.ProgettoLorenzo.Core;

import java.util.ArrayList;
import java.util.List;

public class Player {
    public final String playerName;
    public final String playerColour;
    public Resources currentRes;
    private List<FamilyMember> familyMemberList = new ArrayList<>();

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

    public List<FamilyMember> getAvaliableFamilyMembers() {
        return this.familyMemberList;
    }
    //public void finalCount() {}
}
