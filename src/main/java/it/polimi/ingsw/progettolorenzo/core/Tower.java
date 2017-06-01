package it.polimi.ingsw.progettolorenzo.core;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.progettolorenzo.Game;

import java.util.*;
import java.util.logging.Logger;

public class Tower {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private final List<Floor> floors = new ArrayList<>();
    private final String type;
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

    public boolean checkTowerOcc(FamilyMember fam, int resToPay) {
        // FIXME make me prettier (?)
        Player actionPl = fam.getParent();
        List<FamilyMember> occupantsList = new ArrayList<>();
        boolean ret = false;
        fillOccupantList(occupantsList);

        for (FamilyMember famMemb : occupantsList) {
            ret = false;
            if ("Dummy".equals(fam.getSkinColour())) {
                ret = true;
            }
            Player occupantPl = famMemb.getParent();
            if (!actionPl.playerName.equals(occupantPl.playerName) ^
                    ((actionPl.playerName.equals(occupantPl.playerName) && "Blank".equals(fam.getSkinColour()) )
                            || (actionPl.playerName == occupantPl.playerName
                            && "Blank".equals(famMemb.getSkinColour())))) {
                ret = true;
                Move.bool = true;
            }
        }
        if (ret) {
            actionPl.sOut("Tower already occupied: ");
            actionPl.sOut("Pay other " + resToPay + " coin to complete your action?: y/n");
            actionPl.sOut(actionPl.currentRes.toString());
            String answer = actionPl.sIn();
            if ("y".equalsIgnoreCase(answer) && (actionPl.currentRes.coin >= resToPay)) {
                return false;
            } else {
                actionPl.sOut("You are not allowed to pay additional coin");
                return true;
            }
        } else if (occupantsList.size() == 0) {
            return false;
        } else {
            actionPl.sOut("You are not allowed to take Cards from this tower");
            return true;
        }
    }

    private void fillOccupantList(List<FamilyMember> list) {
        for (Floor fl : floors) {
            if(fl.isBusy()) {
                list.add(fl.getFamMember());
            }
        }
    }
}
