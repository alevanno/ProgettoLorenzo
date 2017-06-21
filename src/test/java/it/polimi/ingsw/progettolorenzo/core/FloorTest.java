package it.polimi.ingsw.progettolorenzo.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.progettolorenzo.GameTest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;


public class FloorTest {
    GameTest gameTest = new GameTest();
    Deck testDeck;
    Player pl;
    Board board;
    PlayerIOLocal inputStream;
    Floor floor1;

    @Before
    public void setup() throws IOException {
        gameTest.setup();
        gameTest.game.loadSettings();
        testDeck = gameTest.testDeck;
        pl = gameTest.game.getPlayers().get(0);
        gameTest.game.setCurrPlayer(pl);
        board = gameTest.game.getBoard();
        inputStream = (PlayerIOLocal) pl.getIo();
        Map<String, Integer> famValues = new HashMap<>();
        famValues.put("Orange", 5);
        famValues.put("Black", 5);
        famValues.put("White", 3);
        pl.famMembersBirth(famValues);
    }

    @Test
    public void checkEnoughValue() throws Exception {
        assertTrue(gameTest.board.towers.get(0).getFloors().get(1).checkEnoughValue(pl.getAvailableFamMembers().get(0)));
        String excomm = "{'period': 2, 'type': 'characters', 'valMalus': 4}";
        JsonObject excommObj = new Gson().fromJson(
                String.format(excomm), JsonObject.class
        );
        pl.setExcommunication(excommObj, 1);
        assertFalse(gameTest.board.towers.get(1).getFloors().get(1).checkEnoughValue(pl.getAvailableFamMembers().get(1)));
        for (Card c : testDeck) {
            if ("Cavaliere".equals(c.cardName)) {
                pl.addCard(c);
                break;
            }
        }
        assertTrue(gameTest.board.towers.get(3).getFloors().get(1).checkEnoughValue(pl.getAvailableFamMembers().get(2)));
    }

    @Test
    public void checkEnoughRes() throws Exception {
        Deck tmpDeck = new Deck();
        for (Card c : testDeck) {
            if ("Ingaggiare i Mercenari".equals(c.cardName) ||
                    "Campagna Militare".equals(c.cardName) ||
                    "Cava di Ghiaia".equals(c.cardName)) {
                tmpDeck.add(c);
            }
        }
        gameTest.game.setBoard(new Board(tmpDeck, gameTest.game));
        Floor testFloor1 = Move.searchCard("Ingaggiare i Mercenari", gameTest.game.getBoard(), pl, "any");
        Floor testFloor2 = Move.searchCard("Campagna Militare", gameTest.game.getBoard(), pl, "any");
        pl.currentResMerge(new Resources.ResBuilder().militaryPoint(2).build());
        assertFalse(testFloor1.checkEnoughRes(pl));
        assertFalse(testFloor2.checkEnoughRes(pl));
        for (Card c : testDeck) {
            if ("Monastero".equals(c.cardName) ||
                    "Borgo".equals(c.cardName)) {
                pl.addCard(c);
            }
        }
        Floor testFloor3 = Move.searchCard("Cava di Ghiaia", gameTest.game.getBoard(), pl, "any");
        assertFalse(testFloor3.checkEnoughRes(pl));
    }

}