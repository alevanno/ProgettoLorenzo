package it.polimi.ingsw.progettolorenzo;

import it.polimi.ingsw.progettolorenzo.client.LocalSingleClient;
import it.polimi.ingsw.progettolorenzo.core.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class GameTest {
    public Game game;
    public GameComponentsTest g = new GameComponentsTest();
    LocalSingleClient client = new LocalSingleClient("Toletti", "Blue");
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

    @Test
    public void endgameMilitaryTest() {
        List<Player> players = Arrays.asList(new Player("Ciccio", "Blue"), new Player("Bello", "Red"),
                new Player("Pallo", "Violet"), new Player("Pinco", "Yellow"));
        Game endgameTest = new Game(players, false, false);
        players.get(0).currentResMerge(new Resources.ResBuilder().militaryPoint(20).build());
        players.get(1).currentResMerge(new Resources.ResBuilder().militaryPoint(20).build());
        players.get(2).currentResMerge(new Resources.ResBuilder().militaryPoint(19).build());
        players.get(3).currentResMerge(new Resources.ResBuilder().militaryPoint(15).build());
        endgameTest.endgameMilitary();
    }
}