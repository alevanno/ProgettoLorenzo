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

    @Test
    public void birth1() {
        testList.addAll( Arrays.asList(
                new FrancescoSforza(pl), new LorenzoDeMedici(pl)));
        assertEquals(2, testList.size());
    }

    @Test
    public void leaderUtilsTest(){
        LeaderCard leader = new LorenzoDeMedici(pl);
        Resources cost = new Resources.ResBuilder().stone(leader.activationCost.get(0)).build();
        assertFalse(LeaderUtils.checkCostResSatisfaction(pl, cost));
        assertFalse(LeaderUtils.checkCardTypeSatisfaction(pl, leader.types.get(0), leader.activationCost.get(0)));
    }

}
