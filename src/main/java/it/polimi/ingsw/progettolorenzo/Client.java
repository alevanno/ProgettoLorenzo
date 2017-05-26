package it.polimi.ingsw.progettolorenzo;


import javafx.beans.Observable;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Observer;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
    private final static int PORT = 29999;
    private final static String IP="127.0.0.1";

    public void startClient() throws IOException {
        System.out.print("Insert player name: ");
        Scanner in = new Scanner(System.in);
        String name = in.nextLine();
        // TODO propose choice color list
        System.out.print("Insert player colour: ");
        String colour = in.nextLine();

        Socket socket = new Socket(IP, PORT);
        System.out.println("Connection Established");
        System.out.println("Waiting for players connection....");
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
    public static void main(String[] args) {
        Client client = new Client();

        try {
            client.startClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientInHandler implements Runnable {
    private Scanner socketIn;

    public ClientInHandler(Scanner socketIn) {
        this.socketIn=socketIn;
    }

    public void run() {
        while (true) {
            String line = socketIn.nextLine();
            System.out.println(line);
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
        }
    }

}

