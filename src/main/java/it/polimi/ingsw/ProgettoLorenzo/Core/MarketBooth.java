package it.polimi.ingsw.ProgettoLorenzo.Core;


import com.google.gson.JsonObject;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class MarketBooth extends Action {
    private List<Resources> bonus = new ArrayList<>();
    private int councilPrivilege;
    private FamilyMember famMember;

    public MarketBooth(JsonObject src) {
        String privilege = "privileges";
        if (src.get(privilege) != null) {
            this.councilPrivilege = 2;

        } else {
            this.councilPrivilege = 0;
            this.bonus.add(Resources.fromJson(src));
        }
    }

    public List<Resources> getBonus() {
        return this.bonus;
    }

    public int getCouncilPrivilege() {
        return this.councilPrivilege;
    }

    public FamilyMember getFamMember() {
        return this.famMember;
    }

    protected void placeFamilyMember(FamilyMember f) {
        this.famMember = f;
    }

    //actionBuilder for MarketBooth class
    public void claimSpace(FamilyMember fam) {
        Player p = fam.getParent();
        this.addAction(new TakeFamilyMember(fam));
        this.addAction(new PlaceFamilyMemberInBooth(fam, this));
        this.bonus.addAll(new Council().chooseMultiPrivilege(this.councilPrivilege));
        for (Resources res : this.bonus) {
            this.addAction(new ResourcesAction(
                    "MarketBooth action", res, fam.getParent()));
        }
    }
}