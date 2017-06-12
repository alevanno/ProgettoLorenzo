package it.polimi.ingsw.progettolorenzo.core;

import it.polimi.ingsw.progettolorenzo.client.RmiClient;

import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class PlayerIO {
    public abstract String sIn();
    public abstract int sInPrompt(int m, int n);
    public abstract boolean sInPromptConf();
    public abstract void sOut(String s);
}

class PlayerIOSocket extends PlayerIO {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private Socket socket;
    private Scanner socketIn;
    private PrintWriter socketOut;

    public PlayerIOSocket(Socket socket) {
        this.socket = socket;
    }

    private void sInInit() {
        try {
            if (this.socketIn == null) {
                this.socketIn = new Scanner(new
                    BufferedReader(new
                    InputStreamReader(this.socket.getInputStream())));
            }
        } catch (IOException e) {
            // FIXME handle this better
            log.log(Level.WARNING, e.getMessage(), e);
        }
    }

    @Override
    public String sIn() {
        this.sInInit();
        return this.socketIn.nextLine();
    }

    @Override
    public int sInPrompt(int minValue, int maxValue) {
        this.sInInit();
        int choice;

        do {
            sOut("Input an int between " + minValue + " and " + maxValue);
            while (!this.socketIn.hasNextInt()) {
                this.socketIn.nextLine();
                this.sOut("Please input an int");
            }
            choice = this.socketIn.nextInt();
            this.socketIn.nextLine();
        } while (choice < minValue || choice > (maxValue));
        return choice;
    }

    @Override
    public boolean sInPromptConf() {
        this.sInInit();
        String choice;

        do {
            sOut("Input 'y' (yes) or 'n' (no)");
            choice = this.socketIn.next().substring(0,1);

        } while (!"y".equalsIgnoreCase(choice) && !"n".equalsIgnoreCase(choice));
        if ("y".equalsIgnoreCase(choice)) { return true; }
        if ("n".equalsIgnoreCase(choice)) { return false; }
        return false;
    }

    @Override
    public void sOut(String s) {
        try {
            if (this.socketOut == null) {
                this.socketOut = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(
                        this.socket.getOutputStream())));
            }
        } catch (IOException e) {
            // FIXME handle this better
            log.log(Level.WARNING, e.getMessage(), e);
        }
        this.socketOut.println(s);
        this.socketOut.flush();
    }
}

class PlayerIORMI extends PlayerIO {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private RmiClient rmi;

    public PlayerIORMI(RmiClient rmi) {
        this.rmi = rmi;
    }

    @Override
    public String sIn() {
        return "";  // TODO
    }

    @Override
    public int sInPrompt(int minValue, int maxValue) {
        try {
            return this.rmi.sInPrompt(minValue, maxValue);
        } catch (RemoteException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return 0;  // FIXME ...
        }
    }

    @Override
    public boolean sInPromptConf() {
        try {
            return this.rmi.sInPromptConf();
        } catch (RemoteException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return false;  // FIXME ...
        }
    }

    @Override
    public void sOut(String s) {
        try {
            this.rmi.sOut(s);
        } catch (RemoteException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
