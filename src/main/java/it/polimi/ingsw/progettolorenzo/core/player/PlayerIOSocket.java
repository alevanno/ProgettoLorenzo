package it.polimi.ingsw.progettolorenzo.core.player;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PlayerIOSocket implements PlayerIO{
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


    public int sInPrompt(int minValue, int maxValue) {
        this.sInInit();
        return PlayerIO.super.sInPrompt(minValue, maxValue, socketIn);
    }

    public boolean sInPromptConf() {
        this.sInInit();
        return PlayerIO.super.sInPromptConf(socketIn);
    }

    @Override
    public void sOut(String s) {
        try {
            if (this.socketOut == null) {
                this.socketOut = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(
                                this.socket.getOutputStream())));
            }
            this.socketOut.println(s);
            this.socketOut.flush();
        } catch (IOException e) {
            // FIXME handle this better
            log.log(Level.WARNING, e.getMessage(), e);
        }
    }
}
