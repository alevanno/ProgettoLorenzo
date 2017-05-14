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
    Set<Resources> set = new HashSet<>();

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

    public Set<Resources> chooseMultiPrivilege(int prvs) {

        while(true) {
            if (this.set.size() == prvs) {
                break;
            }
            Resources res = this.choosePrivilege();
            if (this.set.contains(res)) {
                System.out.print("Invalid choise! Please select a right privilege: ");
                return chooseMultiPrivilege(prvs);
            }
            this.set.add(res);
        }
        return this.set;
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
}
