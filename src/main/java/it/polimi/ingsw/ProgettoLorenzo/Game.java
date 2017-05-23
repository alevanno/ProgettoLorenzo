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
        this.board = new Board(
            StreamSupport.stream(this.unhandledCards.spliterator(), false)
                .filter(c -> c.cardPeriod == period)
                .limit(16)  //make configurable before Board() is instantiated?
                .collect(Deck::new, Deck::add, Deck::addAll)
        );
    }

    private void loadCards() {
        JsonArray cardsData = Utils.getJsonArray("cards.json");
        for (JsonElement c : cardsData) {
            this.unhandledCards.add(new Card(c.getAsJsonObject()));
        }
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
