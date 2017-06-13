package it.polimi.ingsw.progettolorenzo.client;


import it.polimi.ingsw.progettolorenzo.Game;
import it.polimi.ingsw.progettolorenzo.core.Player;
import java.io.IOException;
import java.util.Arrays;

public class LocalSingleClient {
    private final String name;
    private final String colour;

    public LocalSingleClient(String name, String colour) {
        this.name = name;
        this.colour = colour;
    }

    public void startClient() throws IOException {
        Player player = new Player(name, colour);
        new Game(Arrays.asList(player), false).run();
    }

    public void endClient() {}
}
