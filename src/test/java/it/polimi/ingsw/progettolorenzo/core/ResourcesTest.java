package it.polimi.ingsw.progettolorenzo.core;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class ResourcesTest {

    @Test
    public void create1() {
        Resources res = new Resources.ResBuilder().coin(2).build();
        assertEquals(2, res.coin);
        assertEquals(0, res.servant);
        assertEquals(0, res.victoryPoint);
    }

    @Test
    public void createFromJson() {
        String jsonString = "{'wood': 1, 'woody': 3, 'faithPoint': 0}";
        JsonObject obj = new Gson().fromJson(jsonString, JsonObject.class);
        Resources res = Resources.fromJson(obj);
        Resources res2 = Resources.fromJson(new JsonObject());
        for (Resources x : Arrays.asList(res,res2)) {
            assertEquals(0, x.faithPoint);
            assertEquals(0, x.coin);
        }
        assertEquals(1, res.wood);
    }

    @Test(expected = NumberFormatException.class)
    public void createFromJsonStringInsteadOfInt() {
        String jsonString = "{'coin': 'bugcatcher'}";
        JsonObject obj = new Gson().fromJson(jsonString, JsonObject.class);
        Resources.fromJson(obj);
    }

    @Test
    public void merge1() {
        Resources op1 = new Resources.ResBuilder().coin(2).build();
        Resources op2 = new Resources.ResBuilder().coin(-3).wood(1).build();
        Resources op3 = new Resources.ResBuilder().servant(1).build();
        Resources res = op1.merge(op2).merge(op3);
        assertEquals(-1, res.coin);
        assertEquals(1, res.wood);
        assertEquals(0, res.faithPoint);
        assertEquals(1, res.servant);
    }

    @Test
    public void setByString() {
        Resources op = new Resources.ResBuilder()
                .setByString("coin", 1)
                .setByString("wood", 1)
                .setByString("stone", 1)
                .setByString("servant", 1)
                .setByString("militaryPoint", 1)
                .setByString("victoryPoint", 1)
                .setByString("faithPoint", 1)
                .build();
        Resources op2 = new Resources.ResBuilder().setByString("", 0).build();
        for (String s : op.resourcesList.keySet()) {
            assertEquals(1, op.getByString(s));
        }
    }

    @Test
    public void multyMerge() {
        Resources op = new Resources.ResBuilder().coin(2).build();
        op = op.multiplyRes(2);
        assertEquals(4, op.coin);
    }

}
