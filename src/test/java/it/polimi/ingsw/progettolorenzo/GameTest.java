package it.polimi.ingsw.progettolorenzo;

import it.polimi.ingsw.progettolorenzo.client.LocalSingleClient;
import it.polimi.ingsw.progettolorenzo.core.Board;
import it.polimi.ingsw.progettolorenzo.core.Deck;
import it.polimi.ingsw.progettolorenzo.core.GameComponentsTest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class GameTest {
    public Game game;
    public GameComponentsTest g = new GameComponentsTest();
    LocalSingleClient client = new LocalSingleClient("Luca", "Blue");

    @Before
    public void setup() {
        g.deckSetup();
        g.boardSetup();
        client.testSingleAction();
        game = client.getGame();
        game.loadSettings();
        Deck deck = g.testDeck;
        client.getGame().setBoard(new Board(deck, game));

    }
    @Test
    public void initGame() throws IOException {
        this.game = client.getGame();
    }
}