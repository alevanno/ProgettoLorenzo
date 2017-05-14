package it.polimi.ingsw.ProgettoLorenzo.Core;

public class Player {
    public final String playerName;
    public final String playerColour;
    public Resources currentRes;

    public Player(String name, String colour) {
        this.playerName = name;
        this.playerColour = colour;
        this.currentRes = new Resources.ResBuilder().build();  // 0 resources
    }
    
    //public void finalCount() {}
}
