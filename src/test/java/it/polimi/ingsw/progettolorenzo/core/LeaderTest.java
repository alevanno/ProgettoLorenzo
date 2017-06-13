package it.polimi.ingsw.progettolorenzo.core;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.net.Socket;
import java.util.*;

public class LeaderTest {
    Socket socket = new Socket();
    Player pl = new Player("LUCA","Blue", socket);
    List<LeaderCard> testList = new ArrayList<>();
    Map<String, LeaderCard> testMap = new HashMap<>();

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
        LeaderCard leader = testMap.get("Lorenzo Dè Medici");
        Resources cost = new Resources.ResBuilder().stone(leader.activationCost.get(0)).build();
        assertFalse(LeaderUtils.checkCostResSatisfaction(pl, cost));
        assertFalse(LeaderUtils.checkMultiType(leader.types, leader.activationCost, pl));
    }

}
