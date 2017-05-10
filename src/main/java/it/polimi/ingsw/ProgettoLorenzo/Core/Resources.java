package it.polimi.ingsw.ProgettoLorenzo.Core;

/**
 * Created by Alessandro on 10/05/2017.
 */
public class Resources {
    private int coin;
    private int wood;
    private int stone;
    private int servant;
    private int victoryPoint;
    private int militaryPoint;
    private int faithPoint;

    public Resources(int coin, int wood, int stone, int servant, int victoryPoint, int militaryPoint, int faithPoint) {
        this.coin = coin;
        this.wood = wood;
        this.stone = stone;
        this.servant = servant;
        this.victoryPoint = victoryPoint;
        this.militaryPoint = militaryPoint;
        this.faithPoint = faithPoint;
    }
}
