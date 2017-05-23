package it.polimi.ingsw.ProgettoLorenzo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import it.polimi.ingsw.ProgettoLorenzo.Core.*;

import java.util.logging.Logger;
import java.util.stream.StreamSupport;

public class Game {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private Board board;
    private Deck unhandledCards = new Deck();
    private Player player = new Player("Test player", "red");

    public Game() {
        MyLogger.setup();
        log.info("Starting the game...");
        this.loadCards();
        this.turn();
    }


    private void resetBoard(int period) {
        // FIXME: bug:
        /*
           The following collects 16 random cards from our Deck, and pass
           them over to Board.  It just so happens that those cards are of
           only 2 types (as every period has 8 cards per type, 8*2=16==limit()).
           This way the instantiated Board will have 2 towers completely
           empty (open question: shall Board fail if we try to instantiate
           (partially) empty towers?).
         */
         Deck d = StreamSupport.stream(
                 this.unhandledCards.spliterator(),false
            )
            .filter(c -> c.cardPeriod == period)
            .limit(16)  //make configurable before Board() is instantiated?
            .collect(Deck::new, Deck::add, Deck::addAll);
         log.finest(String.format("Collected %d cards to give away", d.size()));
         this.board = new Board(d);
    }

    private void loadCards() {
        JsonArray cardsData = Utils.getJsonArray("cards.json");
        for (JsonElement c : cardsData) {
            this.unhandledCards.add(new Card(c.getAsJsonObject()));
        }
        log.fine(String.format("Loaded %d cards", this.unhandledCards.size()));
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
