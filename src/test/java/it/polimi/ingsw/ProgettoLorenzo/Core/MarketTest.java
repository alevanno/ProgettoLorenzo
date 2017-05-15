package it.polimi.ingsw.ProgettoLorenzo.Core;

import java.io.FileNotFoundException;

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
        FamilyMember fam = new FamilyMember(new Player("Pino", "Red"),1,"Red");
        market.booths.get(0).claimSpace(fam);
        assertNotNull(market.booths.get(0).getFamMember());
    }

}
