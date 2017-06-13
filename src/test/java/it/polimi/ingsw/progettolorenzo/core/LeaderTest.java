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
        gameTest.initGame();
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
        testList.addAll(Arrays.asList(
                new BartolomeoColleoni(pl),
                new CesareBorgia(pl),
                new CosimoDeMedici(pl),
                new FedericoDaMontefeltro(pl),
                new FilippoBrunelleschi(pl),
                new FrancescoSforza(pl),
                new GiovanniDalleBandeNere(pl),
                new GirolamoSavonarola(pl),
                new LorenzoDeMedici(pl),
                new LeonardoDaVinci(pl),
                new LucreziaBorgia(pl),
                new LudovicoAriosto(pl),
                new LudovicoIIIGonzaga(pl),
                new LudovicoIlMoro(pl),
                new MichelangeloBuonarroti(pl),
                new PicoDellaMirandola(pl),
                new SandroBotticelli(pl),
                new SantaRita(pl),
                new SigismondoMalatesta(pl),
                new SistoIV(pl)));
        for (LeaderCard card : testList) {
            testMap.put(card.getName(), card);
        }

    }

    @Test
    public void size() {
        assertEquals(20, testList.size());
    }

    @Test
    public void leaderUtilsTest(){
        String action = "y\ny\nn\ny\n1\n1";
        LeaderCard leader = testMap.get("Lorenzo Dè Medici");
        LeaderCard leader2 = testMap.get("Francesco Sforza");
        LeaderCard leader3 = testMap.get("Filippo Brunelleschi");
        LeaderCard leader4 = testMap.get("Federico Da Montafeltro");
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
        leader2.activation = true;
        Resources tmp = new Resources.ResBuilder().build().merge(pl.currentRes);
        leader2.apply();
        assertTrue(pl.currentRes.servant < tmp.servant);
    }
}
