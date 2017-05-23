package it.polimi.ingsw.ProgettoLorenzo.Core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;


// FIXME This class suffers from a very rare form of DRY violation.
// FIXME Breeding code is nice, but this is not a rabbit family.

public class Resources {
    public final int coin;
    public final int wood;
    public final int stone;
    public final int servant;
    public final int victoryPoint;
    public final int militaryPoint;
    public final int faithPoint;

    private Map<String, Integer> resourcesList = new HashMap<>();

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

        this.resourcesList.put("coin", this.coin);
        this.resourcesList.put("wood", this.wood);
        this.resourcesList.put("stone", this.stone);
        this.resourcesList.put("servant", this.servant);
        this.resourcesList.put("victoryPoint", this.victoryPoint);
        this.resourcesList.put("militaryPoint", this.militaryPoint);
        this.resourcesList.put("faithPoint", this.faithPoint);
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
        return new ResBuilder()
            .coin(Utils.returnZeroIfMissing(src, "coin"))
            .wood(Utils.returnZeroIfMissing(src,"wood"))
            .stone(Utils.returnZeroIfMissing(src,"stone"))
            .servant(Utils.returnZeroIfMissing(src,"servant"))
            .victoryPoint(Utils.returnZeroIfMissing(src,"victoryPoint"))
            .militaryPoint(Utils.returnZeroIfMissing(src,"militaryPoint"))
            .faithPoint(Utils.returnZeroIfMissing(src,"faithPoint"))
            .build();
    }

    public static Resources fromJson(JsonElement src) {
        return Resources.fromJson(src.getAsJsonObject());
    }


    public Resources merge(Resources op) {
        return new ResBuilder()
            .coin(this.coin + op.coin)
            .wood(this.wood + op.wood)
            .stone(this.stone + op.stone)
            .servant(this.servant + op.servant)
            .victoryPoint(this.victoryPoint + op.victoryPoint)
            .militaryPoint(this.militaryPoint + op.militaryPoint)
            .faithPoint(this.faithPoint + op.faithPoint)
            .build();
    }

    public Resources inverse() {
        return new ResBuilder()
            .coin(-this.coin)
            .wood(-this.wood)
            .stone(-this.stone)
            .servant(-this.servant)
            .victoryPoint(-this.victoryPoint)
            .militaryPoint(-this.militaryPoint)
            .faithPoint(-this.faithPoint)
            .build();
    }

    public Resources multiplyRes(int mult) {
        return new ResBuilder()
                .coin(this.coin * mult)
                .wood(this.wood * mult)
                .stone(this.stone * mult)
                .servant(this.servant * mult)
                .victoryPoint(this.victoryPoint * mult)
                .militaryPoint(this.militaryPoint * mult)
                .faithPoint(this.faithPoint * mult)
                .build();
    }

    @Override
    public String toString() {
        String out = "{";

        Iterator it = this.resourcesList.entrySet().iterator();
        boolean i = false;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if ((int)pair.getValue() != 0) {
                if (i) { out += ", "; }
                out += pair.getKey().toString() + ": " + pair.getValue();
                i = true;
            }
        }
        out += "}";
        return out;
    }

    public List<Integer> getAsList() {
        List<Integer> resList = new ArrayList<>();
        resList.add(this.coin);
        resList.add(this.stone);
        resList.add(this.wood);
        resList.add(this.servant);
        resList.add(this.victoryPoint);
        resList.add(this.militaryPoint);
        resList.add(this.faithPoint);
        return resList;
    }

    public boolean isNegative() {
        for(int i : this.getAsList()) {
            if (i < 0) {
                return true;
            }
        } return false;
    }
}
