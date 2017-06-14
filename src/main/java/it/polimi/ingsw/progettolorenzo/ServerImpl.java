package it.polimi.ingsw.progettolorenzo;

import it.polimi.ingsw.progettolorenzo.client.RmiClient;
import it.polimi.ingsw.progettolorenzo.core.Player;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

class ConnectionService implements Runnable {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private final ServerImpl server;

    public ConnectionService(ServerImpl server) {
        this.server = server;
    }

    @Override
    public void run() {
        try {
            this.runC();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private void runC() throws IOException {
        // RMI
        int port = Config.Server.rmi.get("port").getAsInt();
        LocateRegistry.createRegistry(port);
        Registry reg = LocateRegistry.getRegistry(port);
        reg.rebind("Lorenzo", this.server);
        log.info("RMI server ready");

        // socket
        port = Config.Server.socket.get("port").getAsInt();
        InetAddress address = InetAddress.getByName(
            Config.Server.socket.get("bind").getAsString()
        );
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port, 0, address);
            log.info("Socket server ready on " + address + ", port: " + port);
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    log.info("new SocketClient connection");
                    server.addPlayer(socket);
                } catch (IOException e) {
                    log.log(Level.SEVERE, e.getMessage(), e);
                    throw e;
                }
            }
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
    }

}

public class ServerImpl extends UnicastRemoteObject implements Server {
    private transient final Logger log = Logger.getLogger(this.getClass().getName());
    private int playersNum;
    private boolean personalBonusBoards;
    private boolean leaderOn;
    private transient List<Player> players = new ArrayList<>();

    private ServerImpl() throws IOException {
        super();
    }

    private void startServer() throws IOException {
        ExecutorService games = Executors.newCachedThreadPool();
        ExecutorService connections = Executors.newSingleThreadExecutor();
        connections.submit(new ConnectionService(this));
        while (true) {
            synchronized (this.players) {
                try {
                    log.warning("players waiting…");
                    this.players.wait();
                    games.submit(
                        new Game(this.players, this.personalBonusBoards,
                                this.leaderOn)
                    );
                    games.shutdown();  // TODO support multiple games
                    break;
                } catch (InterruptedException e) {
                    log.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }

    }

    private void addPlayer(Player player) {
        synchronized (this.players) {
            this.players.add(player);
            if (this.players.size() == 1) {
                this.firstPlayer(player);
            }
            if (this.players.size() == this.playersNum) {
                this.players.notify();
            }
        }
    }

    @Override
    public void addPlayer(
        String name, String colour, RmiClient rmiClient
    ) throws RemoteException {
        Player player = new Player(name, colour, rmiClient);
        this.addPlayer(player);
    }

    public void addPlayer(Socket socket) throws IOException {
        Scanner socketIn = new Scanner(socket.getInputStream());
        String name = socketIn.nextLine();
        String colour = socketIn.nextLine();
        Player player = new Player(name, colour, socket);
        this.addPlayer(player);
    }

    private void firstPlayer(Player pl) {
        pl.sOut("It seems you're the first player! :)");
        pl.sOut("You get to choose how this game will be played.");
        pl.sOut("How many players?");
        this.playersNum = pl.sInPrompt(1, 4);
        pl.sOut("Basic or advanced rules? (LeaderCards and " +
                "different bonus board)");
        String bonusBoard = pl.sIn();
        if ("advanced".equalsIgnoreCase(bonusBoard)) {
            this.personalBonusBoards = true;
            this.leaderOn = true;
        } else {
            this.personalBonusBoards = false;
            this.leaderOn = false;
        }
        log.info(String.format("Game settings: %d players, %s boards",
            this.playersNum, this.personalBonusBoards));
    }

    public static void main(String[] args) {
        Logger log = Logger.getLogger(ServerImpl.class.getName());
        MyLogger.setup();
        // starts the server
        try {
            ServerImpl server = new ServerImpl();
            server.startServer();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
