package it.polimi.ingsw.progettolorenzo.client;

import it.polimi.ingsw.progettolorenzo.Config;
import it.polimi.ingsw.progettolorenzo.client.inf.Interface;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketClient implements ClientInterface {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private final String name;
    private final String colour;
    private Socket socket;
    private Interface c;

    public SocketClient(String name, String colour, Interface inf) {
        this.name = name;
        this.colour = colour;
        this.c = inf;
    }

    @Override
    public void startClient() throws IOException {
       String address = Config.Client.socket.get("serverAddress").getAsString();
        int port = Config.Client.socket.get("port").getAsInt();
        log.info("Connecting to " + address + ":" + port + "…");
        this.socket = new Socket(address, port);
        this.c.printLine("Connection Established");
        this.c.printLine("Waiting for players connection....");
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        out.println(this.name);
        out.println(this.colour);
        out.flush();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future in = executor.submit(new InHandler(new BufferedReader(new
                InputStreamReader(socket.getInputStream())), c));
        executor.submit(new OutHandler(new
                PrintWriter(new BufferedWriter(new
                OutputStreamWriter(socket.getOutputStream()))), c));
        try {
            in.get();
        } catch (ExecutionException | InterruptedException e) {
            System.exit(0);
        }
        System.exit(0);
    }

    @Override
    public void endClient() {
        if (this.socket != null) {
            try {
                this.socket.close();
            } catch (IOException e) {
                log.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

}

class InHandler implements Runnable {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private BufferedReader socketIn;
    private Interface c;

    public InHandler(BufferedReader socketIn, Interface inf) {
        this.socketIn=socketIn;
        this.c= inf;
    }

    public void run() {
        while (true) {
            try {
                String line = socketIn.readLine();
                if (line.equalsIgnoreCase("quit")) {
                    this.c.printLine("You have been disconnected from the server");
                    break;
                }
                this.c.printLine(line);
           } catch (IOException e) {
                log.log(Level.SEVERE, e.getMessage(), e);
                break;
            }
        }
    }
}

class OutHandler implements Runnable {
    private PrintWriter socketOut;
    private Interface c;

    public OutHandler(PrintWriter socketOut, Interface inf) {
        this.socketOut = socketOut;
        this.c = inf;
    }

    public void run() {
        while (true) {
            String inputLine = this.c.readLine();
            socketOut.println(inputLine);
            socketOut.flush();

            if (inputLine.equals("quit")) {
                break;
            }
        }
    }
}