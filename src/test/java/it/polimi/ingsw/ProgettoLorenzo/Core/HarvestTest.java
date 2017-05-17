package it.polimi.ingsw.ProgettoLorenzo.Core;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by Alessandro on 17/05/2017.
 */
public class HarvestTest {
    @Test
    public void testHarv() throws FileNotFoundException {
        ClassLoader classLoader = HarvestTest.class.getClassLoader();
        String filename = classLoader.getResource("cards.json").getFile();
        JsonArray data = new JsonParser().parse(new FileReader(filename))
                .getAsJsonArray();
        Card test = new Card(data.get(0).getAsJsonObject());
        Player p1 = new Player("Ciccio", "Red");

        p1.addCard(test);
        int val = 5;
        Harvest h = new Harvest();
        h.harv(p1, val);
    }
}
