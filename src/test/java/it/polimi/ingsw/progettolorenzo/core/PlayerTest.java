package it.polimi.ingsw.progettolorenzo.core;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.Test;

import java.net.Socket;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class PlayerTest {
    @Test
    public void testEndgame() throws Exception {
        Socket socket = new Socket();
        Player p1 = new Player("test", "red", socket);
        JsonArray data = Utils.getJsonArray("cards.json");
        List<String> excommTest = new ArrayList<>();
        excommTest.add("{'period': 3, 'lostVictoryRes': {'resources': {'coin': 1, 'servant': 1, 'stone': 1, 'wood': 1}}}");
        excommTest.add("{'period': 3, 'lostVictoryCost': {'resources': {'stone': 1,'wood': 1}}, 'type': 'buildings' }");
        excommTest.add("{'period': 3, 'lostVictoryRes': {'resources': {'victoryPoint': 5}}}");
        excommTest.add("{'period': 3, 'noVictoryType': 'territories'}");
        for (int c=0; c<14; c++) {
            p1.addCard(new Card(data.get(new Random().nextInt(95)).getAsJsonObject()));
        }
        p1.currentRes = new Resources.ResBuilder().coin(10).servant(10).stone(10).wood(10)
                .faithPoint(10).militaryPoint(15).victoryPoint(40).build();
        for (String excomm: excommTest) {
            JsonObject excommObj = new Gson().fromJson(
                    String.format(excomm), JsonObject.class
            );
            p1.setExcommunication(excommObj, 2);
            p1.endgame();
        }
    }

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
        Card c1 = new Card(obj);
        Player p1 = new Player("test", "red", socket);
        p1.addCard(c1);
        assertEquals(1, p1.listCards().size());
        p1.takeCard(0);
        assertEquals(0, p1.listCards().size());
    }
}
