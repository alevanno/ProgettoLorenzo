package it.polimi.ingsw.progettolorenzo.core;


import it.polimi.ingsw.progettolorenzo.Game;
import it.polimi.ingsw.progettolorenzo.GameTest;
import org.junit.Before;
import static org.junit.Assert.*;
import org.junit.Test;

import java.io.IOException;

public class ImmediateActionTest {
    GameTest gameTest = new GameTest();
    Deck testDeck;
    Player pl;


    @Before
    public void setup() throws IOException {
        gameTest.setup();
        gameTest.initGame();
        testDeck = gameTest.g.testDeck;
        pl = gameTest.game.getPlayers().get(0);
    }
    /*@Test
    public void immRes() {
        Resources tmp = new Resources.ResBuilder().build().merge(pl.currentRes);
        for (Card c : testDeck) {
            if (c.immediateEff.containsKey("resources")) {
                Action act = new CardImmediateAction(c, pl);
                act.logActions();
                act.apply();
            }
        }
        for (int i : pl.currentRes.getAsList()) {
            assertFalse(pl.currentRes.getAsList().get(i)
                    > tmp.getAsList().get(i));
        }
    }*/

}
