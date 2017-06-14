package it.polimi.ingsw.progettolorenzo;

import it.polimi.ingsw.progettolorenzo.client.LocalSingleClient;
import it.polimi.ingsw.progettolorenzo.core.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;

public class GameTest {
    public Game game;
    public GameComponentsTest g = new GameComponentsTest();
    LocalSingleClient client = new LocalSingleClient("Luca", "Blue");
    PlayerIOLocal inputStream;
    Player pl;


    @Before
    public void setup() {
        g.deckSetup();
        g.boardSetup();
        client.testSingleAction();
        game = client.getGame();
        Deck deck = g.testDeck;
        pl = game.getPlayers().get(0);

        client.getGame().setBoard(new Board(deck, game));
        inputStream = (PlayerIOLocal) pl.getIo();


    }
    @Test
    public void initGame() throws IOException {
        this.game = client.getGame();
    }

    @Test
    public void assignLeadersTest() {
        game.loadSettings();
        for (Player pl : game.getPlayers()) {
            assertEquals(4, pl.getLeaderCards().size());
        }
    }

    @Test
    public void fakeGame() {
        String action = "1\nn\n8\n1\n1\nn\n8\n1\n1\nn\n8\n1\n1\nn\n8\n1\n1\nn\n8\n1\n1\nn\n8\n1\n1\nn\n8\n1\n1\nn\n8\n1\n1\nn\n8\n1\n1\nn\n8\n1\n1\nn\n8\n1\n1\nn\n8\n1\n1\nn\n8\n1\n1\nn\n8\n1\n1\nn\n8\n1\n1\nn\n8\n1\n1\nn\n8\n1\n1\nn\n8\n1\n1\nn\n8\n1\n1\nn\n8\n1\n1\nn\n8\n1\n1\nn\n8\n1\n1\nn\n8\n1\n1\nn\n8\n1";
        inputStream.setIn(action);
        game.run();
    }
}