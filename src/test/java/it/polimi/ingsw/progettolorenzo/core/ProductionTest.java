package it.polimi.ingsw.progettolorenzo.core;

import com.google.gson.JsonArray;
import org.junit.Test;

import java.net.Socket;
import java.util.Random;

import static org.junit.Assert.*;


public class ProductionTest {
    @Test
    public void claimFamMain() throws Exception {
    }

    @Test
    public void claimFamSec() throws Exception {
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