package it.polimi.ingsw.progettolorenzo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import it.polimi.ingsw.progettolorenzo.core.*;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

public class Game implements Runnable {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private Board board;
    private List<String> types = Arrays.asList(
            "territories", "buildings", "characters", "ventures");
    private HashMap<String, Deck> unhandledCards = new HashMap<>();
    private List<Player> players = new ArrayList<>();
    private int currTurn = 0;

    private int availableSlot = 0;

    public Game(List<Player> listPlayers) {
        MyLogger.setup();
        log.info("Starting the game...");
        this.players = listPlayers;
        for(Player p : players) {
            System.out.println(p.currentRes);
        }
    }

    public void run() {
        // init players
        this.initPlayers();

        // init cards
        this.loadCards();

        // actually start the game
        this.turn();
    }

    private void initPlayers() {
        int i = 5;
        for (Player p: this.players) {
            p.currentRes = (p.currentRes.merge(new Resources.ResBuilder().coin(i).build()));
            log.fine("Player " + p.playerName + " obtained " + i + " starting coin");
            i++;
        }
    }

    public Player getAvailablePlayer() {
        Player p = players.get(availableSlot);
        availableSlot++;
        return p;

    }

    private void resetBoard(int period) {
        Deck deck = new Deck();
        this.unhandledCards.forEach((n, d) -> {
            deck.addAll(
                    StreamSupport.stream(d.spliterator(), false)
                            .filter(c -> c.cardPeriod == period)
                            .limit(4) // FIXME make configurable before Board() is istantiated
                            .collect(Deck::new, Deck::add, Deck::addAll)
            );
        });
        log.finer(String.format(
                "Collected %d cards to give away", deck.size()));
        this.board = new Board(deck);
    }

    private void loadCards() {
        JsonArray cardsData = Utils.getJsonArray("cards.json");

        for (String i : types) {
            this.unhandledCards.put(i, new Deck());
        }
        for (JsonElement c : cardsData) {
            Card card = new Card(c.getAsJsonObject());
            this.unhandledCards.get(card.cardType).add(card);
        }
        StringBuilder sb = new StringBuilder();
        this.unhandledCards.forEach((n, d) -> sb.append(n + "=" + d.size() + " "));
        log.fine(String.format("Loaded %scards", sb));
    }

    private void turn() { //which is comprised of 4 rounds
        List<Player> playersOrder = new ArrayList<>(players);
        this.resetBoard(currTurn / 2 + 1);
        Map<String, Integer> famValues = new HashMap<>();
        famValues.put("Orange", new Random().nextInt(5) + 1);
        famValues.put("Black", new Random().nextInt(5) + 1);
        famValues.put("White", new Random().nextInt(5) + 1);
        log.fine("Dices thrown");


        for (Player pl : players) {
            pl.famMembersBirth(famValues);
            pl.sOut("Dice thrown!");
            pl.sOut("Values: " + famValues.get("Orange")
                    + ", " + famValues.get("Black") + ", " + famValues.get("White") + " setted to Orange, Black " +
                    "and White Family Member");
        }
        for (int r = 1; r <= 4; r++) {
            this.round(playersOrder);
            currTurn++;
            if (currTurn == 6) {
                this.endgame();
                break;
            }
        }
    }

    private void round(List<Player> playersOrder) {
        // TODO implement other Actions;
        for (Player pl : playersOrder) {
            // giocata con pl passato come parametro
            pl.sOut("Turn " + this.currTurn + ": Player " + pl.playerName + "'s is the next player for this round:");
            while (true) {
                pl.sOut("Which family member do you want to use?: ");
                pl.sOut(pl.displayFamilyMembers());
                Integer famMem = pl.sInI(); //FIXME make me prettier
                pl.sOut("Which action do you want to try?: ");
                String action = pl.sIn();
                if (action.equals("Floor")) {
                    // FIXME this should ask the tower type and handle it
                    pl.sOut("Insert tower number:");
                    int towerNumber  = pl.sInI();
                    pl.sOut("Insert floor number:");
                    int floorNumber = pl.sInI();
                    Floor fl = this.board.towers.get(towerNumber).getFloors()
                            .get(floorNumber);
                    boolean ret = fl.claimFloor(pl.getAvailableFamMembers().get(famMem));
                    if (!ret) {
                        pl.sOut("Action not allowed! Please enter a valid action:");

                    } else {
                        pl.sOut("Action attempted successfully");
                        fl.logActions();
                        fl.apply();
                        pl.sOut(pl.currentRes.toString());
                        break;
                    }
                }
            }
        }
    }

    private void endgameMilitary (List<Player> players) {
        //FIXME this is horrid, but I'm not sure it could be written some other way
        //1st gets 5 victoryP, 2nd gets 2 victoryP, if more than one player is first he gets the prize and the second gets nothing
        players.sort(Comparator.comparing(p -> p.currentRes.militaryPoint));
        int plWithMaxMilitary = 0;
        int MaxMilitary = players.get(0).currentRes.militaryPoint;
        int SecMaxMilitary = 0;
        for (Player p: players) {
            if (p.currentRes.militaryPoint == MaxMilitary) {
                plWithMaxMilitary++;
                p.currentRes.merge(new Resources.ResBuilder().victoryPoint(5).build());
            } else if (p.currentRes.militaryPoint > SecMaxMilitary) {
                SecMaxMilitary = p.currentRes.militaryPoint;
            }
        }
        if (plWithMaxMilitary > 1) {
            for (Player p: players) {
                if (p.currentRes.militaryPoint == SecMaxMilitary) {
                    p.currentRes.merge(new Resources.ResBuilder().victoryPoint(3).build());
                }
            }
        }
    }

    //TODO testing
    private void endgame() {
        List<Integer> territoriesVictory = Arrays.asList(1, 4, 10, 20);
        List<Integer> charactersVictory = Arrays.asList(1, 3, 6, 10, 15, 21);
        endgameMilitary(this.players);
        for (Player pl: players) {
            int countTerritories = 0;
            int countCharacters = 0;
            Resources purpleFinal = new Resources.ResBuilder().build();
            int sumResources = (pl.currentRes.coin + pl.currentRes.servant + pl.currentRes.stone + pl.currentRes.wood);
            for (Card i : pl.listCards()) {
                if (i.cardType.equals("territories")) {
                    countTerritories++;
                }
                if (i.cardType.equals("characters")) {
                    countCharacters++;
                }
                if (i.cardType.equals("ventures")) {
                    purpleFinal.merge(Resources.fromJson(i.permanentEff.get("purpleFinal")));
                }
            }
            pl.currentRes.merge(purpleFinal);
            pl.currentRes.merge(new Resources.ResBuilder().victoryPoint(territoriesVictory.get(countTerritories - 3)).build());
            pl.currentRes.merge(new Resources.ResBuilder().victoryPoint(charactersVictory.get(countCharacters - 1)).build());
            pl.currentRes.merge(new Resources.ResBuilder().victoryPoint(sumResources / 5).build());

            System.out.println(pl.playerName + "scores" + pl.currentRes.victoryPoint + " Victory points");
            System.out.println("Addio, addio, amici addio...");
        }
    }
}
