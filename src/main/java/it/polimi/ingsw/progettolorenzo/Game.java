package it.polimi.ingsw.progettolorenzo;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.progettolorenzo.core.*;
import it.polimi.ingsw.progettolorenzo.core.exc.CardNotFoundException;
import it.polimi.ingsw.progettolorenzo.core.exc.GameAlreadyStartedException;
import it.polimi.ingsw.progettolorenzo.core.player.PlayerOperation;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * This enum represents the status of the game.
 */
enum GameStatus {
    /**
     * The game has not started yet, it is waiting for players.
     */
    INIT(" "),
    /**
     * The game is ongoing, it can't be joined anymore.
     */
    STARTED("[STARTED]"),
    /**
     * The game has ended.
     */
    ENDED("[ENDED]");

    private String str;
    GameStatus(String str) {
        this.str = str;
    }
    public String str() {
        return this.str;
    }
}

/**
 * This class represents a whole game.  It collects the users, deals with
 * I/O with them, controls the turning, the final point counting, players
 * timeouts and all the other aspects related to the actual game.
 */
public class Game implements Runnable {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private GameStatus state = GameStatus.INIT;
    private Board board;
    private List<String> types = Arrays.asList(
            "territories", "buildings", "characters", "ventures");
    private List<String> actions;
    private HashMap<String, Deck> unhandledCards = new HashMap<>();
    private final int maxPlayers;
    private final boolean personalBonusBoards;
    private final boolean leaderOn;
    private List<Player> players = new ArrayList<>(); //active players
    private List<Player> playersOrder; //the order of the players during the round
    private int halfPeriod;
    private Player currPlayer;
    private List<JsonObject> excomms = new ArrayList<>();
    private static final int TURN_TIMEOUT = Config.Game.TURN_TIMEOUT;
    private Map<String, Integer> famValues;


    public Game(Player firstPlayer, int maxPlayers,
                boolean personalBonusBoards, boolean leaderOn) {
        log.info("Starting the game...");
        firstPlayer.setParentGame(this);
        this.players.add(firstPlayer);
        this.maxPlayers = maxPlayers;
        this.personalBonusBoards = personalBonusBoards;
        this.leaderOn = leaderOn;
        if (maxPlayers > 1) {
            firstPlayer.sOut("The game is ready.  Waiting for other players now");
        }
    }

    /**
     * Add a new {@link Player} to the Game, and then starts the game if the
     * number of players of this game has been reached.
     * @param pl the player to be added
     * @throws GameAlreadyStartedException thrown when trying to add a {@link Player}
     *   to an already started game.
     */
    protected void addPlayer(Player pl) throws GameAlreadyStartedException {
        log.fine("Adding player to Game: " + pl.toString());
        synchronized (this.players) {
            if (this.state != GameStatus.INIT) {
                throw new GameAlreadyStartedException();
            }
            pl.setParentGame(this);
            this.players.add(pl);
            if (this.players.size() == this.maxPlayers) {
                this.players.notify();  // actually started the game
            }
        }
    }

    /**
     * The main function of the game thread; it loads the game settings, and awaits
     * for the players to join, then starts the first turn.
     **/
    public void run() {
        try {
            if (this.maxPlayers != 1) {
                synchronized (this.players) {
                    while (this.players.size() < this.maxPlayers) {
                        this.players.wait();
                    }
                }
            }
            this.state = GameStatus.STARTED;
            this.loadSettings();
            // starts the game and handles the turns
            this.turnController();
            this.state = GameStatus.ENDED;
        } catch (InterruptedException e) {
            log.log(Level.SEVERE, "Game "+this+" interrupted, shutting down…", e);
            this.players.forEach(x -> {
                x.sOut("ANNOUNCEMENT: Game is shutting down NOW!");
                x.sOut("quit");
            });
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public String toString() {
        return String.format(
            "%s%d/%d players, %s personal Boards, %s leaders cards",
            this.state.str(), this.players.size(), this.maxPlayers,
            this.personalBonusBoards, this.leaderOn
        );
    }

    /**
     * Assigns the initial resources to all players.
     */
    private void initPlayers() {
        int initialCoins = 5;
        for (Player p: this.players) {
            p.currentResMerge(
                    new Resources.ResBuilder().coin(initialCoins).build());
            log.fine(String.format("Player %s obtained %d starting coins",
                    p.playerName, initialCoins));
            initialCoins++;
        }
    }

    /**
     * Loads the excommunications and select 3 of them to be used in the game.
     */
    protected void initExcomm() {
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
            log.info("Excommunication loaded: " + excomm);
        }
    }

    /**
     * Instantiate a new {@link Board}, picking cards out of unhandled ones.
     * @param period the period for which to create the board
     */
    private void resetBoard(int period) {
        Deck deck = new Deck();
        this.unhandledCards.forEach((n, d) -> //n: type, d: deck
            deck.addAll(
                StreamSupport.stream(d.spliterator(), false)
                    .filter(c -> c.cardPeriod == period)
                    .limit(4)
                    .collect(Deck::new, Deck::add, Deck::addAll)
            )
        );
        for (Map.Entry<String,Deck> entry : this.unhandledCards.entrySet()) {
            Deck tmpDeck = entry.getValue();
            tmpDeck.listCards().forEach(c -> {
                for (Card card : deck) {
                    if (c.cardName.equals(card.cardName)) {
                        try {
                            tmpDeck.remove(c);
                        } catch (CardNotFoundException e) {
                            // FIXME deal appropriately with the exception
                        }
                    }
                }
            });
        }
        log.info(String.format(
                "Collected %d cards to give away", deck.size()));
        this.board = new Board(deck, this);
    }

    /**
     * Pick a Bonus Tile for every user and assign them to each user.
     * Run when the game first starts.
     */
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

    /**
     * Container method calling several game initializers.
     */
    public void loadSettings() {
        // init players
        this.initPlayers();

        //assign leader cards
        if(this.leaderOn) {
            this.assignLeaderCards();
            this.actions = Arrays.asList(
                    "Floor", "Market", "CouncilPalace", "Production",
                    "Harvest", "ActivateLeaderCard", "DiscardLeaderCard", "SkipRound");
            } else {
            this.actions = Arrays.asList(
                    "Floor", "Market", "CouncilPalace", "Production",
                    "Harvest", "SkipRound");
            }

        //assign bonus tile
        this.assignBonusT();

        // init excommunication tiles
        this.initExcomm();

        // init cards
        this.loadCards();
    }

    /**
     * The method is used to determine the player order in the next round:
     * it ensures that, if the player places a Family Member in
     * the council during this round, and gets a place higher than the
     * current, he is moved up in the players list
     * @param pl the player that gets the first available space
     * @param councilPlace the place that the player currently occupies in
     *                     the council palace
     * @return the boolean value represents whether or not the player has
     * moved up in the order
     */
    public boolean getFirstAvailPlace(Player pl, int councilPlace) {
        int index = players.indexOf(pl);
        if (index > councilPlace) {
            players.remove(index);
            players.add(councilPlace, pl);
            return true;
        }
        return false;
    }

    /**
     * it loads cards information from a json, split them according to card type
     * and finally shuffle them.
     */
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

    /**
     * it handles turns progress, checks if is time for the {@link #reportToVatican(int)}
     * or to {@link #endgame()}.
     * @throws InterruptedException is thrown if the current thread is interrupted
     */
    private void turnController() throws InterruptedException {
        for (halfPeriod = 1; halfPeriod < 7; halfPeriod++) {
            this.turn();
            if (halfPeriod % 2 == 0) {
                this.reportToVatican(halfPeriod);
            }
            if (halfPeriod == 6) {
                this.endgame();
            }
        }
    }

    /**
     * The turn is comprised of 4 rounds. It fills the map of family member
     * values to be use in {@link Player#famMembersBirth(Map)} ,
     * reset the usages of {@link LeaderCard}s and calls {@link #round(List, int)}
     * with the player order as param.
     * @throws InterruptedException is thrown if the current thread is interrupted
     */
    private void turn() throws InterruptedException {
        this.famValues = new HashMap<>();
        //the order stays the same for the duration of the turn
        playersOrder = new ArrayList<>(players);
        this.resetBoard((halfPeriod +1) / 2);
        this.famValues.put("Orange", new Random().nextInt(5) + 1);
        this.famValues.put("Black", new Random().nextInt(5) + 1);
        this.famValues.put("White", new Random().nextInt(5) + 1);
        log.fine("Dices thrown");

        for (Player pl : players) {
            pl.famMembersBirth(this.famValues);
            pl.sOut("Dice thrown!");
            pl.sOut("Values: " + this.famValues);
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

    /**
     * It basically checks if the player owns the excommunication
     * making him to skip the turn; then calls {@link #operation(Player)}.
     * @param playersOrder the order of the players
     * @param round the number of the round (in a turn)
     * @throws InterruptedException is thrown if the current thread is interrupted
     */
    private void round(List<Player> playersOrder, int round) throws InterruptedException {
        List<Player> skippedPlayers = new ArrayList<>();
        for (Player pl : playersOrder) {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }
            if (pl.getExcommunications().get(1).has("skipRound") && round == 1) {
                skippedPlayers.add(pl);
                pl.sOut("You skip the first round due to your excommunication");
                continue;
            }
            operation(pl);
        }
        for (Player pl : skippedPlayers) {
            operation(pl);
        }
    }

    public void operation (Player pl) {
        PlayerOperation op = new PlayerOperation(this, pl);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> f = executor.submit(op);
        try {
            f.get(TURN_TIMEOUT, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log.log(Level.INFO, "Player operation timed out", e);
            f.cancel(true);
            timeExpired(pl);
        } catch ( InterruptedException | ExecutionException in) {
            f.cancel(true);
            log.log(Level.SEVERE, in.getMessage(), in);
        }
        executor.shutdownNow();
    }

    public void timeExpired(Player pl) {
        pl.sOut("Time expired!");
        pl.sOut("quit");
        pl.setTimeouted();
    }

    /**
     * Calls the leader cards birth and randomly assigns 4 of them to every
     * player.
     */
    private void assignLeaderCards() {
        Map<String, LeaderCard> leaderMap = LeaderUtils.leadersBirth();
        List<LeaderCard> valuesList = new ArrayList<>(leaderMap.values());
        for (Player pl : players) {
            for (int i = 0; i < 4; i++) {
                int randomIndex = new Random().nextInt(valuesList.size());
                LeaderCard randomCard = valuesList.get(randomIndex);
                valuesList.remove(randomCard);
                pl.getLeaderCards().add(randomCard);
                randomCard.setPlayer(pl);
                log.fine("Leader Card " + randomCard.getName() + " assigned to "
                + pl.playerName);
            }
        }
    }

    protected void reportToVatican (int currTurn) {
        JsonArray faithVictory = Utils.getJsonArray("faithTrack.json");
        for (Player pl: players) {
            int period = currTurn/2;
            int plFaithP = pl.getCurrentRes().faithPoint;
            pl.sOut("You have " + plFaithP + " Faith Points. The Church requires " + (period + 2));
            if (plFaithP < period + 2) {
                pl.sOut("You are excommunicated");
                excommunicate(pl, period);
            }
            else {
                pl.sOut("Do you want to support the Church? If not you will be excommunicated");
                if (pl.sInPromptConf()) {
                    if (pl.leaderIsActive("Sisto IV")) {
                        pl.currentResMerge(
                                new Resources.ResBuilder().victoryPoint(5).build());
                    }
                    Resources victoryChurch = Resources.fromJson(faithVictory.get(pl.getCurrentRes().faithPoint));
                    pl.currentResMerge(victoryChurch);
                    log.info(String.format(
                        "Player %s supported the Church: he gains %d " +
                            "victoryPoints", pl, victoryChurch.victoryPoint));
                    pl.currentResMerge(new Resources.ResBuilder()
                            .faithPoint(plFaithP).build().inverse());
                } else {
                    excommunicate(pl, period);
                }
            }
        }
    }

    private void excommunicate(Player p, int period) {
        p.setExcommunication(excomms.get(period-1), period-1);
    }

    protected void endgameMilitary () {
        //1st gets 5 victoryP, 2nd gets 2 victoryP, if more than one player is first he gets the prize and the second gets nothing
        players.sort(Comparator.comparing(((Player p) -> p.getCurrentRes().militaryPoint)).reversed());
        int plWithMaxMilitary = 0;
        int maxMilitary = players.get(0).getCurrentRes().militaryPoint;
        int secMaxMilitary = 0;
        for (Player p: players) {
            if (p.getCurrentRes().militaryPoint == maxMilitary) {
                plWithMaxMilitary++;
                p.currentResMerge(new Resources.ResBuilder().victoryPoint(5).build());
                log.info("Player " + p + " has the highest militaryPoint: he gains 5 victoryPoint");
            } else if (p.getCurrentRes().militaryPoint > secMaxMilitary) {
                secMaxMilitary = p.getCurrentRes().militaryPoint;
            }
        }
        if (plWithMaxMilitary == 1) {
            for (Player p: players) {
                if (p.getCurrentRes().militaryPoint == secMaxMilitary) {
                    p.currentResMerge(new Resources.ResBuilder().victoryPoint(3).build());
                    log.info("Player " + p + " has the second highest militaryPoint: he gains 3 victoryPoint");
                }
            }
        }
    }

    private void endgame() {
        endgameMilitary();
        for (Player pl: players) {
            pl.endgame();
            String msg = String.format("%s scores %d",
                    pl.playerName, pl.getCurrentRes().victoryPoint);
            for (Player p: players) {
                p.sOut(msg);
            }
            log.info(msg);
            pl.sOut("quit");
        }
    }

    public void setCurrPlayer(Player pl) {
        this.currPlayer = pl;
    }

    public Board getBoard() { return this.board; }

    public void setBoard(Board board) {
        this.board = board;
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public int getNumOfPlayers() { return players.size(); }

    public int getHalfPeriod() { return halfPeriod; }

    public List<String> getActions() { return actions; }

    private JsonObject serialize(Player targetPlayer) {
        Map<String, Object> ret = new HashMap<>();
        List<JsonObject> playersJ = new ArrayList<>();
        this.players.stream().filter(
            p -> p.playerName != targetPlayer.playerName
        ).forEach(
            p -> playersJ.add(p.serialize())
        );
        ret.put("players", playersJ);
        ret.put("you", targetPlayer.serialize());
        ret.put("currentPlayer", this.currPlayer.serialize());
        if (this.famValues != null) {
            ret.put("famValues", this.famValues);
        }
        ret.put("board", this.board.serialize());
        ret.put("excomms", this.excomms);
        return new Gson().fromJson(new Gson().toJson(ret), JsonObject.class);
    }

    public void displayGame() {
        this.players.forEach(p ->
            p.sOut("☃" + new Gson().toJson(this.serialize(p)))
        );
    }

}
