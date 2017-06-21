package it.polimi.ingsw.progettolorenzo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import it.polimi.ingsw.progettolorenzo.client.LocalSingleClient;
import it.polimi.ingsw.progettolorenzo.core.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GameTest {
    public Deck testDeck = new Deck();
    public Board board;
    public Game game;
    LocalSingleClient client = new LocalSingleClient("Toletti", "Blue");
    PlayerIOLocal inputStream;
    Player pl;


    @Before
    public void deckSetup() {
        JsonArray cardsData = Utils.getJsonArray("cards.json");
        for (JsonElement c : cardsData) {
            Card card = new Card(c.getAsJsonObject());
            this.testDeck.add(card);
        }
    }

    @Before
    public void boardSetup() {
        this.board = new Board(this.testDeck, game);
    }

    @Before
    public void setup() {
        this.deckSetup();
        this.boardSetup();
        client.testSingleAction();
        game = client.getGame();
        pl = game.getPlayers().get(0);
        client.getGame().setBoard(new Board(testDeck, game));
        inputStream = (PlayerIOLocal) pl.getIo();


    }


    @Test
    public void boardBirth1() {
        assertNotNull(this.board);
    }

    @Test
    public void towersBirth() {
        Assert.assertEquals(4, this.board.towers.size());
    }
    @Test
    public void prodBirth() {
        assertNotNull(this.board.productionArea);
    }
    @Test
    public void harvBirth() {
        assertNotNull(this.board.harvestArea);
    }
    @Test
    public void councilBirth() {
        assertNotNull(this.board.councilPalace);
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
        Game endgameTest = new Game(players.get(0), 4, false, false);
        for (int i=1; i<4; i++) {
            endgameTest.addPlayer(players.get(i));
        }
        int maxPts = 30;
        players.get(0).currentResMerge(new Resources.ResBuilder().militaryPoint(5).build()); //ciccio
        players.get(1).currentResMerge(new Resources.ResBuilder().militaryPoint(17).build()); //bello
        players.get(2).currentResMerge(new Resources.ResBuilder().militaryPoint(maxPts).build()); //pallo
        players.get(3).currentResMerge(new Resources.ResBuilder().militaryPoint(maxPts).build()); //pinco
        endgameTest.endgameMilitary();
        assertEquals(maxPts, endgameTest.getPlayers().get(0).getCurrentRes().militaryPoint);
    }

    @Test
    public void endgameMilitaryTest2() {
        List<Player> players = Arrays.asList(new Player("Ciccio", "Blue"), new Player("Bello", "Red"),
                new Player("Pallo", "Violet"), new Player("Pinco", "Yellow"));
        Game endgameTest = new Game(players.get(0), 4, false, false);
        for (int i=1; i<4; i++) {
            endgameTest.addPlayer(players.get(i));
        }
        int secMaxPts = 17;
        players.get(0).currentResMerge(new Resources.ResBuilder().militaryPoint(17).build()); //ciccio
        players.get(1).currentResMerge(new Resources.ResBuilder().militaryPoint(17).build()); //bello
        players.get(2).currentResMerge(new Resources.ResBuilder().militaryPoint(30).build()); //pallo
        players.get(3).currentResMerge(new Resources.ResBuilder().militaryPoint(17).build()); //pinco
        endgameTest.endgameMilitary();
        assertEquals(secMaxPts, players.get(1).getCurrentRes().militaryPoint);
    }

    @Test
    public void reportToVaticanTest(){
        game.initExcomm();
        String action = "y\ny";
        inputStream.setIn(action);
        pl.currentResMerge(new Resources.ResBuilder().faithPoint(4).build());
        game.reportToVatican(2);
        pl.currentResMerge(new Resources.ResBuilder().faithPoint(3).build());
        game.reportToVatican(4);
        pl.currentResMerge(new Resources.ResBuilder().faithPoint(9).build());
        game.reportToVatican(6);
        assertEquals(0, pl.getCurrentRes().faithPoint);
    }
}