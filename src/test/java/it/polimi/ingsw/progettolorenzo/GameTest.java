package it.polimi.ingsw.progettolorenzo;

import it.polimi.ingsw.progettolorenzo.client.LocalSingleClient;
import it.polimi.ingsw.progettolorenzo.core.Board;
import it.polimi.ingsw.progettolorenzo.core.GameComponentsTest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;


public class GameTest {
    public Game game;
    public GameComponentsTest g = new GameComponentsTest();
    LocalSingleClient client = new LocalSingleClient("Luca", "Blue");

    @Before
    public void setup() {
        g.deckSetup();
        g.boardSetup();
        client.testSingleAction();
        client.getGame().loadSettings();
        client.getGame().setBoard(g.board);

    }
    @Test
    public void initGame() throws IOException {
        this.game = client.getGame();
    }
}