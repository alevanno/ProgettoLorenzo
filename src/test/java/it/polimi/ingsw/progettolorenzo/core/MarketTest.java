package it.polimi.ingsw.progettolorenzo.core;

import java.io.FileNotFoundException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MarketTest {

    @Test
    public void marketBoothsBirth() throws FileNotFoundException {
        Market market = new Market();
        assertEquals(4,market.booths.size());
    }

    @Test
    public void familyMParameter() throws FileNotFoundException {
        Market market = new Market();
        Socket socket = new Socket();
        Player p = new Player("Pino", "Red", socket);
        Map<String, Integer> famValues = new HashMap<>();
        famValues.put("Orange", new Random().nextInt(5) + 1);
        famValues.put("Black", new Random().nextInt(5) + 1);
        famValues.put("White", new Random().nextInt(5) + 1);
        p.famMembersBirth(famValues);
        FamilyMember fam = p.getAvailableFamMembers().get(0);
        market.booths.get(0).claimSpace(fam);
        market.booths.get(0).apply();
        assertNotNull(market.booths.get(0).getFamMember());

    }

}
