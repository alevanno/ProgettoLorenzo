package it.polimi.ingsw.progettolorenzo;

import com.google.gson.JsonObject;
import it.polimi.ingsw.progettolorenzo.core.Utils;

public class Config {
    private static final JsonObject allConf = Utils.getJsonObject("settings.json");
    public static final JsonObject server = allConf.get("server").getAsJsonObject();
    public static final JsonObject client = allConf.get("client").getAsJsonObject();

    private Config() {
        throw new IllegalStateException("Utility class");
    }

    public static class Server {
        public static final JsonObject socket = server.get("socket").getAsJsonObject();
    }

    public static class Client {
        public static final JsonObject socket = client.get("socket").getAsJsonObject();
    }
}
