package it.polimi.ingsw.ProgettoLorenzo;

import com.google.gson.*;
import it.polimi.ingsw.ProgettoLorenzo.Core.TerrainCard;

import java.io.FileReader;

public class Foo {
    public static void main(String[] args) throws java.io.FileNotFoundException {
        // FIXME ... find where it looks for the start of the relative path....
        String filename = "D:\\Dropbox\\Projects\\ProgettoLorenzo\\target\\classes\\it\\polimi\\ingsw\\ProgettoLorenzo\\Core\\cards.json";
        JsonArray data = new JsonParser().parse(new FileReader(filename)).getAsJsonArray();
        TerrainCard card = new TerrainCard(data.get(0).getAsJsonObject());
        card.print();
    }
}