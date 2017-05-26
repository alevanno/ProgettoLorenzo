package it.polimi.ingsw.progettolorenzo;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private static final int PORT = 29999;
    private static String IP;
    private Socket socket;

    private void printLine(String format, Object... args) {
        if (System.console() != null) {
            System.console().format(format, args);
            System.console().flush();
        } else {
            System.out.println(String.format(format, args));
        }
    }

    private String readLine(String format, Object... args) {
        if (System.console() != null) {
            return System.console().readLine(format, args);
        }
        System.out.print(String.format(format, args));
        return new Scanner(System.in).nextLine();
    }

    public void startClient() throws IOException {
        String name = readLine("Insert player name: ");
        // TODO propose choice color list
        printLine("Player colour:");
        printLine("You can choose between: Blue | Red | Yellow | Green");
        String colour = readLine("Please insert your colour: ");
        this.IP = InetAddress.getLocalHost().getHostAddress();
        this.socket = new Socket(IP, PORT);
        printLine("Connection Established");
        printLine("Waiting for players connection....");
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        out.println(name);
        out.println(colour);
        out.flush();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(new ClientInHandler(new
                Scanner(socket.getInputStream())));
        executor.submit(new ClientOutHandler(new
                PrintWriter(socket.getOutputStream())));
    }

    public void closeSocket() {
        if (this.socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                log.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.startClient();
        } catch (IOException e) {
            client.log.log(Level.SEVERE, e.getMessage(), e);
            client.closeSocket();
            System.exit(1);
        }
    }
}

class ClientInHandler implements Runnable {
    private Scanner socketIn;

    public ClientInHandler(Scanner socketIn) {
        this.socketIn=socketIn;
    }
    private final Logger log = Logger.getLogger(this.getClass().getName());

    public void run() {
        while (true) {
            String line = socketIn.nextLine();
            try {
                while (line == null) {
                    line = socketIn.nextLine();
                }
                System.out.println(line);
            } catch (Exception e) {
                log.log(Level.SEVERE, e.getMessage(), e);
                break;
            }
        }
    }
}

class ClientOutHandler implements Runnable {
    private PrintWriter socketOut;

    public ClientOutHandler(PrintWriter socketOut) {
        this.socketOut = socketOut;
    }

    public void run() {
        Scanner stdin = new Scanner(System.in);
        while (true) {

            String inputLine = stdin.nextLine();
            socketOut.println(inputLine);
            socketOut.flush();
            if (inputLine.equals("quit")) {
                break;
            }
        }
    }
}

