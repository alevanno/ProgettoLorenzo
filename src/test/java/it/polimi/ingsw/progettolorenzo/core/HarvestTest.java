package it.polimi.ingsw.progettolorenzo.core;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.polimi.ingsw.progettolorenzo.GameTest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.*;


public class HarvestTest {
    GameTest gameTest = new GameTest();
    Deck testDeck;
    Player pl;
    Board board;
    PlayerIOLocal inputStream;

    @Before
    public void setup() throws IOException {
        gameTest.setup();
        gameTest.game.loadSettings();
        testDeck = gameTest.g.testDeck;
        pl = gameTest.game.getPlayers().get(0);
        gameTest.game.setCurrPlayer(pl);
        board = gameTest.game.getBoard();
        inputStream = (PlayerIOLocal) pl.getIo();
        Map<String, Integer> famValues = new HashMap<>();
        famValues.put("Orange", 7);
        famValues.put("Black", 0);
        famValues.put("White", 7);
        pl.famMembersBirth(famValues);
    }

    @Test
    public void claimFamMainTest() throws Exception {
        // with leader card
        LeaderCard ariosto = new LudovicoAriosto();
        ariosto.activation = true;
        ariosto.setPlayer(pl);
        pl.getLeaderCards().add(ariosto);
        assertTrue(board.harvestArea.claimFamMain(pl.getAvailableFamMembers().get(0)));
        assertFalse(board.harvestArea.claimFamMain(pl.getAvailableFamMembers().get(1)));
        board.harvestArea.apply();
    }

    @Test
    public void claimFamSec() throws Exception {
        Resources tmp = new Resources.ResBuilder().build().merge(pl.currentRes);
        board.harvestArea.claimFamSec(pl.getAvailableFamMembers().get(0));
        board.harvestArea.apply();
        System.out.println(pl.currentRes);
        assertTrue(pl.currentRes.wood > tmp.wood
                && pl.currentRes.stone > tmp.stone);
    }

    @Test
    public void harvStaticCardTest() {
        String action = "1\n1";
        inputStream.setIn(action);
        for (Card c : testDeck) {
            if ("Maniero".equals(c.cardName)) {
                pl.addCard(c);
            }
        }
        // test harvStaticCardTest
        board.harvestArea.harv(pl, 7);
        board.harvestArea.apply();
        assertEquals(2, pl.currentRes.militaryPoint);
        assertEquals(6, pl.currentRes.servant);
    }

    @Test
    public void harvCouncPriv() {
        String action = "1\n1";
        inputStream.setIn(action);
        for (Card c : testDeck) {
            if ("Città".equals(c.cardName)) {
                pl.addCard(c);
            }
        }
        // test harvCouncPriv
        board.harvestArea.harv(pl, 7);
        board.harvestArea.apply();
        assertEquals(4, pl.currentRes.wood);
        assertEquals(4, pl.currentRes.stone);
    }

    @Test
    public void excommTest() {
        String excomm = "{'period': 1,'harvMalus': 3 }";
        JsonObject excommObj = new Gson().fromJson(
                String.format(excomm), JsonObject.class);
        pl.setExcommunication(excommObj, 0);
        board.harvestArea.harv(pl, 3);
        board.harvestArea.apply();
        assertEquals(5, pl.currentRes.coin);
    }

    @Test
    public void noCardTest() {
        for (Card c : testDeck) {
            if ("Ducato".equals(c.cardName)) {
                pl.addCard(c);
            }
        }
        board.harvestArea.harv(pl, 3);
        board.harvestArea.apply();
        assertEquals(3, pl.currentRes.wood);
    }



}