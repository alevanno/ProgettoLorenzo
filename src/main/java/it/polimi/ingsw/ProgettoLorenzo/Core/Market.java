package it.polimi.ingsw.ProgettoLorenzo.Core;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Market extends Action {

    List<MarketBooth> booths = new ArrayList<>();



    public Market() throws FileNotFoundException {
        ClassLoader classLoader = Council.class.getClassLoader();
        String resources = classLoader.getResource("market.json").getFile();
        JsonObject data = new JsonParser().parse(new FileReader(resources))
               .getAsJsonObject();
        booths.add(new MarketBooth(data.get("firstBooth").getAsJsonObject()));
        booths.add(new MarketBooth(data.get("secondBooth").getAsJsonObject()));
        booths.add(new MarketBooth(data.get("thirdBooth").getAsJsonObject()));
        booths.add(new MarketBooth(data.get("fourthBooth").getAsJsonObject()));
    }

    public List<MarketBooth> getBooths() {
        return booths;
    }

    public void putFamMember(FamilyMember fam) throws FileNotFoundException{
        System.out.println("Where do you want to put your Family Member?: ");
        for (int i = 1; i-1 < 4; i++) {
            if (i == 4) {
                System.out.println(i + " Booth: " + '[' + booths.get(i-1).getCouncilPrivilege()+ " councilPrivileges" + ']');
            } else {
                System.out.println(i + " Booth: " + booths.get(i - 1).getBonus());
            }

        }
        Scanner in = new Scanner(System.in);
        System.out.println(booths.get(in.nextInt() - 1).claimSpace(fam));

    }
}
