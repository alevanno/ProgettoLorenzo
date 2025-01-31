package it.polimi.ingsw.progettolorenzo.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;

/**
 * This class represent the data structure which contains the resources of the game.
 * victory points, military points and faith points are placed here in addition to
 * coin, wood, stone and servant to make coding easier.
 * It allows to easily exchange the single int resources between players and the game itself.
 * All the fields are added to a Map.
 *
 * Resources class is not directly instantiable; the structure of the static
 * class {@link ResBuilder} allows to {@link ResBuilder#build()} every kind of
 * Resources.
 * The class also handle by itself creating resources from file by{@link #fromJson(JsonElement)}.
 *
 * @see ResBuilder
 */
public class Resources {
    public final int coin;
    public final int wood;
    public final int stone;
    public final int servant;
    public final int victoryPoint;
    public final int militaryPoint;
    public final int faithPoint;

    public final Map<String, Integer> resourcesList = new HashMap<>();

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

    /**
     * It allows to get the value of a single integer resource by name.
     * @param s the string representing the resource.
     * @return the int corresponding to the value retrieved from the map, with key s.
     */
    public int getByString(String s) {
        return this.resourcesList.get(s);
    }

    /**
     * This is the static class that allows to create new instances of {@link Resources} class.
     * There is a method for every single integer resource. Every class field has a default
     * value of zero, making possible to create various combination of resources.
     * The {@link #build()} method really creates the resources object filling
     * the constructor with the fields of its class.
     */
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

        /**
         * It assign the desired value to a ResBuilder field by matching
         * the name with the string s.
         * @param s the string representing the name of the resource to set
         * @param val the integer representing the value of the resource
         * @return the same ResBuilder instance with the new desired field's value.
         */
        public ResBuilder setByString(String s, int val) {
            if("coin".equals(s)) {
                return this.coin(val);
            }
            if("wood".equals(s)) {
                return this.wood(val);
            }
            if("stone".equals(s)) {
                return this.stone(val);
            }
            if("servant".equals(s)) {
                return this.servant(val);
            }
            if("victoryPoint".equals(s)) {
                return this.victoryPoint(val);
            }
            if("militaryPoint".equals(s)) {
                return this.militaryPoint(val);
            }
            if("faithPoint".equals(s)) {
                return this.faithPoint(val);
            }
            return this;
        }

        /**
         * It has to be invoked to effectively create the new instance of Resources.
         * @return A new instance of Resources class filling all the constructor's
         * fields.
         */
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

    /**
     * This method allows to directly create Resources from file.
     * The JsonObject is unpacked by {@link Utils#returnZeroIfMissing(JsonObject, String)}
     * and every single value is used as param of the methods that fill class fields.
     * @param src the JsonObject containing all the needed information to create a resource.
     * @return A new Resources instance.
     */
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

    /**
     * It first checks that the src is not null, then
     * return the {@link #fromJson(JsonObject)}.
     * Otherwise, it creates a new empty instance of Resources.
     * @param src the JsonElement containing the resources information.
     * @return An empty or filled instance of Resources.
     */
    public static Resources fromJson(JsonElement src) {
        if (src != null) {
            return Resources.fromJson(src.getAsJsonObject());
        } else {
            return new Resources.ResBuilder().build();
        }
    }

    /**
     * It sums every single "this" integer value with the correspondent
     * value of the param Resources and create a new Resources with
     * the updated fields.
     * @param op the Resources object to be merged.
     * @return the new Resources instance which fields are the sum of the old and the param value.
     */
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

    /**
     * It creates a new instance of Resources class by inverting the value of every field
     * @return the new Resources instance with all the inverted fields .
     */
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

    /**
     * It creates a new instance of Resources class by multiplying every class field with the param int.
     * @param mult the integer value used for multiplying every class field.
     * @return the new Resources instance with all the fields multiplied by mult param.
     */
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

    /**
     * It fills a List with all the class fields.
     * @return the List containing all the class fields.
     */
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

    /**
     * Simple method checking the presence of a negative class field.
     * @return the boolean value representing the presence of a negative class field.
     */
    public boolean isNegative() {
        for(int i : this.getAsList()) {
            if (i < 0) {
                return true;
            }
        } return false;
    }
}
