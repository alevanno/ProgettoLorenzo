package it.polimi.ingsw.ProgettoLorenzo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import it.polimi.ingsw.ProgettoLorenzo.Core.*;

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
    private Player player;

    public Game() {
        MyLogger.setup();
        log.info("Starting the game...");

        // FIXME testing player
        this.player = new Player("Test player", "red");

        // init cards
        this.loadCards();

        // actually start the game
        this.turn();
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

    private void turn() {
        this.resetBoard(1);  // FIXME
        this.player.famMembersBirth();
        this.round();
    }

    private void round() {
        // FIXME - example
        Floor fl = this.board.towers.get(0).getFloors().get(1);
        boolean ret = fl.claimFloor(this.player.getAvailableFamMembers().get(0));
        if (!ret) {
            log.warning("Not allowed to claim the floor");
        }
        fl.logActions();
        fl.apply();
    }
}
