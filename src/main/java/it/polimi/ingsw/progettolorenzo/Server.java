package it.polimi.ingsw.progettolorenzo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final static int PORT = 29999;

    public void startServer() throws IOException {
        ExecutorService executor = Executors.newCachedThreadPool();
        ServerSocket serverSocket = new ServerSocket(PORT); System.out.println("Server socket ready on port: " + PORT);
        System.out.println("Server ready");
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                //executor.submit(new ClientHandler(socket));
            } catch (IOException e) {
                break;
            }
        }
        executor.shutdown();
        serverSocket.close();
    }

    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
