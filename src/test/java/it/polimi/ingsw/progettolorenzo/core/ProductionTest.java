package it.polimi.ingsw.progettolorenzo.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.progettolorenzo.GameTest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;


public class ProductionTest {
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
        assertTrue(board.productionArea.claimFamMain(pl.getAvailableFamMembers().get(0)));
        assertFalse(board.productionArea.claimFamMain(pl.getAvailableFamMembers().get(1)));
        board.productionArea.apply();
    }

    @Test
    public void claimFamSec() throws Exception {
        Resources tmp = new Resources.ResBuilder().build().merge(pl.currentRes);
        board.productionArea.claimFamSec(pl.getAvailableFamMembers().get(0));
        board.productionArea.apply();
        System.out.println(pl.currentRes);
        assertTrue(pl.currentRes.victoryPoint > tmp.victoryPoint
        && pl.currentRes.coin > tmp.coin);
    }

    @Test
    public void prodConvTest() {
        String action = "1\n1";
        inputStream.setIn(action);
        for (Card c : testDeck) {
            if ("Falegnameria".equals(c.cardName)) {
                pl.addCard(c);
            }
        }
        // test prodConversion
        Resources tmp = new Resources.ResBuilder().build().merge(pl.currentRes);
        board.productionArea.prod(pl, 7);
        board.productionArea.apply();
        assertTrue(pl.currentRes.wood < tmp.coin);
    }

    @Test
    public void prodConvTest2() {
        System.out.println(pl.currentRes);
        String action = "1\n1\n1";
        inputStream.setIn(action);
        for (Card c : testDeck) {
            if ("Residenza".equals(c.cardName)) {
                pl.addCard(c);
            }
        }
        // test prodConversion
        Resources tmp = new Resources.ResBuilder().build().merge(pl.currentRes);
        board.productionArea.prod(pl,7);
        board.productionArea.apply();
        assertTrue(pl.currentRes.wood > tmp.wood);
        System.out.println(pl.currentRes);
    }

    @Test
    public void prodMultiplierTest() {
        String action = "1\n1";
        inputStream.setIn(action);
        for (Card c : testDeck) {
            if ("Teatro".equals(c.cardName)) {
                pl.addCard(c);
            }
            if ("characters".equals(c.cardType)){
                pl.addCard(c);
            }
        }
        // test prodMultiplier
        board.productionArea.prod(pl, 7);
        board.productionArea.apply();
        assertEquals(25, pl.currentRes.victoryPoint);
    }

    @Test
    public void prodStaticCardTest() {
        String action = "1\n1";
        inputStream.setIn(action);
        for (Card c : testDeck) {
            if ("Fortezza".equals(c.cardName)) {
                pl.addCard(c);
            }
        }
        // test prodStaticCardTest
        board.productionArea.prod(pl, 7);
        board.productionArea.apply();
        assertEquals(2, pl.currentRes.militaryPoint);
        assertEquals(3, pl.currentRes.victoryPoint);
    }

    @Test
    public void prodCouncPriv() {
        String action = "1\n1";
        inputStream.setIn(action);
        for (Card c : testDeck) {
            if ("Castelletto".equals(c.cardName)) {
                pl.addCard(c);
            }
        }
        // test prodCouncPriv
        board.productionArea.prod(pl, 7);
        board.productionArea.apply();
        assertEquals(3, pl.currentRes.wood);
        assertEquals(3, pl.currentRes.victoryPoint);
    }

    @Test
    public void excommTest() {
        String excomm = "{'period': 1,'prodMalus': 3 }";
        JsonObject excommObj = new Gson().fromJson(
                String.format(excomm), JsonObject.class);
        pl.setExcommunication(excommObj,0);
        board.productionArea.prod(pl, 3);
        assertEquals(5, pl.currentRes.coin);
    }

    @Test
    public void noCardTest() {
        for (Card c : testDeck) {
            if ("Zecca".equals(c.cardName)) {
                pl.addCard(c);
            }
        }
        board.productionArea.prod(pl, 4);
        board.productionArea.apply();
        assertEquals(1, pl.currentRes.victoryPoint);
    }
}