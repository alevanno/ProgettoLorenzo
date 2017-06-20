package it.polimi.ingsw.progettolorenzo.core;


import it.polimi.ingsw.progettolorenzo.Game;
import it.polimi.ingsw.progettolorenzo.GameTest;
import org.junit.Before;
import static org.junit.Assert.*;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImmediateActionTest {
    GameTest gameTest = new GameTest();
    Deck testDeck;
    Player pl;
    List<String> nameList = new ArrayList<>(Arrays.asList(
            "Vescovo", "Cardinale", "Bosco", "Nobile"
    ));


    @Before
    public void setup() throws IOException {
        gameTest.setup();
        gameTest.initGame();
        gameTest.game.loadSettings();
        testDeck = gameTest.g.testDeck;
        pl = gameTest.game.getPlayers().get(0);
    }
    @Test
    public void immRes() {
        LeaderCard leader = new SantaRita();
        leader.activation = true;
        pl.getLeaderCards().add(leader);
        Resources tmp = new Resources.ResBuilder().build().merge(pl.getCurrentRes());
        for (Card c : testDeck) {
            for(String name : nameList) {
                if(name.equals(c.cardName)){
                    Action act = new CardImmediateAction(c, pl);
                    act.logActions();
                    act.apply();
                }
            }
        }
        for (int i = 0; i < pl.getCurrentRes().getAsList().size(); i++) {
            assertTrue(pl.getCurrentRes().getAsList().get(i) >= tmp.getAsList().get(i));
        }
    }
}
