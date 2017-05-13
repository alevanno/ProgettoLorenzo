package it.polimi.ingsw.ProgettoLorenzo.Core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Utils {
    public static int returnZeroIfMissing(JsonObject src, String key) {
        // .get() returns null if the key is missing, and would cause
        // getAsInt to throw a NullPointerException
        JsonElement tmp = src.get(key);
        if (tmp != null) {
            return tmp.getAsInt();
        } else {
            return 0;
        }
    }
}
