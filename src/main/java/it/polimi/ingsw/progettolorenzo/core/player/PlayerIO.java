package it.polimi.ingsw.progettolorenzo.core.player;

import java.util.Scanner;

/**
 * The PlayerIO represents the IO interface between the
 * {@link it.polimi.ingsw.progettolorenzo.client.Client} and the model.
 */
public interface PlayerIO {
    /**
     * @return string obtained from the client
     */
    String sIn();

    /**
     * Asks the client to input an integer between minValue and maxValue,
     * @param minValue the minimum accepted value
     * @param maxValue the maximum accepted value
     * @return the inserted value
     */
    int sInPrompt(int minValue, int maxValue);

    /**
     * Same as {@link #sInPrompt(int, int)}, but also specifying a Scanner.
     * @see #sInPrompt(int, int)
     * @param minValue the minimum accepted value
     * @param maxValue the maximum accepted value
     * @param in the scanner interface to be used
     * @return the inserted value
     */
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

    /**
     * Asks the client for confirmation.
     * @return the boolean value representing the confirmation
     */
    boolean sInPromptConf();

    /**
     * Same as {@link #sInPromptConf()}, but also specifying a Scanner.
     * @param in the scanner interface to be used
     * @return the boolean value representing the confirmation
     */
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

    /**
     * Send a message to the client.
     * @param s the string to be sent
     */
    void sOut(String s);
}
