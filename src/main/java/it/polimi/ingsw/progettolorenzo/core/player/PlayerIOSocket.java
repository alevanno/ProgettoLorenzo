package it.polimi.ingsw.progettolorenzo.core.player;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of {@link PlayerIO} using sockets.
 */
public class PlayerIOSocket implements PlayerIO{
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private Socket socket;
    private Scanner socketIn;
    private PrintWriter socketOut;

    public PlayerIOSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * If not yet opened, create a new buffered scanner through the socket.
     */
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

    /**
     * {@link PlayerIO#sIn()} implementation.
     * @return the next line from socket
     */
    @Override
    public String sIn() {
        this.sInInit();
        return this.socketIn.nextLine();
    }

    /**
     * @see PlayerIO#sInPrompt(int, int, Scanner)
     * @param minValue the minimum accepted value
     * @param maxValue the maximum accepted value
     * @return the inserted value
     */
    public int sInPrompt(int minValue, int maxValue) {
        this.sInInit();
        return PlayerIO.super.sInPrompt(minValue, maxValue, socketIn);
    }

    /**
     * @see PlayerIO#sInPromptConf()
     * @return
     */
    public boolean sInPromptConf() {
        this.sInInit();
        return PlayerIO.super.sInPromptConf(socketIn);
    }

    /**
     * {@link PlayerIO#sOut(String)} implementation.
     * It creates a new PrintWriter from the socket output stream.
     * @param s the string to be sent
     */
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
