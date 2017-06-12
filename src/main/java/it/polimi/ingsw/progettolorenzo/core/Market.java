package it.polimi.ingsw.progettolorenzo.core;


import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Market {

    List<MarketBooth> booths = new ArrayList<>();
    private int numOfBooths;

    public int getNumOfBooths() {
        return numOfBooths;
    }

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

    // TODO it needs improvements and it has to show the occupantPlayer
    public void displayBooths(Player pl) {
        pl.sOut("Where do you want to put your Family Member?: ");
        if (pl.getParentGame().getNumOfPlayers() > 3) {
            numOfBooths = 4;
        } else { numOfBooths = 2; }
        for (int i = 1; i-1 < numOfBooths; i++) {
            if (booths.get(i - 1).getFamMember() != null) {
                pl.sOut(i + " Booth: occupied by " + booths.get(i - 1).getFamMember().getParent().playerName);
            } else {
                if (i == 4) {
                    pl.sOut(i + " Booth: " + '[' + booths.get(i - 1).getCouncilPrivilege() + " councilPrivileges" + ']');
                } else {
                    pl.sOut(i + " Booth: " + booths.get(i - 1).getBonus());
                }
            }
        }
    }
}
