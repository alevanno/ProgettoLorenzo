package it.polimi.ingsw.ProgettoLorenzo;

import com.google.gson.JsonObject;
import it.polimi.ingsw.ProgettoLorenzo.Core.*;

import java.util.logging.Logger;

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


    private void resetBoard() {
        this.board = new Board(this.unhandledCards);
    }

    private void loadCards() {
        // FIXME ....lol......
        JsonObject data0 = Utils.getJsonArray("cardsModel.json")
                .get(0).getAsJsonObject();
        this.unhandledCards.add(new Card(data0));
        this.unhandledCards.add(new Card(data0));
        this.unhandledCards.add(new Card(data0));
        this.unhandledCards.add(new Card(data0));
    }

    private void turn() {
        this.resetBoard();
        this.player.famMembersBirth();
        this.round();
    }

    private void round() {
        this.board.towers.get(0).getFloors().get(0).claimFloor(this.player
                .getAvailableFamMembers().get(0));
    }
}
