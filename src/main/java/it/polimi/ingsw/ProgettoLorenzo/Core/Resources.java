package it.polimi.ingsw.ProgettoLorenzo.Core;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;


public class Resources {
    //private Map<String, Integer> resourcesList = new HashMap<>();
    private final int coin;
    private final int wood;
    private final int stone;
    private final int servant;
    private final int victoryPoint;
    private final int militaryPoint;
    private final int faithPoint;

    private Resources(
            final int coin,
            final int wood,
            final int stone,
            final int servant,
            final int victoryPoint,
            final int militaryPoint,
            final int faithPoint) {
        this.coin = coin;
        this.wood = wood;
        this.stone = stone;
        this.servant = servant;
        this.victoryPoint = victoryPoint;
        this.militaryPoint = militaryPoint;
        this.faithPoint = faithPoint;
    }


    public static class ResBuilder {
        private int nCoin = 0;
        private int nWood = 0;
        private int nStone = 0;
        private int nServant = 0;
        private int nVictoryPoint = 0;
        private int nMilitaryPoint = 0;
        private int nFaithPoint = 0;

        public ResBuilder coin(final int newCoin) {
            this.nCoin = newCoin;
            return this;
        }
        public ResBuilder wood(final int newWood) {
            this.nWood = newWood;
            return this;
        }
        public ResBuilder stone(final int newStone) {
            this.nStone = newStone;
            return this;
        }
        public ResBuilder servant(final int newServant) {
            this.nServant = newServant;
            return this;
        }
        public ResBuilder victoryPoint(final int newVictoryPoint) {
            this.nVictoryPoint = newVictoryPoint;
            return this;
        }
        public ResBuilder militaryPoint(final int newMilitaryPoint) {
            this.nMilitaryPoint = newMilitaryPoint;
            return this;
        }
        public ResBuilder faithPoint(final int newFaithPoint) {
            this.nFaithPoint = newFaithPoint;
            return this;
        }

        public Resources build() {
            return new Resources(
                this.nCoin,
                this.nWood,
                this.nStone,
                this.nServant,
                this.nVictoryPoint,
                this.nMilitaryPoint,
                this.nFaithPoint
            );
        }
    }


    public static Resources fromJson(JsonObject src) {
        // FIXME find a nicer way to do this

        System.out.println(src);
        return new ResBuilder()
            .coin(src.get("coin").getAsInt())
            .wood(src.get("wood").getAsInt())
            .stone(src.get("stone").getAsInt())
            .build();

        //this.resourcesList.forEach((key, value) -> value = src.get(key).getAsInt());
    }

    private String appendIfNotZero(String key, int value) {
        if (value != 0) {
            return key + value;
        }
        return "";
    }

    @Override
    public String toString() {
        String out = new String("");

        System.out.println(this.coin);
        System.out.println(this.wood);
        System.out.println(this.stone);
        //this.resourcesList.forEach((key, value) -> out.concat(appendIfNotZero(key, value)));
        return out;
    }
}
