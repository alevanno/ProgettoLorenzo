package it.polimi.ingsw.progettolorenzo.core;

import it.polimi.ingsw.progettolorenzo.GameTest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MoveTest {
    GameTest gameTest = new GameTest();
    Deck testDeck;
    Player pl;
    Board board;
    PlayerIOLocal inputStream;

    @Before
    public void setup() throws IOException {
        gameTest.setup();
        gameTest.initGame();
        testDeck = gameTest.g.testDeck;
        pl = gameTest.game.getPlayers().get(0);
        board = gameTest.game.getBoard();
        System.out.println(board);
        inputStream = (PlayerIOLocal) pl.getIo();
        Map<String, Integer> famValues = new HashMap<>();
        famValues.put("Orange", 6);
        famValues.put("Black", 6);
        famValues.put("White", 6);
        pl.famMembersBirth(famValues);
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

    @Test
    public void floorActionTest(){
        String cardName = "Avamposto Commerciale";
        inputStream.setIn(cardName);
        // not enough resources
        assertFalse(Move.floorAction(board, pl.getAvailableFamMembers().get(0)));
        // ok and confirmation
        inputStream.setIn("Bosco\n y");
        assertTrue(Move.floorAction(board, pl.getAvailableFamMembers().get(0)));
        inputStream.setIn("Esattoria\n n");
        assertFalse(Move.floorAction(board, pl.getAvailableFamMembers().get(0)));
    }
}
