package it.polimi.ingsw.progettolorenzo.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utils {
    private static final Logger log = Logger.getLogger(Utils.class.getName());

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

    public static String getResourceFilePath(String filename) {
        ClassLoader classLoader = Utils.class.getClassLoader();
        return classLoader.getResource(filename).getFile();
    }

    private static JsonElement getJsonFile(String filename) {
        try (FileReader f = new FileReader(getResourceFilePath(filename))) {
            return new JsonParser().parse(f);
        } catch (FileNotFoundException e) {
            log.severe(String.format("File %s not found", filename));
            log.log(Level.SEVERE, e.getMessage(), e);
            System.exit(1);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            System.exit(1);
        } catch (Exception e) {
            log.severe(String.format("Critical issue while loading %s:",
                    filename));
            log.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }
        return null;
    }
    public static JsonObject getJsonObject(String filename) {
        return getJsonFile(filename).getAsJsonObject();
    }
    public static JsonArray getJsonArray(String filename) {
        return getJsonFile(filename).getAsJsonArray();
    }

    public static int intPrompt(int minValue, int maxValue) {
        int choice;
        do {
            System.out.println("Input an int between " + minValue + " and " + maxValue);
            Scanner in = new Scanner(System.in);
            while (!in.hasNextInt()) {
                in.next();
                System.out.println("Please input an int");
            }
            choice = in.nextInt();
        } while (choice < minValue || choice > (maxValue));
        return choice;
    }

    public static String displayActions() {
        List<String> actions = Arrays.asList(
                "Floor", "Market", "CouncilPalace", "Production",
                "Harvest");
        int i = 1;
        StringBuilder ret = new StringBuilder();
        for (String action : actions) {
            ret.append(i + " " + action);
            ret.append(" | ");
            i++;
        }
        return ret.toString();
    }
}
