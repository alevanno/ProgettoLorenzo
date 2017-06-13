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
        gameTest.game.setCurrPlayer(pl);
        board = gameTest.game.getBoard();
        inputStream = (PlayerIOLocal) pl.getIo();
        Map<String, Integer> famValues = new HashMap<>();
        famValues.put("Orange", 7);
        famValues.put("Black", 7);
        famValues.put("White", 7);
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
        // ok and confirmation
        inputStream.setIn("Bosco\ny\n");
        assertTrue(Move.floorAction(board, pl.getAvailableFamMembers().get(0)));
        inputStream.setIn("Esattoria\nn\n");
        assertFalse(Move.floorAction(board, pl.getAvailableFamMembers().get(0)));
    }

    @Test
    public void floorWithCardTest(){
        Deck tmpDeck = new Deck();
        for(Card c : testDeck) {
            if("Avamposto Commerciale".equals(c.cardName) ||
                    "Badessa".equals(c.cardName))
                tmpDeck.add(c);
        }
        gameTest.game.setBoard(new Board(tmpDeck, gameTest.game));
        String action = "Badessa\ny\n3\ny\nAvamposto Commerciale\ny\ny";
        inputStream.setIn(action);
        assertTrue(Move.floorAction(gameTest.game.getBoard(), pl.getAvailableFamMembers().get(0)));
    }
}
