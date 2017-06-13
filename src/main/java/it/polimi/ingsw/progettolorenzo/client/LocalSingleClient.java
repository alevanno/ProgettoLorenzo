package it.polimi.ingsw.progettolorenzo.client;


import it.polimi.ingsw.progettolorenzo.Game;
import it.polimi.ingsw.progettolorenzo.core.Player;
import java.io.IOException;
import java.util.Arrays;

public class LocalSingleClient implements ClientInterface{
    private final String name;
    private final String colour;
    Game g;

    public LocalSingleClient(String name, String colour) {
        this.name = name;
        this.colour = colour;
    }

    @Override
    public void startClient() {
        Player player = new Player(name, colour);
        g = new Game(Arrays.asList(player), true, true);
        g.run();
    }

    public void testSingleAction() {
        Player player = new Player(name, colour);
        g = new Game(Arrays.asList(player), true, true);
    }

    public Game getGame() {
        return g;
    }

    @Override
    public void endClient() {}
}
