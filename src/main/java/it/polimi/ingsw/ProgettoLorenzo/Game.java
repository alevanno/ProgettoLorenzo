package it.polimi.ingsw.ProgettoLorenzo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import it.polimi.ingsw.ProgettoLorenzo.Core.*;

import java.util.HashMap;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

public class Game {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private Board board;
    private String[] types = new String[4];
    private HashMap<String, Deck> unhandledCards = new HashMap<>();
    private Player player;

    public Game() {
        MyLogger.setup();
        log.info("Starting the game...");

        // FIXME testing player
        this.player = new Player("Test player", "red");

        // init the cards
        this.types[0] = "territories";
        this.types[1] = "buildings";
        this.types[2] = "characters";
        this.types[3] = "ventures";
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
         log.finest(String.format(
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
        this.board.towers.get(0).getFloors().get(0).claimFloor(this.player
                .getAvailableFamMembers().get(0));
    }
}
