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

public class ServerImpl extends UnicastRemoteObject implements Server {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private int playersNum;
    private boolean personalBonusBoards;
    private List<Player> players = new ArrayList<>();

    private ServerImpl() throws IOException {
        super();
    }

    private void startServer() throws IOException {
        ExecutorService executor = Executors.newCachedThreadPool();
        // socket
        int port = Config.Server.socket.get("port").getAsInt();
        InetAddress address = InetAddress.getByName(
            Config.Server.socket.get("bind").getAsString()
        );
        ServerSocket serverSocket = new ServerSocket(port, 0, address);
        log.info("ServerImpl socket ready on port: " + port);
        // RMI
        port = Config.Server.rmi.get("port").getAsInt();
        LocateRegistry.createRegistry(port);
        Registry reg = LocateRegistry.getRegistry(port);
        reg.rebind("Lorenzo", this);
        log.info("ServerImpl ready");
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                log.info("new SocketClient connection");
                this.addPlayer(socket);
           } catch (IOException e) {
                log.log(Level.SEVERE, e.getMessage(), e);
                throw e;
            }
            if (this.players.size() == this.playersNum) {
                break;
            }
        }
        executor.submit(new Game(this.players, this.personalBonusBoards));
        executor.shutdown();
        serverSocket.close();
    }

    @Override
    public synchronized void addPlayer(
        String name, String colour, RmiClient rmiClient
    ) throws RemoteException {
        Player player = new Player(name, colour, rmiClient);
        this.players.add(player);
        if (this.players.size() == 1) {
            this.firstPlayer(player);
        }
    }

    public synchronized void addPlayer(Socket socket) throws IOException {
        Scanner socketIn = new Scanner(socket.getInputStream());
        String name = socketIn.nextLine();
        String colour = socketIn.nextLine();
        Player player = new Player(name, colour, socket);
        this.players.add(player);
        if (this.players.size() == 1) {
            this.firstPlayer(player);
        }

    }

    private void firstPlayer(Player pl) {
        pl.sOut("It seems you're the first player! :)");
        pl.sOut("You get to choose how this game will be played.");
        pl.sOut("How many players?");
        this.playersNum = pl.sInPrompt(1, 4);
        pl.sOut("Same or different bonus boards?");
        String bonusBoard = pl.sIn();
        if ("same".equalsIgnoreCase(bonusBoard)) {
            this.personalBonusBoards = false;
        } else {
            this.personalBonusBoards = true;
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
