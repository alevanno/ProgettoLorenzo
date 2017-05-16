package it.polimi.ingsw.ProgettoLorenzo.Core;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class Council extends Action {
    private List<FamilyMember> playerOrder = new ArrayList<FamilyMember>();
    private final List<Resources> privilegeChoices;
    public final Resources bonusEntry;
    private FamilyMember famMember;
    Set<Resources> privilegeSet = new HashSet<>();

    public Council () throws FileNotFoundException {
        ClassLoader classLoader = Council.class.getClassLoader();
        String filename = classLoader.getResource("council.json").getFile();
        JsonObject data = new JsonParser().parse(new FileReader(filename))
                .getAsJsonObject();
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

    public Resources choosePrivilege() {  // FIXME maybe make static somehow?
        System.out.println("You can chose a privilege between: ");
        for (int i = 1; i-1 < this.privilegeChoices.size(); i++) {
            System.out.printf("%d: %s%n", i, this.privilegeChoices.get(i-1));
        }
        Scanner in = new Scanner(System.in);
        int res = in.nextInt();
        return this.privilegeChoices.get(res-1);
    }

    //actionBuilder for Council class
    public void claimSpace(FamilyMember fam) {
        this.famMember = fam;
        System.out.println(this.famMember.getSkinColor() + " family member of " + this.famMember.getParent().playerColour
        + " player placed in Council Palace");
        this.addAction(new ResourcesAction("bonus entry from Council", this.bonusEntry));
        Set<Resources> resSet = chooseMultiPrivilege(1);
        for(Resources res : resSet) {
            this.addAction(new ResourcesAction("resources from Council", res));
        }
    }
}
