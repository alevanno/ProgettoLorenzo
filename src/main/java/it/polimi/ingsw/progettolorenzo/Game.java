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
    private List<String> actions = Arrays.asList(
            "Floor", "Market", "CouncilPalace", "Production",
            "Harvest", "ActivateLeaderCard");
    private HashMap<String, Deck> unhandledCards = new HashMap<>();
    private List<Player> players = new ArrayList<>(); //active players and their order
    private int halfPeriod;
    private Player currPlayer;
    private List<JsonObject> excomms = new ArrayList<>();
    private final boolean personalBonusBoards;
    private int famMemIncrease;

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

        this.loadSettings();
        // starts the game and handles the turns
        for (halfPeriod = 1; halfPeriod < 7 ; halfPeriod++) {
            this.turn();
            if (halfPeriod % 2 == 0) {
                this.reportToVatican(halfPeriod);
            }
            if (halfPeriod == 6) {
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
        this.unhandledCards.forEach((n, d) -> //n: type, d: deck
            deck.addAll(
                    StreamSupport.stream(d.spliterator(), false)
                            .filter(c -> c.cardPeriod == period)
                            .limit(4) // FIXME make configurable before Board() is istantiated
                            .collect(Deck::new, Deck::add, Deck::addAll)
            )
        );
        for (String type : this.unhandledCards.keySet()) {
            Deck tmpDeck = this.unhandledCards.get(type);
            tmpDeck.listCards().forEach(c -> {
                for (Card card : deck) {
                    if (c.cardName.equals(card.cardName)) {
                        tmpDeck.remove(c);
                    }
                }
            });
        }
        log.info(String.format(
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

    public void loadSettings() {
        // init players
        this.initPlayers();

        //assign bonus tile
        this.assignBonusT();

        // init excommunication tiles
        this.initExcomm();

        // init cards
        this.loadCards();
    }

    public boolean getFirstAvailPlace(Player pl, int councPlace) {
        int index = players.indexOf(pl);
        if (index > councPlace) {
            players.remove(index);
            players.add(councPlace, pl);
            return true;
        }
        return false;
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
        this.unhandledCards.forEach((x,y) -> y.shuffleCards());
        StringBuilder sb = new StringBuilder();
        this.unhandledCards.forEach((n, d) -> sb.append(n + "=" + d.size() + " "));
        log.fine(String.format("Loaded %scards", sb));
    }

    private void turn() { //which is comprised of 4 rounds
        List<Player> playersOrder = new ArrayList<>(players); //the order stays the same for the duration of the turn
        this.resetBoard((halfPeriod +1) / 2);
        Map<String, Integer> famValues = new HashMap<>();
        famValues.put("Orange", new Random().nextInt(5) + 1);
        famValues.put("Black", new Random().nextInt(5) + 1);
        famValues.put("White", new Random().nextInt(5) + 1);
        log.fine("Dices thrown");

        for (Player pl : players) {
            pl.famMembersBirth(famValues);
            pl.sOut("Dice thrown!");
            pl.sOut("Values: " + famValues);
            for (LeaderCard leader : pl.getLeaderCards()) {
                if(leader.hasOnePerRoundAbility()){
                    leader.setOnePerRoundUsage(false);
                }
            }
        }

        for (int r = 1; r <= 4; r++) {
            this.round(playersOrder, r);
        }
    }

    private void round(List<Player> playersOrder, int round) {
        List<Player> skippedPlayers = new ArrayList<>();
        for (Player pl : playersOrder) {
            if (pl.getExcommunications().get(1).has("skipRound") && round == 1) {
                skippedPlayers.add(pl);
                pl.sOut("You skip the first round due to your excommunication");
                continue;
            }
            this.operation(pl);
        }
        for (Player pl : skippedPlayers) {
            this.operation(pl);
        }
    }

    private void operation(Player pl) {
        currPlayer = pl;
        pl.sOut("Turn " + this.halfPeriod + ": Player " + pl.playerName +
                " is the next player for this round:");
        pl.sOut("Current Res: " + pl.currentRes.toString());
        boolean ret = false;
        while (true) {
            this.board.displayBoard();
            pl.sOut("Which family member do you want to use?: ");
            pl.sOut(pl.displayFamilyMembers());
            FamilyMember famMem = pl.getAvailableFamMembers()
                    .get(pl.sInPrompt(1, pl.getAvailableFamMembers().size()) - 1);
            pl.sOut(famMem.getSkinColour() + " family member selected");
            famMemIncrease = pl.increaseFamValue(famMem);
            pl.sOut("Available actions:");
            pl.sOut(Utils.displayList(actions));
            pl.sOut("Which action do you want to try?: ");
            board.displayBoard();
            String action = actions.get(pl.sInPrompt(1, actions.size()) - 1);
            //TODO this should be more streamlined

            if ("Floor".equalsIgnoreCase(action)) {
                ret = Move.floorAction(this.board, famMem);
            } else if ("Market".equalsIgnoreCase(action)) {
                ret = Move.marketAction(this.board, famMem);
            } else if ("CouncilPalace".equalsIgnoreCase(action)) {
                ret = Move.councilAction(this.board, famMem);
            } else if ("Production".equalsIgnoreCase(action)) {
                ret = Move.prodAction(this.board, famMem);
            } else if ("Harvest".equalsIgnoreCase(action)) {
                ret = Move.harvAction(this.board, famMem);
            } else if ("ActivateLeaderCard".equalsIgnoreCase(action)) {
                pl.activateLeaderCard();
                continue;
            }
            if (ret) {
                break;
            } else {
                // placed here to abort this operation if player is not satisfied, reverts the value increase by servants
                pl.sOut("Reverting famMemIncrease");
                pl.revertFamValue(famMem, famMemIncrease);
                pl.sOut("Current Res: " + pl.currentRes.toString());
            }
        }
    }

    private void reportToVatican (int currTurn) {
        //TODO testing
        JsonArray faithVictory = Utils.getJsonArray("faithTrack.json");
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
                    for (LeaderCard leader : pl.getLeaderCards()) {
                        if("SistoIV".equals(leader.getName())
                                && leader.isActivated()) {
                            pl.currentRes = pl.currentRes.merge(
                                    new Resources.ResBuilder().victoryPoint(5).build());
                            break;
                        }
                    }
                    pl.currentRes = pl.currentRes.merge(Resources.fromJson(faithVictory.get(pl.currentRes.faithPoint)));
                    //FIXME this is so stupid, the player's currentRes should not be final...
                    pl.currentRes = pl.currentRes.merge(new Resources.ResBuilder()
                            .faithPoint(plFaithP).build().inverse());
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
        endgameMilitary();
        for (Player pl: players) {
            pl.endgame();
            String msg = String.format("%s scores %d",
                    pl.playerName, pl.currentRes.victoryPoint);
            pl.sOut(msg);
            log.info(msg);
        }
    }

    public Player getCurrPlayer() {
        return currPlayer;
    }

    public Board getBoard() { return this.board; }

    public List<Player> getPlayers() {
        return this.players;
    }

    public int getNumOfPlayers() { return players.size(); }

    public void addFamMemIncrease(int incomingIncrease) {
        this.famMemIncrease += incomingIncrease;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setCurrPlayer(Player pl) {
        this.currPlayer = pl;
    }
}
