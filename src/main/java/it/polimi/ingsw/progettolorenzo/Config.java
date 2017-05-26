package it.polimi.ingsw.progettolorenzo;

import com.google.gson.JsonObject;
import it.polimi.ingsw.progettolorenzo.core.Utils;

public class Config {
    private static final JsonObject allConf = Utils.getJsonObject("settings.json");
    public static final JsonObject server = allConf.get("server").getAsJsonObject();
    public static final JsonObject client = allConf.get("client").getAsJsonObject();
}
