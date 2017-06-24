package it.polimi.ingsw.progettolorenzo.core.exc;

public class GameAlreadyStartedException extends RuntimeException {
    public GameAlreadyStartedException() {
        super("This Game already started!");
    }
}
