package it.polimi.ingsw.progettolorenzo.core;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.polimi.ingsw.progettolorenzo.GameTest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PlayerTest {
    GameTest gameTest = new GameTest();
    Deck testDeck;
    Player pl;

    @Before
    public void setup() throws IOException{
        gameTest.setup();
        gameTest.initGame();
        testDeck = gameTest.g.testDeck;
        pl = gameTest.game.getPlayers().get(0);
    }


    @Test
    public void revertIncreaseValue() throws Exception {
        pl.revertIncreaseValue(4);
        assertEquals(7, pl.currentRes.servant);
    }

    @Test
    public void testRevertFamValue() throws Exception {

        Map<String, Integer> famValues = new HashMap<>();
        famValues.put("Orange", 6);
        famValues.put("Black", 3);
        famValues.put("White", 1);
        pl.famMembersBirth(famValues);
        assertEquals(6,pl.getAvailableFamMembers().get(0).getActionValue());
        assertEquals(3,pl.getAvailableFamMembers().get(1).getActionValue());
        assertEquals(1,pl.getAvailableFamMembers().get(2).getActionValue());
        assertEquals(3, pl.currentRes.servant);
        pl.revertFamValue(pl.getAvailableFamMembers().get(0),3);
        assertEquals(6, pl.currentRes.servant);
        pl.revertFamValue(pl.getAvailableFamMembers().get(1),3);
        assertEquals(9, pl.currentRes.servant);
        pl.revertFamValue(pl.getAvailableFamMembers().get(2),1);
        assertEquals(10, pl.currentRes.servant);
        assertEquals(3,pl.getAvailableFamMembers().get(0).getActionValue());
        assertEquals(0,pl.getAvailableFamMembers().get(1).getActionValue());
        assertEquals(0,pl.getAvailableFamMembers().get(2).getActionValue());
    }

    @Test
    public void testEndgame() throws Exception {
        JsonArray data = Utils.getJsonArray("cards.json");
        List<String> excommTest = new ArrayList<>();
        excommTest.add("{'period': 3, 'lostVictoryRes': {'resources': {'coin': 1, 'servant': 1, 'stone': 1, 'wood': 1}}}");
        excommTest.add("{'period': 3, 'lostVictoryCost': {'resources': {'stone': 1,'wood': 1}}, 'type': 'buildings' }");
        excommTest.add("{'period': 3, 'lostVictoryRes': {'resources': {'victoryPoint': 5}}}");
        excommTest.add("{'period': 3, 'noVictoryType': 'territories'}");
        for (int c=0; c<14; c++) {
            pl.addCard(new Card(data.get(new Random().nextInt(95)).getAsJsonObject()));
        }
        pl.currentRes = new Resources.ResBuilder().coin(10).servant(10).stone(10).wood(10)
                .faithPoint(10).militaryPoint(15).victoryPoint(40).build();
        for (String excomm: excommTest) {
            JsonObject excommObj = new Gson().fromJson(
                    String.format(excomm), JsonObject.class
            );
            pl.setExcommunication(excommObj, 2);
            pl.endgame();
        }
    }

    @Test
    public void testFamilyMemberBirth() {
        Map<String, Integer> famValues = new HashMap<>();
        famValues.put("Orange", new Random().nextInt(5) + 1);
        famValues.put("Black", new Random().nextInt(5) + 1);
        famValues.put("White", new Random().nextInt(5) + 1);
        pl.famMembersBirth(famValues);
        assertEquals(4, pl.getAvailableFamMembers().size());
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

    @Test
    public void confirmationTest(){
        Action act = gameTest.game.getBoard().towers.get(0).getFloors().get(0);
        PlayerIOLocal inputStream = (PlayerIOLocal) pl.getIo();
        String confirmationY = "y";
        String confirmationN = "n";
        inputStream.setIn(confirmationY);
        assertTrue(Move.confirmation(pl, act));
        inputStream.setIn(confirmationN);
        assertFalse(Move.confirmation(pl,act));
    }
}
