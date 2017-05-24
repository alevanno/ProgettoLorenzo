package it.polimi.ingsw.progettolorenzo.core;

import com.google.gson.JsonObject;
import java.util.*;
import static it.polimi.ingsw.progettolorenzo.core.Utils.intPrompt;

public class Council extends Action {
    private List<FamilyMember> playerOrder = new ArrayList<>();
    private final List<Resources> privilegeChoices;
    public final Resources bonusEntry;
    Set<Resources> privilegeSet = new HashSet<>();

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

    public Set<Resources> chooseMultiPrivilege(int privileges) {
        while(true) {
            if (this.privilegeSet.size() == privileges) {
                break;
            }
            Resources res = this.choosePrivilege();
            //Doesn't allow the player to select the same privilege as before
            if (this.privilegeSet.contains(res)) {
                System.out.print("Invalid choice! Please select a different privilege: ");
                return chooseMultiPrivilege(privileges);
            }
            this.privilegeSet.add(res);
        }
        return this.privilegeSet;
    }

    public Resources choosePrivilege() {
        int i = 1;
        int res;
        System.out.println("You can chose a privilege between: ");
        for (; i-1 < this.privilegeChoices.size(); i++) {
            System.out.printf("%d: %s%n", i, this.privilegeChoices.get(i-1));
        }
        res = intPrompt(1, i-1);
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
                "Council privilege", this.choosePrivilege(), p));
    }

    protected void placeFamilyMember(FamilyMember f) {
        this.playerOrder.add(f);
    }
}
