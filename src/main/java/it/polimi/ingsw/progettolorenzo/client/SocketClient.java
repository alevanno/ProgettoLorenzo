package it.polimi.ingsw.progettolorenzo.client;

import it.polimi.ingsw.progettolorenzo.Config;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketClient {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private Socket socket;

    public void startClient() throws IOException {
        String name = Console.readLine("Insert player name: ");
        boolean ok = false;
        List<String> colourList = Arrays.asList("Blue", "Red", "Yellow", "Green", "Brown", "Violet");
        String colour = "";
        while (!ok) {
            Console.printLine("You can choose between: " + colourList.toString());
            colour = Console.readLine("Please choose your colour: ");
            if(colourList.contains(colour)) {
                ok = true;
            } else {
                Console.printLine("Please choose a valid colour!");

            }
        }
        this.socket = new Socket(
                Config.client.get("serverAddress").getAsString(),
                Config.client.get("port").getAsInt()
        );
        Console.printLine("Connection Established");
        Console.printLine("Waiting for players connection....");
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        out.println(name);
        out.println(colour);
        out.flush();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(new InHandler(new BufferedReader(new
                InputStreamReader(socket.getInputStream()))));
        executor.submit(new OutHandler(new
                PrintWriter(new BufferedWriter(new
                OutputStreamWriter(socket.getOutputStream())))));
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
        SocketClient client = new SocketClient();
        try {
            client.startClient();
        } catch (IOException e) {
            client.log.log(Level.SEVERE, e.getMessage(), e);
            client.closeSocket();
            System.exit(1);
        }
    }
}

class InHandler implements Runnable {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private BufferedReader socketIn;

    public InHandler(BufferedReader socketIn) {
        this.socketIn=socketIn;
    }

    public void run() {
        while (true) {
            try {
                String line = socketIn.readLine();
                if (line.equalsIgnoreCase("quit")
                        || line.equalsIgnoreCase("end")) {
                    break;
                }
                switch (line.substring(0, 1)) {
                    case "☃":
                        new Console().formatBoard(line.substring(1));
                        break;
                    default:
                        Console.printLine(line);
                        break;
                }
            } catch (IOException e) {
                log.log(Level.SEVERE, e.getMessage(), e);
                break;
            }
        }
    }
}

class OutHandler implements Runnable {
    private PrintWriter socketOut;

    public OutHandler(PrintWriter socketOut) {
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




