package it.polimi.ingsw.progettolorenzo.core.player;

import java.io.*;
import java.util.Scanner;

public class PlayerIOLocal implements PlayerIO {
    private Scanner in = new Scanner(System.in);

    @Override
    public String sIn() {
        return this.in.nextLine();
    }

    @Override
    public int sInPrompt(int minValue, int maxValue) {
        return PlayerIO.super.sInPrompt(minValue, maxValue, in);
    }

    @Override
    public boolean sInPromptConf() {
        return PlayerIO.super.sInPromptConf(in);
    }

    @Override
    public void sOut(String s) {
        System.out.println(s);
    }

    public void setIn(String s) {
        InputStream stream = new ByteArrayInputStream(s.getBytes());

        this.in = new Scanner(new BufferedReader(new InputStreamReader(stream)));
    }
}