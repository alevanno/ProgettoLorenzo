package it.polimi.ingsw.ProgettoLorenzo;

import com.google.gson.*;
import it.polimi.ingsw.ProgettoLorenzo.Core.*;

public class Foo {

    public static void main(String[] args) {
        JsonArray data = Utils.getJsonArray("cards.json");
        Card test = new Card(data.get(0).getAsJsonObject());
        Player p1 = new Player("Ciccio", "Red");
        JsonArray databt = Utils.getJsonArray("bonusTile.json");
        BonusTile testbt = new BonusTile(databt.get(0).getAsJsonObject());
        p1.setBonusTile(testbt);
        System.out.println("foo " + p1.bonusT.getHarvestRes().toString());

        p1.addCard(test);
        int val = 5;
        Harvest h = new Harvest();
        h.harv(p1, val);
    }
        /*ClassLoader classLoader = Foo.class.getClassLoader();
        String filename = classLoader.getResource("cards.json").getFile();
        JsonArray data = new JsonParser().parse(new FileReader(filename))
                .getAsJsonArray();
        Card test = new Card(data.get(0).getAsJsonObject());
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
        Deck tmpList = new Deck();
        tmpList.add(test);
        tmpList.add(test);
        tmpList.add(test);
        tmpList.add(test);
        Tower tower = new Tower(1, tmpList);
        Floor testFloor = tower.getFloors().get(0);
        testFloor.claimFloor(player.getAvailableFamMembers().get(2));
        testFloor.setPlayer(player);
        testFloor.apply();
        System.out.println(player.currentRes);

    }*/


}
