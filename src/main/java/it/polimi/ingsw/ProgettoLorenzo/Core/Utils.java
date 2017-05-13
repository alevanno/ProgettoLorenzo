package it.polimi.ingsw.ProgettoLorenzo.Core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Utils {
    public static int returnZeroIfMissing(JsonObject src, String key) {
        try {
            // .get() returns null if the key is missing, and would cause
            // getAsInt to throw a NullPointerException
            return src.get(key).getAsInt();
        } catch (NullPointerException e) {
            return 0;
        }
    }
}
