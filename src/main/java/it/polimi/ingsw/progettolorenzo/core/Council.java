package it.polimi.ingsw.progettolorenzo.core;

import com.google.gson.JsonObject;
import java.util.*;
import static it.polimi.ingsw.progettolorenzo.core.Utils.intPrompt;

public class Council extends Action {
    private List<Player> playerOrder = new ArrayList<>();
    private final List<Resources> privilegeChoices;
    public final Resources bonusEntry;

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

    public Set<Resources> chooseMultiPrivilege(int privileges, Player pl) {
        Set<Resources> privilegeSet = new HashSet<>();

        while(true) {
            if (privilegeSet.size() == privileges) {
                break;
            }
            Resources res = this.choosePrivilege(pl);
            //Doesn't allow the player to select the same privilege as before
            if (privilegeSet.contains(res)) {
                pl.sOut("Invalid choice! Please select a different privilege: ");
                return chooseMultiPrivilege(privileges, pl);
            }
            privilegeSet.add(res);
        }
        return privilegeSet;
    }


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

    //actionBuilder for Council class
    public void claimSpace(FamilyMember fam) {
        Player p = fam.getParent();
        this.addAction(new TakeFamilyMember(fam));
        this.addAction(new PlaceFamilyMemberInCouncil(fam, this));
        this.addAction(new ResourcesAction(
                "bonus entry from Council", this.bonusEntry, p));
        // FIXME make the number of privilege selectable?
        this.addAction(new ResourcesAction(
                "Council privilege", this.choosePrivilege(p), p));
    }

    protected void placeFamilyMember(FamilyMember f) {
        this.playerOrder.add(f.getParent());
    }
}
