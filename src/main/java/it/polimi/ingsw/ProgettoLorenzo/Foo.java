package it.polimi.ingsw.ProgettoLorenzo;

import com.google.gson.*;
import it.polimi.ingsw.ProgettoLorenzo.Core.TerrainCard;

import java.io.FileReader;

public class Foo {

    public static void main(String[] args) throws java.io.FileNotFoundException {
        ClassLoader classLoader = Foo.class.getClassLoader();
        String filename = classLoader.getResource("cards.json").getFile();
        JsonArray data = new JsonParser().parse(new FileReader(filename)).getAsJsonArray();
        TerrainCard card = new TerrainCard(data.get(0).getAsJsonObject());
        card.print();
    }
}