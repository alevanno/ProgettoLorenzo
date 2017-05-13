package it.polimi.ingsw.ProgettoLorenzo.Core;


import com.google.gson.JsonObject;

public class MarketBooth {
    private Resources bonus;
    private FamilyMember familyMember;

    public MarketBooth(JsonObject src) {
        this.bonus = Resources.fromJson(src);
    }

    public Resources claimSpace(FamilyMember fam) {
        this.familyMember = fam;
        return bonus;
    }
}
