package it.polimi.ingsw.progettolorenzo.core;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class PlayerIOLocal implements PlayerIO{
    private Scanner in;

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
        InputStream stream = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
        this.in = new Scanner(new BufferedReader(new InputStreamReader(stream)));
    }
}
