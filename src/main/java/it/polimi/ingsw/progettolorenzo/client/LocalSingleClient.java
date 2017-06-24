package it.polimi.ingsw.progettolorenzo.client;


import it.polimi.ingsw.progettolorenzo.Game;
import it.polimi.ingsw.progettolorenzo.core.Player;

public class LocalSingleClient implements ClientInterface {
    private final String name;
    private final String colour;
    private Game g;

    public LocalSingleClient(String name, String colour) {
        this.name = name;
        this.colour = colour;
    }

    @Override
    public void startClient() {
        Player player = new Player(name, colour);
        g = new Game(player, 1, false, true);
        g.run();
    }

    public void testSingleAction() {
        Player player = new Player(name, colour);
        g = new Game(player, 1, false, true);
    }

    public Game getGame() {
        return g;
    }

    @Override
    public void endClient() {}
}
