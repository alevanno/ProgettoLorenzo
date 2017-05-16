package it.polimi.ingsw.ProgettoLorenzo.Core;


public class FamilyMember {

    private Player parent;
    private int actionValue;
    private String skinColor;

    public FamilyMember(Player parent, int actionValue, String skinColor) {
        this.parent = parent;
        this.actionValue = actionValue;
        this.skinColor = skinColor;
    }

    public String getSkinColor() {
        return this.skinColor;
    }

    public void setSkinColor(String skinColor) {
        this.skinColor = skinColor;
    }

    public Player getParent() {
        return parent;
    }

    public int getActionValue() {
        return actionValue;
    }
}
