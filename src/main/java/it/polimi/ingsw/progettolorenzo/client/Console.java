package it.polimi.ingsw.progettolorenzo.client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import it.polimi.ingsw.progettolorenzo.core.Resources;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Console {
    private transient final Logger log = Logger.getLogger(this.getClass().getName());

    protected static void printLine(String format, Object... args) {
        if (System.console() != null) {
            System.console().format(format+"%n", args);
            System.console().flush();
        } else {
            System.out.println(String.format(format, args));
        }
    }

    protected static String readLine(String format, Object... args) {
        if (System.console() != null) {
            return System.console().readLine(format, args);
        }
        System.out.print(String.format(format, args));
        return new Scanner(System.in).nextLine();
    }

    public void formatBoard(String input) {
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
            this.mainPanel.setLayoutManager(new LinearLayout(Direction
                    .HORIZONTAL));

            this.setComponent(mainPanel.withBorder(Borders.doubleLineBevel
                    ("Towers")));
            for (Map.Entry<String,JsonElement> entry : input.entrySet()) {
                switch (entry.getKey()) {
                    case "towers":
                        entry.getValue().getAsJsonArray().forEach(
                                t -> this.addTower(t.getAsJsonObject())
                        );
                        break;
                }
            }
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
