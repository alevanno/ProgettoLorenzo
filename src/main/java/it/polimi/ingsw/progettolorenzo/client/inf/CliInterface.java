package it.polimi.ingsw.progettolorenzo.client.inf;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import it.polimi.ingsw.progettolorenzo.core.Resources;

import java.io.Console;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

interface CliIO {
    void printLine(String format, Object... args);
    String readLine();
    String readLine(String format, Object... args);
}

class CliIOConsole implements CliIO {
    private Console console = System.console();

    @Override
    public void printLine(String format, Object... args) {
        console.format(format+"%n", args);
        console.flush();
    }

    @Override
    public String readLine() {
        return console.readLine();
    }

    @Override
    public String readLine(String format, Object... args) {
        return System.console().readLine(format, args);
    }

}

class CliIoBase implements CliIO {
    private Scanner scanner = new Scanner(System.in);

    @Override
    public void printLine(String format, Object... args) {
        System.out.println(String.format(format, args));
    }

    @Override
    public String readLine() {
        return scanner.nextLine();
    }

    @Override
    public String readLine(String format, Object... args) {
        this.printLine(format, args);
        return this.readLine();
    }
}

public class CliInterface implements Interface {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private CliIO io;

    public CliInterface() {
        if (System.console() != null) {
            log.fine("Using a Console object for I/O");
            this.io = new CliIOConsole();
        } else {
            log.fine("Using basic I/O");
            this.io = new CliIoBase();
        }
    }

    @Override
    public void printLine(String format, Object... args) {
        if (format.startsWith("☃")) {
            this.formatBoard(format.substring(1));
            return;
        }
        this.io.printLine(format, args);
    }

    @Override
    public String readLine() {
        return this.io.readLine();
    }

    @Override
    public String readLine(String format, Object... args) {
        return this.io.readLine(format, args);
    }

    private void formatBoard(String input) {
        JsonObject boardIn = new Gson().fromJson(input, JsonObject.class);
        printLine("The board as it is now:");
        try {
            Screen screen = new DefaultTerminalFactory().setInitialTerminalSize(new TerminalSize(135, 50)).createScreen();
            WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);
            screen.startScreen();
            BoardWindow win = new BoardWindow(boardIn);
            textGUI.addWindow(win);
            win.waitUntilClosed();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }

    }

    private class BoardWindow extends BasicWindow {
        Panel mainPanel = new Panel();
        public BoardWindow(JsonObject input) {
            super("Board");
            JsonObject boardJ = input.get("board").getAsJsonObject();
            this.mainPanel.setLayoutManager(new LinearLayout(Direction
                    .HORIZONTAL));

            this.setComponent(mainPanel.withBorder(Borders.doubleLineBevel
                    ("Towers")));
            // TODO handle also other elements of the board
            boardJ.get("towers").getAsJsonArray().forEach(
                t -> addTower(t.getAsJsonObject())
            );
        }

        public void addTower(JsonObject input) {
            Panel panel = new Panel();
            this.mainPanel.addComponent(
                    panel.withBorder(Borders.doubleLineBevel(
                            "tower"
                    ))
            );
            panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
            Panel typeLabelPanelBox = new Panel();
            typeLabelPanelBox.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
            typeLabelPanelBox.addComponent(new Label("Type"));
            typeLabelPanelBox.addComponent(new Label(input.get("type").getAsString()));
            input.get("floors").getAsJsonArray().forEach(
                    f -> panel.addComponent(addFloor(f.getAsJsonObject()))
            );
        }

        private Border addFloor(JsonObject input) {
            Panel panel = new Panel();
            panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
            panel.addComponent(Panels.horizontal(
                    new Label("Value"),
                    new Label(input.get("value").getAsString())
            ));
            panel.addComponent(Panels.horizontal(
                    new Label("bonus"),
                    new Label(Resources.fromJson(input.get("bonus")).toString())
            ));
            JsonElement card;
            if ((card = input.get("card")) != null) {
                panel.addComponent(addCard(card.getAsJsonObject()));
            }
            JsonElement famMember;
            if ((famMember = input.get("famMember")) != null) {
                panel.addComponent(addFamMember(famMember.getAsJsonObject()));
            }

            return panel.withBorder(Borders.singleLineReverseBevel("floor"));
        }

        private Border addCard(JsonObject input) {
            Panel panel = new Panel();
            panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
            panel.addComponent(Panels.horizontal(
                new Label("Period:"),
                new Label(input.get("period").getAsString())
            ));
            panel.addComponent(Panels.horizontal(
                new Label("Type:"),
                new Label(input.get("type").getAsString())
            ));
            panel.addComponent(new Label("Cost:"));
            for( JsonElement x : input.get("cost").getAsJsonArray()) {
                panel.addComponent(
                    new Label(Resources.fromJson(x).toString())
                );
            }
            return panel.withBorder(Borders.singleLine(
                "Card: " + input.get("name").getAsString()
            ));
        }

        private Border addFamMember(JsonObject input) {
            Panel panel = new Panel();
            panel.setLayoutManager(new LinearLayout((Direction.VERTICAL)));
            panel.addComponent(Panels.horizontal(
                    new Label("Fam colour:"),
                    new Label(input.get("colour").getAsString())
            ));
            return panel.withBorder(Borders.singleLineReverseBevel(
                    "Occupant: " + input.get("parent").getAsString()
            ));
        }
    }
}
