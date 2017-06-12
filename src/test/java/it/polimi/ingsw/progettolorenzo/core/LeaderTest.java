package it.polimi.ingsw.progettolorenzo.core;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LeaderTest {
    Socket socket = new Socket();
    Player pl = new Player("LUCA","Blue", socket);
    List<LeaderCard> testList = new ArrayList<>();

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
    }

    @Test
    public void size() {
        assertEquals(19, testList.size());
    }

    @Test
    public void leaderUtilsTest(){
        LeaderCard leader = new LorenzoDeMedici(pl);
        Resources cost = new Resources.ResBuilder().stone(leader.activationCost.get(0)).build();
        assertFalse(LeaderUtils.checkCostResSatisfaction(pl, cost));
        assertFalse(LeaderUtils.checkCardTypeSatisfaction(pl, leader.types.get(0), leader.activationCost.get(0)));
    }

}
