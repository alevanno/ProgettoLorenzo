package it.polimi.ingsw.progettolorenzo;

import it.polimi.ingsw.progettolorenzo.core.Player;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private final static int PORT = 29999;
    private Game game;
    public void startServer() throws IOException {

        ExecutorService executor = Executors.newCachedThreadPool();
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server socket ready on port: " + PORT);
        System.out.println("Server ready");
        List<Player> listPlayers = new ArrayList<>();
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    log.info("new Client connection");
                    Scanner socketIn = new Scanner(socket.getInputStream());
                    String name = socketIn.nextLine();
                    String colour = socketIn.nextLine();
                    Player player = new Player(name, colour, socket);
                    listPlayers.add(player);
                } catch (IOException e) {
                    log.log(Level.SEVERE, e.getMessage(), e);
                    break;
                }
                if (listPlayers.size() == 2) {
                    break;
                }


            }
        executor.submit(new Game(listPlayers));
        executor.shutdown();
        serverSocket.close();
    }

    public static void main(String[] args) {
        MyLogger.setup();
        Server server = new Server();
        // starts the server
        try {
            server.startServer();
        } catch (IOException e) {
            server.log.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
