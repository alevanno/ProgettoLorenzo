package it.polimi.ingsw.progettolorenzo;

import it.polimi.ingsw.progettolorenzo.client.RmiClient;
import it.polimi.ingsw.progettolorenzo.core.Player;
import it.polimi.ingsw.progettolorenzo.core.exc.GameAlreadyStartedException;

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
        try (ServerSocket sock = new ServerSocket(port, 0, address)) {
            log.info("Socket server ready on " + address + ", port: " + port);
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket socket = sock.accept();
                    log.info("new SocketClient connection");
                    server.addPlayer(socket);
                } catch (IOException e) {
                    log.log(Level.SEVERE, e.getMessage(), e);
                    throw e;
                }
            }
        }
    }

}

public class ServerImpl extends UnicastRemoteObject implements Server {
    private transient final Logger log = Logger.getLogger(this.getClass().getName());
    private transient List<Game> games = new ArrayList<>();
    private transient ExecutorService gamesExecutor = Executors.newCachedThreadPool();
    private transient ExecutorService tempPlayers = Executors.newCachedThreadPool();

    private ServerImpl() throws IOException {
        super();
    }

    private void startServer() throws IOException {
        ExecutorService connections = Executors.newSingleThreadExecutor();
        connections.submit(new ConnectionService(this));
    }

    private class AddPlayer implements Runnable {
        private Player player;

        protected AddPlayer(Player player) {
            log.fine("Creating a new player (new thread)");
            this.player =  player;
        }

        @Override
        public void run() {
            ServerImpl.this.addPlayer(this.player);
        }
    }

    private void addPlayer(Player pl) {
        synchronized (this.games) {
            if (this.games.size() == 0) {
                pl.sOut("No currently running games, starting a new one…");
                Game g = this.firstPlayer(pl);
                this.games.add(g);
                this.gamesExecutor.submit(g);
                return;
            }
            pl.sOut("Available games:");
            this.games.forEach(x -> pl.sOut("  + " + x.toString()));
            pl.sOut("Do you want to join one of them? Otherwise a new game will be created");
            if (pl.sInPromptConf()) {
                if (this.games.size() == 1) {
                    try {
                        this.games.get(0).addPlayer(pl);
                    } catch (GameAlreadyStartedException e) {
                        pl.sOut("Fatal Error.  The game already started. " +
                            "Try to connect again.");
                        log.log(Level.SEVERE, e.getMessage(), e);
                    }
                } else {
                    pl.sOut("Which game do you want to join?");
                    try {
                        this.games.get(pl.sInPrompt(1, this.games.size()) - 1)
                            .addPlayer(pl);
                    } catch (GameAlreadyStartedException e) {
                        pl.sOut("Fatal Error.  The game you want to join " +
                            "already started.  Try connecting again");
                        log.log(Level.SEVERE, e.getMessage(), e);
                    }
                }
            } else {
                Game g = this.firstPlayer(pl);
                this.games.add(g);
                this.gamesExecutor.submit(g);
            }
        }
    }

    @Override
    public void addPlayer(
        String name, String colour, RmiClient rmiClient
    ) throws RemoteException {
        Player player = new Player(name, colour, rmiClient);
        this.tempPlayers.submit(new AddPlayer(player));
    }

    public void addPlayer(Socket socket) throws IOException {
        Scanner socketIn = new Scanner(socket.getInputStream());
        String name = socketIn.nextLine();
        String colour = socketIn.nextLine();
        Player player = new Player(name, colour, socket);
        this.tempPlayers.submit(new AddPlayer(player));
    }

    private Game firstPlayer(Player pl) {
        pl.sOut("It seems you're the first player! :)");
        pl.sOut("You get to choose how this game will be played.");
        pl.sOut("How many players?");
        int maxplayers = pl.sInPrompt(1, 4);
        pl.sOut("Basic or advanced rules? (LeaderCards and " +
            "different bonus board)");
        String bonusBoard = pl.sIn();
        boolean personalBonusBoards = false;
        boolean leaderOn = false;
        if ("advanced".equalsIgnoreCase(bonusBoard)) {
            personalBonusBoards = true;
            leaderOn = true;
        }
        log.info(String.format(
            "Game settings: %d players, %s boards, %s leader cars",
            maxplayers, personalBonusBoards, leaderOn
        ));
        return new Game(pl, maxplayers, personalBonusBoards, leaderOn);
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
