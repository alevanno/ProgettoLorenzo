package it.polimi.ingsw.ProgettoLorenzo;

import com.google.gson.*;
import it.polimi.ingsw.ProgettoLorenzo.Core.*;

import java.io.FileReader;

public class Foo {

    public static void main(String[] args) throws java.io.FileNotFoundException {
        ClassLoader classLoader = Foo.class.getClassLoader();
        String filename = classLoader.getResource("cards.json").getFile();
        JsonArray data = new JsonParser().parse(new FileReader(filename)).getAsJsonArray();
        Deck deck = new Deck();
        for (int i = 0; i < data.size(); i++){
            deck.add(new TerrainCard(data.get(i).getAsJsonObject()));
        }
        //System.out.println(deck);
        //deck.shuffleCards();
        //System.out.println(deck);

       // Council council = new Council();
        //System.out.println(council.choosePrivilege());
        Player player = new Player("Pino", "Red");
        FamilyMember fam = new FamilyMember(player, 1, player.playerColour);
        Market market = new Market();
        market.putFamMember(fam);


    }

}