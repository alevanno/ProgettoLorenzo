package it.polimi.ingsw.progettolorenzo;

import com.google.gson.JsonObject;
import it.polimi.ingsw.progettolorenzo.core.Utils;

public class Config {
    private static final JsonObject allConf = Utils.getJsonObject("settings.json");
    public static final JsonObject server = allConf.get("server").getAsJsonObject();
    public static final JsonObject client = allConf.get("client").getAsJsonObject();
    public static final JsonObject game = allConf.get("game").getAsJsonObject();

    private Config() {
        throw new IllegalStateException("Utility class");
    }

    public static class Server {
        public static final JsonObject socket = server.get("socket").getAsJsonObject();
        public static final JsonObject rmi = server.get("rmi").getAsJsonObject();
        private Server() {
            throw new IllegalStateException("Utility class");
        }
    }

    public static class Client {
        public static final JsonObject socket = client.get("socket").getAsJsonObject();
        public static final JsonObject rmi = client.get("rmi").getAsJsonObject();
        private Client() {
            throw new IllegalStateException("Utility class");
        }
    }

    public static class Game {
        public static final int turnTimeout = game.get("timeout").getAsInt();
        private Game() {
            throw new IllegalStateException("Utility class");
        }
    }
}
