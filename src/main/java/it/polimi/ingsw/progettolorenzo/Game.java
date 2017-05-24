package it.polimi.ingsw.progettolorenzo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import it.polimi.ingsw.progettolorenzo.core.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

public class Game {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private Board board;
    private List<String> types = Arrays.asList(
            "territories", "buildings", "characters", "ventures");
    private HashMap<String, Deck> unhandledCards = new HashMap<>();
    private List<Player> players = new ArrayList<>();
    private int currTurn = 0;
    //FIXME the following attributes are for testing purposes
    private List<String> names = Arrays.asList(
            "Luca", "Alessandro", "Mattia", "Max");
    private List<String> colours = Arrays.asList(
            "red", "blue", "orange", "purple");

    public Game() {
        MyLogger.setup();
        log.info("Starting the game...");

        // FIXME testing player
        this.initPlayers(2);

        // init cards
        this.loadCards();

        // actually start the game
        this.turn();
    }

    private void initPlayers(int number) { //TODO risorse iniziali player a seconda del piazzamento
        for (int i=0; i<number; i++) {
            this.players.add(new Player(names.get(i), colours.get(i)));
        }
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
        this.unhandledCards.forEach((n, d) -> sb.append(n+"="+d.size()+" "));
        log.fine(String.format("Loaded %scards", sb));
    }

    private void turn() { //which is comprised of 4 rounds
        List<Player> playersOrder = new ArrayList<>(players);
        this.resetBoard(currTurn/2+1);
        for (Player pl: players) {
            pl.famMembersBirth();
        }
        for (int r=1; r<=4; r++) {
            this.round(playersOrder);
        }
        currTurn++;
    }

    private void round(List<Player> playersOrder) {
        for (Player pl: playersOrder) {
            //giocata con pl passato come parametro
        }

        /* FIXME - example
        Floor fl = this.board.towers.get(0).getFloors().get(1);
        boolean ret = fl.claimFloor(this.player.getAvailableFamMembers().get(0));
        if (!ret) {
            log.warning("Not allowed to claim the floor");
        }
        fl.logActions();
        fl.apply();*/
    }
}
