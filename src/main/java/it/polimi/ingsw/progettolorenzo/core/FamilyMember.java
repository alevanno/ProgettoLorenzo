package it.polimi.ingsw.progettolorenzo.core;


import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class FamilyMember {

    private Player parent;
    private int actionValue;
    private String skinColour;

    public FamilyMember(Player parent, int actionValue, String skinColour) {
        this.parent = parent;
        this.actionValue = actionValue;
        this.skinColour = skinColour;
    }

    public String getSkinColour() {
        return this.skinColour;
    }

    public void setSkinColour(String skinColour) {
        this.skinColour = skinColour;
    }

    public Player getParent() {
        return parent;
    }

    public int getActionValue() {
        return actionValue;
    }

    public void setActionValue(int actionValue) {
        this.actionValue = actionValue;
    }

    public JsonObject serialize() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("colour", this.skinColour);
        ret.put("parentName", this.parent.playerName);
        ret.put("parentColour", this.parent.playerColour);
        return new Gson().fromJson(new Gson().toJson(ret), JsonObject.class);
    }
}
