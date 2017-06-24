package it.polimi.ingsw.progettolorenzo.core;

import java.util.Scanner;

interface PlayerIO {
    String sIn();

    int sInPrompt(int minValue, int maxValue);

    default int sInPrompt(int minValue, int maxValue, Scanner in){
        int choice;
        do {
            sOut("Input an int between " + minValue + " and " + maxValue);
            while (!in.hasNextInt()) {
                in.nextLine();
                this.sOut("Please input an int");
            }
            choice = in.nextInt();
            in.nextLine();
        } while (choice < minValue || choice > (maxValue));
        return choice;
    }

    boolean sInPromptConf();

    default boolean sInPromptConf(Scanner in) {
        String choice;
        do {
            sOut("Input 'y' (yes) or 'n' (no)");
            choice = in.next().substring(0,1);
        } while (!"y".equalsIgnoreCase(choice) && !"n".equalsIgnoreCase(choice));
        if ("y".equalsIgnoreCase(choice)) {
            return true;
        }
        if ("n".equalsIgnoreCase(choice)) {
            return false;
        }
        return false;
    }

    void sOut(String s);
}
