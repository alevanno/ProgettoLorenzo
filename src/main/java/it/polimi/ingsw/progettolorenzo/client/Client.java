package it.polimi.ingsw.progettolorenzo.client;

import it.polimi.ingsw.progettolorenzo.MyLogger;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    public static void main(String[] args) throws IOException {
        final Logger log = Logger.getLogger(Client.class.getName());
        MyLogger.setup();
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

        LocalSingleClient client = new LocalSingleClient(name, colour);
        /*SocketClient client = new SocketClient(name, colour);
        RmiClientImpl client = new RmiClientImpl(name, colour);*/

        try {
            client.startClient();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            client.endClient();
            System.exit(1);
        }
    }
}
