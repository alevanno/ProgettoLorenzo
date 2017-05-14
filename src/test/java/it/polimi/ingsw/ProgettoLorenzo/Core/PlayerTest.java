package it.polimi.ingsw.ProgettoLorenzo.Core;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class PlayerTest {
    @Test
    public void test1() {
        Player p1 = new Player("test", "red");
        p1.familyMembersBirth();
        assertEquals(4, p1.getAvaliableFamilyMembers().size());
    }
}
