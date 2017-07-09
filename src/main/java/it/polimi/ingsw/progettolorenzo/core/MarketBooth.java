package it.polimi.ingsw.progettolorenzo.core;


import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The class representing a single market place.
 */
public class MarketBooth extends Action {
    private List<Resources> bonus = new ArrayList<>();
    private int councilPrivilege;
    private FamilyMember famMember;

    /**
     * It loads info from file.
     * @param src the JsonObject containing the info to fills class fields
     */
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

    /**
     * The macro action builder. It checks for excommunications and for the minimum required value too.
     * Player can claim the space if getFamMember == null or if he has Ariosto.
     * With Ariosto a player can claim the space even if he did so himself
     * previously, granted that one of the famMem is the Blank one
     * @param fam the family member claiming the space
     * @return the boolean value representing the success (or not) of the claim
     */
    public boolean claimSpace(FamilyMember fam) {
        Player p = fam.getParent();
        if (p.getExcommunications().get(1).has("blockedMarket")) {
            p.sOut("Your excommunication prevents you to put your famMembers in the Market");
            return false;
        }
        if (fam.getActionValue() < 1) {
            p.sOut("You need an action value of at least 1");
            return false;
        }

        if(this.getFamMember() == null || p.leaderIsActive("Ludovico Ariosto") &&
                (!p.equals(this.getFamMember().getParent()) || p.equals(this.getFamMember().getParent()) &&
                        ("Blank".equals(fam.getSkinColour()) || "Blank".equals(this.getFamMember().getSkinColour())))) {
            this.addAction(new TakeFamilyMember(fam));
            if (this.getFamMember() == null) {
                this.addAction(new PlaceFamilyMemberInBooth(fam, this));
            }
            this.bonus.addAll(new Council().chooseMultiPrivilege(this.councilPrivilege, p));
            for (Resources res : this.bonus) {
                this.addAction(new ResourcesAction(
                        "MarketBooth action", res, p));
            }
            return true;
        }
        p.sOut("This market place is already occupied!");
        return false;
    }

    /**
     * Serialize single booth information
     * @return the JsonObject containing booth information
     */
    public JsonObject serialize() {
        Map<String, Object> ret = new HashMap<>();
        if ( this.famMember != null) {
            ret.put("famMember", this.famMember.serialize());
        }
        return new Gson().fromJson(new Gson().toJson(ret), JsonObject.class);
    }
}