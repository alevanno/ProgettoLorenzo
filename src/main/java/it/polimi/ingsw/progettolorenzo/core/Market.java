package it.polimi.ingsw.progettolorenzo.core;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is basically the container of the {@link MarketBooth}s.
 */
public class Market {
    List<MarketBooth> booths = new ArrayList<>();
    private int numOfBooths;

    /**
     * The info of the booth type are loaded  from file.
     */
    public Market()  {
        JsonObject data = Utils.getJsonObject("market.json");
        booths.add(new MarketBooth(data.get("firstBooth").getAsJsonObject()));
        booths.add(new MarketBooth(data.get("secondBooth").getAsJsonObject()));
        booths.add(new MarketBooth(data.get("thirdBooth").getAsJsonObject()));
        booths.add(new MarketBooth(data.get("fourthBooth").getAsJsonObject()));
    }

    public int getNumOfBooths() {
        return numOfBooths;
    }

    public List<MarketBooth> getBooths() {
        return booths;
    }

    /**
     * It handles the format of visualization (for command line interface)
     * @param pl the player to which the booths have to be displayed
     */
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

    /**
     * Serialize the booths information into a JsonArray
     * @return the JsonArray containing the booths information
     */
    public JsonArray serialize() {
        List<JsonObject> ret = new ArrayList<>();
        this.booths.forEach(b -> ret.add(b.serialize()));
        return new Gson().fromJson(new Gson().toJson(ret), JsonArray.class);
    }
}
