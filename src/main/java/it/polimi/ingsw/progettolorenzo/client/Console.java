package it.polimi.ingsw.progettolorenzo.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.progettolorenzo.core.Card;
import it.polimi.ingsw.progettolorenzo.core.Resources;

import java.util.Map;

public class Console {
    public static void formatBoard(String input) {
        JsonObject boardIn = new Gson().fromJson(input, JsonObject.class);
        Client.printLine("The board as it is now:");
        for (Map.Entry<String, JsonElement> entry : boardIn.entrySet()) {
            switch (entry.getKey()) {
                case "towers":
                    Console.formatTowers(entry.getValue().getAsJsonArray());
                    break;
            }
        }
    }

    public static void formatTowers(JsonArray input) {
        input.forEach(t -> Console.formatTower(t.getAsJsonObject()));
    }

    public static void formatTower(JsonObject input) {
        Client.printLine("===============================================");
        Client.printLine("Tower type: %s", input.get("type").getAsString());
        input.get("floors").getAsJsonArray().forEach(
                f -> Console.formatFloor(f.getAsJsonObject())
        );
        Client.printLine("===============================================");
    }

    public static void formatFloor(JsonObject input) {
        Client.printLine("-----------------------------------------------");
        Client.printLine("Floor:");
        Client.printLine("value: %d", input.get("value").getAsInt());
        JsonElement bonus = input.get("bonus");
        System.out.println("not there yet");
        if (bonus != null) {
            Client.printLine("bonus: %s", Resources.fromJson(bonus).toString());
        }
        System.out.println("I got here");
        JsonElement card;
        if ((card = input.get("card")) != null) {
            Client.printLine("card: %s", new Card(card.getAsJsonObject()).toString());
        }
        Client.printLine("-----------------------------------------------");
    }
}
