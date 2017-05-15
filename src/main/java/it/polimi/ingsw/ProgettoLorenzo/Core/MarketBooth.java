package it.polimi.ingsw.ProgettoLorenzo.Core;


import com.google.gson.JsonObject;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class MarketBooth {
    private List<Resources> bonus = new ArrayList<>();
    private int councilPrivilege;
    private FamilyMember famMember;

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

    public FamilyMember getFamMember() {
        return this.famMember;
    }

    //this is the generic claimSpace that returns the resources bonuses to claim
    public List<Resources> claimSpace(FamilyMember fam) throws FileNotFoundException {
        this.famMember = fam;
        if (councilPrivilege == 2) {
            bonus.addAll(new Council().chooseMultiPrivilege(councilPrivilege));
        }
        return bonus;
    }
}
