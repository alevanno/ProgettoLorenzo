package it.polimi.ingsw.progettolorenzo.core;


import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Market {

    List<MarketBooth> booths = new ArrayList<>();



    public Market()  {
        JsonObject data = Utils.getJsonObject("market.json");
        booths.add(new MarketBooth(data.get("firstBooth").getAsJsonObject()));
        booths.add(new MarketBooth(data.get("secondBooth").getAsJsonObject()));
        booths.add(new MarketBooth(data.get("thirdBooth").getAsJsonObject()));
        booths.add(new MarketBooth(data.get("fourthBooth").getAsJsonObject()));
    }

    public List<MarketBooth> getBooths() {
        return booths;
    }

    public void putFamMember(FamilyMember fam) {
        System.out.println("Where do you want to put your Family Member?: ");
        for (int i = 1; i-1 < 4; i++) {
            if (i == 4) {
                System.out.println(i + " Booth: " + '[' + booths.get(i-1).getCouncilPrivilege()+ " councilPrivileges" + ']');
            } else {
                System.out.println(i + " Booth: " + booths.get(i - 1).getBonus());
            }

        }
        Scanner in = new Scanner(System.in);
        booths.get(in.nextInt() - 1).claimSpace(fam);

    }
}
