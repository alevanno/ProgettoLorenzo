package it.polimi.ingsw.ProgettoLorenzo.Core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

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

    public static String getResourceFilePath(String filename) {
        ClassLoader classLoader = Utils.class.getClassLoader();
        return classLoader.getResource(filename).getFile();
    }

    public static JsonObject getJsonObject(String filename) {
        JsonObject data = new JsonObject();
        try (FileReader f = new FileReader(getResourceFilePath(filename))) {
            data = new JsonParser().parse(f).getAsJsonObject();
        } catch (IOException e) {
            System.err.println(String.format("File %s not found", filename));
        }
        return data;
    }

    public static JsonArray getJsonArray(String filename) {
        JsonArray data = new JsonArray();
        try (FileReader f = new FileReader(getResourceFilePath(filename))) {
            data = new JsonParser().parse(f).getAsJsonArray();
        } catch (IOException e) {
            System.err.println(String.format("File %s not found", filename));
        }
        return data;
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
}
