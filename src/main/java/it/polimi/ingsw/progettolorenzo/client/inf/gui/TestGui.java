package it.polimi.ingsw.progettolorenzo.client.inf.gui;

import it.polimi.ingsw.progettolorenzo.core.Utils;

import java.io.*;
import java.util.stream.Collectors;

/**
 * test class, reads a file containing a JSON like the one sent by the
 * server and displays the board.
 * Used to debug the gui.
 */
public class TestGui {
    public static void main(String[] args) throws Exception {
        GuiInterface g = new GuiInterface();
        FileInputStream fis = new FileInputStream(
            Utils.getResourceFilePath("testJson")
        );
        InputStreamReader in = new InputStreamReader(fis, "UTF-8");
        String result = new BufferedReader(in)
            .lines().collect(Collectors.joining("\n"));
        fis.close();
        g.printLine(result);
    }

}
