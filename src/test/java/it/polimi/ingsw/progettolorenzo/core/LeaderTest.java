package it.polimi.ingsw.progettolorenzo.core;

import it.polimi.ingsw.progettolorenzo.GameTest;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class LeaderTest {
    GameTest gameTest = new GameTest();
    Deck testDeck;
    Player pl;
    Board board;
    PlayerIOLocal inputStream;
    List<LeaderCard> testList = new ArrayList<>();
    Map<String, LeaderCard> testMap = new HashMap<>();

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


    @Before
    public void birth(){
        testMap = LeaderUtils.leadersBirth();
    }

    @Test
    public void size() {
        assertEquals(20, testMap.size());
    }

    @Test
    public void leaderUtilsTest(){
        String action = "y\ny\nn\ny\n1\n1";
        LeaderCard leader = testMap.get("Lorenzo Dè Medici");
        leader.setPlayer(pl);
        LeaderCard leader2 = testMap.get("Francesco Sforza");
        leader2.setPlayer(pl);
        LeaderCard leader3 = testMap.get("Filippo Brunelleschi");
        leader3.setPlayer(pl);
        LeaderCard leader4 = testMap.get("Federico Da Montafeltro");
        leader4.setPlayer(pl);
        leader3.activation = true;
        leader4.activation = true;
        Resources cost = new Resources.ResBuilder().stone(leader.activationCost.get(0)).build();
        assertFalse(LeaderUtils.checkCostResSatisfaction(pl, cost));
        assertFalse(LeaderUtils.checkMultiType(leader.types, leader.activationCost, pl));
        inputStream.setIn(action);
        assertTrue(LeaderUtils.commonApply(pl, leader2, false, true));
        // already activated
        assertFalse(LeaderUtils.commonApply(pl, leader3, false, true));
        // OnePerRound not activated
        assertFalse(LeaderUtils.commonApply(pl, leader4, false, true));
        // OnePerRound -> play!
        assertTrue(LeaderUtils.commonApply(pl, leader4, false, true));
    }
    @Test
    public void OneHarvProdTest() {
        String action = "y\ny\n1\ny\ny";
        inputStream.setIn(action);
        LeaderCard leader2 = testMap.get("Francesco Sforza");
        leader2.setPlayer(pl);
        leader2.activation = true;
        Resources tmp = new Resources.ResBuilder().build().merge(pl.currentRes);
        leader2.apply();
        assertTrue(pl.currentRes.servant < tmp.servant);
    }

    @Test
    public void onePerRoundTest() {
        //sforza already tested in OneHarvProd
        //Montafeltro test
        String action = "y\n1\ny\ny\ny\ny\ny\ny\n2\n";
        inputStream.setIn(action);
        LeaderCard montafeltro = testMap.get("Federico Da Montafeltro");
        montafeltro.setPlayer(pl);
        montafeltro.activation = true;
        montafeltro.apply();
        assertEquals(6, pl.getAvailableFamMembers().get(0).getActionValue());
        //Buonarroti test
        LeaderCard buonarroti = testMap.get("Michelangelo Buonarroti");
        buonarroti.setPlayer(pl);
        buonarroti.activation = true;
        Resources tmp = new Resources.ResBuilder().build().merge(pl.currentRes);
        buonarroti.apply();
        assertTrue(pl.currentRes.coin > tmp.coin);
        // BandeNere test
        LeaderCard bandeNere = testMap.get("Giovanni Dalle Bande Nere");
        bandeNere.setPlayer(pl);
        bandeNere.activation = true;
        tmp = new Resources.ResBuilder().build().merge(pl.currentRes);
        bandeNere.apply();
        assertTrue(pl.currentRes.coin > tmp.coin &&
        pl.currentRes.wood > tmp.wood && pl.currentRes.stone > tmp.stone);
        // CosimoDeMedici test
        LeaderCard cosimo = testMap.get("Cosimo Dè Medici");
        cosimo.setPlayer(pl);
        cosimo.activation = true;
        tmp = new Resources.ResBuilder().build().merge(pl.currentRes);
        cosimo.apply();
        assertTrue(pl.currentRes.servant > tmp.servant &&
                pl.currentRes.victoryPoint > tmp.victoryPoint );
        // Colleoni test
        LeaderCard colleoni = testMap.get("Bartolomeo Colleoni");
        colleoni.setPlayer(pl);
        colleoni.activation = true;
        tmp = new Resources.ResBuilder().build().merge(pl.currentRes);
        colleoni.apply();
        assertTrue(pl.currentRes.victoryPoint > tmp.victoryPoint);
        // Botticelli test
        LeaderCard botticelli = testMap.get("Sandro Botticelli");
        botticelli.setPlayer(pl);
        botticelli.activation = true;
        tmp = new Resources.ResBuilder().build().merge(pl.currentRes);
        botticelli.apply();
        assertTrue(pl.currentRes.militaryPoint > tmp.militaryPoint &&pl.currentRes.victoryPoint > tmp.victoryPoint);
        // Gonzaga test
        LeaderCard gonzaga = testMap.get("Ludovico III Gonzaga");
        gonzaga.setPlayer(pl);
        gonzaga.activation = true;
        tmp = new Resources.ResBuilder().build().merge(pl.currentRes);
        // choice n 2 in privileges
        gonzaga.apply();
        assertTrue(pl.currentRes.servant > tmp.servant);
    }
}
