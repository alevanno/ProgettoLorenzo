package it.polimi.ingsw.progettolorenzo;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
    private final static int PORT = 29999;
    private final static String IP="127.0.0.1";
    private Socket socket;

    public void startClient() throws IOException {
        System.out.print("Insert player name: ");
        Scanner in = new Scanner(System.in);
        String name = in.nextLine();
        // TODO propose choice color list
        System.out.print("Insert player colour: ");
        String colour = in.nextLine();

        this.socket = new Socket(IP, PORT);
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

    public void closeSocket() {
        try {
            socket.close();
        } catch (IOException e){

        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.startClient();
        } catch (IOException e) {
            e.printStackTrace();
            client.closeSocket();
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
            try {
                String line = socketIn.nextLine();
                System.out.println(line);
            } catch (Exception e) {
                e.getStackTrace();
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

