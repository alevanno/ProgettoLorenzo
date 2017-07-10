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

/**
 * Thread maintaining the socket connection established, and starting the RMI.
 */
class ConnectionService implements Runnable {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private final ServerImpl server;

    /**
     * @param server reference to the actual Server
     */
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

    /**
     * Starts the RMI server and main socket loop (spawning a new thread for
     * every incoming connection).
     * @throws IOException
     */
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

/**
 * Main server class, containing the server entry point
 * {@link ServerImpl#main(String[])}.
 * It takes care of spawning a {@link ConnectionService} to initialize the
 * sockets, and new {@link Game} for every new incoming {@link Player}
 * wanting a new game.
 */
public class ServerImpl extends UnicastRemoteObject implements Server {
    private final transient Logger log = Logger.getLogger(this.getClass().getName());
    /**
     * List referencing all Games ever happened since the server started.
     */
    private final transient List<Game> games = new ArrayList<>();
    /**
     * Thread pool for the ongoing {@list Game}s.
     */
    private final transient ExecutorService gamesExecutor = Executors.newCachedThreadPool();
    /**
     * Thread pool for dealing with players initializations before they join
     * a {@link Game}.
     */
    private final transient ExecutorService tempPlayers = Executors.newCachedThreadPool();

    private ServerImpl() throws IOException {
        super();
    }

    private void startServer() {
        ExecutorService connections = Executors.newSingleThreadExecutor();
        connections.submit(new ConnectionService(this));
    }

    /**
     * This class contains method for early handling of {@link Player}'s
     * connections, before they join a {@link Game}.
     */
    private class AddPlayer implements Runnable {
        private Player pl;

        /**
         * @param player reference to the new {@link Player}.
         */
        protected AddPlayer(Player player) {
            log.fine("Creating a new player (new thread)");
            this.pl = player;
        }

        /**
         * Main method of the thread, doing:
         * <p><ul>
         *     <li> if there are no {@link Game}s running, just start a new
         *     one and attach the player, without prompting anything;
         *     <li> if instead there are known {@link Game}s already, print a
         *     list of them;
         *     <li> ask the client whether they want to connect to a running
         *     game or starting a new one;
         * </ul></p>
         *
         * To instantiate a new {@link Game} the {@link #firstPlayer()} method
         * is used to perform the initial configuration, and the returned
         * {@link Game} is then added to {@link #games} and the thread pool
         * {@link #gamesExecutor} to be run later.
         *
         * @see #firstPlayer()
         */
        @Override
        public void run() {
            synchronized (games) {
                if (games.isEmpty()) {
                    pl.sOut("No currently running games, starting a new one…");
                    Game g = this.firstPlayer();
                    games.add(g);
                    gamesExecutor.submit(g);
                    return;
                }
                pl.sOut("Available games:");
                games.forEach(x -> pl.sOut("  + " + x.toString()));
                pl.sOut("Do you want to join one of them? Otherwise a new game will be created");
                if (pl.sInPromptConf()) {
                    if (games.size() == 1) {
                        try {
                            games.get(0).addPlayer(pl);
                        } catch (GameAlreadyStartedException e) {
                            pl.sOut("Fatal Error.  The game already started. " +
                                "Try to connect again.");
                            log.log(Level.SEVERE, e.getMessage(), e);
                        }
                    } else {
                        pl.sOut("Which game do you want to join?");
                        try {
                            games.get(pl.sInPrompt(1, games.size()) - 1)
                                .addPlayer(pl);
                        } catch (GameAlreadyStartedException e) {
                            pl.sOut("Fatal Error.  The game you want to join " +
                                "already started.  Try connecting again");
                            log.log(Level.SEVERE, e.getMessage(), e);
                        }
                    }
                } else {
                    Game g = this.firstPlayer();
                    games.add(g);
                    gamesExecutor.submit(g);
                }
            }
        }

        /**
         * Asks the player some questions and then instance a new
         * {@link Game} with the selected settings.
         * @return the newly instantiated {@link Game}
         */
        private Game firstPlayer() {
            pl.sOut("It seems you're the first player! :)");
            pl.sOut("You get to choose how this game will be played.");
            pl.sOut("How many players?");
            int maxplayers = pl.sInPrompt(1, 4);
            boolean personalBonusBoards = false;
            boolean leaderOn = false;
            switch(Config.game.get("rules").getAsString()) {
                case "basic":
                    break;
                case "advanced":
                    personalBonusBoards = true;
                    leaderOn = true;
                    break;
                case "ask":
                    pl.sOut("Basic or advanced rules? (LeaderCards and " +
                        "different bonus board)");
                    String bonusBoard = pl.sIn();
                    if ("advanced".equalsIgnoreCase(bonusBoard)) {
                        personalBonusBoards = true;
                        leaderOn = true;
                    }
                    break;
                default:
                    log.severe("game.rules setting not valid");
            }
            log.info(String.format(
                "Game settings: %d players, %s boards, %s leader cards",
                maxplayers, personalBonusBoards, leaderOn
            ));
            return new Game(pl, maxplayers, personalBonusBoards, leaderOn);
        }
    }

    /**
     * Adds a new {@link Player} using RMI, it's called through RMI directly
     * by the client.
     * @see AddPlayer#addPlayer(String, String, RmiClient)
     * @param name name of the Player
     * @param colour colour of the Player
     * @param rmiClient reference to the client's RMI endpoint
     * @throws RemoteException
     */
    @Override
    public void addPlayer(
        String name, String colour, RmiClient rmiClient
    ) throws RemoteException {
        Player player = new Player(name, colour, rmiClient);
        this.tempPlayers.submit(new AddPlayer(player));
    }

    /**
     * Adds a new {@link Player} directly using a socket, called by the
     * {@link ConnectionService} for every accepted incoming connection.
     * It will receive the Player's name and colour by the client as first
     * strings after the connection acceptance, instance a new
     * {@link Player} and then starts a new thread for actually handling it.
     * @see AddPlayer#addPlayer(Socket)
     * @param socket reference to the socket linked to the client
     * @throws IOException
     */
    public void addPlayer(Socket socket) throws IOException {
        Scanner socketIn = new Scanner(socket.getInputStream());
        String name = socketIn.nextLine();
        String colour = socketIn.nextLine();
        Player player = new Player(name, colour, socket);
        this.tempPlayers.submit(new AddPlayer(player));
    }

    @Override
    public boolean equals(Object obj) {
        // sonar wants that all subclasses of classes that overrides equals()
        // override equals() themselves.
        // Just return false, two ServerImpl objects are never equal really.
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Program entry point for the server.
     * @param args
     */
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
