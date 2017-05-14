package it.polimi.ingsw.ProgettoLorenzo.Core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class PlayerTest {
    @Test
    public void testFamilyMemberBirth() {
        Player p1 = new Player("test", "red");
        p1.familyMembersBirth();
        assertEquals(4, p1.getAvailableFamilyMembers().size());
    }

    @Test
    public void testAddCard1() {
        String jsonString = "{'name': 'test', 'type': foo, 'period': 0}";
        JsonObject obj = new Gson().fromJson(jsonString, JsonObject.class);
        Card c1 = new TerrainCard(obj);
        Player p1 = new Player("test", "red");
        p1.addCard(c1);
        assertEquals(1, p1.listCards().size());
        p1.takeCard(0);
        assertEquals(0, p1.listCards().size());
    }
}
