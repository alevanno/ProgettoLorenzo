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
        //test.apply(player);
        System.out.println(player.currentRes);
    }

}