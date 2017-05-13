package it.polimi.ingsw.ProgettoLorenzo.Core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Test;
import static org.junit.Assert.*;

public class ResourcesTest {

    @Test
    public void create1 () {
        Resources res = new Resources.ResBuilder().coin(2).build();
        assertEquals(2, res.coin);
        assertEquals(0, res.servant);
        assertEquals(0, res.victoryPoint);
    }

    @Test
    public void createFromJson () {
        String jsonString = "{'wood': 1, 'woody': 3, 'faithPoint': 0}";
        JsonObject obj = new Gson().fromJson(jsonString, JsonObject.class);
        Resources res = Resources.fromJson(obj);
        assertEquals(1, res.wood);
        assertEquals(0, res.faithPoint);
        assertEquals(0, res.coin);
    }

    @Test(expected = NumberFormatException.class)
    public void createFromJsonStringInsteadOfInt () {
        String jsonString = "{'coin': 'bugcatcher'}";
        JsonObject obj = new Gson().fromJson(jsonString, JsonObject.class);
        Resources res = Resources.fromJson(obj);
    }
}
