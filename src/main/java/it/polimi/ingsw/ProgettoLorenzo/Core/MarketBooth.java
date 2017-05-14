package it.polimi.ingsw.ProgettoLorenzo.Core;


import com.google.gson.JsonObject;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MarketBooth {
    private List<Resources> bonus = new ArrayList<>();
    private int councilPrivilege;
    private FamilyMember familyMember;

    public MarketBooth(JsonObject src) throws FileNotFoundException {
        String privilege = "privileges";
        if (src.get(privilege) != null) {
            councilPrivilege = 2;

        } else {
            councilPrivilege = 0;
            bonus.add(Resources.fromJson(src));
        }
    }

    public List<Resources> getBonus() {
        return bonus;
    }

    public int getCouncilPrivilege() {
        return councilPrivilege;
    }

    public FamilyMember getFamilyMember() {
        return this.familyMember;
    }

    //this is the generic claimSpace that returns the resources bonuses to claim
    public List<Resources> claimSpace(FamilyMember fam) throws FileNotFoundException {
        this.familyMember = fam;
        if (councilPrivilege == 2) {
            bonus.addAll(new Council().chooseMultiPrivilege(councilPrivilege));
        }
        return bonus;
    }
}
