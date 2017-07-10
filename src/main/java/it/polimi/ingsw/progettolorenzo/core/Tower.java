package it.polimi.ingsw.progettolorenzo.core;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;
import java.util.logging.Logger;

/**
 * This class represents the container of the {@link Floor}s.
 * It handles by itself the player's occupancy.
 */
public class Tower {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private final List<Floor> floors = new ArrayList<>();
    private final String type;

    /**
     * All fields info are loaded from file.
     * @param type the type of development cards filling the tower
     * @param floors the JsonArray containing the floors info
     * @param cardList cards the tower has to be filled
     */
    public Tower(String type, JsonArray floors, Deck cardList) {
        this.type = type;

        log.fine(String.format(
            "Instantiating a tower [type %s, %d floors]…",
            type, floors.size()));

        Iterator<JsonElement> fit = floors.iterator();
        Iterator<Card> cit = cardList.iterator();
        while (fit.hasNext() && cit.hasNext()) {
            JsonObject f = fit.next().getAsJsonObject();
            Card c = cit.next();
            int floorValue = f.get("floorValue").getAsInt();
            JsonElement fjbonus = f.get("bonus");
            Resources fbonus;
            if (fjbonus != null) {
                fbonus = Resources.fromJson(fjbonus);
            } else {  // the floor doesn't specify any bonus
                fbonus = new Resources.ResBuilder().build();
            }
            this.floors.add(new Floor(fbonus, c, this, floorValue));
        }
    }

    public List<Floor> getFloors() {
        return floors;
    }

    public String getType() {
        return this.type;
    }

    public List<String> getTowerCardsName() {
        List<String> nameList = new ArrayList<>();

        for (Floor fl : this.floors) {
            if(fl.getCard() == null) {
                continue;
            }
            nameList.add(fl.getCard().getCardName());
        }
        return nameList;
    }

    /**
     * It serializes tower info
     * @see Floor#serialize()
     * @return the JsonObject with the serialized info
     */
    public JsonObject serialize() {
        Map<String,Object> ret = new HashMap<>();
        ret.put("type", this.type);
        List<JsonObject> floors = new ArrayList<>();
        this.floors.forEach(
                f -> floors.add(f.serialize())
        );
        ret.put("floors", floors);
        return new Gson().fromJson(new Gson().toJson(ret), JsonObject.class);
    }

    /**
     * It handles 3 different status:
     *          0: free (0 {@link Floor#accessFloor(Player, int)}
     *          1: occupied by another player, or by own neutral famMem (3 {@link Floor#accessFloor(Player, int)}
     *          2: occupied by the same player's colored famMem (unavailable)
     * @param fam the family member try accessing the tower
     * @return the int representing the specific status
     */
    public int checkTowerOcc(FamilyMember fam) {
        Player actionPl = fam.getParent();
        List<FamilyMember> occupantsList = new ArrayList<>();
        fillOccupantList(occupantsList);

        if (occupantsList.size() == 0) {
            return 0; //tutti gli else successivi presuppongono torre occupata
        } else if ("Dummy".equals(fam.getSkinColour())) {
            return 1;
        } else {
            for (FamilyMember famMemb : occupantsList) {
                Player occupantPl = famMemb.getParent();
                if (occupantPl.equals(actionPl) && !"Blank".equals(fam.getSkinColour())
                        && !"Blank".equals(famMemb.getSkinColour())) {
                    return 2;
                }
            }
            return 1;
        }
    }

    /**
     * Private method that fills the list of the family member (so players) occupying the tower.
     * Dummy family member aren't counted as occupants.
     * @see Move#floorActionWithCard(Player, Card, String, int, Resources)
     * @param list the list to fill
     */
    private void fillOccupantList(List<FamilyMember> list) {
        for (Floor fl : floors) {
            if(fl.isBusy() && !"Dummy".equals(fl.getFamMember().getSkinColour())) { 
                list.add(fl.getFamMember());
            }
        }
    }
}
