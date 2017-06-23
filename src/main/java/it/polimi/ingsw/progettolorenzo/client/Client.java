package it.polimi.ingsw.progettolorenzo.client;

import it.polimi.ingsw.progettolorenzo.Config;
import it.polimi.ingsw.progettolorenzo.MyLogger;
import it.polimi.ingsw.progettolorenzo.client.inf.Interface;
import it.polimi.ingsw.progettolorenzo.client.inf.CliInterface;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    public static void main(String[] args) throws IOException {
        final Logger log = Logger.getLogger(Client.class.getName());
        MyLogger.setup();
        Interface c;
        switch (Config.client.get("interface").getAsString()) {
            case "cli":
                c = new CliInterface();
                break;
            default:
                throw new IllegalArgumentException("client.interface variable not valid");
        }

        String name = c.readLine("Insert player name: ");
        boolean ok = false;
        List<String> colourList = Arrays.asList("Blue", "Red", "Yellow", "Green", "Brown", "Violet");
        String colour = "";
        while (!ok) {
            c.printLine("You can choose between: " + colourList.toString());
            colour = c.readLine("Please choose your colour: ");
            if (colourList.contains(colour)) {
                ok = true;
            } else {
                c.printLine("Please choose a valid colour!");
            }
        }

        String mode = Config.client.get("mode").getAsString();
        if ("ask".equals(mode)) {
            while (!"rmi".equals(mode) &&
                   !"socket".equals(mode) &&
                   !"local".equals(mode)) {
                mode = c.readLine("RMI or socket? ").replace("\n", "");
            }
        }
        ClientInterface client;
        switch (mode) {
            case "rmi":
                client = new RmiClientImpl(name, colour, c);
                break;
            case "socket":
                client = new SocketClient(name, colour, c);
                break;
            case "local":
                client = new LocalSingleClient(name, colour);
                break;
            default:
                throw new IllegalArgumentException("client.mode variable not valid");
        }

        try {
            client.startClient();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            client.endClient();
            System.exit(1);
        }
    }
}
