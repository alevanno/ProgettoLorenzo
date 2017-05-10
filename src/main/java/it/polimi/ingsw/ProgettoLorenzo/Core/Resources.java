package it.polimi.ingsw.ProgettoLorenzo.Core;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alessandro on 10/05/2017.
 */
public class Resources {
    private Map<String, Integer> resourcesList = new HashMap<>();
    private Integer coin;
    private Integer wood;
    private Integer stone;
    private int servant;
    private int victoryPoint;
    private int militaryPoint;
    private int faithPoint;

    public Resources(JsonObject src) {
        // FIXME find a nicer way to do this
        this.resourcesList.put("coin", this.coin);
        this.resourcesList.put("wood", this.wood);
        this.resourcesList.put("stone", this.stone);

        System.out.println(src);
        this.coin = src.get("coin").getAsInt();
        this.wood = src.get("wood").getAsInt();
        this.stone = src.get("stone").getAsInt();

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
