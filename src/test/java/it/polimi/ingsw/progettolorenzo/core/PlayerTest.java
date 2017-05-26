package it.polimi.ingsw.progettolorenzo.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Test;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class PlayerTest {
    @Test
    public void testFamilyMemberBirth() {
        Socket socket = new Socket();
        Player p1 = new Player("test", "red", socket);
        Map<String, Integer> famValues = new HashMap<>();
        famValues.put("Orange", new Random().nextInt(5) + 1);
        famValues.put("Black", new Random().nextInt(5) + 1);
        famValues.put("White", new Random().nextInt(5) + 1);
        p1.famMembersBirth(famValues);
        assertEquals(4, p1.getAvailableFamMembers().size());
    }

    @Test
    public void testAddCard1() {
        Socket socket = new Socket();
        String jsonString = "{'name': 'test', 'type': foo, 'period': 0}";
        JsonObject obj = new Gson().fromJson(jsonString, JsonObject.class);
        Card c1 = new TerrainCard(obj);
        Player p1 = new Player("test", "red", socket);
        p1.addCard(c1);
        assertEquals(1, p1.listCards().size());
        p1.takeCard(0);
        assertEquals(0, p1.listCards().size());
    }
}
