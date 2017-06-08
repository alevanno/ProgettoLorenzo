package it.polimi.ingsw.progettolorenzo.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import it.polimi.ingsw.progettolorenzo.Game;
import org.junit.Before;
import org.junit.Test;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


public class GameComponentsTest {
    Socket sock1 = new Socket();
    private Deck testDeck = new Deck();
    Board board;

    List<Player> players = new ArrayList<>(Arrays.asList(
            new Player("luca","Red", sock1)
    ));
    Game game = new Game(players,false);

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

    @Test
    public void boardBirthNull() {
        assertNull("Null",game.getBoard());
    }
    @Test
    public void boardBirth1() {
        assertNotNull(this.board);
    }

    @Test
    public void towersBirth() {
        assertEquals(4, this.board.towers.size());
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
}
