package it.polimi.ingsw.progettolorenzo;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.progettolorenzo.core.*;

import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Game implements Runnable {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private Board board;
    private List<String> types = Arrays.asList(
            "territories", "buildings", "characters", "ventures");
    private HashMap<String, Deck> unhandledCards = new HashMap<>();
    private List<Player> players = new ArrayList<>();
    private int currTurn;
    private Player currPlayer;
    private List<JsonObject> excomms = new ArrayList<>();
    private final boolean personalBonusBoards;

    private int availableSlot = 0;

    public Game(List<Player> listPlayers, boolean personalBonusBoards) {
        MyLogger.setup();
        log.info("Starting the game...");
        listPlayers.forEach(
                p -> p.setParentGame(this)
        );
        this.players = listPlayers;
        this.personalBonusBoards = personalBonusBoards;
    }

    public void run() {
        // init players
        this.initPlayers();

        //assign bonus tile
        this.assignBonusT();

        // init excommunication tiles
        this.initExcomm();

        // init cards
        this.loadCards();

        // starts the game and handles the turns
        for (currTurn = 1; currTurn < 7 ; currTurn++) {
            this.turn();
            if (currTurn % 2 == 0) {
                this.reportToVatican(currTurn);
            }
            if (currTurn == 6) {
                this.endgame();
            }
        }
    }

    private void initPlayers() {
        int initialCoins = 5;
        for (Player p: this.players) {
            p.currentRes = p.currentRes.merge(
                    new Resources.ResBuilder().coin(initialCoins).build());
            log.fine(String.format("Player %s obtained %d starting coins",
                    p.playerName, initialCoins));
            initialCoins++;
        }
    }

    private void initExcomm() {
        JsonArray excommFile = Utils.getJsonArray("excommunication.json");
        for (JsonElement excommP : excommFile) {
            // turn the JsonArray into a Java List, then shuffle it
            Type listType = new TypeToken<List<JsonObject>>() {}.getType();
            List<JsonObject> allExcomms = new Gson().fromJson(
                    excommP.getAsJsonArray(), listType);
            Collections.shuffle(allExcomms);
            // pick the first, random one
            JsonObject excomm = allExcomms.get(0).getAsJsonObject();
            excomms.add(excomm);
            log.info("Excomunication loaded: " + excomm);
        }
    }

    private void resetBoard(int period) {
        Deck deck = new Deck();
        this.unhandledCards.forEach((n, d) ->
            deck.addAll(
                    StreamSupport.stream(d.spliterator(), false)
                            .filter(c -> c.cardPeriod == period)
                            .limit(4) // FIXME make configurable before Board() is istantiated
                            .collect(Deck::new, Deck::add, Deck::addAll)
            )
        );
        log.finer(String.format(
                "Collected %d cards to give away", deck.size()));
        this.board = new Board(deck, this);
    }

    private void assignBonusT() {
        JsonArray allBonuses = Utils.getJsonArray("bonusTile.json");
        if (!this.personalBonusBoards) {
            BonusTile bonusTile = new BonusTile(allBonuses.get(0)
                    .getAsJsonObject());
            for (Player pl : this.players) {
                pl.setBonusTile(bonusTile);
            }
        } else {
            // turn the bonus tiles into a list and take out the default one
            List<BonusTile> bonuses = StreamSupport.stream(
                    allBonuses.spliterator(), true)
                .map(JsonElement::getAsJsonObject)
                .filter(b -> b.get("number").getAsInt() != 0)
                .map(BonusTile::new)
                .collect(Collectors.toList());
            // then shuffle and assign
            Collections.shuffle(bonuses);
            players.forEach(
                    p -> p.setBonusTile(bonuses.remove(bonuses.size()-1))
            );
        }
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
        this.resetBoard((currTurn +1) / 2);
        Map<String, Integer> famValues = new HashMap<>();
        famValues.put("Orange", new Random().nextInt(5) + 1);
        famValues.put("Black", new Random().nextInt(5) + 1);
        famValues.put("White", new Random().nextInt(5) + 1);
        log.fine("Dices thrown");

        for (Player pl : players) {
            pl.famMembersBirth(famValues);
            pl.sOut("Dice thrown!");
            pl.sOut("Values: " + famValues);
        }

        for (int r = 1; r <= 4; r++) {
            this.round(playersOrder);
        }
    }


    public boolean floorAction(FamilyMember famMem) { //TODO this, along with other "moves" maybe should be put in a dedicated class
        Player pl = famMem.getParent();

        pl.sOut("Which card do you want to obtain?: ");
        String cardName = pl.sIn();
        Floor floor = null;
        for (Tower t : this.board.towers) {
            for (Floor fl : t.getFloors()) {
                if (fl.getCard() != null) {
                    if (fl.getCard().cardName.equals(cardName)) {
                        floor = fl;
                        break;
                    }
                    continue;
                }
            }
        }
        if (floor != null) {
            boolean ret = floor.claimFloor(famMem);
            if (!ret) {
                pl.sOut("Action not allowed! Please enter a valid action:");
                return false;

            } else {
                pl.sOut("Action attempted successfully");
                floor.logActions();
                floor.apply();
                pl.sOut(pl.currentRes.toString());
                return true;
            }
        } else {
            pl.sOut("Card " + cardName
                    + " was already taken!: please choose an other action: ");
            return false;
        }
    }


    private void round(List<Player> playersOrder) {
        // TODO implement other Actions;
        for (Player pl : playersOrder) {
            currPlayer = pl;
            pl.sOut("Turn " + this.currTurn + ": Player " + pl.playerName +
                    " is the next player for this round:");
            while (true) {
                pl.sOut("Which family member do you want to use?: ");
                pl.sOut(pl.displayFamilyMembers());
                int famMem = pl.sInPrompt(1,4);
                this.board.displayBoard();

                //FIXME make me prettier
                pl.sOut("Which action do you want to try?: ");
                String action = pl.sIn();
                if ("floor".equalsIgnoreCase(action) && floorAction(pl.getAvailableFamMembers().get(famMem))) {
                    break;
               }
            }
        }
    }

    private void reportToVatican (int currTurn) {
        //FIXME should this be loaded from a Json?
        List<Integer> faithVictory = Arrays.asList(0, 1, 2, 3, 4, 5, 7, 9, 11, 13, 15, 17, 19, 22, 25, 30);
        //TODO
        for (Player pl: players) {
            int period = currTurn/2;
            int plFaithP = pl.currentRes.faithPoint;
            pl.sOut("You have " + plFaithP + " Faith Points. The Church requires " + (period + 2));
            if (plFaithP < period + 2) {
                pl.sOut("You are excommunicated");
                excommunicate(pl, period);
            }
            else {
                pl.sOut("What do you want to do? \n1. Support the Church \n2. Be excommunicated");
                int choice = pl.sInPrompt(1, 2);
                if (choice == 1) {
                    pl.currentRes = pl.currentRes.merge(new Resources.ResBuilder().victoryPoint(pl.currentRes.faithPoint).build());
                    //FIXME this is so stupid, the player's currentRes should not be final...
                    pl.currentRes = pl.currentRes.merge(new Resources.ResBuilder().faithPoint(plFaithP).build().inverse());
                } else {
                    excommunicate(pl, period);
                }
            }
        }
    }
     // TODO discuss this excommunications implementation
    private void excommunicate(Player p, int period) {
        p.setExcommunication(excomms.get(period-1), period-1);
    }

    private void endgameMilitary () {
        //FIXME this is horrid, but I'm not sure it could be written some other way
        //1st gets 5 victoryP, 2nd gets 2 victoryP, if more than one player is first he gets the prize and the second gets nothing
        players.sort(Comparator.comparing(p -> p.currentRes.militaryPoint));
        int plWithMaxMilitary = 0;
        int maxMilitary = players.get(0).currentRes.militaryPoint;
        int secMaxMilitary = 0;
        for (Player p: players) {
            if (p.currentRes.militaryPoint == maxMilitary) {
                plWithMaxMilitary++;
                p.currentRes = p.currentRes.merge(new Resources.ResBuilder().victoryPoint(5).build());
            } else if (p.currentRes.militaryPoint > secMaxMilitary) {
                secMaxMilitary = p.currentRes.militaryPoint;
            }
        }
        if (plWithMaxMilitary > 1) {
            for (Player p: players) {
                if (p.currentRes.militaryPoint == secMaxMilitary) {
                    p.currentRes = p.currentRes.merge(new Resources.ResBuilder().victoryPoint(3).build());
                }
            }
        }
    }

    //TODO testing
    private void endgame() {
        List<Integer> territoriesVictory = Arrays.asList(1, 4, 10, 20);
        List<Integer> charactersVictory = Arrays.asList(1, 3, 6, 10, 15, 21);
        endgameMilitary();
        for (Player pl: players) {
            int countTerritories = 0;
            int countCharacters = 0;
            Resources purpleFinal = new Resources.ResBuilder().build();
            int sumResources = (pl.currentRes.coin + pl.currentRes.servant + pl.currentRes.stone + pl.currentRes.wood);
            for (Card i : pl.listCards()) { //switch is oh, so pretty, but I don't think we can use it with complex if statements
                //that we need for excommunications
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
            pl.currentRes = pl.currentRes.merge(purpleFinal);
            pl.currentRes = pl.currentRes.merge(new Resources.ResBuilder().victoryPoint(territoriesVictory.get(countTerritories - 3)).build());
            pl.currentRes = pl.currentRes.merge(new Resources.ResBuilder().victoryPoint(charactersVictory.get(countCharacters - 1)).build());
            pl.currentRes = pl.currentRes.merge(new Resources.ResBuilder().victoryPoint(sumResources / 5).build());

            String msg = String.format("%s scores %d",
                    pl.playerName, pl.currentRes.victoryPoint);
            pl.sOut(msg);
            log.info(msg);
        }
    }

    public Player getCurrPlayer() {
        return currPlayer;
    }

    public Board getBoard() {
        return this.board;
    }
}
