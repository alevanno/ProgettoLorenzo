package it.polimi.ingsw.progettolorenzo.client;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    public static void main(String[] args) {
        final Logger log = Logger.getLogger(Client.class.getName());
        String name = Console.readLine("Insert player name: ");
        boolean ok = false;
        List<String> colourList = Arrays.asList("Blue", "Red", "Yellow", "Green", "Brown", "Violet");
        String colour = "";
        while (!ok) {
            Console.printLine("You can choose between: " + colourList.toString());
            colour = Console.readLine("Please choose your colour: ");
            if (colourList.contains(colour)) {
                ok = true;
            } else {
                Console.printLine("Please choose a valid colour!");
            }
        }

        SocketClient client = new SocketClient();
        try {
            client.startClient(name, colour);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            client.endClient();
            System.exit(1);
        }
    }
}
