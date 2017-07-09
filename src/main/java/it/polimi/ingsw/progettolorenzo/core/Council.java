package it.polimi.ingsw.progettolorenzo.core;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.*;

/**
 * The class representing the Council Palace.
 * The player order changes by placing here a family member.
 */
public class Council extends Action {
    private final List<Resources> privilegeChoices;
    public final Resources bonusEntry;
    private int firstAvailSpace = 0;
    List<FamilyMember> councilSpace = new ArrayList<>();

    /**
     * The list of privileges is filled from file.
     */
    public Council() {
        JsonObject data = Utils.getJsonObject("council.json");
        this.bonusEntry = Resources.fromJson(data.get("bonusEntry")
               .getAsJsonObject());

        Iterator it = data.get("privileges").getAsJsonArray().iterator();
        this.privilegeChoices = new ArrayList<>();
        while (it.hasNext()) {
            this.privilegeChoices.add(
                    Resources.fromJson((JsonObject)it.next())
            );
        }
    }

    /**
     * It handles the choice between multiple privileges.
     * It does not allow the player to select the same privilege as before
     *
     * @see #choosePrivilege(Player)
     * @param privileges the number of privileges
     * @param pl the player claiming the privilege
     * @return the set of Resources representing the choices
     */
    public Set<Resources> chooseMultiPrivilege(int privileges, Player pl) {
        Set<Resources> privilegeSet = new HashSet<>();

        while(true) {
            if (privilegeSet.size() == privileges) {
                break;
            }
            Resources res = this.choosePrivilege(pl);
            if (privilegeSet.contains(res)) {
                pl.sOut("Invalid choice! Please select a different privilege: ");
                return chooseMultiPrivilege(privileges, pl);
            }
            privilegeSet.add(res);
        }
        return privilegeSet;
    }

    /**
     * It propose the choice between different combinations of Resources.
     * @param pl the player claiming the privilege
     * @return the Resources object representing the choice
     */
    public Resources choosePrivilege(Player pl) {
        int i = 1;
        int res;
        pl.sOut("You can chose a privilege between: ");
        for (; i-1 < this.privilegeChoices.size(); i++) {
            pl.sOut(String.format("  %d: %s",
                    i, this.privilegeChoices.get(i - 1)));
        }
        res = pl.sInPrompt(1, i-1);
        return this.privilegeChoices.get(res - 1);
    }

    /**
     * This is the macro Action builder. It add to the actions list
     * all the {@link BaseAction} to be in order applied.
     * It checks also the action value of the family member.
     * @param fam
     * @return
     */
    //actionBuilder for Council class
    public boolean claimSpace(FamilyMember fam) {
        Player p = fam.getParent();
        if (fam.getActionValue() < 1) {
            p.sOut("You need an action value of at least 1");
            return false;
        }
        this.addAction(new TakeFamilyMember(fam));
        this.addAction(new PlaceFamilyMemberInCouncil(fam, this));
        this.addAction(new ResourcesAction(
                "Bonus entry from Council", this.bonusEntry, p));
        this.addAction(new ResourcesAction(
                "Council privilege", this.choosePrivilege(p), p));
        return true;
    }

    /**
     * It place the family member in council and increments the firstAvailSpace
     * field that is handle by {@link it.polimi.ingsw.progettolorenzo.Game}
     * @param fam
     */
    protected void placeFamilyMember(FamilyMember fam) {
        councilSpace.add(fam);
        if (fam.getParent().getParentGame().getFirstAvailPlace(fam.getParent(), firstAvailSpace)) {
            firstAvailSpace++;
        }
    }

    public JsonArray serialize() {
        List<JsonObject> ret = new ArrayList<>();
        this.councilSpace.forEach(f -> ret.add(f.serialize()));
        return new Gson().fromJson(new Gson().toJson(ret), JsonArray.class);
    }
}
