package it.polimi.ingsw.ProgettoLorenzo;

import com.google.gson.*;
import it.polimi.ingsw.ProgettoLorenzo.Core.*;

import java.io.FileReader;

public class Foo {

    public static void main(String[] args) throws java.io.FileNotFoundException {
        ClassLoader classLoader = Foo.class.getClassLoader();
        String filename = classLoader.getResource("cards.json").getFile();
        JsonArray data = new JsonParser().parse(new FileReader(filename))
                .getAsJsonArray();
        Card test = new TerrainCard(data.get(0).getAsJsonObject());
        test.actionBuilder();
        Player player = new Player("Pino", "Red");
        test.setPlayer(player);
        player.famMembersBirth();
        test.apply();
        System.out.println(player.currentRes);
        Council counc = new Council();
        counc.claimSpace(player.getAvailableFamMembers().get(0));
        counc.setPlayer(player);
        counc.apply();
        System.out.println(player.currentRes);
        Market market = new Market();
        MarketBooth booth = market.getBooths().get(3); //try with the MultiPriv
        booth.setPlayer(player);
        booth.claimSpace(player.getAvailableFamMembers().get(1));
        booth.apply();
        System.out.println(player.currentRes);


    }


}
