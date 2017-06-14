package it.polimi.ingsw.progettolorenzo.core;

import com.google.gson.JsonArray;
import it.polimi.ingsw.progettolorenzo.GameTest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.*;


public class ProductionTest {
    GameTest gameTest = new GameTest();
    Deck testDeck;
    Player pl;
    Board board;
    PlayerIOLocal inputStream;

    @Before
    public void setup() throws IOException {
        gameTest.setup();
        gameTest.game.loadSettings();
        testDeck = gameTest.g.testDeck;
        pl = gameTest.game.getPlayers().get(0);
        gameTest.game.setCurrPlayer(pl);
        board = gameTest.game.getBoard();
        inputStream = (PlayerIOLocal) pl.getIo();
        Map<String, Integer> famValues = new HashMap<>();
        famValues.put("Orange", 7);
        famValues.put("Black", 7);
        famValues.put("White", 7);
        pl.famMembersBirth(famValues);
    }


    @Test
    public void claimFamMainTest() throws Exception {
        // with leader card
        LeaderCard ariosto = new LudovicoAriosto();
        ariosto.activation = true;
        ariosto.setPlayer(pl);
        pl.getLeaderCards().add(ariosto);
        assertTrue(board.productionArea.claimFamMain(pl.getAvailableFamMembers().get(0)));
    }

    @Test
    public void claimFamSec() throws Exception {
        Resources tmp = new Resources.ResBuilder().build().merge(pl.currentRes);
        board.productionArea.claimFamSec(pl.getAvailableFamMembers().get(0));
        board.productionArea.apply();
        System.out.println(pl.currentRes);
        assertTrue(pl.currentRes.victoryPoint > tmp.victoryPoint
        && pl.currentRes.coin > tmp.coin);
    }

    @Test
    public void prodConvTest() {
        String action = "1\n1";
        inputStream.setIn(action);
        for (Card c : testDeck) {
            if ("Falegnameria".equals(c.cardName)) {
                pl.addCard(c);
            }
        }
        // test prodConversion
        Resources tmp = new Resources.ResBuilder().build().merge(pl.currentRes);
        board.productionArea.prod(pl,7);
        board.productionArea.apply();
        assertTrue(pl.currentRes.wood < tmp.coin);
    }

    @Test
    public void prodMultiplierTest() {
        String action = "1\n1";
        inputStream.setIn(action);
        for (Card c : testDeck) {
            if ("Teatro".equals(c.cardName)) {
                pl.addCard(c);
            }
            if ("characters".equals(c.cardType)){
                pl.addCard(c);
            }
        }
        // test prodMultiplier
        board.productionArea.prod(pl, 7);
        board.productionArea.apply();
        assertEquals(25, pl.currentRes.victoryPoint);
    }
    /*

    @Test
    public void testProd() throws Exception { //TODO more specific testing
        Socket socket = new Socket();
        Player p1 = new Player("test", "red", socket);
        JsonArray allBonuses = Utils.getJsonArray("bonusTile.json");
        BonusTile bonusTile = new BonusTile(allBonuses.get(0)
                .getAsJsonObject());
        p1.setBonusTile(bonusTile);
        JsonArray data = Utils.getJsonArray("cards.json");
        for (int c=0; c<14; c++) {
            p1.addCard(new Card(data.get(new Random().nextInt(95)).getAsJsonObject()));
        }
        Production pr = new Production();
        pr.prod(p1, 6);
    }*/

}