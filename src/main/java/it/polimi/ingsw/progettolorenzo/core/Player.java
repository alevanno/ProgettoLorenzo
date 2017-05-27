package it.polimi.ingsw.progettolorenzo.core;

import com.google.gson.JsonObject;
import it.polimi.ingsw.progettolorenzo.Game;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Player {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    public final String playerName;
    public final String playerColour;
    public final Socket playerSocket;
    public Resources currentRes;  // FIXME make private
    private List<FamilyMember> famMemberList = new ArrayList<>();
    private Deck cards = new Deck();
    private List<JsonObject> excommunications = new ArrayList<>();
    private BonusTile bonusT;
    private Game parentGame;
    private Scanner socketIn;
    private PrintWriter socketOut;


    public Player(String name, String colour, Socket socket) {
        this.playerName = name;
        this.playerColour = colour;
        this.currentRes = new Resources.ResBuilder().servant(3).stone(2).wood(2).build();
        this.playerSocket = socket;
        log.info(String.format(
                "New player: %s (colour: %s, resources: %s)",
                name, colour, this.currentRes));
    }

    private void sInInit() {
        try {
            if (this.socketIn == null) {
                this.socketIn = new Scanner(new
                        BufferedReader(new
                        InputStreamReader(this.playerSocket.getInputStream())));
            }
        } catch (IOException e) {
            // FIXME handle this better
            log.log(Level.WARNING, e.getMessage(), e);
        }
    }

    public String sIn()  {
        this.sInInit();
        return this.socketIn.nextLine();
    }

    public int sInPrompt(int minValue, int maxValue)  {
        this.sInInit();
        int choice;

        do {
            sOut("Input an int between " + minValue + " and " + maxValue);
            while (!this.socketIn.hasNextInt()) {
                this.socketIn.nextInt();
                this.sOut("Please input an int");
            }
            choice = this.socketIn.nextInt();
        } while (choice < minValue || choice > (maxValue));
        return choice;
    }

    public PrintWriter getSocketOut() {
        return socketOut;
    }

    public void sOut(String s) {
        try {
            if (this.socketOut != null) {
                this.socketOut.println(s);
                this.socketOut.flush();
            } else {
                this.socketOut = new PrintWriter(new
                        BufferedWriter(new
                        OutputStreamWriter(this.playerSocket.getOutputStream())));
            }
        } catch (IOException e) {
            // FIXME handle this better
            log.log(Level.WARNING, e.getMessage(), e);
        }
    }

    public void famMembersBirth(Map<String, Integer> famValues) {
        // FIXME - sanely set actionValue
        this.famMemberList.add(
                new FamilyMember(this, famValues.get("Orange"), "Orange"));
        this.famMemberList.add(
                new FamilyMember(this, famValues.get("Black"), "Black"));
        this.famMemberList.add(
                new FamilyMember(this, famValues.get("White"), "White"));
        this.famMemberList.add(
                new FamilyMember(this, 0, "Blank"));
        log.fine("4 family members attached to " + this);
    }

    public BonusTile getBonusT() {
        return bonusT;
    }

    public Game getParentGame() {
        return parentGame;
    }

    public void setBonusTile(BonusTile bt) {
        log.info(String.format("[%s] Set bonus tile %s",
                this, bt));
        this.bonusT = bt;
    }

    public void setExcommunication(JsonObject e, int index) {
        this.excommunications.add(index, e);
    }

    public void setParentGame(Game parentGame) {
        this.parentGame = parentGame;
    }

    public void addCard(Card toadd) {
        this.cards.add(toadd);
    }

    public Deck listCards() {
        return this.cards.listCards();
    }

    protected Card takeCard(int idx) {
        return this.cards.remove(idx);
    }

    public List<FamilyMember> getAvailableFamMembers() {
        return this.famMemberList;
    }

    public String displayFamilyMembers() {
        int i = 1;
        StringBuilder ret = new StringBuilder();
        for (FamilyMember fam : this.famMemberList) {
            ret.append(i + " " + fam.getSkinColor() + " " + fam.getActionValue());
            ret.append(" | ");
            i++;
        }
        return ret.toString();
    }

    protected void takeFamilyMember(FamilyMember famMember) {
        if (!this.famMemberList.remove(famMember)) {
            System.exit(1);
        }
    }

    public String toString() {
        return this.playerName;
    }
}
